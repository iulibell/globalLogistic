package com.admin.controller;

import com.admin.service.ManagerService;
import com.api.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/manager")
public class ManagerController {
    @Resource
    private ManagerService managerService;

    @GetMapping("/getOrder")
    public CommonResult<?> getOrder(@RequestParam(defaultValue = "1") int pageNum,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(managerService.getOrder(pageNum, pageSize));
    }

    @GetMapping("/getOrderById")
    public CommonResult<?> getOrderById(@RequestParam String orderId) {
        return CommonResult.success(managerService.getOrderById(orderId));
    }
}
