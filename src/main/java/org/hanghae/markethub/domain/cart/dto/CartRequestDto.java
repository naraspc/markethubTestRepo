package org.hanghae.markethub.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hanghae.markethub.domain.item.entity.Item;

@Getter
@Builder
@AllArgsConstructor
public class CartRequestDto {
    private Item item;
    private int quantity;
}
