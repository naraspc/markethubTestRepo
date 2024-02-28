package org.hanghae.markethub.domain.purchase.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
import org.hanghae.markethub.domain.purchase.dto.PurchaseResponseDto;
import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.hanghae.markethub.domain.purchase.repository.PurchaseRepository;
import org.hanghae.markethub.global.constant.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ItemService itemService;

    @Transactional
    public PurchaseResponseDto createOrder(PurchaseRequestDto purchaseRequestDto, String email) {

        List<Purchase> existingPurchases = purchaseRepository.findAllByStatusAndEmail(Status.EXIST, email);
        // 조회된 구매건 삭제
        checkListPurchaseExist(existingPurchases);

        Purchase purchase = Purchase.builder()
                .status(purchaseRequestDto.status())
                .email(email)
                .itemName(purchaseRequestDto.itemName())
                .quantity(purchaseRequestDto.quantity())
                .price(purchaseRequestDto.price())
                .itemId(purchaseRequestDto.itemId())
                .build();

        purchaseRepository.save(purchase);
        return PurchaseResponseDto.fromPurchase(purchase);

    }

    @Transactional
    protected void checkListPurchaseExist(List<Purchase> existingPurchases) {
        if (!existingPurchases.isEmpty()) {
            deleteAllPurchase(existingPurchases);
        }
    }

    @Transactional
    public void createPurchaseByCart(List<PurchaseRequestDto> purchaseRequestDtoList, String email) {


        List<Purchase> existingPurchases = purchaseRepository.findAllByStatusAndEmail(Status.EXIST, email);
        checkListPurchaseExist(existingPurchases);

        for (PurchaseRequestDto purchaseRequestDto : purchaseRequestDtoList) {
            // PurchaseRequestDto에 있는 정보를 바탕으로 구매를 처리합니다.
            Purchase purchase = Purchase.builder()
                    .status(purchaseRequestDto.status())
                    .email(email)
                    .itemName(purchaseRequestDto.itemName())
                    .quantity(purchaseRequestDto.quantity())
                    .price(purchaseRequestDto.price())
                    .itemId(purchaseRequestDto.itemId())
                    .build();
            purchaseRepository.save(purchase);

        }
    }


    @Transactional(readOnly = true)
    public List<PurchaseResponseDto> findAllPurchaseByEmail(String email) {
        List<Purchase> purchase = purchaseRepository.findByStatusAndEmailOrderByCreatedTimeDesc(Status.EXIST,email);
        if (purchase == null) {
            throw new EntityNotFoundException("Purchase not found for email: " + email);
        }
        return PurchaseResponseDto.fromListPurchaseEntity(purchase);
    }

    @Transactional(readOnly = true)
    public PurchaseResponseDto findSinglePurchase(Long id) {

        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

        return PurchaseResponseDto.fromPurchase(purchase);
    }


    @Transactional(readOnly = true)
    public Map<String, List<Purchase>> GetGroupPurchaseByImpUid(String email) {

        List<Purchase> purchaseList = purchaseRepository.findAllByStatusNotInAndEmail(
                Arrays.asList(Status.DELETED, Status.EXIST),
                email
        );



        return getPurchaseGroups(purchaseList);
    }

    @NotNull
    private static Map<String, List<Purchase>> getPurchaseGroups(List<Purchase> purchaseList) {
        Map<String, List<Purchase>> purchaseGroups = new HashMap<>();
        for (Purchase purchase : purchaseList) {
            String impUid = purchase.getImpUid();

            if (!purchaseGroups.containsKey(impUid)) {
                purchaseGroups.put(impUid, new ArrayList<>());
            }

            purchaseGroups.get(impUid).add(purchase);
        }
        return purchaseGroups;
    }

    public List<PurchaseResponseDto> mapToPurchaseResponseDto(List<Purchase> purchases) {
        return PurchaseResponseDto.fromListPurchaseEntity(purchases);
    }
    @Transactional(readOnly = true)
    public Map<String, List<PurchaseResponseDto>> findAllOrderedPurchaseGroupedByImpUid(String email) {
        Map<String, List<Purchase>> purchaseGroups = GetGroupPurchaseByImpUid(email);

        return getMapDTOFromMapEntity(purchaseGroups);
    }

    @NotNull
    private Map<String, List<PurchaseResponseDto>> getMapDTOFromMapEntity(Map<String, List<Purchase>> purchaseGroups) {
        Map<String, List<PurchaseResponseDto>> groupedPurchaseResponse = new HashMap<>();
        for (Map.Entry<String, List<Purchase>> entry : purchaseGroups.entrySet()) {
            String impUid = entry.getKey();
            List<Purchase> purchases = entry.getValue();
            List<PurchaseResponseDto> purchaseResponseDtos = mapToPurchaseResponseDto(purchases);
            groupedPurchaseResponse.put(impUid, purchaseResponseDtos);
        }
        return groupedPurchaseResponse;
    }


    @Transactional
    public void ChangeStatusToCancelled(String impUid) {
        List<Purchase> purchaseList = purchaseRepository.findAllByImpUid(impUid);
        purchaseList.forEach(Purchase::setStatusToCancelled);
    }
    @Transactional
    public void rollbackItemsQuantity(String itemUid) throws JsonProcessingException {
        List<Purchase> purchaseList = purchaseRepository.findAllByImpUid(itemUid);

        for (Purchase purchase : purchaseList) {
            Long itemId = purchase.getItemId();
            int quantity = purchase.getQuantity();
            itemService.increaseQuantity(itemId, quantity);
        }
    }


    @Transactional
    public void deletePurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Purchase not found"));


        purchase.setStatusToDelete();

    }

    @Transactional
    public void updatePurchaseStatusToOrdered(String userEmail) {
        // 해당 유저의 모든 구매 기록 가져오기
        List<Purchase> purchases = purchaseRepository.findAllByStatusAndEmail(Status.EXIST,userEmail);

        // 각 구매 기록의 상태를 "ORDERED"로 변경
        purchases.forEach(Purchase::setStatusToOrderComplete);
        purchaseRepository.saveAll(purchases); // 변경된 모든 엔티티를 일괄 저장
    }

    @Transactional
    public void updateImpUidForPurchases(String email, String newImpUid) {
        List<Purchase> purchases = purchaseRepository.findAllByStatusAndEmail(Status.EXIST, email);

        for (Purchase purchase : purchases) {
            purchase.setItemUidByOrederd(newImpUid); // 변경 사항 적용
        }
        // 더티 체킹에 의해 변경 사항이 DB에 자동 반영됨
    }

    @Transactional
    public void deleteAllPurchase(List<Purchase> purchases) {
        for (Purchase purchase : purchases){
            purchase.setStatusToDelete();
        }
    }



}
