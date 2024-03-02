package org.hanghae.markethub.domain.event.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateEventDto {
	private Long itemId;
	private int quantity;
	private int price;
}
