package org.hanghae.markethub.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.store.service.StoreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
	private final StoreService storeService;

	@PostMapping("/{userId}")
	@ResponseBody
	public void createStore(@PathVariable Long userId) {
		storeService.createStore(userId);
	}

	@DeleteMapping("/{userId}")
	@ResponseBody
	public void deleteStore(@PathVariable Long userId) {
		storeService.deleteStore(userId);
	}

	@GetMapping
	public String getStoreItems(Model model) {
		model.addAttribute("storeItems", storeService.getStoreItems());
		return "storeItems";
	}

	@GetMapping("/{itemId}")
	public String getStoreItem(@PathVariable Long itemId, Model model) {
		model.addAttribute("storeItems", storeService.getStoreItem(itemId));
		return "storeItem";
	}

	@GetMapping("/category")
	public String findByCategory(@RequestParam String category, Model model){
		model.addAttribute("storeItems", storeService.findByCategory(category));
		return "storeItems";
	}

	@GetMapping("/main")
	public String getStorePage(Model model) {
		Long storeId = 1L;
		model.addAttribute("storeId", storeId);
		return "storeMain";
	}
}
