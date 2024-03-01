package org.hanghae.markethub.domain.event.dto;

import lombok.Builder;
import lombok.Getter;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;

@Builder
@Getter
public class EventDto {
	private ItemsResponseDto itemsResponseDto;
	private int oldPrice;
}
