package org.hanghae.markethub.domain.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

	@InjectMocks
	private StoreService storeService;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	RedisTemplate<String, String> redisTemplateMock;

	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private AwsS3Service awsS3Service;

	@Test
	@DisplayName("스토어 생성 성공")
	void createStore() {
		User user = User.builder()
				.id(1L)
				.email("1234@naver.com")
				.password("1234")
				.name("lee")
				.phone("010-1234")
				.address("서울시")
				.role(Role.ADMIN)
				.status(Status.EXIST)
				.build();

		given(userRepository.save(user)).willReturn(user);
		User save = userRepository.save(user);
		;
		storeService.createStore(user);
	}

	@Test
	@DisplayName("스토어 생성 실패")
	void createStoreFail() {
		User user = User.builder()
				.id(1L)
				.email("1234@naver.com")
				.password("1234")
				.name("lee")
				.phone("010-1234")
				.address("서울시")
				.role(Role.ADMIN)
				.status(Status.EXIST)
				.build();


		Store store = Store.builder()
				.user(user)
				.status(Status.EXIST)
				.build();

		User save = userRepository.save(user);
		Store saveStore = storeRepository.save(store);

		given(storeRepository.findByUserId(user.getId())).willReturn(Optional.of(store));
		assertThrows(IllegalArgumentException.class, () -> storeService.createStore(user));
	}

	@Test
	@DisplayName("스토어 삭제 성공")
	void deleteStore() {
		User user = User.builder()
				.id(1L)
				.email("1234@naver.com")
				.password("1234")
				.name("lee")
				.phone("010-1234")
				.address("서울시")
				.role(Role.ADMIN)
				.status(Status.EXIST)
				.build();


		Store store = Store.builder()
				.user(user)
				.status(Status.EXIST)
				.build();

		given(storeRepository.findByUserId(user.getId())).willReturn(Optional.of(store));

		storeService.deleteStore(user);
	}

	@Test
	@DisplayName("스토어 삭제 실패")
	void deleteStoreFail() {
		User user = User.builder()
				.id(1L)
				.email("1234@naver.com")
				.password("1234")
				.name("lee")
				.phone("010-1234")
				.address("서울시")
				.role(Role.ADMIN)
				.status(Status.EXIST)
				.build();


		given(storeRepository.findByUserId(user.getId())).willReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> storeService.deleteStore(user));
	}

	@Test
	@DisplayName("스토어 조회 성공")
	void getStoreSuccess() {
		User user = User.builder()
				.id(1L)
				.email("1234@naver.com")
				.password("1234")
				.name("lee")
				.phone("010-1234")
				.address("서울시")
				.role(Role.ADMIN)
				.status(Status.EXIST)
				.build();


		Store store = Store.builder()
				.user(user)
				.status(Status.EXIST)
				.build();

		given(storeRepository.findByUserId(user.getId())).willReturn(Optional.of(store));
		storeService.findByUsergetStore(user.getId());
	}

	@Test
	@DisplayName("스토어 조회 실패")
	void getStoreFail() {
		User user = User.builder()
				.id(1L)
				.email("1234@naver.com")
				.password("1234")
				.name("lee")
				.phone("010-1234")
				.address("서울시")
				.role(Role.ADMIN)
				.status(Status.EXIST)
				.build();

		assertThrows(IllegalArgumentException.class, () -> storeService.findByUsergetStore(user.getId()));
	}

	@Test
	@DisplayName("스토어 아이템 조회 성공")
	void getStoreItem() {
		User user = User.builder()
				.id(1L)
				.email("1234@naver.com")
				.password("1234")
				.name("lee")
				.phone("010-1234")
				.address("서울시")
				.role(Role.ADMIN)
				.status(Status.EXIST)
				.build();

		Store store = Store.builder()
				.id(1L)
				.user(user)
				.status(Status.EXIST)
				.build();

		Item item = Item.builder()
				.id(1L)
				.itemName("컴퓨터")
				.itemInfo("컴퓨터 입니다")
				.price(5000)
				.status(Status.EXIST)
				.category("전자제품")
				.user(user)
				.store(store)
				.build();

		given(storeRepository.save(store)).willReturn(store);
		given(userRepository.save(user)).willReturn(user);
		given(itemRepository.save(item)).willReturn(item);
		User save = userRepository.save(user);
		Store saveStore = storeRepository.save(store);
		Item saveItem = itemRepository.save(item);
		given(itemRepository.findById(saveItem.getId())).willReturn(Optional.of((saveItem)));
		ItemsResponseDto storeItem = storeService.getStoreItem(saveItem.getId(), save);
		assertEquals(storeItem.getItemName(), item.getItemName());
		assertEquals(storeItem.getItemName(), item.getItemName());
		assertEquals(storeItem.getPrice(), item.getPrice());
		assertEquals(storeItem.getCategory(), item.getCategory());
	}


	@Test
	@DisplayName("스토어 아이템 조회 실패 1 - 아이템이 없는 경우")
	void getStoreItemFail() {
		User user = User.builder()
				.id(1L)
				.email("1234@naver.com")
				.password("1234")
				.name("lee")
				.phone("010-1234")
				.address("서울시")
				.role(Role.ADMIN)
				.status(Status.EXIST)
				.build();

		Store store = Store.builder()
				.id(1L)
				.user(user)
				.status(Status.EXIST)
				.build();

		Item item = Item.builder()
				.id(1L)
				.itemName("컴퓨터")
				.itemInfo("컴퓨터 입니다")
				.price(5000)
				.status(Status.EXIST)
				.category("전자제품")
				.user(user)
				.store(store)
				.build();

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> storeService.getStoreItem(2L, user));
		assertEquals("No such item", exception.getMessage());
	}

	@Test
	@DisplayName("스토어 아이템 조회 실패 2 - 본인의 아이템이 아닌 경우")
	void getStoreItemFail2() {
		User user = User.builder()
				.id(1L)
				.email("1234@naver.com")
				.password("1234")
				.name("lee")
				.phone("010-1234")
				.address("서울시")
				.role(Role.ADMIN)
				.status(Status.EXIST)
				.build();

		User user2 = User.builder()
				.id(2L)
				.email("1234@naver.com")
				.password("1234")
				.name("lee")
				.phone("010-1234")
				.address("서울시")
				.role(Role.ADMIN)
				.status(Status.EXIST)
				.build();

		Store store = Store.builder()
				.id(1L)
				.user(user)
				.status(Status.EXIST)
				.build();

		Item item = Item.builder()
				.id(1L)
				.itemName("컴퓨터")
				.itemInfo("컴퓨터 입니다")
				.price(5000)
				.status(Status.EXIST)
				.category("전자제품")
				.user(user)
				.store(store)
				.build();

		given(itemRepository.findById(item.getId())).willReturn(Optional.of((item)));
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> storeService.getStoreItem(item.getId(), user2));
		assertEquals("본인 상품은 조회 가능합니다.", exception.getMessage());
	}
}