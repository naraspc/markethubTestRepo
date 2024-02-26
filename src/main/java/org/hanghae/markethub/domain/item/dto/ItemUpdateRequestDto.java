package org.hanghae.markethub.domain.item.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemUpdateRequestDto {
	private String itemName;
	private int price;
	private int quantity;
	private String itemInfo;
	private String category;
}
