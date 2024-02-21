package org.hanghae.markethub.domain.item.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.dto.ValidQuantity;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.domain.user.security.UserDetailsImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public String getAllItems(Model model) throws JsonProcessingException {
		model.addAttribute("items", itemService.getItems());
		return "Allitems";
	}

	@GetMapping("/{itemId}")
	public String getItem(@PathVariable Long itemId, Model model) throws JsonProcessingException {
		model.addAttribute("items", itemService.getItem(itemId));
		return "item";
	}

//	@GetMapping("/category")
//	public String findByCategory(@RequestParam String category, Model model) {
//		model.addAttribute("items", itemService.findByCategory(category));
//		return "Allitems";
//	}

	@GetMapping("/itemName")
	@ResponseBody
	public List<ItemsResponseDto> findByCategory(@RequestParam String itemName, Model model) {
		model.addAttribute("items", itemService.findByCategory(itemName));
		return itemService.findByCategory(itemName);
	}

	@PostMapping
	@ResponseBody
	public void createItem(@RequestPart("itemData") ItemCreateRequestDto itemCreateRequestDto,
						   @RequestPart(value = "files", required = false) List<MultipartFile> file ,
						   @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
		itemService.createItem(itemCreateRequestDto, file, userDetails.getUser());
	}

	@PatchMapping("/{itemId}")
	@ResponseBody
	private void updateItem(@PathVariable Long itemId,
							@RequestPart("itemData") ItemUpdateRequestDto itemUpdateRequestDto,
							@AuthenticationPrincipal UserDetailsImpl userDetails) throws JsonProcessingException {
		itemService.updateItem(itemId, itemUpdateRequestDto, userDetails.getUser());
	}

	@DeleteMapping("/{itemId}")
	@ResponseBody
	private void deleteItem(@PathVariable Long itemId,
							@AuthenticationPrincipal UserDetailsImpl userDetails) {
		itemService.deleteItem(itemId, userDetails.getUser());
	}

	@PostMapping("/validQuantity")
	@ResponseBody
	public boolean validQuantity(@RequestBody ValidQuantity validQuantity) throws JsonProcessingException {
		System.out.println();
		return itemService.decreaseItemForRedis(validQuantity.getItemId(), validQuantity.getQuantity());
	}
}
