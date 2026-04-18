package com.admin.controller;

import com.admin.service.ReviewerService;
import com.api.CommonResult;
import com.dto.RegisterParamDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reviewer")
@Tag(name = "ReviewerController", description = "审核员管理接口")
public class ReviewerController {

    @Resource
    private ReviewerService reviewerService;

    @PostMapping("/getRegisterFromSys")
    public void getRegisterFromSys(@Valid @RequestBody RegisterParamDto registerParamDto) {
        reviewerService.getRegisterFromSys(registerParamDto);
    }

    @GetMapping("/fetchRegisterApplication")
    public CommonResult<?> fetchRegisterApplication(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(reviewerService.fetchRegisterApplication(pageNum, pageSize));
    }

    @PostMapping("/accessRegister")
    public CommonResult<?> accessRegister(@Valid @RequestBody RegisterParamDto registerParamDto) {
        reviewerService.accessRegister(registerParamDto);
        return CommonResult.success("该注册申请已通过!");
    }

    @PostMapping("/rejectRegister")
    public CommonResult<?> rejectRegister(@Valid @RequestBody RegisterParamDto registerParamDto) {
        reviewerService.rejectRegister(registerParamDto);
        return CommonResult.success("该注册申请已被退回!");
    }

    @GetMapping("/getOrderReview")
    public CommonResult<?> getOrderReview(@RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(reviewerService.getOrderReview(pageNum, pageSize));
    }

    @PostMapping("/accessOrderReview")
    public CommonResult<?> accessOrderReview(@RequestParam String orderId,
                                             @RequestParam String remark) {
        return reviewerService.accessOrderReview(orderId, remark);
    }

    @PostMapping("/rejectOrderReview")
    public CommonResult<?> rejectOrderReview(@RequestParam String orderId,
                                             @RequestParam String remark) {
        return reviewerService.rejectOrderReview(orderId, remark);
    }
}
