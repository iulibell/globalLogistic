package com.admin.service;

import com.admin.entity.RegisterApplication;
import com.api.CommonResult;
import com.dto.RegisterParamDto;

import java.util.List;

public interface ReviewerService {
    /**
     * 通过注册申请审核(reviewer操作)
     * @param registerParamDto 注册dto
     */
    void accessRegister(RegisterParamDto registerParamDto);

    /**
     * 获取全部注册申请,定时任务进行清理已审核的申请(reviewer操作)
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 注册申请列表
     */
    List<RegisterApplication> fetchRegisterApplication(int pageNum, int pageSize);

    /**
     * 退回注册申请审核(reviewer操作)
     * @param registerParamDto 注册dto
     */
    void rejectRegister(RegisterParamDto registerParamDto);

    /**
     * 从system模块中获取注册dto后插入注册审核表(reviewer操作)
     * @param registerParamDto 注册dto
     */
    void getRegisterFromSys(RegisterParamDto registerParamDto);

    /**
     * 获取订单审核列表
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 订单审核列表
     */
    CommonResult<?> getOrderReview(int pageNum, int pageSize);

    /**
     * 通过订单的审核
     * @param orderId 订单id
     * @param remark 若无备注则无需加
     * @return 结果信息
     */
    CommonResult<?> accessOrderReview(String orderId, String remark);

    /**
     * 订单审核退回
     * @param orderId 订单id
     * @param remark 写明退回的理由
     * @return 结果信息
     */
    CommonResult<?> rejectOrderReview(String orderId, String remark);
}
