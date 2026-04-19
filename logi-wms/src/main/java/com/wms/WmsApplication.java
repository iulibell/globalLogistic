package com.wms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.wms", "com.service", "com.exception"})
@EnableFeignClients
public class WmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(WmsApplication.class,args);
    }
}
