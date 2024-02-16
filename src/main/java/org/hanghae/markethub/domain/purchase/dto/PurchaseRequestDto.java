package org.hanghae.markethub.domain.purchase.dto;

import lombok.Builder;
import org.hanghae.markethub.global.constant.Status;

import java.math.BigDecimal;

public record PurchaseRequestDto(
        Status status,
        String itemName,
        int quantity,
        Long itemId,
        BigDecimal price
) {

}
