//package org.hanghae.markethub.domain.purchase.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
//import org.hanghae.markethub.domain.purchase.dto.PurchaseResponseDto;
//import org.hanghae.markethub.domain.purchase.service.PurchaseService;
//import org.hanghae.markethub.global.constant.Status;
//import org.hanghae.markethub.global.jwt.JwtUtil;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(PurchaseController.class)
//@MockBean(JpaMetamodelMappingContext.class)
//public class PurchaseControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private PurchaseService purchaseService;
//
//    @MockBean
//    private JwtUtil jwtUtil;
//
//    @Test
//    @WithMockUser
//    public void createPurchase_Unauthorized() throws Exception {
//        // Given
//        String tokenValue = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwibmFtZSI6InRlc3RAdGVzdC5jb20iLCJhdXRoIjoiVVNFUiIsImV4cCI6MTcwODQyMTY0NywiaWF0IjoxNzA4MDYxNjQ3fQ.s7NDM2SlhV_BF17Fv80SaFZ5AgbPpa3DddC2pCdpwbg";
//        Cookie authCookie = new Cookie("Authorization", "Bearer " + tokenValue);
//
//        PurchaseRequestDto requestDto = new PurchaseRequestDto(Status.EXIST, "Item Name", 1, 1L, new BigDecimal("100.00"));
//
//        // 토큰 값이 null로 설정되어서 Unauthorized를 예상합니다.
//        given(jwtUtil.getUserEmail(any(HttpServletRequest.class))).willReturn(null);
//
//        // When & Then
//        mockMvc.perform(post("/api/purchase")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                        .cookie(authCookie).with(csrf()))
//                .andExpect(status().isUnauthorized())
//                .andExpect(content().string("Failed to retrieve user email from token."));
//    }
//
//    @Test
//    @WithMockUser
//    public void createPurchase_BadRequest() throws Exception {
//        // Given
//        String tokenValue = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwibmFtZSI6InRlc3RAdGVzdC5jb20iLCJhdXRoIjoiVVNFUiIsImV4cCI6MTcwODQyMTY0NywiaWF0IjoxNzA4MDYxNjQ3fQ.s7NDM2SlhV_BF17Fv80SaFZ5AgbPpa3DddC2pCdpwbg";
//        Cookie authCookie = new Cookie("Authorization", "Bearer " + tokenValue);
//
//        PurchaseRequestDto requestDto = new PurchaseRequestDto(Status.DELETED, "Item Name", 1, 1L, new BigDecimal("100.00"));
//
//        // 토큰 값이 test@test.com으로 설정되어서 BadRequest를 예상합니다.
//        given(jwtUtil.getUserEmail(any(HttpServletRequest.class))).willReturn("test@test.com");
//
//        // When & Then
//        mockMvc.perform(post("/api/purchase")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                        .cookie(authCookie).with(csrf()))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("상품이 존재하지 않습니다."));
//    }
//
//    // 개짜증나네
//    @Test
//    @WithMockUser
//    public void createPurchase_Success() throws Exception {
//        // Given
//        PurchaseResponseDto.ItemDetailsDto itemDetails = new PurchaseResponseDto.ItemDetailsDto(
//                "Item Name",
//                new BigDecimal("100.00"),
//                1,
//                1L
//        );
//
//        PurchaseRequestDto requestDto = new PurchaseRequestDto(Status.EXIST, "Item Name", 1, 1L, new BigDecimal("100.00"));
//
//        // 토큰 값을 쿠키에 저장
//        String tokenValue = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwibmFtZSI6InRlc3RAdGVzdC5jb20iLCJhdXRoIjoiVVNFUiIsImV4cCI6MTcwODQyMTY0NywiaWF0IjoxNzA4MDYxNjQ3fQ.s7NDM2SlhV_BF17Fv80SaFZ5AgbPpa3DddC2pCdpwbg";
//        Cookie authCookie = new Cookie("Authorization", "Bearer " + tokenValue);
//
//        // Given the extracted token, set up the behavior of jwtUtil.getUserEmail()
//        given(jwtUtil.getUserEmail(any(HttpServletRequest.class))).willReturn("test@test.com");
//
//        // When & Then
//        mockMvc.perform(post("/api/purchase")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                        .cookie(authCookie).with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Purchase created successfully."));
//    }
//    @Test
//    @WithMockUser()
//    public void createPurchaseByCart_Success() throws Exception {
//        // Given
//        List<PurchaseRequestDto> purchaseRequestDtoList = new ArrayList<>();
//        PurchaseRequestDto requestDto1 = new PurchaseRequestDto(Status.EXIST, "Item Name 1", 1, 1L, new BigDecimal("100.00"));
//        PurchaseRequestDto requestDto2 = new PurchaseRequestDto(Status.EXIST, "Item Name 2", 2, 2L, new BigDecimal("200.00"));
//        purchaseRequestDtoList.add(requestDto1);
//        purchaseRequestDtoList.add(requestDto2);
//
//        // 쿠키에 토큰 값을 설정
//        String tokenValue = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwibmFtZSI6InRlc3RAdGVzdC5jb20iLCJhdXRoIjoiVVNFUiIsImV4cCI6MTcwODQyMTY0NywiaWF0IjoxNzA4MDYxNjQ3fQ.s7NDM2SlhV_BF17Fv80SaFZ5AgbPpa3DddC2pCdpwbg";
//        Cookie authCookie = new Cookie("Authorization", "Bearer " + tokenValue);
//
//        given(jwtUtil.getUserEmail(any(HttpServletRequest.class))).willReturn("test@test.com");
//
//        // When & Then
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/purchase/createPurchases")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(purchaseRequestDtoList))
//                        .cookie(authCookie)
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Purchase created success"));
//
//        // Verify that purchaseService.createPurchaseByCart() was called with the correct arguments
//        verify(purchaseService, times(1)).createPurchaseByCart(eq(purchaseRequestDtoList), eq("test@test.com"));
//    }
//
//    @Test
//    public void createPurchaseByCart_Unauthorized() throws Exception {
//        // Given
//        List<PurchaseRequestDto> purchaseRequestDtoList = new ArrayList<>();
//        PurchaseRequestDto requestDto1 = new PurchaseRequestDto(Status.EXIST, "Item Name 1", 1, 1L, new BigDecimal("100.00"));
//        PurchaseRequestDto requestDto2 = new PurchaseRequestDto(Status.EXIST, "Item Name 2", 2, 2L, new BigDecimal("200.00"));
//        purchaseRequestDtoList.add(requestDto1);
//        purchaseRequestDtoList.add(requestDto2);
//
//        // 쿠키에 토큰 값이 없음을 가정
//        Cookie authCookie = new Cookie("Authorization", "");
//
//        // When & Then
//        mockMvc.perform(MockMvcRequestBuilders.post("/createPurchases")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(purchaseRequestDtoList))
//                        .cookie(authCookie)
//                        .with(csrf()))
//                .andExpect(status().isUnauthorized())
//                .andExpect(content().string("Failed to retrieve user email from token."));
//
//        // Verify that purchaseService.createPurchaseByCart() was not called
//        verify(purchaseService, never()).createPurchaseByCart(anyList(), anyString());
//    }
//}
