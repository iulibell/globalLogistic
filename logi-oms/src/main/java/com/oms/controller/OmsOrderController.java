package com.oms.controller;

import com.api.CommonResult;
import com.oms.dto.OmsOrderDto;
import com.oms.service.OmsOrderReviewService;
import com.oms.service.OmsOrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oms")
public class OmsOrderController {
    @Resource
    private OmsOrderService omsOrderService;
    @Resource
    private OmsOrderReviewService omsOrderReviewService;

    @PostMapping("/sys/addOrder")
    public CommonResult<?> addOrder(@RequestBody OmsOrderDto omsOrderDto) {
        if (omsOrderDto.getCategory() == (short) 0) {
            omsOrderService.addOrder(omsOrderDto);
            return CommonResult.success("订单发起成功,请等待审核!");
        }
        omsOrderReviewService.addOrderView(omsOrderDto);
        return CommonResult.success("订单发起成功,请尽快完成支付!");
    }

    @PostMapping("/sys/deleteOrder")
    public void deleteOrder(@RequestParam String orderId) {
        omsOrderService.deleteOrder(orderId);
    }

    @PostMapping("/sys/markOrderPaymentTimeout")
    public CommonResult<Boolean> markOrderPaymentTimeout(@RequestParam String orderId) {
        return CommonResult.success(omsOrderService.markOrderPaymentTimeout(orderId));
    }

    @GetMapping("/getOrder")
    public CommonResult<?> getOrder(@RequestParam(defaultValue = "1") int pageNum,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(omsOrderService.getOrder(pageNum, pageSize));
    }

    @GetMapping("/getOrderById")
    public CommonResult<?> getOrderById(@RequestParam String orderId) {
        return CommonResult.success(omsOrderService.getOrderById(orderId));
    }

    @PostMapping("/cancelOrder")
    public CommonResult<?> cancelOrder(@RequestParam String orderId) {
        omsOrderService.cancelOrder(orderId);
        return CommonResult.success("你已取消订单");
    }

    @PostMapping("/payForOrder")
    public CommonResult<?> payForOrder(@RequestParam String orderId) {
        omsOrderService.payForOrder(orderId);
        return CommonResult.success("支付成功!");
    }
}
