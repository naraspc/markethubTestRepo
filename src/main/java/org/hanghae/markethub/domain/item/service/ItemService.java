package org.hanghae.markethub.domain.item.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.dto.RedisItemResponseDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.service.StoreService;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository itemRepository;
	private final AwsS3Service awsS3Service;
	private final StoreService storeService;
	private final RedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	public Item getItemValid(Long itemId){
		Item item = itemRepository.findById(itemId).orElse(null);
		System.out.println(item.getItemName());
		return item;
	}

	public void createItem(ItemCreateRequestDto requestDto,
						   List<MultipartFile> files,
						   User user) {
		Store findStore = storeService.findByUsergetStore(user.getId());

		if(requestDto.getQuantity() < 0 || requestDto.getPrice() <0) {
			throw new IllegalArgumentException("가격 또는 재고는 0 이하일 수 없습니다.");
		}

		Item item = Item.builder()
				.itemName(requestDto.getItemName())
				.itemInfo(requestDto.getItemInfo())
				.price(requestDto.getPrice())
				.quantity(requestDto.getQuantity())
				.category(requestDto.getCategory())
				.status(Status.EXIST)
//				.user(user)
				.store(findStore)
				.build();

		Item save = itemRepository.save(item);
//		if (files != null) {
//			awsS3Service.uploadFiles(files, save.getId());
//		}
		createItemForRedis(save, files);

	}

	public void createItemForRedis(Item item, List<MultipartFile> file) {
		String key = "item";
		try {
			awsS3Service.uploadFiles(file, item.getId());
			List<String> objectUrlsForItem = awsS3Service.getObjectUrlsForItemTest(item);
			RedisItemResponseDto dto = item.convertToDto(item, objectUrlsForItem);
			String json = objectMapper.writeValueAsString(dto);
			String itemKey = "item:" + item.getId();
			double score = item.getId();
			redisTemplate.opsForZSet().add(key, itemKey, score);
			redisTemplate.opsForValue().set(itemKey, json);

		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

//	public List<ItemsResponseDto> getItems() {
//		return itemRepository.findAll().stream()
//				.map(item -> {
//					List<String> pictureUrls = awsS3Service.getObjectUrlsForItem(item.getId());
//					return ItemsResponseDto.fromEntity(item, pictureUrls);
//				})
//				.collect(Collectors.toList());
//	}

	public List<ItemsResponseDto> getItems() throws JsonProcessingException {
		String key = "item";
		Set<String> itemKeys = redisTemplate.opsForZSet().range(key, 0, 5);

		List<ItemsResponseDto> itemsResponseDtos = new ArrayList<>();
		for (String itemKey : itemKeys) {
			String json = (String) redisTemplate.opsForValue().get(itemKey);
			RedisItemResponseDto redisItemResponseDto = objectMapper.readValue(json, RedisItemResponseDto.class);
			ItemsResponseDto itemsResponseDto = ItemsResponseDto.builder()
					.id(redisItemResponseDto.getId())
					.itemName(redisItemResponseDto.getItemName()) // Set your item name here
					.price(redisItemResponseDto.getPrice()) // Set your item price here
					.quantity(redisItemResponseDto.getQuantity())
					.itemInfo(redisItemResponseDto.getItemInfo())
					.category(redisItemResponseDto.getCategory())
					.pictureUrls(redisItemResponseDto.getPictureUrls())
					.build();
			itemsResponseDtos.add(itemsResponseDto);
		}
		return itemsResponseDtos;
	}

//	public ItemsResponseDto getItem(Long itemId) throws JsonProcessingException {
//		String key = "item";
//		String findKey= key+ ":" + itemId;
//		String getKey = (String) redisTemplate.opsForValue().get(findKey);
//		if (getKey == null) {
//			Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("No such Item"));
//			List<String> objectUrlsForItem = awsS3Service.getObjectUrlsForItemTest(item);
//			RedisItemResponseDto dto = item.convertToDto(item, objectUrlsForItem);
//			String json = objectMapper.writeValueAsString(dto);
//			String itemKey = "item:" + item.getId();
//			double score = item.getId();
//			redisTemplate.opsForZSet().add(key, itemKey, score);
//			redisTemplate.opsForValue().set(itemKey, json);
//			return ItemsResponseDto.fromEntity(item, awsS3Service.getObjectUrlsForItemTest(item));
//		}
//		RedisItemResponseDto redisItemResponseDto = objectMapper.readValue(getKey, RedisItemResponseDto.class);
//		return ItemsResponseDto.builder()
//				.id(redisItemResponseDto.getId())
//				.itemName(redisItemResponseDto.getItemName())
//				.price(redisItemResponseDto.getPrice())
//				.quantity(redisItemResponseDto.getQuantity())
//				.itemInfo(redisItemResponseDto.getItemInfo())
//				.category(redisItemResponseDto.getCategory())
//				.pictureUrls(redisItemResponseDto.getPictureUrls())
//				.build();
//
//	}

//		public ItemsResponseDto getItem(Long itemId) throws JsonProcessingException {
//		String key = "item:" + itemId;
//
//		String json = (String) redisTemplate.opsForValue().get(key);
//		RedisItemResponseDto redisItemResponseDto = objectMapper.readValue(json, RedisItemResponseDto.class);
//		return ItemsResponseDto.builder()
//				.id(redisItemResponseDto.getId())
//				.itemName(redisItemResponseDto.getItemName())
//				.price(redisItemResponseDto.getPrice())
//				.quantity(redisItemResponseDto.getQuantity())
//				.itemInfo(redisItemResponseDto.getItemInfo())
//				.category(redisItemResponseDto.getCategory())
//				.pictureUrls(redisItemResponseDto.getPictureUrls())
//				.build();
//
//	}
		public ItemsResponseDto getItem(Long itemId) {
		Item item = itemRepository.findById(itemId).orElseThrow(
				() -> new IllegalArgumentException("No such item"));
		return ItemsResponseDto.fromEntity(item, awsS3Service.getObjectUrlsForItem(item.getId()));
	}

	@Transactional
	public void updateItem(Long itemId, ItemUpdateRequestDto requestDto, User user) throws JsonProcessingException {

		Item item = itemRepository.findById(itemId).orElseThrow(
				() -> new IllegalArgumentException("No such item"));
//
//		if (item.getUser().getId() != user.getId()) {
//			throw new IllegalArgumentException("본인 상품만 수정이 가능합니다.");
//		}
		item.updateItem(requestDto);
		updateItemForRedis(item);

	}

	public void updateItemForRedis(Item item) throws JsonProcessingException {
		String itemKey= "item:" + item.getId();
		List<String> pictureUrls = awsS3Service.getObjectUrlsForItem(item.getId());
		RedisItemResponseDto dto = item.convertToDto(item, pictureUrls);
		String json = objectMapper.writeValueAsString(dto);
		redisTemplate.opsForValue().set(itemKey, json);

	}

	@Transactional
	public void deleteItem(Long itemId, User user) {

		Item item = itemRepository.findById(itemId).orElseThrow(
				() -> new IllegalArgumentException("No such item"));
//		if (item.getUser().getId() != user.getId()) {
//			throw new IllegalArgumentException("본인 상품만 수정이 가능합니다.");
//		}
		deleteItemForRedis(itemId);
		item.deleteItem();
	}

	public void deleteItemForRedis(Long itemId) {
		String key = "item:" + itemId;
		redisTemplate.delete(key);
	}

//	public List<ItemsResponseDto> findByCategory(String itemName) {
//		return itemRepository.findByItemNameContaining(itemName).stream()
//				.map(item -> {
//					List<String> pictureUrls = awsS3Service.getObjectUrlsForItemTest(item);
//					return ItemsResponseDto.fromEntity(item, pictureUrls);
//				})
//				.collect(Collectors.toList());
//	}

	public Page<ItemsResponseDto> findByCategory(String itemName, int page, int size) {
		System.out.println();
		Pageable pageable = PageRequest.of(page, size);
		return itemRepository.findByItemNameContaining(itemName, pageable)
				.map(item -> {
					List<String> pictureUrls = awsS3Service.getObjectUrlsForItemTest(item);
					return ItemsResponseDto.fromEntity(item, pictureUrls);
				});
	}

//	public List<ItemsResponseDto> findByCategory(String itemName) throws JsonProcessingException {
//		String key = "item";
//		Set<String> itemKeys = redisTemplate.opsForZSet().range(key, 0, -1);
//
//		List<ItemsResponseDto> itemsResponseDtos = new ArrayList<>();
//		for (String itemKey : itemKeys) {
//			String json = (String) redisTemplate.opsForValue().get(itemKey);
//			RedisItemResponseDto redisItemResponseDto = objectMapper.readValue(json, RedisItemResponseDto.class);
//			if (redisItemResponseDto.getItemName().contains(itemName)) {
//				ItemsResponseDto itemsResponseDto = ItemsResponseDto.builder()
//						.id(redisItemResponseDto.getId())
//						.itemName(redisItemResponseDto.getItemName()) // Set your item name here
//						.price(redisItemResponseDto.getPrice()) // Set your item price here
//						.quantity(redisItemResponseDto.getQuantity())
//						.itemInfo(redisItemResponseDto.getItemInfo())
//						.category(redisItemResponseDto.getCategory())
//						.pictureUrls(redisItemResponseDto.getPictureUrls())
//						.build();
//				itemsResponseDtos.add(itemsResponseDto);
//			}
//		}
//		return itemsResponseDtos;
//	}


	@Transactional
	public void decreaseQuantity(Long itemId, int quantity) {
		Item item = itemRepository.findById(itemId).orElseThrow();
		if(quantity > item.getQuantity()) {
			throw new IllegalArgumentException("상품의 재고가 부족합니다.");
		}
			item.decreaseItemQuantity(quantity);
	}

	public boolean isSoldOut(Long itemId) {
		Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("No such item"));
		if (item.getQuantity() == 0) {
			return true;
		}
		return false;
	}

	@Scheduled(cron = "00 47 20 * * ?")
	public void createRedisItem () {
		String key = "item";
		List<Item> items = itemRepository.findAllWithPictures();
		for (Item item : items) {
			try {
				List<String> pictureUrls = awsS3Service.getObjectUrlsForItemTest(item);
				RedisItemResponseDto dto = item.convertToDto(item, pictureUrls);
				String json = objectMapper.writeValueAsString(dto);
				String itemKey = "item:" + item.getId();
				double score = item.getId();
				redisTemplate.opsForZSet().add(key, itemKey, score);
				redisTemplate.opsForValue().set(itemKey, json);

			} catch (JsonProcessingException e) {
				System.out.println(e.getMessage());
			}
		}
	}


	public boolean decreaseItemForRedis(Long itemId, int quantity) throws JsonProcessingException {
		String key = "item:" + itemId;
		String json = (String)redisTemplate.opsForValue().get(key);
		RedisItemResponseDto redisItemResponseDto = objectMapper.readValue(json, RedisItemResponseDto.class);
		if(redisItemResponseDto.getQuantity() >= quantity) {
			return true;
		}
		throw new IllegalArgumentException("재고가 부족합니다.");
	}
}
