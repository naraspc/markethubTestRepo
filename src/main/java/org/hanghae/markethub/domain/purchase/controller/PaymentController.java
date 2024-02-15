package org.hanghae.markethub.domain.purchase.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.domain.purchase.dto.IamportResponseDto;
import org.hanghae.markethub.domain.purchase.dto.PaymentRequestDto;
import org.hanghae.markethub.domain.purchase.dto.RefundRequestDto;
import org.hanghae.markethub.domain.purchase.service.PurchaseService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import retrofit2.HttpException;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

@RestController
public class PaymentController {

    private final PurchaseService purchaseService;
    private final ItemService itemService;
    private final IamportClient iamportClient;
    private final RestTemplate restTemplate;

    String secretKey = "KuT8n5XYtxPTo4c0VoRTQLrZeHJUOsx3h7zBXgrltDcL6yiH7KZ5ulZJVJWPeqRvPxfuE5B7u1G7Ioxc";
    String apiKey = "4067753427514612";

    @Autowired
    public PaymentController(PurchaseService purchaseService, ItemService itemService, RestTemplate restTemplate) {
        this.itemService = itemService;
        this.purchaseService = purchaseService;
        this.restTemplate = restTemplate;
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
                for (PaymentRequestDto.PurchaseItemDto item : paymentRequestDto.items()) {
                    if (itemService.isSoldOut(item.itemId())) {
                        boolean cancelResult = cancelPayment(new RefundRequestDto(imp_uid, paymentRequestDto.amount(), "재고가 부족합니다."));
                        System.out.println("환불 처리 결과(재고 부족): " + cancelResult);
                        throw new BadRequestException("재고가 부족합니다");

                    }
                    try {
                        itemService.decreaseQuantity(item.itemId(), item.quantity());
                    } catch (Exception e) {
                        boolean cancelResult = cancelPayment(new RefundRequestDto(imp_uid, paymentRequestDto.amount(), "구매 수량이 재고보다 많습니다"));
                        System.out.println("환불 처리 결과(구매수량보다 재고가 많아요): " + cancelResult);
                        throw new IllegalArgumentException("상품의 재고가 부족합니다.");
                    }
                }
                purchaseService.updatePurchaseStatusToOrdered(paymentRequestDto.email());
                return iamportClient.paymentByImpUid(imp_uid);
            } finally {
                fairLock.unlock();
                System.out.println("공정락 해제");
            }

        } else {
            throw new RuntimeException("공정락을 획득할 수 없습니다.");
        }
    }

    @PostMapping("/api/payment/cancel")
    private boolean cancelPayment(@RequestBody RefundRequestDto refundRequestDto) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.iamport.kr/payments/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = getAccessToken(new PaymentRequestDto.getToken("4067753427514612", "KuT8n5XYtxPTo4c0VoRTQLrZeHJUOsx3h7zBXgrltDcL6yiH7KZ5ulZJVJWPeqRvPxfuE5B7u1G7Ioxc"));

        // Authorization 헤더에 토큰을 추가합니다.
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<RefundRequestDto> request = new HttpEntity<>(refundRequestDto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        System.out.println(response);
        return response.getStatusCode() == HttpStatus.OK;
    }

    @PostMapping("/api/payment/token")
    public String getAccessToken(@RequestBody PaymentRequestDto.getToken tokenData) {
        String url = "https://api.iamport.kr/users/getToken";


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<PaymentRequestDto.getToken> request = new HttpEntity<>(tokenData, headers);

        ResponseEntity<IamportResponseDto> response = restTemplate.postForEntity(url, request, IamportResponseDto.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            System.out.println("토큰발급완료");
            return response.getBody().response().access_token();
        } else {
            throw new RuntimeException("액세스 토큰을 받아오는데 실패했습니다. 상태 코드: " + response.getStatusCode());
        }
    }


}



