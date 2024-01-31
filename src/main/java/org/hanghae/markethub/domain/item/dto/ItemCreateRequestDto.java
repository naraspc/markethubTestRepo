package org.hanghae.markethub.domain.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCreateRequestDto {
	private String itemName;
	private int price;
	private int quantity;
	private String itemInfo;
	private String category;
}
