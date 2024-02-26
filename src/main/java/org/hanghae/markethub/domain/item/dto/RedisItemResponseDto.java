package org.hanghae.markethub.domain.item.dto;

import lombok.Builder;
import lombok.Getter;


import java.util.List;

@Getter
@Builder
public class RedisItemResponseDto {
	private Long id;
	private String itemName;
	private int quantity;
	private int price;
	private String itemInfo;
	private String category;
	private List<String> pictureUrls;

}
