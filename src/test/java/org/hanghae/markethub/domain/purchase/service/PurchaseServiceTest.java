//package org.hanghae.markethub.domain.purchase.service;
//
//
//import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
//import org.hanghae.markethub.domain.purchase.dto.PurchaseResponseDto;
//import org.hanghae.markethub.domain.purchase.entity.Purchase;
//import org.hanghae.markethub.domain.purchase.repository.PurchaseRepository;
//import org.hanghae.markethub.global.constant.Status;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@Transactional
//class PurchaseServiceTest {
//
//    @Mock
//    private PurchaseRepository purchaseRepository;
//
//    @Captor // 객체를 캡처하여 안에있는 값을 확인하는데 사용합니다.
//    private ArgumentCaptor<Purchase> purchaseArgumentCaptor;
//    @InjectMocks
//    private PurchaseService purchaseService;
//
//    private PurchaseRequestDto purchaseRequestDto;
//    private Purchase purchase;
//    private List<Purchase> purchaseList;
//    private final String userEmail = "user@example.com";
//    @BeforeEach
//    void setUp() {
//        purchaseRequestDto = new PurchaseRequestDto(Status.ORDER_COMPLETE, "Test Item", 1, 1L, new BigDecimal("100.00"));
//        purchase = Purchase.builder()
//                .id(1L)
//                .itemName("Test Item")
//                .quantity(1)
//                .price(new BigDecimal("100.00"))
//                .status(Status.ORDER_COMPLETE)
//                .email("user@example.com")
//                .build();
//        purchaseList = new ArrayList<>();
//        purchaseList.add(purchase);
//
//    }
//
//    @Test
//    @Tag("성공테스트")
//    @DisplayName("기존 주문내역 있을때 주문하기")
//    void whenCreatingOrderWithExistingPurchases_thenExistingPurchasesAreDeleted() {
//        // Given 기존 구매가 있을 때
//        Purchase existingPurchase = Purchase.builder()
//                .status(Status.EXIST)
//                .email(userEmail)
//                .itemName("Old Item")
//                .quantity(1)
//                .price(new BigDecimal("50.00"))
//                .itemId(2L)
//                .build();
//        when(purchaseRepository.findAllByStatusAndEmail(Status.EXIST, userEmail)).thenReturn(List.of(existingPurchase));
//
//        // When 새 주문 생성 시
//        purchaseService.createOrder(purchaseRequestDto, userEmail);
//
//        // Then 기존 구매는 삭제됩니다
//        verify(purchaseRepository, times(1)).deleteAll(anyList());
//    }
//
//    @Test
//    @Tag("성공테스트")
//    @DisplayName("기존 주문내역 없을때 주문하기")
//    void whenCreatingOrder_thenPurchaseIsSaved() {
//        // Given 기존 구매가 없을 때
//        when(purchaseRepository.findAllByStatusAndEmail(Status.EXIST, userEmail)).thenReturn(Collections.emptyList());
//
//        // When 새 주문 생성 시
//        PurchaseResponseDto responseDto = purchaseService.createOrder(purchaseRequestDto, userEmail);
//
//        // Then 구매가 저장되고 예상된 응답이 반환됩니다
//        verify(purchaseRepository, times(1)).save(any(Purchase.class));
//        assertNotNull(responseDto);
//        assertEquals(purchaseRequestDto.itemName(), responseDto.itemDetails().itemName());
//    }
//
//    @Test
//    @Tag("예외테스트")
//    @DisplayName("이메일 기반 주문찾을때 예외처리")
//    void findAllPurchaseByEmailWhenEmailNotFound() {
//        when(purchaseRepository.findByStatusAndEmailOrderByCreatedTimeDesc(Status.EXIST, "nonexistent@example.com"))
//                .thenThrow(new EntityNotFoundException("Purchase not found for email: nonexistent@example.com"));
//        assertThrows(EntityNotFoundException.class, () -> purchaseService.findAllPurchaseByEmail("nonexistent@example.com"));
//    }
//
//    @Test
//    @Tag("성공테스트")
//    @DisplayName("이메일 기반 주문 찾기 성공")
//    void findAllPurchaseByEmailSuccessfully() {
//        when(purchaseRepository.findByStatusAndEmailOrderByCreatedTimeDesc(Status.EXIST, "user@example.com"))
//                .thenReturn(purchaseList);
//        List<PurchaseResponseDto> results = purchaseService.findAllPurchaseByEmail("user@example.com");
//        assertThat(results).hasSize(1);
//        assertThat(results.get(0).purchaseId()).isEqualTo(purchase.getId());
//    }
//
//    @Test
//    @Tag("예외테스트")
//    @DisplayName("단건주문 불러오기 예외처리")
//    void findSinglePurchaseWhenPurchaseNotFound() {
//        when(purchaseRepository.findById(2L)).thenThrow(new EntityNotFoundException("Purchase not found"));
//        assertThrows(EntityNotFoundException.class, () -> purchaseService.findSinglePurchase(2L));
//    }
//
//    @Test
//    @Tag("성공테스트")
//    @DisplayName("단건주문 불러오기")
//    void findSinglePurchaseSuccessfully() {
//        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
//        PurchaseResponseDto result = purchaseService.findSinglePurchase(1L);
//        assertThat(result.purchaseId()).isEqualTo(purchase.getId());
//    }
//
//    @Test
//    @Tag("성공테스트")
//    @DisplayName("삭제 테스트")
//    void deletePurchaseSuccessfully() {
//        // Given
//        Purchase purchase = Purchase.builder()
//                .id(1L)
//                .status(Status.EXIST) // 초기 상태 설정
//                .build();
//
//        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
//
//        // When
//        purchaseService.deletePurchase(1L);
//
//        // Then
//        assertThat(purchase.getStatus()).isEqualTo(Status.DELETED);
//    }
//
//    @Test
//    @Tag("예외테스트")
//    @DisplayName("삭제 시 not found ")
//    void deletePurchaseWhenPurchaseNotFound() {
//        when(purchaseRepository.findById(2L)).thenThrow(new EntityNotFoundException("Purchase not found"));
//        assertThrows(EntityNotFoundException.class, () -> purchaseService.deletePurchase(2L));
//    }
//
//    @Test
//    @Tag("성공테스트")
//    @DisplayName("주문완료처리 ")
//    void updatePurchaseStatusToOrderedSuccessfully() {
//        when(purchaseRepository.findAllByStatusAndEmail(Status.EXIST, "user@example.com")).thenReturn(purchaseList);
//        purchaseService.updatePurchaseStatusToOrdered("user@example.com");
//        verify(purchaseRepository).saveAll(anyList());
//    }
//
//    @Test
//    @Tag("예외테스트")
//    @DisplayName("주문완료처리 not found")
//    void updatePurchaseStatusToOrderedWhenEmailNotFound() {
//        when(purchaseRepository.findAllByStatusAndEmail(Status.EXIST, "nonexistent@example.com"))
//                .thenThrow(new EntityNotFoundException("No purchases found for email: nonexistent@example.com"));
//        assertThrows(EntityNotFoundException.class, () -> purchaseService.updatePurchaseStatusToOrdered("nonexistent@example.com"));
//    }
//}
