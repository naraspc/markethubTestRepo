package org.hanghae.markethub.domain.purchase.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
import org.hanghae.markethub.domain.purchase.dto.PurchaseResponseDto;
import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.hanghae.markethub.domain.purchase.repository.PurchaseRepository;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.jwt.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    public PurchaseResponseDto createOrder(PurchaseRequestDto purchaseRequestDto, String email) {

        Purchase purchase = Purchase.builder()
                .status(purchaseRequestDto.status())
                .email(purchaseRequestDto.email())
                .itemName(purchaseRequestDto.itemName())
                .quantity(purchaseRequestDto.quantity())
                .price(purchaseRequestDto.price())
                .build();

        purchaseRepository.save(purchase);

        return PurchaseResponseDto.fromPurchase(purchase);



    }
    @Transactional
    public List<PurchaseResponseDto> createPurchaseByCart(List<PurchaseRequestDto> purchaseRequestDtoList, String email) {
        List<Purchase> purchaseList = new ArrayList<>();

        for (PurchaseRequestDto purchaseRequestDto : purchaseRequestDtoList) {
            // PurchaseRequestDto에 있는 정보를 바탕으로 구매를 처리합니다.
            Purchase purchase = Purchase.builder()
                    .status(purchaseRequestDto.status())
                    .email(email)
                    .itemName(purchaseRequestDto.itemName())
                    .quantity(purchaseRequestDto.quantity())
                    .price(purchaseRequestDto.price())
                    .build();
            purchaseRepository.save(purchase);

            // 구매 목록에 추가합니다.
            purchaseList.add(purchase);
        }

        // 구매 목록을 PurchaseResponseDto 형태로 변환하여 반환합니다.
        return purchaseList.stream()
                .map(PurchaseResponseDto::fromPurchase)
                .collect(Collectors.toList());
    }


//    @Transactional
//    public PurchaseResponseDto createSingleOrder(PurchaseRequestDto.SinglePurchaseRequestDto singlePurchaseRequestDto) {
//
//
//        Purchase purchase = Purchase.builder()
//                .status(singlePurchaseRequestDto.status())
//                .email(singlePurchaseRequestDto.email())
//                .itemName(singlePurchaseRequestDto.itemName())
//                .quantity(singlePurchaseRequestDto.quantity())
//                .price(singlePurchaseRequestDto.price())
//                .build();
//
//        purchaseRepository.save(purchase);
//
//        return PurchaseResponseDto.fromPurchase(purchase);
//    }

    @Transactional(readOnly = true)
    public List<PurchaseResponseDto> findAllPurchaseByEmail(String email) {
        List<Purchase> purchase = purchaseRepository.findByEmailOrderByCreatedTimeDesc(email);
        if (purchase == null) {
            throw new EntityNotFoundException("Purchase not found for email: " + email);
        }
        return PurchaseResponseDto.fromListPurchaseEntity(purchase);
    }

    @Transactional(readOnly = true)
    public PurchaseResponseDto findOrderedPurchaseByEmail(String email, Status status) {

        Purchase purchase = purchaseRepository.findByStatusAndEmail(status, email);


        return PurchaseResponseDto.fromPurchase(purchase);
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponseDto> findAllOrderedPurchaseByEmail(String email, Status status){
        List<Purchase> purchaseList = purchaseRepository.findAllByStatusAndEmail(status,email);

        return PurchaseResponseDto.fromListPurchaseEntity(purchaseList);
    }

    @Transactional
    public void deletePurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

        purchase.setStatusToDelete();

    }

}
