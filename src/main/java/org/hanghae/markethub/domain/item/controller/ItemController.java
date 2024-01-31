package org.hanghae.markethub.domain.item.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemCreateResponseDto;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {
	private final ItemService itemService;
	@PostMapping
	public ItemCreateResponseDto createItem(@RequestBody ItemCreateRequestDto itemCreateRequestDto) {
		return itemService.createItem(itemCreateRequestDto);
	}
}
