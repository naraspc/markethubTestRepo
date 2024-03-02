package org.hanghae.markethub.domain.home;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.event.service.EventService;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
		int eventTime = eventService.getEventTime();
		model.addAttribute("eventTime", eventService.getEventTime());
		return "index";
	}
}
