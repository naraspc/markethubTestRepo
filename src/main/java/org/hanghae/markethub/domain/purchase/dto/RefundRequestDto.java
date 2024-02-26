package org.hanghae.markethub.domain.purchase.dto;

import java.math.BigDecimal;

public record RefundRequestDto(
        String imp_uid,
        double checksum,
        String reason,
        Long itemId,
        int quantity
) {

}
