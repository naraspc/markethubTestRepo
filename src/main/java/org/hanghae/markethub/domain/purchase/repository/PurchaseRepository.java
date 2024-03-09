package org.hanghae.markethub.domain.purchase.repository;

//import io.lettuce.core.dynamic.annotation.Param;
import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByStatusAndEmailOrderByCreatedTimeDesc(Status status, String email);

    List<Purchase> findAllByStatusAndEmail(Status status, String email);

    List<Purchase> findAllByImpUid(String impUid);

    // Status가 EXIST가 아닌 Purchase 조회
    @Query("SELECT p FROM Purchase p WHERE p.status NOT IN (:statuses) AND p.email = :email")
    List<Purchase> findAllByStatusNotInAndEmail(@Param("statuses") List<Status> statuses, @Param("email") String email);

    Purchase findByStatusAndItemIdAndEmail(Status status,Long itemId, String email);
}
