package org.hanghae.markethub.domain.store.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;
	private final AwsS3Service awsS3Service;

	public void createStore(Long userId) {
		User user = userRepository.findById(5L).orElseThrow(
				() ->new IllegalArgumentException("No such store")); // 인증 구현 후 제거 예정
		User user1 = userRepository.findById(userId).orElseThrow();
		Store store = Store.builder()
				.user(user1)
				.status(Status.EXIST)
				.build();
		storeRepository.save(store);
	}

	@Transactional
	public void deleteStore(Long userId) {
		Long storeId= 7L; // 인증 구현 후 제거 예정
		Store store = storeRepository.findById(storeId).orElseThrow(
				() -> new IllegalArgumentException("No such store"));
		store.deleteStore();
	}

	public List<ItemsResponseDto> getStoreItems() {
		Long userId = 1L;
		return itemRepository.findByUserId(userId).stream()
				.map(item -> {
					List<String> pictureUrls = awsS3Service.getObjectUrlsForItem(item.getId());
					return ItemsResponseDto.fromEntity(item, pictureUrls);
				})
				.collect(Collectors.toList());
	}

	public ItemsResponseDto getStoreItem(Long itemId) {
		Item item = itemRepository.findById(itemId).orElseThrow(
				() -> new IllegalArgumentException("No such item"));
		return ItemsResponseDto.fromEntity(item, awsS3Service.getObjectUrlsForItem(item.getId()));
	}

	public List<ItemsResponseDto> findByCategory(String category) {
		return itemRepository.findByCategory(category).stream()
				.map(item -> {
					List<String> pictureUrls = awsS3Service.getObjectUrlsForItem(item.getId());
					return ItemsResponseDto.fromEntity(item, pictureUrls);
				})
				.collect(Collectors.toList());
	}
}
