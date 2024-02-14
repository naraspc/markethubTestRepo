package org.hanghae.markethub.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.service.StoreService;
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
	private final AwsS3Service awsS3Service;
	private final StoreService storeService;

	public void createItem(ItemCreateRequestDto requestDto,
						   List<MultipartFile> files,
						   User user) throws IOException {
		Store findStore = storeService.findByUsergetStore(user.getId());

		Item item = Item.builder()
				.itemName(requestDto.getItemName())
				.itemInfo(requestDto.getItemInfo())
				.price(requestDto.getPrice())
				.quantity(requestDto.getQuantity())
				.category(requestDto.getCategory())
				.status(Status.EXIST)
				.user(user)
				.store(findStore)
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
	public void updateItem(Long itemId, ItemUpdateRequestDto requestDto, User user) {

		Item item = itemRepository.findById(itemId).orElseThrow(
				() -> new IllegalArgumentException("No such item"));

		if (item.getUser().getId() != user.getId()) {
			throw new IllegalArgumentException("본인 상품만 수정이 가능합니다.");
		}
		item.updateItem(requestDto);
	}

	@Transactional
	public void deleteItem(Long itemId, User user) {

		Item item = itemRepository.findById(itemId).orElseThrow(
				() -> new IllegalArgumentException("No such item"));
		if (item.getUser().getId() != user.getId()) {
			throw new IllegalArgumentException("본인 상품만 수정이 가능합니다.");
		}
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

	@Transactional
	public void decreaseQuantity(Long itemId, int quantity) {
		Item item = itemRepository.findById(itemId).orElseThrow();
		if(item.getQuantity() > 0) {
			item.decreaseItemQuantity(quantity);
		}
	}
}
