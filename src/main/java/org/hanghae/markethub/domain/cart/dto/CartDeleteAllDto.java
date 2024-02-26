package org.hanghae.markethub.domain.cart.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CartDeleteAllDto {
    private List<String> cartIds;
}
