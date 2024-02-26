package org.hanghae.markethub.domain.purchase.dto;

import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.hanghae.markethub.global.constant.Status;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record PurchaseResponseDto(
        Long purchaseId,
        Status status,
        String impUid,
        LocalDateTime lastModifiedTime,
        ItemDetailsDto itemDetails
) {
    public static PurchaseResponseDto fromPurchase(Purchase purchase) {
        return new PurchaseResponseDto(
                purchase.getId(),
                purchase.getStatus(),
                purchase.getImpUid(),
                purchase.getLastModifiedTime(),
                new ItemDetailsDto(
                        purchase.getItemName(),
                        purchase.getPrice(),
                        purchase.getQuantity(),
                        purchase.getItemId()
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
            int quantity,
            Long itemId
    ) {
    }
}