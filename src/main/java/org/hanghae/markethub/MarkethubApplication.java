package org.hanghae.markethub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class MarkethubApplication {

    public static void main(String[] args) {
            try{
                SpringApplication.run(MarkethubApplication.class, args);
            }catch(Exception e){
                e.printStackTrace();
            }

    }
}


