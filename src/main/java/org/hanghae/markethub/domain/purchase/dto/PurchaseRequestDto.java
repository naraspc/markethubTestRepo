package org.hanghae.markethub.domain.purchase.dto;

import lombok.Builder;
import org.hanghae.markethub.global.constant.Status;

public record PurchaseRequestDto(
        Status status,
        Long cartId
) {
    public record SinglePurchaseRequestDto(
            Status status,
            Long itemId
    ) {

    }


}
