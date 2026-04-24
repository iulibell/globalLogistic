package com.oms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.oms", "com.service", "com.exception"},exclude = {
        org.apache.seata.spring.boot.autoconfigure.SeataSpringFenceAutoConfiguration.class
})
@MapperScan("com.oms.dao")
@EnableFeignClients(basePackages = "com.oms.service.client")

public class OmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OmsApplication.class, args);
    }
}
