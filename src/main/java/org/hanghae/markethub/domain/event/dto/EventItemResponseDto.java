package org.hanghae.markethub.domain.event.dto;

import lombok.Builder;
import lombok.Getter;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;

@Getter
@Builder
public class EventItemResponseDto {
	private ItemsResponseDto items;
	private int oldPrice;
}