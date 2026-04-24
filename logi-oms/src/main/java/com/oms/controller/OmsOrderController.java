package com.oms.controller;

import com.api.CommonResult;
import com.oms.dto.OmsOrderDto;
import com.oms.service.OmsOrderReviewService;
import com.oms.service.OmsOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oms")
@Tag(name = "OmsOrderController", description = "订单域：电商下单、支付、取消、查询及与审核/超时相关的系统回调。")
public class OmsOrderController {
    @Resource
    private OmsOrderService omsOrderService;
    @Resource
    private OmsOrderReviewService omsOrderReviewService;

    @PostMapping("/sys/addOrder")
    @Operation(
            summary = "创建订单",
            description = "电商侧下单：普通商品走即时单；特殊商品（category=1）进入待审核队列并落库。")
    public CommonResult<?> addOrder(@RequestBody OmsOrderDto omsOrderDto) {
        if (omsOrderDto.getCategory() == (short) 0) {
            omsOrderService.addOrder(omsOrderDto);
            return CommonResult.success("oms_order_submit_wait_review");
        }
        omsOrderReviewService.addOrderView(omsOrderDto);
        return CommonResult.success("oms_order_submit_pay");
    }

    @PostMapping("/sys/deleteOrder")
    @Operation(summary = "删除订单（系统）", description = "超时未支付等场景由系统调用，物理删除或清理订单数据。")
    public void deleteOrder(@RequestParam String orderId) {
        omsOrderService.deleteOrder(orderId);
    }

    @PostMapping("/sys/markOrderPaymentTimeout")
    @Operation(summary = "标记订单支付超时", description = "由调度或消息服务回调，记录订单支付超时状态。")
    public CommonResult<Boolean> markOrderPaymentTimeout(@RequestParam String orderId) {
        return CommonResult.success(omsOrderService.markOrderPaymentTimeout(orderId));
    }

    @GetMapping("/getOrder")
    @Operation(summary = "分页查询我的订单", description = "需登录且具备业务侧权限；返回当前用户可见订单分页。")
    public CommonResult<?> getOrder(@RequestParam(defaultValue = "1") int pageNum,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(omsOrderService.getOrder(pageNum, pageSize));
    }

    @GetMapping("/getOrderById")
    @Operation(summary = "按订单号查询详情", description = "根据 orderId 返回单笔订单 DTO。")
    public CommonResult<?> getOrderById(@RequestParam String orderId) {
        return CommonResult.success(omsOrderService.getOrderById(orderId));
    }

    @GetMapping("/sys/getOrderById")
    @Operation(summary = "系统查询单个订单", description = "系统间调用：根据 orderId 获取单笔订单详情。")
    public CommonResult<?> getOrderByIdForSys(@RequestParam String orderId) {
        return CommonResult.success(omsOrderService.getOrderByIdForSys(orderId));
    }

    @PostMapping("/cancelOrder")
    @Operation(summary = "用户取消订单", description = "买家主动取消未完结订单并释放相关资源。")
    public CommonResult<?> cancelOrder(@RequestParam String orderId) {
        omsOrderService.cancelOrder(orderId);
        return CommonResult.success("oms_order_cancelled");
    }

    @PostMapping("/payForOrder")
    @Operation(summary = "支付成功回调", description = "支付完成后更新订单状态并触发后续履约流程。")
    public CommonResult<?> payForOrder(@RequestParam String orderId) {
        boolean paid = omsOrderService.payForOrder(orderId);
        if (paid) {
            return CommonResult.success("oms_order_paid");
        }
        return CommonResult.success("oms_order_paid_idempotent");
    }
}
