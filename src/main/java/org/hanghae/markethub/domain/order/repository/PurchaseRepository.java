package org.hanghae.markethub.domain.order.repository;

import org.hanghae.markethub.domain.order.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
