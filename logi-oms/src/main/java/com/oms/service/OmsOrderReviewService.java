package com.oms.service;

import com.oms.dto.OmsOrderDto;
import com.oms.dto.OmsOrderReviewDto;

import java.util.List;

public interface OmsOrderReviewService {

    /**
     * 由电商系统调用
     * @param omsOrderDto 订单商品dto
     */
    void addOrderView(OmsOrderDto omsOrderDto);

    /**
     * 获取特殊订单的审核列表(reviewer操作)
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 特殊订单审核列表
     */
    List<OmsOrderReviewDto> getOrderReview(int pageNum,int pageSize);

    /**
     * 通过特殊商品订单请求(reviewer操作)
     * @param orderId 商品id
     */
    void accessOrderReview(String orderId,String remark);

    /**
     * 退回特殊商品的订单请求(reviewer操作)
     * @param orderId 商品id
     */
    void rejectOrderReview(String orderId,String remark);
}
