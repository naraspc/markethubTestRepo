package org.hanghae.markethub.domain.store.repository;

import org.hanghae.markethub.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
