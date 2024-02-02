package org.hanghae.markethub.domain.cart.controller;

import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.service.CartService;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartService cartService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @DisplayName("장바구니 추가 성공")
    @Test
    void cartSuccess() {
        // given
        CartRequestDto requestDto = cartRequestDto();

        doReturn(ResponseEntity.ok("Success Cart"))
                .when(cartService)
                .addCart(any(User.class), any(CartRequestDto.class));

        // when
        ResponseEntity<String> responseEntity = cartController.addCart(requestDto.getItem().getUser(),requestDto);

        // then
        assertNotNull(responseEntity);
        assertEquals("Success Cart", responseEntity.getBody());

    }

    private CartRequestDto cartRequestDto(){
        User user = User.builder()
                .id(1L)
                .email("1234@naver.com")
                .password("1234")
                .name("lee")
                .phone("010-1234")
                .address("서울시")
                .role(Role.ADMIN)
                .status(Status.EXIST)
                .build();
        Store store = Store.builder()
                .id(1L)
                .user(user)
                .status(Status.EXIST)
                .build();

        Item item = Item.builder()
                .id(1L)
                .user(user)
                .itemName("노트북")
                .price(500000)
                .quantity(5)
                .itemInfo("구형 노트북")
                .category("가전 제품")
                .status(Status.EXIST)
                .store(store)
                .build();

        CartRequestDto req = new CartRequestDto();
        req.setItem(item);
        req.setQuantity(1);
        return req;
    }
}