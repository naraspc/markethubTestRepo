package org.hanghae.markethub.domain.purchase.dto;

import lombok.Builder;
import org.hanghae.markethub.global.constant.Status;

import java.math.BigDecimal;

public record PurchaseRequestDto(
        Status status,
        String itemName,
        String email,
        int quantity,
        BigDecimal price
) {
    public record SinglePurchaseRequestDto(
            Status status,
            String itemName,
            String email,
            int quantity,
            BigDecimal price
    ) {

    }


}
