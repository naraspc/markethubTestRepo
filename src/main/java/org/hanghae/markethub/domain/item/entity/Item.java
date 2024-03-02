package org.hanghae.markethub.domain.item.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.dto.RedisItemResponseDto;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(indexes = {
		@Index(name = "idx_item_itemName", columnList = "itemName")
})
@DynamicUpdate
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String itemName;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	private String itemInfo;

	@Column(nullable = false)
	private String category;

	@Enumerated(value = EnumType.STRING)
	private Status status;

	@ManyToOne
	@JoinColumn(name ="store_id",nullable = false)
	private Store store;


	@ManyToOne
	@JoinColumn(name ="user_id",nullable = false)
	private User user;

	public Item updateItem(ItemUpdateRequestDto requestDto) {
		this.itemName = requestDto.getItemName();
		this.price = requestDto.getPrice();
		this.quantity = requestDto.getQuantity();
		this.itemInfo = requestDto.getItemInfo();
		this.category = requestDto.getCategory();
		return this;
	}

	public RedisItemResponseDto convertToDto(Item item, List<String> url) {
		return RedisItemResponseDto
				.builder()
				.id(item.getId())
				.itemName(item.getItemName())
				.price(item.getPrice())
				.itemInfo(item.getItemInfo())
				.quantity(item.getQuantity())
				.category(item.getCategory())
				.pictureUrls(url)
				.build();
	}

	public void decreaseItemQuantity(int quantity) {
		this.quantity -= quantity;
	}

	public void increaseItemQuantity(int quantity) {
		this.quantity += quantity;
	}

	public Item updateItemForEvent(int price, int quantity) {
		this.price = price;
		this.quantity = quantity;
		return this;
	}

	public void deleteItem() {
		this.status = Status.DELETED;
	}


}
