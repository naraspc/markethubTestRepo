package org.hanghae.markethub.domain.store.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;

	public void createStore() {
		User user = userRepository.findById(1L).orElseThrow(); // 인증 구현 후 제거 예정
		Store store = Store.builder()
				.user(user)
				.status(Status.EXIST)
				.build();
		storeRepository.save(store);
	}

	public void deleteStore() {
		Long storeId= 1L; // 인증 구현 후 제거 예정
		storeRepository.findById(storeId).orElseThrow().deleteStore();
	}
}
