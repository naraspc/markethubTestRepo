package org.hanghae.markethub.domain.purchase.repository;

import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.hanghae.markethub.global.constant.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class PurchaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Test
    void findByStatusAndEmailOrderByCreatedTimeDesc() {
        // 데이터 준비
        Purchase purchase1 = createTestPurchase("test@example.com", Status.ORDER_COMPLETE);
        entityManager.persist(purchase1);

        // 실행 & 검증
        List<Purchase> result = purchaseRepository.findByStatusAndEmailOrderByCreatedTimeDesc(Status.ORDER_COMPLETE, "test@example.com");
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
        assertThat(result.get(0).getStatus()).isEqualTo(Status.ORDER_COMPLETE);
    }

    @Test
    void findAllByStatusAndEmail() {
        // 데이터 준비
        Purchase purchase1 = createTestPurchase("test2@example.com", Status.IN_DELIVERY);
        entityManager.persist(purchase1);

        // 실행 & 검증
        List<Purchase> result = purchaseRepository.findAllByStatusAndEmail(Status.IN_DELIVERY, "test2@example.com");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Status.IN_DELIVERY);
        assertThat(result.get(0).getEmail()).isEqualTo("test2@example.com");
    }

    @Test
    void findAllByStatusNotExistAndEmail() {
        // 데이터 준비
        Purchase purchase1 = createTestPurchase("test3@example.com", Status.DELETED); // Status가 DELETED이므로, NOT EXIST 조건에 맞음
        entityManager.persist(purchase1);

        // 실행 & 검증
        List<Purchase> result = purchaseRepository.findAllByStatusNotExistAndEmail(Status.EXIST, "test3@example.com");
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getStatus()).isNotEqualTo(Status.EXIST);
        assertThat(result.get(0).getEmail()).isEqualTo("test3@example.com");
    }

    private Purchase createTestPurchase(String email, Status status) {
        return Purchase.builder()
                .itemName("Test Item")
                .email(email)
                .quantity(1)
                .price(BigDecimal.valueOf(100))
                .status(status)
                .build();
    }
}