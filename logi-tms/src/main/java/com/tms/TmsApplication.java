package com.tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.tms", "com.service", "com.exception"})
@EnableFeignClients
public class TmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TmsApplication.class,args);
    }
}
