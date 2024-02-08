package org.hanghae.markethub.domain.purchase.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
import org.hanghae.markethub.domain.purchase.dto.PurchaseResponseDto;
import org.hanghae.markethub.domain.purchase.service.PurchaseService;
import org.hanghae.markethub.domain.user.dto.UserResponseDto;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.security.UserDetailsImpl;
import org.hanghae.markethub.global.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/purchase")
public class PurchaseController {



    private final PurchaseService purchaseService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public String showPurchase() {
        return "Purchase";
    }

    // 구매자 정보 조회
    @GetMapping("/buyerInfo")
    public ResponseEntity<UserResponseDto> getBuyerInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User buyer = userDetails.getUser(); // UserDetailsImpl에서 User 가져오기
        UserResponseDto buyerResponseDto = new UserResponseDto(buyer); // UserResponseDto로 변환
        return ResponseEntity.ok(buyerResponseDto);
    }

    // 받는 사람 정보 조회
    @GetMapping("/recipientInfo")
    public ResponseEntity<UserResponseDto> getRecipientInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User recipient = userDetails.getUser(); // UserDetailsImpl에서 User 가져오기
        UserResponseDto recipientResponseDto = new UserResponseDto(recipient); // UserResponseDto로 변환
        return ResponseEntity.ok(recipientResponseDto);
    }


    @PostMapping
    public ResponseEntity<String> createPurchase(@RequestBody PurchaseRequestDto purchaseRequestDto, HttpServletRequest req) {
        String email = jwtUtil.getUserEmail(req);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve user email from token.");
        }
        PurchaseResponseDto purchaseResponseDto = purchaseService.createOrder(purchaseRequestDto, email);
        return ResponseEntity.ok("Purchase created successfully.");
    }

    @PostMapping("/singleBuy")
    public ResponseEntity<String> createSinglePurchase(@RequestBody PurchaseRequestDto.SinglePurchaseRequestDto singlePurchaseRequestDto) {
        try {
            PurchaseResponseDto purchaseResponseDto = purchaseService.createSingleOrder(singlePurchaseRequestDto);
            return ResponseEntity.ok("주문 등록 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e.getMessage());
        }
    }

    @GetMapping("/single")
    public ResponseEntity<?> findPurchaseByEmail(HttpServletRequest req) {
        String email = jwtUtil.getUserEmail(req);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve user email from token.");
        }
        PurchaseResponseDto purchaseResponseDto = purchaseService.findPurchaseByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(purchaseResponseDto);
    }

    @GetMapping("/allPurchase")
    public ResponseEntity<?> findAllPurchaseByEmail(HttpServletRequest req) {
        String email = jwtUtil.getUserEmail(req);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve user email from token.");
        }
        List<PurchaseResponseDto> responseDtoList = purchaseService.findAllPurchaseByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePurchaseById(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.ok("delete successfully");
    }
}
