package org.hanghae.markethub.domain.purchase.service;

import lombok.RequiredArgsConstructor;

import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
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
    private final ItemRepository itemRepository;

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
    public PurchaseResponseDto createSingleOrder(PurchaseRequestDto.SinglePurchaseRequestDto singlePurchaseRequestDto) {
        Item item = itemRepository.findById(singlePurchaseRequestDto.itemId()).orElseThrow(() -> new IllegalArgumentException("Item not found for ID: " + singlePurchaseRequestDto.itemId()));

        Purchase purchase = Purchase.builder()
                .status(singlePurchaseRequestDto.status())
                .item(item)
                .build();

        return PurchaseResponseDto.fromPurchase(purchase);
    }

    //R


    //U


    //D
}
