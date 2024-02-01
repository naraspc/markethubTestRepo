package org.hanghae.markethub.domain.item.controller;

import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemControllerTest {

	@Autowired
	private ItemService itemService;

//	@Test
//	void createItem() {
//		ItemCreateRequestDto build = ItemCreateRequestDto.builder()
//				.itemName("김")
//				.itemInfo("김")
//				.price(5000)
//				.quantity(5)
//				.category("전자제품")
//				.build();
//
//		itemService.createItem(build);
//	}

	@Test
	void updateItem() {
		ItemCreateRequestDto build = ItemCreateRequestDto.builder()
				.itemName("김")
				.itemInfo("김")
				.price(5000)
				.quantity(5)
				.category("전자제품")
				.build();
		itemService.createItem(build);
	}
}