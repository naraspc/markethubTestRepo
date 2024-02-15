package org.hanghae.markethub.domain.purchase.dto;

import java.math.BigDecimal;

public record RefundRequestDto(
        String imp_uid,
        BigDecimal checksum,
        String reason
) {

}
