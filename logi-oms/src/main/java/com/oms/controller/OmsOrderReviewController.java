package com.oms.controller;

import com.api.CommonResult;
import com.oms.service.OmsOrderReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oms/reviewer")
@Tag(name = "OmsOrderReviewController", description = "特殊商品订单审核：列表、通过（锁库存等）、退回（解锁）。")
public class OmsOrderReviewController {

    @Resource
    private OmsOrderReviewService omsOrderReviewService;

    @GetMapping("/getOrderReview")
    @Operation(
        summary = "商品审核列表",
        description = "获取特殊商品的审核列表；审核员权限。")
    public CommonResult<?> getOrderReview(@RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(omsOrderReviewService.getOrderReview(pageNum, pageSize));
    }

    @PostMapping("/accessOrderReview")
    @Operation(
            summary = "审核通过",
            description = "更新审核与订单状态，锁定 WMS 库存、写入订单缓存并发送 TTL 消息。")
    public CommonResult<?> accessOrderReview(@RequestParam String orderId,
                                             @RequestParam String remark) {
        omsOrderReviewService.accessOrderReview(orderId, remark);
        return CommonResult.success("oms_review_product_passed");
    }

    @PostMapping("/rejectOrderReview")
    @Operation(
            summary = "审核退回",
            description = "拒绝特殊商品订单，解锁 WMS 库存并同步订单/审核状态。")
    public CommonResult<?> rejectOrderReview(@RequestParam String orderId,
                                             @RequestParam String remark) {
        omsOrderReviewService.rejectOrderReview(orderId, remark);
        return CommonResult.success("oms_review_order_returned");
    }
}
