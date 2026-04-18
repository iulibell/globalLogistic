package com.tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TmsApplication.class,args);
    }
}
