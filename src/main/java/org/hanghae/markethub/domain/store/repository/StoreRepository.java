package org.hanghae.markethub.domain.store.repository;

import org.hanghae.markethub.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
	@Query("SELECT p FROM Store p WHERE p.user.id = :userId AND p.status = 'EXIST'")
	Optional<Store> findByUserId(Long userId);
}
