package org.hanghae.markethub.domain.item.service;

import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest2 {
	@Mock
	ItemRepository itemRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	StoreRepository storeRepository;

	@InjectMocks
	ItemService itemService;

	@Mock
	AwsS3Service awsS3Service;

	@Test
	@DisplayName("아이템 등록")
	void createItem() throws IOException {
		// Given
		MockMultipartFile file1 = new MockMultipartFile("files", "filename1.txt", "text/plain", "file1 data".getBytes());

		User user = User.builder()
				.id(1L)
				.name("LEE")
				.password("1234")
				.address("서울시")
				.email("sd@naver.com")
				.phone("010")
				.status(Status.EXIST)
				.role(Role.ADMIN)
				.build();
		Store store = Store.builder()
				.id(1L)
				.user(user)
				.status(Status.EXIST)
				.build();

		ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
				.itemName("컴퓨터")
				.itemInfo("컴퓨터입니다.")
				.quantity(5)
				.price(5000)
				.category("잡화")
				.build();

		// 모의 객체를 사용하여 Repository의 동작을 설정
		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(storeRepository.findById(1L)).willReturn(Optional.of(store));
		given(itemRepository.save(any(Item.class))).willAnswer(invocation -> invocation.getArgument(0));

		// When
//		itemService.createItem(requestDto, List.of(file1));

		// Then
		// save 메서드가 올바른 item 객체로 호출되었는지 확인
		verify(itemRepository).save(any(Item.class));

	}

	@Test
	@DisplayName("아이템 전체 조회")
	void getAllItems() {

		User user = User.builder()
				.id(1L)
				.name("LEE")
				.password("1234")
				.address("서울시")
				.email("sd@naver.com")
				.phone("010")
				.status(Status.EXIST)
				.role(Role.ADMIN)
				.build();
		Store store = Store.builder()
				.id(1L)
				.user(user)
				.status(Status.EXIST)
				.build();

		Item item1 = Item.builder()
				.id(1L)
				.itemName("컴퓨터")
				.itemInfo("컴퓨터 입니다")
				.price(5000)
				.status(Status.EXIST)
				.category("전자제품")
				.user(user)
				.store(store)
				.build();

		List<Item> items = new ArrayList<>();
		items.add(item1);

		given(itemRepository.findAll()).willReturn(items);

		// when
		List<ItemsResponseDto> result = itemService.getItems();
		// then
		assertThat(result.get(0).getItemName()).isEqualTo("컴퓨터");
		assertThat(result.get(0).getItemInfo()).isEqualTo("컴퓨터 입니다");
		assertThat(result.get(0).getPrice()).isEqualTo(5000);
		assertThat(result.get(0).getCategory()).isEqualTo("전자제품");
		assertThat(result.size()).isEqualTo(1);

	}

	@Test
	@DisplayName("아이템 단건 조회")
	void getAllItem() {

		Item item = Item.builder()
				.id(1L)
				.itemName("컴퓨터")
				.itemInfo("컴퓨터 입니다")
				.price(5000)
				.status(Status.EXIST)
				.category("전자제품")
				.build();

		given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));

		// when
		ItemsResponseDto result = itemService.getItem(item.getId());

		// then
		assertThat(result.getItemName()).isEqualTo("컴퓨터");
		assertThat(result.getItemInfo()).isEqualTo("컴퓨터 입니다");
		assertThat(result.getPrice()).isEqualTo(5000);
		assertThat(result.getCategory()).isEqualTo("전자제품");
	}

	@Test
	@DisplayName("아이템 수정")
	void updateItem() {
		// Given
		Long itemId = 1L;
		ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder()
				.itemName("새로운 아이템")
				.price(10000)
				.quantity(10)
				.itemInfo("새로운 아이템입니다.")
				.category("새로운 카테고리")
				.build();

		Item existingItem = Item.builder()
				.id(itemId)
				.itemName("기존 아이템")
				.price(5000)
				.quantity(5)
				.itemInfo("기존 아이템입니다.")
				.category("기존 카테고리")
				.build();

		given(itemRepository.findById(itemId)).willReturn(Optional.of(existingItem));

		// When
//		itemService.updateItem(itemId, requestDto);

		// Then
		assertThat(existingItem.getItemName()).isEqualTo("새로운 아이템");
		assertThat(existingItem.getPrice()).isEqualTo(10000);
		assertThat(existingItem.getQuantity()).isEqualTo(10);
		assertThat(existingItem.getItemInfo()).isEqualTo("새로운 아이템입니다.");
		assertThat(existingItem.getCategory()).isEqualTo("새로운 카테고리");
	}

	@Test
	@DisplayName("아이템 삭제")
	void deleteItem() {
		Long itemId = 1L;
		Item existingItem = Item.builder()
				.id(itemId)
				.itemName("기존 아이템")
				.price(5000)
				.quantity(5)
				.itemInfo("기존 아이템입니다.")
				.category("기존 카테고리")
				.build();

		given(itemRepository.findById(itemId)).willReturn(Optional.of(existingItem));

//		itemService.deleteItem(itemId);
		assertThat(existingItem.getStatus()).isEqualTo(Status.DELETED);
	}
}
