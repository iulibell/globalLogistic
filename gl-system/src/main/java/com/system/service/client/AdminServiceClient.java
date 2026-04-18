package com.system.service.client;

import com.dto.RegisterParamDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("logi-admin")
public interface AdminServiceClient {
    @PostMapping("/admin/reviewer/getRegisterFromSys")
    void getRegisterFromSys(RegisterParamDto registerParamDto);
}
