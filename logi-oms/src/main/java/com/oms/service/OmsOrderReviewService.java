package com.oms.service;

import com.oms.dto.OmsOrderDto;
import com.oms.dto.OmsOrderReviewDto;

import java.util.List;

/**
 * OMS 特殊订单审核服务接口。
 */
public interface OmsOrderReviewService {

    /**
     * 由电商系统创建特殊商品审核单。
     *
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
     * 通过特殊商品订单请求(reviewer操作)。
     *
     * @param orderId 订单id
     * @param remark  审核备注
     */
    void accessOrderReview(String orderId,String remark);

    /**
     * 退回特殊商品的订单请求(reviewer操作)。
     *
     * @param orderId 订单id
     * @param remark  退回原因
     */
    void rejectOrderReview(String orderId,String remark);
}
