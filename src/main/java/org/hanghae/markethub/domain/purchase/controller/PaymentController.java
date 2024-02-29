package org.hanghae.markethub.domain.purchase.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.domain.purchase.dto.IamportResponseDto;
import org.hanghae.markethub.domain.purchase.dto.PaymentRequestDto;
import org.hanghae.markethub.domain.purchase.dto.RefundRequestDto;
import org.hanghae.markethub.domain.purchase.service.PurchaseService;
import org.hanghae.markethub.global.security.jwt.JwtUtil;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class PaymentController {

    // test init
    private final PurchaseService purchaseService;
    private final ItemService itemService;
    private final IamportClient iamportClient;
    private final RedissonClient redissonClient; // Redisson 클라이언트 주입
    private final JwtUtil jwtUtil;

    static final String secretKey = "KuT8n5XYtxPTo4c0VoRTQLrZeHJUOsx3h7zBXgrltDcL6yiH7KZ5ulZJVJWPeqRvPxfuE5B7u1G7Ioxc";
    static final String apiKey = "4067753427514612";

    @Autowired
    public PaymentController(PurchaseService purchaseService, ItemService itemService, RedissonClient redissonClient, JwtUtil jwtUtil) {
        this.itemService = itemService;
        this.purchaseService = purchaseService;
        this.redissonClient = redissonClient;
        this.jwtUtil = jwtUtil;
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }


    @PostMapping("/verify")
    public IamportResponse<Payment> paymentByImpUid(@RequestBody PaymentRequestDto paymentRequestDto, HttpServletRequest req) throws IamportResponseException, IOException {
        String email = jwtUtil.getUserEmail(req);
        RLock lock = redissonClient.getFairLock("payment:" + paymentRequestDto.impUid());
        try {
            // 락을 최대 10초 동안 대기하고, 락을 획득하면 최대 5초 동안 유지
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                try {
                    // 비즈니스 로직 처리
                    processPurchase(paymentRequestDto, email);
                    return iamportClient.paymentByImpUid(paymentRequestDto.impUid());
                } finally {
                    lock.unlock(); // 작업 완료 후 락 해제
                }
            } else {
                throw new IllegalStateException("Unable to acquire lock for payment processing");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Lock acquisition interrupted", e);
        }
    }


    private void processPurchase(PaymentRequestDto paymentRequestDto, String email) throws IOException, InterruptedException {
        // DTO에서 impUid를 직접 참조
        String impUid = paymentRequestDto.impUid();

        for (PaymentRequestDto.PurchaseItemDto item : paymentRequestDto.items()) {
            if (itemService.isSoldOut(item.itemId())) {
                handleSoldOut(impUid, paymentRequestDto.amount());
            } else {
                try {
                    itemService.decreaseQuantity(item.itemId(), item.quantity()); // 구매한 수량만큼 재고 감소
                    purchaseService.updateImpUidForPurchases(email, impUid); // purchase 엔티티에 구매 id 저장
                } catch (Exception e) {
                    handleQuantityExceeded(impUid, paymentRequestDto.amount());
                }
            }
        }
        purchaseService.updatePurchaseStatusToOrdered(paymentRequestDto.email());
    }

    private void handleSoldOut(String impUid, double amount) throws IOException {
        cancelPayment(new RefundRequestDto(impUid, amount, "재고가 부족합니다."));
        throw new BadRequestException("재고가 부족합니다");
    }

    private void handleQuantityExceeded(String impUid, double amount) throws IOException{
        cancelPayment(new RefundRequestDto(impUid, amount, "구매 수량이 재고보다 많습니다"));
        throw new IllegalArgumentException("상품의 재고가 부족합니다.");
    }


    //02 24 이부분 수정중이었음 결제취소쪽 서비스로직 작성중
//    @PostMapping("/api/payment/cancel")
//    private boolean cancelPayment(@RequestBody RefundRequestDto refundRequestDto) throws JsonProcessingException {
//        RestTemplate restTemplate = new RestTemplate();
//
//
//
//        String url = "https://api.iamport.kr/payments/cancel";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        String token = getAccessToken(new PaymentRequestDto.getToken(apiKey, secretKey));
//
//        // Authorization 헤더에 토큰을 추가합니다.
//        headers.set("Authorization", "Bearer " + token);
//
//        // 취소 요청 생성
//        HttpEntity<RefundRequestDto> request = new HttpEntity<>(refundRequestDto, headers);
//
//        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
//        System.out.println(request + " : 요청");
//        System.out.println(response.getStatusCode() + " : 응답코드");
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            // 각 아이템에 대해 수량 롤백 진행
//            purchaseService.rollbackItemsQuantity(refundRequestDto.imp_uid());
//            // 주문 상태 변경
//            purchaseService.ChangeStatusToCancelled(refundRequestDto.imp_uid());
//            return true;
//        } else {
//            return false;
//        }
//    }
    @PostMapping("/api/payment/cancel")
    private boolean cancelPayment(@RequestBody RefundRequestDto refundRequestDto) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.iamport.kr/payments/cancel";

        // 요청 파라미터 설정
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("imp_uid", refundRequestDto.imp_uid());
        formData.add("checksum", String.valueOf(refundRequestDto.checksum()));
        formData.add("reason", refundRequestDto.reason());

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String token = getAccessToken(new PaymentRequestDto.getToken(apiKey, secretKey));
        headers.set("Authorization", "Bearer " + token);

        // 요청 객체 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // 요청 보내기
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println(requestEntity + " : 요청");
        System.out.println(response.getStatusCode() + " : 응답코드");

        // 응답 처리
        if (response.getStatusCode() == HttpStatus.OK) {
            // 각 아이템에 대해 수량 롤백 진행
            purchaseService.rollbackItemsQuantity(refundRequestDto.imp_uid());
            // 주문 상태 변경
            purchaseService.ChangeStatusToCancelled(refundRequestDto.imp_uid());
            return true;
        } else {
            return false;
        }
    }

    @PostMapping("/api/payment/token")
    public String getAccessToken(@RequestBody PaymentRequestDto.getToken tokenData) throws RestClientException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.iamport.kr/users/getToken";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("imp_key", tokenData.imp_key());
        formData.add("imp_secret", tokenData.imp_secret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            IamportResponseDto iamportResponseDto = objectMapper.readValue(response.getBody(), IamportResponseDto.class);
            return iamportResponseDto.response().access_token();
        } else {
            throw new HttpClientErrorException(response.getStatusCode(), "Failed to retrieve access token.");
        }
    }
}


