package org.hanghae.markethub.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.service.StoreService;
import org.hanghae.markethub.global.security.impl.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
	private final StoreService storeService;

	@PostMapping
	@ResponseBody
	public void createStore(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		storeService.createStore(userDetails.getUser());
	}

	@DeleteMapping
	@ResponseBody
	public void deleteStore(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		storeService.deleteStore(userDetails.getUser());
	}

	@GetMapping
	public String getStoreItems(@AuthenticationPrincipal UserDetailsImpl userDetails,
								Model model) {
		model.addAttribute("storeItems", storeService.getStoreItems(userDetails.getUser()));
		return "storeItems";
	}

	@GetMapping("/{itemId}")
	public String getStoreItem(@PathVariable Long itemId, Model model,
							   @AuthenticationPrincipal UserDetailsImpl userDetails) {
		model.addAttribute("storeItems", storeService.getStoreItem(itemId, userDetails.getUser()));
		return "storeItem";
	}

	@GetMapping("/category")
	public String findByCategory(@RequestParam String category, Model model,
								 @AuthenticationPrincipal UserDetailsImpl userDetails){
		model.addAttribute("storeItems", storeService.findByCategory(category, userDetails.getUser()));
		return "storeItems";
	}

	@GetMapping("/main")
	public String getStorePage(@AuthenticationPrincipal UserDetailsImpl userDetails,
							   Model model) {
		Optional<Store> storePage = storeService.getStorePage(userDetails.getUser());
		if (!storeService.getStorePage(userDetails.getUser()).isPresent()) {
			return "storeMain";
		}
		model.addAttribute("storeId",storePage.get().getId());
		return "storeMain";
	}
}
