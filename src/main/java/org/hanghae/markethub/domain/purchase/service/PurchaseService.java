package org.hanghae.markethub.domain.purchase.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
import org.hanghae.markethub.domain.purchase.dto.PurchaseResponseDto;
import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.hanghae.markethub.domain.purchase.repository.PurchaseRepository;
import org.hanghae.markethub.domain.user.dto.UserResponseDto;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.jwt.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    public PurchaseResponseDto createOrder(PurchaseRequestDto purchaseRequestDto, String email) {
       User user = findUserByEmail(email);

        List<Cart> cart = cartRepository.findAllByUserAndStatusOrderByCreatedTime(user, Status.EXIST);

        Purchase purchase = Purchase.builder()
                .status(purchaseRequestDto.status())
                .cart(cart)
                .build();

        purchaseRepository.save(purchase);

        return PurchaseResponseDto.fromPurchase(purchase);



    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Transactional
    public PurchaseResponseDto createSingleOrder(PurchaseRequestDto.SinglePurchaseRequestDto singlePurchaseRequestDto) {
        Item item = itemRepository.findById(singlePurchaseRequestDto.itemId()).orElseThrow(() -> new IllegalArgumentException("Item not found for ID: " + singlePurchaseRequestDto.itemId()));

        Purchase purchase = Purchase.builder()
                .status(singlePurchaseRequestDto.status())
                .item(item)
                .build();

        return PurchaseResponseDto.fromPurchase(purchase);
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponseDto> findAllPurchaseByEmail(String email) {
        List<Purchase> purchase = purchaseRepository.findByUserId(email);
        if (purchase == null) {
            throw new EntityNotFoundException("Purchase not found for email: " + email);
        }
        return PurchaseResponseDto.fromListPurchaseEntity(purchase);
    }

    @Transactional(readOnly = true)
    public PurchaseResponseDto findPurchaseByEmail(String email) {

        Purchase purchase = purchaseRepository.findByUserEmail(email);
        if (purchase == null) {
            throw new EntityNotFoundException("Purchase not found for email: " + email);
        }

        return PurchaseResponseDto.fromSingleItemPurchase(purchase);
    }


    @Transactional
    public void deletePurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

        purchase.setStatusToDelete();

    }

}
