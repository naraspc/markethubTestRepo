package org.hanghae.markethub.domain.purchase.service;

import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
import org.hanghae.markethub.domain.purchase.dto.PurchaseResponseDto;
import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.hanghae.markethub.domain.purchase.repository.PurchaseRepository;
import org.hanghae.markethub.global.constant.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PurchaseServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("주문 생성 로직 테스트")
    void 주문하기테스트() {
        // Given
        String userId = "testUser";
        Status status = Status.EXIST;
        PurchaseRequestDto requestDto = new PurchaseRequestDto(status);

        List<Cart> carts = new ArrayList<>();

        when(cartRepository.findByUserId(userId)).thenReturn(carts);
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(i -> i.getArgument(0));

        // When
        PurchaseResponseDto responseDto = purchaseService.createOrder(requestDto, userId);

        // Then
        assertNotNull(responseDto);
        assertEquals(responseDto.status(), status);
        assertEquals(responseDto.carts().size(), carts.size());
        verify(cartRepository).findByUserId(userId);
        verify(purchaseRepository).save(any(Purchase.class));
    }
}