package com.system.service;

import com.api.CommonResult;
import com.dto.RegisterParamDto;
import com.system.entity.SysUser;

public interface RegisterService {
    CommonResult<String> register(RegisterParamDto registerParam);
}
