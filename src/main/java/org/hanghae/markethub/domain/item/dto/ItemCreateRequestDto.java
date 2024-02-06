package org.hanghae.markethub.domain.item.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemCreateRequestDto {
	private String itemName;
	private int price;
	private int quantity;
	private String itemInfo;
	private String category;
}
