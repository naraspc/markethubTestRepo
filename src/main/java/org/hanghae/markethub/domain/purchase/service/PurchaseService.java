package org.hanghae.markethub.domain.purchase.service;

import lombok.RequiredArgsConstructor;

import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
import org.hanghae.markethub.domain.purchase.dto.PurchaseResponseDto;
import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.hanghae.markethub.domain.purchase.repository.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CartRepository cartRepository;

    //C
    public PurchaseResponseDto createOrder(PurchaseRequestDto purchaseRequestDto, String userId) {
        List<Cart> cart = cartRepository.findByUserId(userId);
        Purchase purchase = Purchase.builder()
                .status(purchaseRequestDto.status())
                .cart(cart)
                .build();

        purchaseRepository.save(purchase);

        return PurchaseResponseDto.fromPurchase(purchase);



    }


    //R


    //U


    //D
}
