package org.hanghae.markethub.domain.cart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

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
    void cartSuccess() throws Exception {
        // given
        CartRequestDto requestDto = CartRequestDto.builder()
                        .item(item())
                        .quantity(1)
                        .build();

        lenient().doReturn(ResponseEntity.ok("Success Cart"))
               .when(cartService)
               .addCart(any(User.class),any(CartRequestDto.class));

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
        );

        // then
        perform.andExpect(status().isOk());
    }

    @DisplayName("장바구니 수정 성공")
    @Test
    void cartUpdate() throws Exception{
        // given
        CartRequestDto requestDto = CartRequestDto.builder()
                .item(item())
                .quantity(4)
                .build();

        lenient().doReturn(ResponseEntity.ok("Success Cart"))
                .when(cartService)
                .updateCart(any(User.class),any(CartRequestDto.class),any(Long.class));

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/cart/{cartId}",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
        );

        // then
        perform.andExpect(status().isOk());

    }

    private Item item(){
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

        return Item.builder()
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
    }

}