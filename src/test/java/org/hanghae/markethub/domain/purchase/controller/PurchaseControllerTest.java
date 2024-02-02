package org.hanghae.markethub.domain.purchase.controller;


import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
import org.hanghae.markethub.domain.purchase.dto.PurchaseResponseDto;
import org.hanghae.markethub.domain.purchase.service.PurchaseService;
import org.hanghae.markethub.global.constant.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;


@WebMvcTest(PurchaseController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PurchaseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseService purchaseService;

    @Test
    @DisplayName("주문 생성 200.OK 확인")
    public void 주문생성확인() throws Exception {
        PurchaseRequestDto requestDto = new PurchaseRequestDto(Status.EXIST); // Assuming Status.PENDING is valid
        PurchaseResponseDto responseDto = new PurchaseResponseDto(1L, Status.EXIST, Collections.emptyList(), null);

        given(purchaseService.createOrder(any(PurchaseRequestDto.class), anyString())).willReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/purchase/{userId}", "user123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Purchase created successfully."));
    }

    @Test
    @DisplayName("주문 생성 예외처리 테스트")
    public void 주문생성예외처리확인() throws Exception {
        PurchaseRequestDto requestDto = new PurchaseRequestDto(Status.EXIST);

        given(purchaseService.createOrder(any(PurchaseRequestDto.class), anyString())).willThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/purchase/{userId}", "user123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(containsString("Error creating purchase")));
    }

}