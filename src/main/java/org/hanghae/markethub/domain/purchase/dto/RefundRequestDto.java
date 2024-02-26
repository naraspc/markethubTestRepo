package org.hanghae.markethub.domain.purchase.dto;

import java.math.BigDecimal;
import java.util.List;

public record RefundRequestDto(
        String imp_uid,
        double checksum,
        String reason
) {
    public record CancelRequestDto(
            String imp_uid,
            double checksum,
            String reason
    ){}


}
