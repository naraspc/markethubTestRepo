package org.hanghae.markethub.domain.purchase.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.transaction.Transactional;
import org.hanghae.markethub.domain.item.service.ItemService;
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
    private final ItemService itemService;

    private final IamportClient iamportClient;

    public PaymentController(PurchaseService purchaseService, ItemService itemService) {
        this.itemService = itemService;
        String secretKey = "ds64MnpMwpkI01umV0VR6aJ2yS8dI0KEP9SiscqMv2wbjJdaat37etKq1UyLBgAv0G3QbiNbsJ4iF3Ik";
        String apiKey = "4067753427514612";
        this.purchaseService = purchaseService;
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }


    @Transactional
    @PostMapping("/verify/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid, @RequestBody PaymentRequestDto paymentRequestDto)
            throws IamportResponseException, IOException {
        purchaseService.updatePurchaseStatusToOrdered(paymentRequestDto.email());

        for (PaymentRequestDto.PurchaseItemDto item : paymentRequestDto.items()) {
            itemService.decreaseQuantity(item.itemId(), item.quantity());
        }
        return iamportClient.paymentByImpUid(imp_uid);
    }


}