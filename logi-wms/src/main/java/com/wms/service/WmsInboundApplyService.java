package com.wms.service;

import com.wms.dto.WmsInboundApplyDto;

import java.math.BigDecimal;
import java.util.List;

public interface WmsInboundApplyService {
    /**
     * 商家申请入库
     * @param wmsInboundApplyDto 入库申请dto
     */
    void addInboundApply(WmsInboundApplyDto wmsInboundApplyDto);

    /**
     * 获取入库申请列表(keeper操作)
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 入库申请列表
     */
    List<WmsInboundApplyDto> getInboundApply(int pageNum, int pageSize);

    /**
     * 根据入库申请id获取单个入库申请(keeper操作)
     * @param applyId 入库申请id
     * @return 入库申请单
     */
    WmsInboundApplyDto getInboundApplyById(String applyId);

    /**
     * 通过入库申请审核(keeper操作)
     * @param applyId 申请id
     */
    void accessInboundApply(String applyId, BigDecimal fee);

    /**
     * 退回入库申请(keeper操作)
     * @param applyId 申请id
     * @param remark 回退理由
     */
    void rejectInboundApply(String applyId, String remark);

    /**
     * 商家支付后进行插入数据,并准备分配司机
     * @param applyId 申请id
     */
    void payForInbound(String applyId);

    /**
     * 支付超时：仅当申请仍为「待支付(2)」时置为「超时未支付(3)」。已支付、已超时、已删除则不修改。
     *
     * @param applyId 申请id
     * @return 本次是否将状态更新为 3
     */
    boolean markInboundApplyPaymentTimeout(String applyId);
}
