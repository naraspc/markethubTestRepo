package org.hanghae.markethub.domain.purchase.dto;

import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.hanghae.markethub.global.constant.Status;

import java.util.List;

public record PurchaseResponseDto(
        Long purchaseId,
        Status status,
//        Long totalPrice,
        List<CartDetailsDto> carts
) {
    public static PurchaseResponseDto fromPurchase(Purchase purchase) {
        List<CartDetailsDto> cartDetailsDto = purchase.getCart().stream()
                .map(cart -> new CartDetailsDto(
                        cart.getCartId(),
                        cart.getItemId(),
                        cart.getQuantity(),
                        cart.getPrice(),
                        cart.getAddress(),
                        cart.getPoint()))
                .toList();

        return new PurchaseResponseDto(
                purchase.getId(),
                purchase.getStatus(),
//                purchase.getTotalPrice(),
                cartDetailsDto
        );
    }

    public record CartDetailsDto(
            Long cartId,
            String itemId,
            int quantity,
            int price,
            String address,
            Long point
    ) {}
}