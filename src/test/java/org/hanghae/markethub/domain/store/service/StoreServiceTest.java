package org.hanghae.markethub.domain.store.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

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
		User save = userRepository.save(user);;
		storeService.createStore(save);
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

		given(storeRepository.save(store)).willReturn(store);
		given(userRepository.save(user)).willReturn(user);

		User save = userRepository.save(user);
		Store saveStore = storeRepository.save(store);

		given(storeRepository.findByUserId(save.getId())).willReturn(Optional.of(saveStore));
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

		given(storeRepository.save(store)).willReturn(store);
		given(userRepository.save(user)).willReturn(user);
		User save = userRepository.save(user);
		Store saveStore = storeRepository.save(store);
		given(storeRepository.findByUserId(save.getId())).willReturn(Optional.of(saveStore));

		storeService.deleteStore(save);
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


		given(userRepository.save(user)).willReturn(user);
		User save = userRepository.save(user);
		assertThrows(IllegalArgumentException.class, () -> storeService.deleteStore(save));
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

		given(storeRepository.save(store)).willReturn(store);
		given(userRepository.save(user)).willReturn(user);
		User save = userRepository.save(user);
		Store saveStore = storeRepository.save(store);
		given(storeRepository.findByUserId(save.getId())).willReturn(Optional.of(saveStore));
		storeService.findByUsergetStore(save.getId());
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


		given(userRepository.save(user)).willReturn(user);
		User save = userRepository.save(user);
		assertThrows(IllegalArgumentException.class, () -> storeService.findByUsergetStore(save.getId()));
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
		storeService.getStoreItem(saveItem.getId(), save);
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

		given(storeRepository.save(store)).willReturn(store);
		given(userRepository.save(user)).willReturn(user);
		given(itemRepository.save(item)).willReturn(item);
		User save = userRepository.save(user);
		Store saveStore = storeRepository.save(store);
		Item saveItem = itemRepository.save(item);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> storeService.getStoreItem(2L, save));
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

		given(storeRepository.save(store)).willReturn(store);
		given(userRepository.save(user)).willReturn(user);
		given(itemRepository.save(item)).willReturn(item);
		User save = userRepository.save(user);
		Store saveStore = storeRepository.save(store);
		Item saveItem = itemRepository.save(item);
		given(itemRepository.findById(saveItem.getId())).willReturn(Optional.of((saveItem)));
//		assertThrows(IllegalArgumentException.class, () -> storeService.getStoreItem(saveItem.getId(), user2));
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> storeService.getStoreItem(saveItem.getId(), user2));
		assertEquals("본인 상품은 조회 가능합니다.", exception.getMessage());
	}
}