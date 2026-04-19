package com.system.service.client;

import com.dto.RegisterParamDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("logi-admin")
public interface AdminServiceClient {
    @PostMapping("/admin/getRegisterFromSys")
    void getRegisterFromSys(@RequestBody RegisterParamDto registerParamDto);
}
