package org.hanghae.markethub.domain.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.service.CartService;
import org.hanghae.markethub.domain.user.entity.User;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
    public void init() {

        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

    }

    @DisplayName("장바구니 추가 controller 연결 성공")
    @Test
    void cartSuccess() throws Exception {
        // given
        List<Long> items = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        CartRequestDto requestDto = CartRequestDto.builder()
                .itemId(items)
                .quantity(quantities)
                .build();

        lenient().doReturn(ResponseEntity.ok("Success Cart"))
                .when(cartService)
                .addCart(any(User.class), any(CartRequestDto.class));

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
        );

        // then
        perform.andExpect(status().isOk());
    }

    @DisplayName("장바구니 수정 controller 연결 성공")
    @Test
    void cartUpdate() throws Exception {
        // given

        List<Long> items = new ArrayList<>();
        items.add(1L);

        List<Integer> quantities = new ArrayList<>();
        quantities.add(3);

        CartRequestDto requestDto = CartRequestDto.builder()
                .itemId(items)
                .quantity(quantities)
                .build();

        List<CartResponseDto> responseDtos = new ArrayList<>();
        CartResponseDto responseDto = CartResponseDto.builder().build();
        responseDtos.add(responseDto);

        lenient().doReturn(responseDtos)
                .when(cartService)
                .updateCart(any(User.class), any(CartRequestDto.class), anyLong());

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/carts/{cartId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
        );

        // then
        perform.andExpect(status().isOk());

    }

    @DisplayName("장바구니 전체 조회 controller 연결 성공")
    @Test
    void getCartsController() throws Exception {
        // given

        List<CartResponseDto> responseDtos = new ArrayList<>();
        CartResponseDto cartResponseDto = CartResponseDto
                .builder().build();

        responseDtos.add(cartResponseDto);

        lenient().doReturn(responseDtos)
                .when(cartService)
                .getCarts(any(User.class));

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/carts")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform.andExpect(status().isOk());
    }

    @DisplayName("장바구니 수정 controller 연결 성공")
    @Test
    void cartDelete() throws Exception {
        // given
        List<Long> items = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        CartRequestDto requestDto = CartRequestDto.builder()
                .itemId(items)
                .quantity(quantities)
                .build();

        List<CartResponseDto> responseDtos = new ArrayList<>();
        CartResponseDto cartResponseDto = CartResponseDto
                .builder().build();

        responseDtos.add(cartResponseDto);

        lenient().doReturn(responseDtos)
                .when(cartService)
                .deleteCart(any(User.class), any(Long.class));

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/carts/{cartId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
        );

        // then
        perform.andExpect(status().isOk());

    }

}