//package org.hanghae.markethub.domain.purchase.controller;
//
//import org.hanghae.markethub.domain.purchase.entity.Purchase;
//import org.hanghae.markethub.domain.purchase.repository.PurchaseRepository;
//import org.hanghae.markethub.domain.purchase.service.PurchaseService;
//import org.hanghae.markethub.global.constant.Status;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class PurchaseServiceTest {
//
//    @Autowired
//    private PurchaseRepository purchaseRepository;
//
//    @Autowired
//    private PurchaseService purchaseService;
//
//    @Test
//    void testVerifyPrice_NotEqual() {
//        // Given
//        Purchase purchase = Purchase.builder()
//                .itemName("Test Item")
//                .email("test@example.com")
//                .quantity(1)
//                .price(BigDecimal.valueOf(10000))
//                .status(Status.ORDER_COMPLETE)
//                .build();
//        purchaseRepository.save(purchase);
//
//        // When
//        boolean result = purchaseService.verifyPrice("test@example.com", BigDecimal.valueOf(15000));
//
//        // Then
//        assertFalse(result);
//    }
//
//    @Test
//    void testVerifyPrice_Equal() {
//        // Given
//        Purchase purchase = Purchase.builder()
//                .itemName("Test Item")
//                .email("test@example.com")
//                .quantity(1)
//                .price(BigDecimal.valueOf(10000))
//                .status(Status.ORDER_COMPLETE)
//                .build();
//        purchaseRepository.save(purchase);
//
//        // When
//        boolean result = purchaseService.verifyPrice("test@example.com", BigDecimal.valueOf(10000));
//
//        // Then
//        assertTrue(result);
//    }
//}