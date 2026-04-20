package com.admin.controller;

import com.admin.service.ReviewerService;
import com.api.CommonResult;
import com.dto.RegisterParamDto;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/admin")
@Tag(name = "ReviewerController", description = "审核员门户：注册申请审核、特殊商品订单审核；含 gl-system 回调写入申请的内部接口。")
public class ReviewerController {

    @Resource
    private ReviewerService reviewerService;

    @PostMapping("/getRegisterFromSys")
    @Operation(
            summary = "接收注册申请（内部回调）",
            description = "由 gl-system 注册流程经 Feign 调用，写入待审核注册记录；路由匿名放行，无用户 token。")
    public void getRegisterFromSys(@Valid @RequestBody RegisterParamDto registerParamDto) {
        reviewerService.getRegisterFromSys(registerParamDto);
    }

    @GetMapping("/reviewer/fetchRegisterApplication")
    @Operation(summary = "分页查询注册申请", description = "审核员权限；返回待处理及历史注册申请列表。")
    public CommonResult<?> fetchRegisterApplication(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(reviewerService.fetchRegisterApplication(pageNum, pageSize));
    }

    @PostMapping("/reviewer/accessRegister")
    @Operation(
            summary = "通过注册申请",
            description = "审核员通过申请：写入 sys_user、清理注册 Redis 并将申请单状态置为已通过。")
    public CommonResult<?> accessRegister(@Valid @RequestBody RegisterParamDto registerParamDto) {
        reviewerService.accessRegister(registerParamDto);
        return CommonResult.success("reviewer_register_approved");
    }

    @PostMapping("/reviewer/rejectRegister")
    @Operation(summary = "拒绝注册申请", description = "审核员驳回申请，仅更新申请单状态为未通过。")
    public CommonResult<?> rejectRegister(@Valid @RequestBody RegisterParamDto registerParamDto) {
        reviewerService.rejectRegister(registerParamDto);
        return CommonResult.success("reviewer_register_rejected");
    }

    @GetMapping("/reviewer/getOrderReview")
    @Operation(summary = "分页查询待审商品订单", description = "审核员权限；转发 OMS 特殊商品审核队列。")
    public CommonResult<?> getOrderReview(@RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(reviewerService.getOrderReview(pageNum, pageSize));
    }

    @PostMapping("/reviewer/accessOrderReview")
    @Operation(
            summary = "商品审核通过",
            description = "审核员通过特殊商品订单；转发 OMS 完成库存锁定、订单状态与消息投递。")
    public CommonResult<?> accessOrderReview(@RequestParam String orderId,
                                             @RequestParam String remark) {
        return reviewerService.accessOrderReview(orderId, remark);
    }

    @PostMapping("/reviewer/rejectOrderReview")
    @Operation(
            summary = "商品审核退回",
            description = "审核员退回特殊商品订单；转发 OMS 解锁库存并更新审核状态。")
    public CommonResult<?> rejectOrderReview(@RequestParam String orderId,
                                             @RequestParam String remark) {
        return reviewerService.rejectOrderReview(orderId, remark);
    }
}
