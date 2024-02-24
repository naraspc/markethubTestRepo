package org.hanghae.markethub.domain.home;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
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

	@GetMapping
	public String getHome( @RequestParam(defaultValue = "0")  int page,
						   @RequestParam(defaultValue = "5")  int size,
						   Model model) throws JsonProcessingException {
		Page<ItemsResponseDto> itemsPage = itemService.getItems(page, size);
		model.addAttribute("itemPage", itemsPage);
		return "index";
	}

//	@GetMapping("/page")
//	@ResponseBody
//	public List<ItemsResponseDto> Page(@RequestParam(defaultValue = "0")  int page,
//									   @RequestParam(defaultValue = "5")  int size) {
//		return itemService.getItems(page, size);
//	}

}
