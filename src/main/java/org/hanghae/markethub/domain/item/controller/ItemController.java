package org.hanghae.markethub.domain.item.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.domain.purchase.dto.PaymentRequestDto;
import org.hanghae.markethub.domain.user.security.UserDetailsImpl;
//import org.hanghae.markethub.global.config.RedissonFairLock;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {
	private final ItemService itemService;
	private final ItemRepository itemRepository;
//	private final RedisTemplate redisTemplate;
//	private final RedissonClient redissonClient;
//	private final RedissonFairLock redissonFairLock;

	@GetMapping
	public String getAllItems(Model model) {
		model.addAttribute("items", itemService.getItems());
		return "items";
	}

	@GetMapping("/{itemId}")
	public String getItem(@PathVariable Long itemId, Model model,
						  @AuthenticationPrincipal UserDetailsImpl userDetails) {
		model.addAttribute("items", itemService.getItem(itemId));
		model.addAttribute("email", userDetails.getUser().getEmail());
		return "item";
	}

	@GetMapping("/posts/{postsId}")
	@ResponseBody
	public String getPosts(@PathVariable Long postsId) {
		return "perfTest postsId : " + postsId;
	}

	@GetMapping("/category")
	public String findByCategory(@RequestParam String category, Model model) {
		model.addAttribute("items", itemService.findByCategory(category));
		return "items";
	}

	@PostMapping
	@ResponseBody
	public void createItem(@RequestPart("itemData") ItemCreateRequestDto itemCreateRequestDto,
						   @RequestPart("files") List<MultipartFile> file,
						   @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
		itemService.createItem(itemCreateRequestDto, file, userDetails.getUser());
	}

	@PatchMapping("/{itemId}")
	@ResponseBody
	private void updateItem(@PathVariable Long itemId,
							@RequestPart("itemData") ItemUpdateRequestDto itemUpdateRequestDto,
							@AuthenticationPrincipal UserDetailsImpl userDetails) {
		itemService.updateItem(itemId, itemUpdateRequestDto, userDetails.getUser());
	}

	@DeleteMapping("/{itemId}")
	@ResponseBody
	private void deleteItem(@PathVariable Long itemId,
							@AuthenticationPrincipal UserDetailsImpl userDetails) {
		itemService.deleteItem(itemId, userDetails.getUser());
	}

//	@GetMapping("/de/{number}")
//	@ResponseBody
//	public void de(@PathVariable Long number) {
//		System.out.println("입출력 : " + number);
//		redissonFairLock.performWithFairLock("dementLock", () -> {
//			Item item = itemRepository.findById(1L).orElseThrow();
//			if(item.getQuantity() > 0) {
//				itemService.decreaseQuantity(1L, 1);
//				System.out.println("success nunber : " + number);
//			}else {
//				//System.out.println("fail number :" + number);
//			}
//		});
//	}
//	@GetMapping("/de/{number}")
//	@ResponseBody
//	public void de(@PathVariable Long number) {
//		System.out.println("입출력 : " + number);
//			Item item = itemRepository.findById(1L).orElseThrow();
//			if(item.getQuantity() > 0) {
//				itemService.decreaseQuantity(1L, 1);
//				System.out.println("success nunber : " + number);
//			}else {
//				//System.out.println("fail number :" + number);
//			}
//
//	}
//	@GetMapping("/de/{number}")
//	@ResponseBody
//	public void de(@PathVariable Long number) {
//		// Redis Streams에 요청 추가
//		String streamKey = "orderStream";
//		Map<String, String> messageBody = new HashMap<>();
//		messageBody.put("orderNumber", number.toString());
//		redisTemplate.opsForStream().add(streamKey, messageBody);
//
//		// 비동기적으로 요청 처리
//		CompletableFuture.runAsync(() -> processOrders());
//	}
//
//	public void processOrders() {
//		String streamKey = "orderStream";
//		RLock lock = redissonClient.getFairLock("dementLock");
//
//		try {
//			lock.lock();
//
//			// Redis Streams에서 요청을 하나씩 가져와 처리
//			List<MapRecord<String, String, String>> records = redisTemplate.opsForStream().read(StreamOffset.fromStart(streamKey));
//			for (MapRecord<String, String, String> record : records) {
//				String orderNumber = record.getValue().get("orderNumber");
//
//				// Item 로직 수행
//				Item item = itemRepository.findById(1L).orElseThrow();
//				if (item.getQuantity() > 0) {
//					itemService.decreaseQuantity(1L, 1);
//					System.out.println("Success for order number: " + orderNumber);
//				} else {
//					System.out.println("Failed for order number: " + orderNumber);
//				}
//
//				// 처리된 메시지는 스트림에서 제거
//				deleteMessageFromStream(streamKey, String.valueOf(record.getId()));
//			}
//
//			if (records.isEmpty()) {
//				// 큐에 더 이상 처리할 요청이 없을 때의 처리
//				System.out.println("No orders to process.");
//			}
//		} finally {
//			lock.unlock();
//		}
//	}
//
//	private void deleteMessageFromStream(String streamKey, String messageId) {
//		redisTemplate.execute((RedisCallback<Long>) connection -> {
//			return connection.streamCommands().xDel(streamKey.getBytes(), Arrays.toString(messageId.getBytes()));
//		});

//	@GetMapping("/de/{number}")
//	@ResponseBody
//	public void de(@PathVariable Long number) {
//			Item item = itemRepository.findById(1L).orElseThrow();
//			if(item.getQuantity() > 0) {
//				itemService.decreaseQuantity(1L, 1);
//				System.out.println("success nunber : " + number);
//			}
//	}
	}
