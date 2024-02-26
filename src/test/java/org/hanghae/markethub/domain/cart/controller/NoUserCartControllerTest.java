//package org.hanghae.markethub.domain.cart.controller;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
//import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
//import org.hanghae.markethub.domain.cart.service.CartRedisService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@ExtendWith(MockitoExtension.class)
//class NoUserCartControllerTest {
//
//    ObjectMapper objectMapper = new ObjectMapper();
//
//    @InjectMocks
//    private NoUserCartController noUserCartController;
//
//    @Mock
//    private CartRedisService cartRedisService;
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    public void init() {
//
//        mockMvc = MockMvcBuilders.standaloneSetup(noUserCartController).build();
//
//    }
//
//
//    @Test
//    void saveRedis() throws Exception {
//        // given
//        List<Long> items = new ArrayList<>();
//        List<Integer> quantities = new ArrayList<>();
//
//        CartRequestDto requestDto = CartRequestDto.builder()
//                .itemId(items)
//                .quantity(quantities)
//                .build();
//
//        doReturn(ResponseEntity.ok("ok"))
//                .when(cartRedisService)
//                .save(any(CartRequestDto.class));
//
//        // when
//        ResultActions perform = mockMvc.perform(
//                MockMvcRequestBuilders.post("/api/carts/nouser")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto))
//        );
//
//        // then
//        perform.andExpect(status().isOk());
//
//    }
//
//    @Test
//    void getAllRedis() throws Exception {
//        // given
//
//        List<CartResponseDto> responseDtos = new ArrayList<>();
//        CartResponseDto cartResponseDto = CartResponseDto
//                .builder().build();
//
//        responseDtos.add(cartResponseDto);
//
//        doReturn(responseDtos)
//                .when(cartRedisService)
//                .getAll();
//
//        // when
//        ResultActions perform = mockMvc.perform(
//                MockMvcRequestBuilders.get("/api/carts/nouser/getAll")
//                        .contentType(MediaType.APPLICATION_JSON)
//        );
//
//        // then
//        perform.andExpect(status().isOk());
//    }
//
//    @Test
//    void deleteCart() throws Exception{
//        // given
//        List<Long> items = new ArrayList<>();
//        List<Integer> quantities = new ArrayList<>();
//
//        CartRequestDto requestDto = CartRequestDto.builder()
//                .itemId(items)
//                .quantity(quantities)
//                .build();
//
//
//        doReturn(ResponseEntity.ok("ok"))
//                .when(cartRedisService)
//                .deleteCart(any(CartRequestDto.class));
//
//        // when
//        ResultActions perform = mockMvc.perform(
//                MockMvcRequestBuilders.delete("/api/carts/nouser")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto))
//        );
//
//        // then
//        perform.andExpect(status().isOk());
//    }
//
//    @Test
//    void updateCart() throws Exception{
//
//        // given
//        List<Long> items = new ArrayList<>();
//        items.add(1L);
//
//        List<Integer> quantities = new ArrayList<>();
//        quantities.add(3);
//
//        CartRequestDto requestDto = CartRequestDto.builder()
//                .itemId(items)
//                .quantity(quantities)
//                .build();
//
//        doReturn(ResponseEntity.ok("ok"))
//                .when(cartRedisService)
//                .updateCart(any(CartRequestDto.class));
//
//        // when
//        ResultActions perform = mockMvc.perform(
//                MockMvcRequestBuilders.patch("/api/carts/nouser")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto))
//        );
//
//        // then
//        perform.andExpect(status().isOk());
//
//    }
//}