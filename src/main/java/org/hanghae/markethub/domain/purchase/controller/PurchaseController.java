package org.hanghae.markethub.domain.purchase.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
import org.hanghae.markethub.domain.purchase.dto.PurchaseResponseDto;
import org.hanghae.markethub.domain.purchase.service.PurchaseService;
import org.hanghae.markethub.domain.user.dto.UserResponseDto;
import org.hanghae.markethub.domain.user.security.UserDetailsImpl;
import org.hanghae.markethub.global.constant.Status;
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
@Slf4j(topic = "결제")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public String showPurchase() {
        return "Purchase";
    }

    @GetMapping("/checkPurchases")
    public String showOrder() {
        return "completed_purchases";
    }

    // 구매자 정보 조회
    @GetMapping("/buyerInfo")
    public ResponseEntity<UserResponseDto> getBuyerInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserResponseDto buyerResponseDto = new UserResponseDto(userDetails.getUser()); // UserResponseDto로 변환
        return ResponseEntity.ok(buyerResponseDto);
    }

    // 받는 사람 정보 조회
    @GetMapping("/recipientInfo")
    public ResponseEntity<UserResponseDto> getRecipientInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserResponseDto recipientResponseDto = new UserResponseDto(userDetails.getUser()); // UserResponseDto로 변환
        return ResponseEntity.ok(recipientResponseDto);
    }


    //주문 생성
    @PostMapping
    public ResponseEntity<String> createPurchase(@RequestBody PurchaseRequestDto purchaseRequestDto, HttpServletRequest req) {
        String email = jwtUtil.getUserEmail(req);

        if (isEmailValid(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve user email from token.");
        }else if(purchaseRequestDto.status() != Status.EXIST) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품이 존재하지 않습니다.");
        }
        PurchaseResponseDto purchaseResponseDto = purchaseService.createOrder(purchaseRequestDto, email);
        return ResponseEntity.ok("Purchase created successfully.");
    }

    // 엔드포인트가 너무 고민됩니다.
    @PostMapping("/createPurchases")
    public ResponseEntity<String> createPurchaseByCart(@RequestBody List<PurchaseRequestDto> purchaseRequestDtoList, HttpServletRequest req) {

        String email = jwtUtil.getUserEmail(req);
        if (isEmailValid(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve user email from token.");
        }
        purchaseService.createPurchaseByCart(purchaseRequestDtoList, email);
        return ResponseEntity.ok("Purchase created success");
    }
// 어카지 responseEntity 따로처리해야하나? 근데 이건 예외처리가 아니지않나? 예외처린가??? 으어


    @GetMapping("/allPurchase")
    public ResponseEntity<?> findAllPurchaseByEmail(HttpServletRequest req) {
        String email = jwtUtil.getUserEmail(req);
        if (isEmailValid(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve user email from token.");
        }
        List<PurchaseResponseDto> responseDtoList = purchaseService.findAllPurchaseByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    // 단건조회 (주문 조회용)
    @GetMapping("/searchPurchase/{id}")
    public ResponseEntity<?> findPurchaseById(HttpServletRequest req, @PathVariable Long id) {
        String email = jwtUtil.getUserEmail(req);
        if (isEmailValid(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve user email from token.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(purchaseService.findSinglePurchase(id));
    }
    @GetMapping("/searchAllPurchase")
    public ResponseEntity<?> findAllPurchaseByOrderCompleted(HttpServletRequest req){
        String email = jwtUtil.getUserEmail(req);
        if (isEmailValid(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve user email from token.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(purchaseService.findAllOrderedPurchaseByEmail(email));

    }
    private boolean isEmailValid(String email) {
        return email==null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePurchaseById(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.ok("delete successfully");
    }
}
