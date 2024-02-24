package org.hanghae.markethub.domain.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.dto.RedisItemResponseDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {
	private final StoreRepository storeRepository;
	private final ItemRepository itemRepository;
	private final AwsS3Service awsS3Service;
	private final RedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	public void createStore(User user) {
		Optional<Store> findStore = storeRepository.findByUserId(user.getId());
		if (findStore.isPresent()) {
			throw new IllegalArgumentException("이미 가입된 계정입니다.");
		}

		Store store = Store.builder()
				.user(user)
				.status(Status.EXIST)
				.build();
		storeRepository.save(store);
	}
	public Optional<Store> getStorePage(User user) {
		Optional<Store> byUserId = storeRepository.findByUserId(user.getId());
		return byUserId;
	}

	@Transactional
	public void deleteStore(User user) {
		Store store = storeRepository.findByUserId(user.getId()).orElseThrow(
				() -> new IllegalArgumentException("No such store"));
		store.deleteStore();
	}

//	public List<ItemsResponseDto> getStoreItems(User user) {
//		String key = "item";
//		Set<String> itemKeys = redisTemplate.opsForZSet().range(key, 0, -1);
//
//		List<ItemsResponseDto> itemsResponseDtos = new ArrayList<>();
//		try {
//			for (String itemKey : itemKeys) {
//				String json = (String) redisTemplate.opsForValue().get(itemKey);
//				RedisItemResponseDto redisItemResponseDto = objectMapper.readValue(json, RedisItemResponseDto.class);
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
//		}catch (Exception e) {
//			e.getMessage();
//		}
//		return itemsResponseDtos;
//	}
//
//	public ItemsResponseDto getStoreItem(Long itemId, User user) {
//		String key = "item:" + itemId;
//		Item item = itemRepository.findById(itemId).orElseThrow(
//				() -> new IllegalArgumentException("No such item"));
//		if(item.getUser().getId() != user.getId()) {
//			throw new IllegalArgumentException("본인 상품은 조회 가능합니다.");
//		}
//		String json = (String) redisTemplate.opsForValue().get(key);
//		try {
//			RedisItemResponseDto redisItemResponseDto = objectMapper.readValue(json, RedisItemResponseDto.class);
//			return ItemsResponseDto.builder()
//					.id(redisItemResponseDto.getId())
//					.itemName(redisItemResponseDto.getItemName())
//					.price(redisItemResponseDto.getPrice())
//					.quantity(redisItemResponseDto.getQuantity())
//					.itemInfo(redisItemResponseDto.getItemInfo())
//					.category(redisItemResponseDto.getCategory())
//					.pictureUrls(redisItemResponseDto.getPictureUrls())
//					.build();
//		}catch (Exception e) {
//			throw new IllegalArgumentException("");
//		}
//	}

	public List<ItemsResponseDto> getStoreItems(User user) {
		return itemRepository.findByUserId(user.getId()).stream()
				.map(item -> {
					List<String> pictureUrls = awsS3Service.getObjectUrlsForItem(item.getId());
					return ItemsResponseDto.fromEntity(item, pictureUrls);
				})
				.collect(Collectors.toList());
	}

	public ItemsResponseDto getStoreItem(Long itemId, User user) {
		Item item = itemRepository.findById(itemId).orElseThrow(
				() -> new IllegalArgumentException("No such item"));
		if (item.getUser().getId() != user.getId()) {
			throw new IllegalArgumentException("본인 상품은 조회 가능합니다.");
		}
		return ItemsResponseDto.fromEntity(item, awsS3Service.getObjectUrlsForItem(item.getId()));
	}

	public List<ItemsResponseDto> findByCategory(String category, User user) {
		return itemRepository.findByCategoryAndStoreId(category, user.getId()).stream()
				.map(item -> {
					List<String> pictureUrls = awsS3Service.getObjectUrlsForItem(item.getId());
					return ItemsResponseDto.fromEntity(item, pictureUrls);
				})
				.collect(Collectors.toList());
	}

	public Store findByUsergetStore(Long userId) {
		return storeRepository.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스토어 입니다"));
	}
}
