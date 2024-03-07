package org.hanghae.markethub.domain.item.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hanghae.markethub.domain.item.config.ElasticSearchConfig;
import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.dto.RedisItemResponseDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.store.service.StoreService;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
	private ElasticSearchConfig elasticSearchConfig;

	@Mock
	private ElasticSearchService elasticSearchService;

	@Mock
	private StoreService storeService;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@InjectMocks
	private ItemService itemService;



	@Test
	@DisplayName("아이템 Pageable 조회")
	void getAllItems() {
		// Given
		int page = 0;
		int size = 10;
		List<Item> items = createSampleItems(size);
		Page<Item> itemPage = new PageImpl<>(items);
		when(itemRepository.findAll(any(Pageable.class))).thenReturn(itemPage);

		// Stubbing awsS3Service
		List<String> pictureUrls = Arrays.asList("url1", "url2", "url3");
		when(awsS3Service.getObjectUrlsForItem(anyLong())).thenReturn(pictureUrls);

		// When
		Page<ItemsResponseDto> resultPage = itemService.getItems(page, size);

		// Then
		assertEquals(size, resultPage.getContent().size()); // 페이지당 아이템 수 확인

		// 각 아이템의 내용과 사진 URL이 올바르게 매핑되었는지 확인
		for (int i = 0; i < size; i++) {
			ItemsResponseDto expectedDto = ItemsResponseDto.fromEntity(items.get(i), pictureUrls);
			ItemsResponseDto actualDto = resultPage.getContent().get(i);

			// 각 필드를 개별적으로 비교하여 동등한지 확인
			assertEquals(expectedDto.getId(), actualDto.getId());
			assertEquals(expectedDto.getItemName(), actualDto.getItemName());
			assertEquals(expectedDto.getPrice(), actualDto.getPrice());
			assertEquals(expectedDto.getQuantity(), actualDto.getQuantity());
			assertEquals(expectedDto.getItemInfo(), actualDto.getItemInfo());
			assertEquals(expectedDto.getCategory(), actualDto.getCategory());
			assertEquals(expectedDto.getPictureUrls(), actualDto.getPictureUrls());
		}
	}

	// 테스트를 위한 샘플 아이템 생성 메서드
	private List<Item> createSampleItems(int count) {
		List<Item> items = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			items.add(Item.builder()
					.id((long) i)
					.itemName("Item " + i)
					.price(1000 + i)
					.quantity(10 - i)
					.itemInfo("Info " + i)
					.category("Category " + i)
					.build());
		}
		return items;


	}

	@Test
	@DisplayName("아이템 단건 조회 - Redis에 캐싱되어 있지 않은 경우")
	void getItem_NotCachedInRedis() throws JsonProcessingException {
		// Given
		Long itemId = 1L;
		Item item = createSampleItem(itemId);

		ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class); // ValueOperations를 모의(mock) 생성
		when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock); // RedisTemplate의 opsForValue() 메서드를 모의(mock)
		when(valueOperationsMock.get(anyString())).thenReturn(null); // opsForValue() 메서드의 반환 값을 설정

		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
		when(awsS3Service.getObjectUrlsForItemTest(any(Item.class))).thenReturn(Arrays.asList("url1", "url2"));

		ZSetOperations<String, String> zSetOperationsMock = mock(ZSetOperations.class); // ZSetOperations를 모의(mock) 생성
		when(redisTemplate.opsForZSet()).thenReturn(zSetOperationsMock); // RedisTemplate의 opsForZSet() 메서드를 모의(mock)
		when(zSetOperationsMock.add(anyString(), anyString(), anyDouble())).thenReturn(true); // opsForZSet() 메서드의 반환 값을 설정

		// When
		ItemsResponseDto resultDto = itemService.getItem(itemId);

		// Then
		assertEquals(item.getId(), resultDto.getId());
		assertEquals(item.getItemName(), resultDto.getItemName());
		assertEquals(item.getPrice(), resultDto.getPrice());
		assertEquals(item.getQuantity(), resultDto.getQuantity());
		assertEquals(item.getItemInfo(), resultDto.getItemInfo());
		assertEquals(item.getCategory(), resultDto.getCategory());
		assertEquals(Arrays.asList("url1", "url2"), resultDto.getPictureUrls());
	}

	@Test
	@DisplayName("아이템 단건 조회 - Redis에 캐싱되어 있는 경우")
	void getItem_CachedInRedis() throws JsonProcessingException {

		// Mocking JSON response from Redis
		String json = "{\"id\":1,\"itemName\":\"컴퓨터\",\"price\":5000,\"quantity\":1,\"itemInfo\":\"컴퓨터 입니다\",\"category\":\"전자제품\",\"pictureUrls\":[]}";

		// Stubbing the method calls for itemRepository and objectMapper
		Item item = Item.builder()
				.id(1L)
				.itemName("컴퓨터")
				.itemInfo("컴퓨터 입니다")
				.price(5000)
				.status(Status.EXIST)
				.category("전자제품")
				.build();

		when(objectMapper.readValue(anyString(), eq(RedisItemResponseDto.class)))
				.thenReturn(RedisItemResponseDto.builder()
						.id(1L)
						.itemName("컴퓨터")
						.price(5000)
						.quantity(1)
						.itemInfo("컴퓨터 입니다")
						.category("전자제품")
						.pictureUrls(new ArrayList<>())
						.build());

		ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
		when(valueOperationsMock.get(anyString())).thenReturn(json);

		ItemsResponseDto responseDto = itemService.getItem(item.getId());

		// Assertions
		assertEquals(item.getId(), responseDto.getId());
		assertEquals(item.getItemName(), responseDto.getItemName());
		assertEquals(item.getPrice(), responseDto.getPrice());
		assertEquals(1, responseDto.getQuantity());
		assertEquals(item.getItemInfo(), responseDto.getItemInfo());
		assertEquals(item.getCategory(), responseDto.getCategory());
		assertEquals(new ArrayList<>(), responseDto.getPictureUrls());
	}


	// 테스트를 위한 샘플 아이템 생성 메서드
	private Item createSampleItem(Long itemId) {
		return Item.builder()
				.id(itemId)
				.itemName("Sample Item")
				.price(1000)
				.quantity(10)
				.itemInfo("Sample Info")
				.category("Sample Category")
				.build();
	}

	@Test
	@DisplayName("아이템 단건 조회 empty")
	void getItem() {
		// Given
		Long itemId = 1L;
		String key = "item";
		String findKey = key + ":" + itemId;
		ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
		when(redisTemplate.opsForValue().get(findKey)).thenReturn(null);
		when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			itemService.getItem(itemId);
		});
		assertEquals("No such Item", exception.getMessage());
	}

	@Test
	@DisplayName("아이템 등록 테스트")
	void testCreateItem() throws IOException {
		// Given
		ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
				.itemName("테스트 아이템")
				.itemInfo("테스트 아이템 정보")
				.price(10000)
				.quantity(5)
				.category("TestCategory")
				.build();
		List<MultipartFile> files = List.of(new MockMultipartFile("file1", "file1.txt", "text/plain", "test".getBytes()));

		User user = User.builder()
				.id(1L)
				.name("TestUser")
				.email("test@example.com")
				.build();

		Store store = Store.builder()
				.id(1L)
				.user(user)
				.build();

		Item expectedItem = Item.builder()
				.id(1L)
				.itemName(requestDto.getItemName())
				.itemInfo(requestDto.getItemInfo())
				.price(requestDto.getPrice())
				.quantity(requestDto.getQuantity())
				.category(requestDto.getCategory())
				.status(Status.EXIST)
				.user(user)
				.store(store)
				.build();

		ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
		ZSetOperations<String, String> zSetOperationsMock = mock(ZSetOperations.class);
		when(redisTemplate.opsForZSet()).thenReturn(zSetOperationsMock);
		when(storeService.findByUsergetStore(anyLong())).thenReturn(store);
		when(itemRepository.save(any(Item.class))).thenReturn(expectedItem);

		// When
		itemService.createItem(requestDto, files, user);

		// Then
		verify(storeService, times(1)).findByUsergetStore(anyLong());
		verify(itemRepository, times(1)).save(any(Item.class));
		verify(awsS3Service, times(1)).uploadFiles(eq(files), any(Item.class));
		verify(elasticSearchConfig, times(1)).syncItemToElasticsearch(any(Item.class));
	}

	@Test
	@DisplayName("아이템 생성 실패 테스트 - 재고 음수일 때")
	void testCreateItem_QuantityNegative() {
		// Given
		ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
				.itemName("Test Item")
				.itemInfo("Test Item Info")
				.price(10000)
				.quantity(-1)  // 음수로 설정하여 재고가 0 이하인 경우 테스트
				.category("TestCategory")
				.build();
		MultipartFile file = mock(MultipartFile.class);
		User user = mock(User.class);

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			itemService.createItem(requestDto, List.of(file), user);
		});
		assertEquals("가격 또는 재고는 0 이하일 수 없습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("아이템 수정 성공 테스트")
	void testUpdateItem_Success() throws JsonProcessingException {
		// Given
		Long itemId = 1L;
		User user = User.builder()
				.id(1L)
				.name("Test User")
				.build();
		Item existingItem = Item.builder()
				.id(itemId)
				.itemName("Old Item Name")
				.itemInfo("Old Item Info")
				.price(10000)
				.quantity(10)
				.category("Test Category")
				.user(user)
				.build();
		ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder()
				.itemName("New Item Name")
				.itemInfo("New Item Info")
				.price(15000)
				.quantity(5)
				.category("New Category")
				.build();

		ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

		// When
		itemService.updateItem(itemId, requestDto, user);

		// Then
		verify(itemRepository, times(1)).findById(itemId);
		verify(elasticSearchConfig, times(1)).syncItemToElasticsearch(any(Item.class));

		// 아이템이 제대로 수정되었는지 확인
		assertEquals(requestDto.getItemName(), existingItem.getItemName());
		assertEquals(requestDto.getItemInfo(), existingItem.getItemInfo());
		assertEquals(requestDto.getPrice(), existingItem.getPrice());
		assertEquals(requestDto.getQuantity(), existingItem.getQuantity());
		assertEquals(requestDto.getCategory(), existingItem.getCategory());
	}

	@Test
	@DisplayName("다른 판매자의 상품 수정 실패 테스트")
	void testUpdateItem_Fail_DifferentSeller() {
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
		assertEquals(Status.DELETED, existingItem.getStatus());
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
	@DisplayName("키워드로 아이템")
	void findByKeyWord_Success() {
		// Given
		String keyword = "test";
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size);


		ItemsResponseDto item1 = ItemsResponseDto.builder().build();
		ItemsResponseDto item2 = ItemsResponseDto.builder().build();

		Page<ItemsResponseDto> fakePage = new PageImpl<>(List.of(item1, item2));
		when(elasticSearchService.searchNativeQuery(keyword, page, size)).thenReturn(fakePage);

		// When
		Page<ItemsResponseDto> result = itemService.findByKeyWord(keyword, page, size);

		// Then
		verify(elasticSearchService, times(1)).searchNativeQuery(keyword, page, size);

		assertEquals(fakePage, result);
	}

	@Test
	@DisplayName("키워드로 아이템 검색 - 일치하는 항목이 없는 경우")
	void findByKeyWord_NoMatch() {
		// Given
		String keyword = "no_keyword";
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size);

		// 검색 결과가 비어있는 페이지를 생성하여 반환
		Page<ItemsResponseDto> emptyPage = new PageImpl<>(Collections.emptyList());
		when(elasticSearchService.searchNativeQuery(keyword, page, size)).thenReturn(emptyPage);

		// When
		Page<ItemsResponseDto> result = itemService.findByKeyWord(keyword, page, size);

		// Then
		// elasticSearchService의 searchNativeQuery() 메서드가 호출되었는지 확인
		verify(elasticSearchService, times(1)).searchNativeQuery(keyword, page, size);

		// 반환된 페이지가 비어 있는지 확인
		assertTrue(result.isEmpty());
	}

}

//	@Test
//	@DisplayName("아이템 재고 감소 성공")
//	void decreaseItemQuantity() throws JsonProcessingException {
//		Long itemId = 1L;
//		int decreaseAmount = 1;
//
//		User user = User.builder()
//				.id(1L)
//				.name("LEE")
//				.password("1234")
//				.address("서울시")
//				.email("sd@naver.com")
//				.phone("010")
//				.status(Status.EXIST)
//				.role(Role.ADMIN)
//				.build();
//
//		Item existingItem = Item.builder()
//				.id(itemId)
//				.itemName("기존 아이템")
//				.price(5000)
//				.quantity(5)
//				.itemInfo("기존 아이템입니다.")
//				.category("기존 카테고리")
//				.user(user)
//				.status(Status.EXIST)
//				.build();
//
//		given(itemRepository.findById(itemId)).willReturn(Optional.of(existingItem));
//		itemService.decreaseQuantity(existingItem.getId(), decreaseAmount);
//		assertEquals(existingItem.getQuantity(), 4);
//	}
//
//	@Test
//	@DisplayName("아이템 재고 감소 성공 실패 - 재고부족")
//	void decreaseItemQuantityFail() {
//		Long itemId = 1L;
//		int decreaseAmount = 1;
//
//		User user = User.builder()
//				.id(1L)
//				.name("LEE")
//				.password("1234")
//				.address("서울시")
//				.email("sd@naver.com")
//				.phone("010")
//				.status(Status.EXIST)
//				.role(Role.ADMIN)
//				.build();
//
//		Item existingItem = Item.builder()
//				.id(itemId)
//				.itemName("기존 아이템")
//				.price(5000)
//				.quantity(0)
//				.itemInfo("기존 아이템입니다.")
//				.category("기존 카테고리")
//				.user(user)
//				.status(Status.EXIST)
//				.build();
//
//		given(itemRepository.findById(itemId)).willReturn(Optional.of(existingItem));
//		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
//				itemService.decreaseQuantity(existingItem.getId(), decreaseAmount));
//
//		assertEquals("상품의 재고가 부족합니다.", exception.getMessage());
//	}
//
//	@Test
//	@DisplayName("재고가 0일 때 isSoldOut() 성공")
//	void isSoldOutWhenQuantityIsZero() {
//		// Given
//		Long itemId = 1L;
//		Item item = Item.builder()
//				.id(itemId)
//				.quantity(0)
//				.build();
//		given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
//
//		// When
//		boolean soldOut = itemService.isSoldOut(itemId);
//
//		// Then
//		assertTrue(soldOut);
//	}
//
//	@Test
//	@DisplayName("재고가 1 이상일 때 isSoldOut() 실패")
//	void isSoldOutWhenQuantityIsGreaterThanZero() {
//		// Given
//		Long itemId = 1L;
//		Item item = Item.builder()
//				.id(itemId)
//				.quantity(1)
//				.build();
//		given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
//
//		// When
//		boolean soldOut = itemService.isSoldOut(itemId);
//
//		// Then
//		assertFalse(soldOut);
//	}

