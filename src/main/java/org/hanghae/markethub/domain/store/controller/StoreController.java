package org.hanghae.markethub.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.store.service.StoreService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
