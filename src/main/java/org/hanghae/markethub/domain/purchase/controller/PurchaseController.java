package org.hanghae.markethub.domain.purchase.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
import org.hanghae.markethub.domain.purchase.dto.PurchaseResponseDto;
import org.hanghae.markethub.domain.purchase.service.PurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase")
public class PurchaseController {


    private final PurchaseService purchaseService;



    @PostMapping("/{userId}")
    public ResponseEntity<String> createPurchase(@PathVariable String userId, PurchaseRequestDto purchaseRequestDto) {
        try {
            PurchaseResponseDto purchaseResponseDto = purchaseService.createOrder(purchaseRequestDto, userId);
            // 정상적으로 처리되었을 때 200 OK 반환
            return ResponseEntity.ok("Purchase created successfully.");
        } catch (Exception e) {
            // 예외 발생 시 500 Internal Server Error 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating purchase: " + e.getMessage());
        }
    }


}

