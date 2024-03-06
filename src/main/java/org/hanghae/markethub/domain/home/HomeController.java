package org.hanghae.markethub.domain.home;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.event.service.EventService;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private final ItemService itemService;
	private final EventService eventService;
	@GetMapping
	public String getHome( @RequestParam(defaultValue = "0")  int page,
						   @RequestParam(defaultValue = "5")  int size,
						   Model model) {
		Page<ItemsResponseDto> itemsPage = itemService.getItems(page, size);
		model.addAttribute("itemPage", itemsPage);
		model.addAttribute("eventTime", eventService.getEventTime());
		return "index";
	}
}
