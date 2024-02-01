package org.hanghae.markethub.domain.store.service;

import org.assertj.core.api.Assertions;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StoreTest {
	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void createStore() {
		User user = User.builder()
				.email("1234@naver.com")
				.password("1234")
				.name("lee")
				.phone("010-1234")
				.address("서울시")
				.role(Role.ADMIN)
				.status(Status.EXIST)
				.build();
		User saveUser = userRepository.save(user);


		Store store = Store.builder()
				.user(saveUser)
				.status(Status.EXIST)
				.build();

		Store saveStore = storeRepository.save(store);

		assertEquals("lee", saveStore.getUser().getName());
	}

	@Test
	@Transactional
	@Commit
	void deleteStore() {
		Long storeId= 7L;
		Store store = storeRepository.findById(storeId).orElseThrow();
		store.setStatus(Status.DELETED);
		Store findStore = storeRepository.findById(storeId).orElseThrow();
		assertEquals(Status.DELETED, findStore.getStatus());
	}
}