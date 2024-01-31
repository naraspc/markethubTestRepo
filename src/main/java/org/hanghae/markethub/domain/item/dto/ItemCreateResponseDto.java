package org.hanghae.markethub.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ItemCreateResponseDto {
	private String itemName;
	private int price;
	private int quantity;
	private String itemInfo;
	private String category;
}
