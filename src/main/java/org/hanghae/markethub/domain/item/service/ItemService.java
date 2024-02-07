package org.hanghae.markethub.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository itemRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;
	private final AwsS3Service awsS3Service;

	public void createItem(ItemCreateRequestDto requestDto,
						   List<MultipartFile> files) throws IOException {
		Optional<User> byId = userRepository.findById(1L); // 유저정보 코드가 생기면 삭제
		Optional<Store> byId1 = storeRepository.findById(1L);

		Item item = Item.builder()
				.itemName(requestDto.getItemName())
				.itemInfo(requestDto.getItemInfo())
				.price(requestDto.getPrice())
				.quantity(requestDto.getQuantity())
				.category(requestDto.getCategory())
				.status(Status.EXIST)
				.user(byId.get())
				.store(byId1.get())
				.build();

		Item save = itemRepository.save(item);
		awsS3Service.uploadFiles(files, save.getId());
	}

	public List<ItemsResponseDto> getItems() {
		return itemRepository.findAll().stream()
				.map(item -> {
					List<String> pictureUrls = awsS3Service.getObjectUrlsForItem(item.getId());
					return ItemsResponseDto.fromEntity(item, pictureUrls);
				})
				.collect(Collectors.toList());
	}

	public ItemsResponseDto getItem(Long itemId) {
		Item item = itemRepository.findById(itemId).orElseThrow(
				() -> new IllegalArgumentException("No such item"));
		return ItemsResponseDto.fromEntity(item, awsS3Service.getObjectUrlsForItem(item.getId()));
	}

	@Transactional
	public void updateItem(Long itemId, ItemUpdateRequestDto requestDto) {
		Item item = itemRepository.findById(itemId).orElseThrow(
				() -> new IllegalArgumentException("No such item"));
		item.updateItem(requestDto);
	}

	@Transactional
	public void deleteItem(Long itemId) {
		Item item = itemRepository.findById(itemId).orElseThrow(
				() -> new IllegalArgumentException("No such item"));
		item.deleteItem();
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
