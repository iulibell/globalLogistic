package com.oms.controller;

import com.api.CommonResult;
import com.oms.service.OmsOrderReviewService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/oms")
public class OmsOrderReviewController {

    @Resource
    private OmsOrderReviewService omsOrderReviewService;

    @GetMapping("/getOrderReview")
    public CommonResult<?> getOrderReview(@RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize){
        return CommonResult.success(omsOrderReviewService.getOrderReview(pageNum,pageSize));
    }

    @PostMapping("/accessOrderReview")
    public CommonResult<?> accessOrderReview(@RequestParam String orderId,
                                             @RequestParam String remark){
        omsOrderReviewService.accessOrderReview(orderId,remark);
        return CommonResult.success("该商品已通过审核");
    }

    @PostMapping("/rejectOrderReview")
    public CommonResult<?> rejectOrderReview(@RequestParam String orderId,
                                             @RequestParam String remark){
        omsOrderReviewService.rejectOrderReview(orderId,remark);
        return CommonResult.success("成功退回该订单申请");
    }
}
