//package org.hanghae.markethub.domain.item.service;
//
//import lombok.RequiredArgsConstructor;
//import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
//import org.hanghae.markethub.domain.item.dto.ItemCreateResponseDto;
//import org.hanghae.markethub.domain.item.entity.Item;
//import org.hanghae.markethub.domain.item.repository.ItemRepository;
//import org.hanghae.markethub.global.constant.Status;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ItemService {
//	private final ItemRepository itemRepository;
//
//	public ItemCreateResponseDto createItem(ItemCreateRequestDto requestDto) {
//
////		Item item = Item.builder()
////				.ItemName(requestDto.getItemName())
////				.itemInfo(requestDto.getItemInfo())
////				.price(requestDto.getPrice())
////				.quantity(requestDto.getQuantity())
////				.category(requestDto.getCategory())
////				.status(Status.EXIST)
////				.build();
//
//		Item save = itemRepository.save(item);
//		return ItemCreateResponseDto.builder()
//				.itemName(save.getItemName())
//				.price(save.getPrice())
//				.quantity(save.getQuantity())
//				.itemInfo(save.getItemInfo())
//				.category(save.getCategory())
//				.build();
//	}
//}
