package org.hanghae.markethub.domain.cart.dto;

import lombok.Getter;
import lombok.Setter;
import org.hanghae.markethub.domain.item.entity.Item;

@Getter
@Setter
public class CartRequestDto {
    private Item item;
    private int quantity;
}
