package com.wms.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient("mall-admin")
public interface MallAdminServiceClient {
    @PostMapping("/admin/sys/accessFromWms")
    void accessFromWms(@RequestParam String applyId,
                       @RequestParam BigDecimal fee);
}
