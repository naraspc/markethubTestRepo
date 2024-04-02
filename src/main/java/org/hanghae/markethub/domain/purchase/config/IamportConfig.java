package org.hanghae.markethub.domain.purchase.config;

import com.siot.IamportRestClient.IamportClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class IamportConfig {

    @Value("${secret.sec.key}")
    private String secretKey ;
    @Value("${api.api.key}")
    private String apiKey ;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(apiKey, secretKey);
    }

    public String getApiKey() {
        return apiKey;
    }
    public String getSecretKey() {
        return secretKey;
    }

}
