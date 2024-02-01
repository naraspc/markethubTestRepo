package org.hanghae.markethub.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemCreateResponseDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateResponseDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository itemRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;
	public ItemCreateResponseDto createItem(ItemCreateRequestDto requestDto) {
		Optional<User> byId = userRepository.findById(1L); // 유저정보 코드가 생기
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

		return ItemCreateResponseDto.builder()
				.itemName(save.getItemName())
				.price(save.getPrice())
				.quantity(save.getQuantity())
				.itemInfo(save.getItemInfo())
				.category(save.getCategory())
				.build();
	}

	@Transactional
	public ItemUpdateResponseDto updateItem(Long itemId, ItemCreateRequestDto requestDto) {
		Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException(""));

		return ItemUpdateResponseDto.builder()
				.itemName(item.getItemName())
				.price(item.getPrice())
				.quantity(item.getQuantity())
				.itemInfo(item.getItemInfo())
				.category(item.getCategory())
				.build();
	}
}
