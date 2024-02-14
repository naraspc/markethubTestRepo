package org.hanghae.markethub.domain.purchase.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.transaction.Transactional;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.domain.purchase.dto.PaymentRequestDto;
import org.hanghae.markethub.domain.purchase.service.PurchaseService;
import org.redisson.Redisson;
import org.redisson.RedissonFairLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class PaymentController {

    private final PurchaseService purchaseService;
    private final ItemService itemService;

    private final IamportClient iamportClient;

    public PaymentController(PurchaseService purchaseService, ItemService itemService ) {
        this.itemService = itemService;
        String secretKey = "ds64MnpMwpkI01umV0VR6aJ2yS8dI0KEP9SiscqMv2wbjJdaat37etKq1UyLBgAv0G3QbiNbsJ4iF3Ik";
        String apiKey = "4067753427514612";
        this.purchaseService = purchaseService;
        this.iamportClient = new IamportClient(apiKey, secretKey);

    }



    @Transactional
    @PostMapping("/verify/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid, @RequestBody PaymentRequestDto paymentRequestDto)
            throws IamportResponseException, IOException, InterruptedException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        // Redisson 클라이언트 생성
        RedissonClient redisson = Redisson.create(config);

        // 공정락(Fair Lock) 사용 예제
        RLock fairLock = redisson.getFairLock("myFairLock");
        fairLock.lock(10, TimeUnit.SECONDS);
        boolean res = fairLock.tryLock(100, 10, TimeUnit.SECONDS);

        if (res) {
            try {
                System.out.println("공정락 획득");
                purchaseService.updatePurchaseStatusToOrdered(paymentRequestDto.email());

                for (PaymentRequestDto.PurchaseItemDto item : paymentRequestDto.items()) {
                    itemService.decreaseQuantity(item.itemId(), item.quantity());
                }
                return iamportClient.paymentByImpUid(imp_uid);
            } finally {
                fairLock.unlock();
                System.out.println("공정락 해제");
            }

        } else {
            throw new RuntimeException("공정락을 획득할 수 없습니다.");
        }
    }
}



