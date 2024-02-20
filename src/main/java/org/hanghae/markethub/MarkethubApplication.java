package org.hanghae.markethub;

import org.hanghae.markethub.global.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class MarkethubApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarkethubApplication.class, args);
    }

}
