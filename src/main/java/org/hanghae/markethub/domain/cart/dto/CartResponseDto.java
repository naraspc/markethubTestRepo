package org.hanghae.markethub.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hanghae.markethub.domain.item.entity.Item;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class CartResponseDto {
    private Long id;
    private Item item;
    private String img;
    private int price;
    private int quantity;
    private LocalDate date;
}
