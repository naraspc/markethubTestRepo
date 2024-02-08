package org.hanghae.markethub.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.item.entity.Item;

@Getter
@Builder
@AllArgsConstructor
public class UpdateValidResponseDto {
    private Item item;
    private Cart cart;
}
