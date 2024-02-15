package org.hanghae.markethub.domain.purchase.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;


public record PaymentRequestDto(
        String email,
        List<PurchaseItemDto> items,
        double amount
){
    public record PurchaseItemDto(
            Long itemId,
            int quantity
    ) {

    }
    public record getToken(
            String imp_key,
            String imp_secret
    ) {}
}