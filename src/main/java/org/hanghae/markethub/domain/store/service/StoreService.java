package org.hanghae.markethub.domain.store.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {
	private final StoreRepository storeRepository;
	private final ItemRepository itemRepository;
	private final AwsS3Service awsS3Service;


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
        return storeRepository.findByUserId(user.getId());
	}

	@Transactional
	public void deleteStore(User user) {

		Store store = storeRepository.findByUserId(user.getId()).orElseThrow(
				() -> new IllegalArgumentException("No such store"));
		store.deleteStore();

	}

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
