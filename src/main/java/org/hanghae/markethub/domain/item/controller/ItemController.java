package org.hanghae.markethub.domain.item.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemCreateResponseDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateResponseDto;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {
	private final ItemService itemService;

	@PostMapping
	public ItemCreateResponseDto createItem(@RequestBody ItemCreateRequestDto itemCreateRequestDto) {
		return itemService.createItem(itemCreateRequestDto);
	}

	@PatchMapping("/{itemId}")
	private ItemUpdateResponseDto updateItem(@PathVariable Long itemId,
											 @RequestBody ItemCreateRequestDto itemCreateRequestDto) {
		return itemService.updateItem(itemId, itemCreateRequestDto);
	}
}
