package org.hanghae.markethub.domain.store.repository;

import org.hanghae.markethub.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
	Optional<Store> findByUserId(Long userId);
}
