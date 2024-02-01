package org.hanghae.markethub.domain.item.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {
	private final ItemService itemService;

	@GetMapping
	@ResponseBody
	public List<ItemsResponseDto> getAllItems() {
		return itemService.getItems();
	}

	@GetMapping("/{itemId}")
	@ResponseBody
	public ItemsResponseDto getItem(@PathVariable Long itemId) {
		return itemService.getItem(itemId);
	}

	@PostMapping
	@ResponseBody
	public void createItem(@RequestPart("itemData") ItemCreateRequestDto itemCreateRequestDto,
						   @RequestPart("files") List<MultipartFile> file) throws IOException {
		 itemService.createItem(itemCreateRequestDto, file);
	}

	@PatchMapping("/{itemId}")
	@ResponseBody
	private void updateItem(@PathVariable Long itemId,
							@RequestPart("itemData") ItemUpdateRequestDto itemUpdateRequestDto) {
		itemService.updateItem(itemId, itemUpdateRequestDto);
	}

	@DeleteMapping("/{itemId}")
	@ResponseBody
	private void deleteItem(@PathVariable Long itemId) {
		itemService.deleteItem(itemId);
	}

	@GetMapping("/home")
	public String home() {
		return "item";
	}
}
