package org.hanghae.markethub.domain.purchase.dto;

import java.util.List;


public record PaymentRequestDto(
        String email,
        String impUid,
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