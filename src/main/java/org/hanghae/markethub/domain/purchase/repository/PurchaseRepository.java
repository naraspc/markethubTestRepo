package org.hanghae.markethub.domain.purchase.repository;

import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findAllByUserEmail(String email);
    Purchase findByUserEmail(String email);
}
