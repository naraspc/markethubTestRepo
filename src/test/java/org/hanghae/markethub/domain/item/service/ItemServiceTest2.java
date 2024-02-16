package org.hanghae.markethub.domain.item.service;

import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.picture.repository.PictureRepository;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.store.service.StoreService;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest2 {
	@Mock
	private ItemRepository itemRepository;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private AwsS3Service awsS3Service;

	@Mock
	private UserRepository userRepository;

	@Mock
	private StoreService storeService;

	@InjectMocks
	private ItemService itemService;

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
	@DisplayName("아이템 전체 조회 empty")
	void getAllItemsEmpty() {
		// given
		given(itemRepository.findAll()).willReturn(Collections.emptyList()); // 아이템이 없는 상황 가정

		// when
		List<ItemsResponseDto> result = itemService.getItems();

		// then
		assertTrue(result.isEmpty());

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
	@DisplayName("아이템 단건 조회 empty")
	void getItem() {
		//given
		Long itemId = 1L;
		given(itemRepository.findById(itemId)).willReturn(Optional.empty());

		// when
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> itemService.getItem(itemId));

		// then
		assertEquals("No such item", exception.getMessage());

	}

	@Test
	@DisplayName("아이템 등록 성공")
	void createItemSuccess() throws IOException {
		MockMultipartFile file1 = new MockMultipartFile("files", "filename1.txt", "text/plain", "file1 data".getBytes());
		List<MultipartFile> files = new ArrayList<>();
		files.add(file1);
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

		given(storeService.findByUsergetStore(user.getId())).willReturn(store);
//		when(storeService.findByUsergetStore(user.getId())).thenReturn(store);
		when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
			Item item = invocation.getArgument(0);
			item.setId(1L); // 아이템이 저장되면 ID가 할당된 것으로 가정하고 설정
			return item;
		});
		itemService.createItem(requestDto, files, user);

	}

	@Test
	@DisplayName("아이템 등록 실패 - 상품 재고, 가격 음수")
	void createItemFail() throws IOException {
		MockMultipartFile file1 = new MockMultipartFile("files", "filename1.txt", "text/plain", "file1 data".getBytes());
		List<MultipartFile> files = new ArrayList<>();
		files.add(file1);
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
				.quantity(-1)
				.price(-1)
				.category("잡화")
				.build();

		given(storeService.findByUsergetStore(user.getId())).willReturn(store);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> itemService.createItem(requestDto, files, user));
		assertEquals("가격 또는 재고는 0 이하일 수 없습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("아이템 수정 성공")
	void updateItem() {
		// Given
		Long itemId = 1L;

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
				.user(user)
				.store(store)
				.itemInfo("기존 아이템입니다.")
				.category("기존 카테고리")
				.build();

		given(itemRepository.findById(itemId)).willReturn(Optional.of(existingItem));
//		given(userRepository.save(user)).willReturn(user);
//		User save = userRepository.save(user);
		// When
		itemService.updateItem(itemId, requestDto, user);

		// Then
		assertThat(existingItem.getItemName()).isEqualTo("새로운 아이템");
		assertThat(existingItem.getPrice()).isEqualTo(10000);
		assertThat(existingItem.getQuantity()).isEqualTo(10);
		assertThat(existingItem.getItemInfo()).isEqualTo("새로운 아이템입니다.");
		assertThat(existingItem.getCategory()).isEqualTo("새로운 카테고리");
	}

	@Test
	@DisplayName("아이템 수정 실패 - 다른 판매자의 상품을 수정 하려는 경우")
	void updateItemFail() {
		// Given
		Long itemId = 1L;

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

		User user2 = User.builder()
				.id(2L)
				.build();

		Store store = Store.builder()
				.id(1L)
				.user(user)
				.status(Status.EXIST)
				.build();

		ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder()
				.itemName("새로운 아이템")
				.price(5000)
				.quantity(5)
				.itemInfo("새로운 아이템입니다.")
				.category("새로운 카테고리")
				.build();

		Item existingItem = Item.builder()
				.id(itemId)
				.itemName("기존 아이템")
				.price(5000)
				.quantity(5)
				.user(user)
				.store(store)
				.itemInfo("기존 아이템입니다.")
				.category("기존 카테고리")
				.build();

		given(itemRepository.findById(existingItem.getId())).willReturn(Optional.of(existingItem));
//		given(userRepository.save(user)).willReturn(user);
//		given(itemRepository.save(existingItem)).willReturn(existingItem);
//		User save = userRepository.save(user);
//		Item save1 = itemRepository.save(existingItem);
		// When
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> itemService.updateItem(itemId, requestDto, user2));
		assertEquals("본인 상품만 수정이 가능합니다.", exception.getMessage());

	}

	@Test
	@DisplayName("아이템 수정 실패 - 아이템이 없는 경우")
	void updateItemFail2() {

		Long itemId = 1L;

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

		ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder()
				.itemName("새로운 아이템")
				.price(5000)
				.quantity(5)
				.itemInfo("새로운 아이템입니다.")
				.category("새로운 카테고리")
				.build();

		given(itemRepository.findById(itemId)).willReturn(Optional.empty());

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
				itemService.updateItem(itemId, requestDto, user));

		assertEquals("No such item", exception.getMessage());

	}

	@Test
	@DisplayName("아이템 삭제 성공")
	void deleteItem() {
		Long itemId = 1L;

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

		Item existingItem = Item.builder()
				.id(itemId)
				.itemName("기존 아이템")
				.price(5000)
				.quantity(5)
				.itemInfo("기존 아이템입니다.")
				.category("기존 카테고리")
				.user(user)
				.status(Status.EXIST)
				.build();

		given(itemRepository.findById(itemId)).willReturn(Optional.of(existingItem));

		itemService.deleteItem(itemId, user);
		assertThat(existingItem.getStatus()).isEqualTo(Status.DELETED);
	}

	@Test
	@DisplayName("아이템 삭제 실패 - 아이템이 없는 경우")
	void deleteItemFail() {
		Long itemId = 1L;

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

		Item existingItem = Item.builder()
				.id(itemId)
				.itemName("기존 아이템")
				.price(5000)
				.quantity(5)
				.itemInfo("기존 아이템입니다.")
				.category("기존 카테고리")
				.user(user)
				.status(Status.EXIST)
				.build();

		given(itemRepository.findById(existingItem.getId())).willReturn(Optional.empty());


		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
				itemService.deleteItem(itemId, user));

		assertEquals("No such item", exception.getMessage());
	}

	@Test
	@DisplayName("아이템 삭제 실패 - 본인 상품이 아닌 경우")
	void deleteItemFail2() {
		Long itemId = 1L;

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
		User user2 = User.builder()
				.id(2L)
				.build();
		Item existingItem = Item.builder()
				.id(itemId)
				.itemName("기존 아이템")
				.price(5000)
				.quantity(5)
				.itemInfo("기존 아이템입니다.")
				.category("기존 카테고리")
				.user(user)
				.status(Status.EXIST)
				.build();

		given(itemRepository.findById(existingItem.getId())).willReturn(Optional.of(existingItem));


		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
				itemService.deleteItem(itemId, user2));

		assertEquals("본인 상품만 수정이 가능합니다.", exception.getMessage());
	}

	@Test
	@DisplayName("카테고리 별 아이템 조회 성공")
	void getItemByCategory() {
		Long itemId = 1L;

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

		Item item1 = Item.builder()
				.id(itemId)
				.itemName("컴퓨터")
				.price(5000)
				.quantity(5)
				.itemInfo("컴퓨터 입니다")
				.category("전자제품")
				.user(user)
				.status(Status.EXIST)
				.build();

		List<Item> items = new ArrayList<>();
		items.add(item1);

		given(itemRepository.findByCategory("category")).willReturn(items);

		// when
		List<ItemsResponseDto> result = itemService.findByCategory("category");
		// then
		assertThat(result.get(0).getItemName()).isEqualTo("컴퓨터");
		assertThat(result.get(0).getItemInfo()).isEqualTo("컴퓨터 입니다");
		assertThat(result.get(0).getPrice()).isEqualTo(5000);
		assertThat(result.get(0).getCategory()).isEqualTo("전자제품");
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("카테고리 별 아이템 조회 empty")
	void getItemByCategoryEmpty() {
		// given
		given(itemRepository.findByCategory("category")).willReturn(Collections.emptyList()); // 아이템이 없는 상황 가정
		// when
		List<ItemsResponseDto> result = itemService.findByCategory("category");
		// then
		assertTrue(result.isEmpty());

	}

	@Test
	@DisplayName("아이템 재고 감소 성공")
	void decreaseItemQuantity() {
		Long itemId = 1L;
		int decreaseAmount = 1;

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

		Item existingItem = Item.builder()
				.id(itemId)
				.itemName("기존 아이템")
				.price(5000)
				.quantity(5)
				.itemInfo("기존 아이템입니다.")
				.category("기존 카테고리")
				.user(user)
				.status(Status.EXIST)
				.build();

		given(itemRepository.findById(itemId)).willReturn(Optional.of(existingItem));
		itemService.decreaseQuantity(existingItem.getId(), decreaseAmount);
		assertEquals(existingItem.getQuantity(), 4);
	}

	@Test
	@DisplayName("아이템 재고 감소 성공 실패 - 재고부족")
	void decreaseItemQuantityFail() {
		Long itemId = 1L;
		int decreaseAmount = 1;

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

		Item existingItem = Item.builder()
				.id(itemId)
				.itemName("기존 아이템")
				.price(5000)
				.quantity(0)
				.itemInfo("기존 아이템입니다.")
				.category("기존 카테고리")
				.user(user)
				.status(Status.EXIST)
				.build();

		given(itemRepository.findById(itemId)).willReturn(Optional.of(existingItem));
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
				itemService.decreaseQuantity(existingItem.getId(), decreaseAmount));

		assertEquals("상품의 재고가 부족합니다.", exception.getMessage());
	}

	@Test
	@DisplayName("재고가 0일 때 isSoldOut() 성공")
	void isSoldOutWhenQuantityIsZero() {
		// Given
		Long itemId = 1L;
		Item item = Item.builder()
				.id(itemId)
				.quantity(0)
				.build();
		given(itemRepository.findById(itemId)).willReturn(Optional.of(item));

		// When
		boolean soldOut = itemService.isSoldOut(itemId);

		// Then
		assertTrue(soldOut);
	}

	@Test
	@DisplayName("재고가 1 이상일 때 isSoldOut() 실패")
	void isSoldOutWhenQuantityIsGreaterThanZero() {
		// Given
		Long itemId = 1L;
		Item item = Item.builder()
				.id(itemId)
				.quantity(1)
				.build();
		given(itemRepository.findById(itemId)).willReturn(Optional.of(item));

		// When
		boolean soldOut = itemService.isSoldOut(itemId);

		// Then
		assertFalse(soldOut);
	}
}
