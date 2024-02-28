package org.hanghae.markethub.domain.item.dto;

import lombok.Builder;
import lombok.Getter;
import org.hanghae.markethub.domain.item.entity.ElasticItem;
import org.hanghae.markethub.domain.item.entity.Item;

import java.util.List;

@Getter
@Builder
public class ItemsResponseDto {
	private Long id;
	private String itemName;
	private int price;
	private int quantity;
	private String itemInfo;
	private String category;
	private List<String> pictureUrls;

	public static ItemsResponseDto fromEntity(Item item, List<String> pictureUrls) {
		return ItemsResponseDto.builder()
				.id(item.getId())
				.itemName(item.getItemName())
				.price(item.getPrice())
				.quantity(item.getQuantity())
				.itemInfo(item.getItemInfo())
				.category(item.getCategory())
				.pictureUrls(pictureUrls)
				.build();
	}

}
