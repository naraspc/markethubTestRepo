package org.hanghae.markethub.domain.order.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.global.constant.Status;


public record OrderRequestDto(
        Status status
) {


}
