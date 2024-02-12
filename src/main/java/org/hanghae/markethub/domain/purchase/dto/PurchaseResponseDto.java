package org.hanghae.markethub.domain.purchase.dto;

import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.hanghae.markethub.global.constant.Status;


import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record PurchaseResponseDto(
        Long purchaseId,
        Status status,
        ItemDetailsDto itemDetails
) {
    public static PurchaseResponseDto fromPurchase(Purchase purchase) {
        return new PurchaseResponseDto(
                purchase.getId(),
                purchase.getStatus(),
                new ItemDetailsDto(
                        purchase.getItemName(),
                        purchase.getPrice(),
                        purchase.getQuantity()
                )
        );
    }

    public static List<PurchaseResponseDto> fromListPurchaseEntity(List<Purchase> purchases) {
        return purchases.stream()
                .map(PurchaseResponseDto::fromPurchase)
                .collect(Collectors.toList());
    }

    public record ItemDetailsDto(
            String itemName,
            BigDecimal price,
            int quantity
    ) {
    }
}