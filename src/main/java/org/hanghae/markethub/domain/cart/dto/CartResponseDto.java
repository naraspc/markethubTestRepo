package org.hanghae.markethub.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae.markethub.domain.item.entity.Item;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {
    private Long id;
    private Item item;
    private String img;
    private int price;
    private int quantity;
}
