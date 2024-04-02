package org.hanghae.markethub.domain.purchase.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hanghae.markethub.domain.purchase.config.IamportConfig;
import org.hanghae.markethub.global.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    // test init
    private final IamportClient iamportClient;
    private final IamportConfig iamportConfig;

    @GetMapping("/test")
    public void paymentByImpUid(HttpServletRequest req) throws IamportResponseException, IOException {
        System.out.println("========= Start ===========");
        System.out.println(iamportConfig.getSecretKey());
        System.out.println(iamportConfig.getApiKey());
        System.out.println(iamportConfig.iamportClient());
        IamportConfig iamportConfig1 = new IamportConfig();
        System.out.println(iamportConfig1);
        Test test = new Test(iamportConfig.getSecretKey());
        System.out.println(test);
        System.out.println("========= End ===========");

    }


}


