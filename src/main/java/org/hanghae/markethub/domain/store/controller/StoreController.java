package org.hanghae.markethub.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.store.service.StoreService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
	private final StoreService storeService;

	@PostMapping
	@ResponseBody
	public void createStore() {
		storeService.createStore();
	}

	@DeleteMapping
	@ResponseBody
	public void deleteStore() {
		storeService.deleteStore();
	}
}
