package com.tms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.tms", "com.service", "com.exception"})
@EnableFeignClients
@MapperScan("com.tms.dao")
public class TmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TmsApplication.class,args);
    }
}
