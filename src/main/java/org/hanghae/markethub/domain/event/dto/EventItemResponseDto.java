package org.hanghae.markethub.domain.event.dto;

import lombok.Builder;
import lombok.Getter;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class EventItemResponseDto {
	private ItemsResponseDto items;
	private int oldPrice;

	public void addItem(ItemsResponseDto itemsResponseDto, int oldPrice) {
		this.items = itemsResponseDto;
		this.oldPrice = oldPrice;
	}
}