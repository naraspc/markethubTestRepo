package org.hanghae.markethub.domain.store.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;

	public void createStore(Long userId) {
		User user = userRepository.findById(5L).orElseThrow(
				() ->new IllegalArgumentException("No such store")); // 인증 구현 후 제거 예정
		User user1 = userRepository.findById(userId).orElseThrow();
		Store store = Store.builder()
				.user(user1)
				.status(Status.EXIST)
				.build();
		storeRepository.save(store);
	}

	@Transactional
	public void deleteStore(Long userId) {
		Long storeId= 7L; // 인증 구현 후 제거 예정
		Store store = storeRepository.findById(storeId).orElseThrow(
				() -> new IllegalArgumentException("No such store"));
		store.deleteStore();
	}
}
