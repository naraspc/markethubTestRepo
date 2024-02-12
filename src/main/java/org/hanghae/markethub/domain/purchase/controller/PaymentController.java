package org.hanghae.markethub.domain.purchase.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.hanghae.markethub.domain.purchase.dto.PaymentRequestDto;
import org.hanghae.markethub.domain.purchase.service.PurchaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentController {

    private final PurchaseService purchaseService;

    private final IamportClient iamportClient;

    public PaymentController(PurchaseService purchaseService) {
        String secretKey = "o8bu8THHgN3JyX3nKOm63EeQtiSEvkk5nLUDpbH0DjVoz9NgN64c4cQY1zXMXgl628DqM5KnYJ7nEFY4";
        String apiKey = "4067753427514612";
        this.purchaseService = purchaseService;
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }


    @PostMapping("/verify/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid, @RequestBody PaymentRequestDto paymentRequestDto)
            throws IamportResponseException, IOException {
        purchaseService.updatePurchaseStatusToOrdered(paymentRequestDto.email());
        return iamportClient.paymentByImpUid(imp_uid);
    }


}