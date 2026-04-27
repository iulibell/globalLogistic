package com.wms.service;

import com.wms.dto.WmsOutboundDto;

import java.util.List;

/**
 * WMS 出库单处理服务接口。
 */
public interface WmsOutboundService {
    /**
     * 创建出库单（由系统侧或门户侧触发）。
     *
     * @param wmsOutboundDto 出库dto
     */
    void createOutbound(WmsOutboundDto wmsOutboundDto);

    /**
     * 仓管确认出库并触发后续物流链路。
     *
     * @param outboundId 出库单id
     */
    void confirmOutbound(String outboundId);

    /**
     * 通过出库单id获取单个出库单(keeper操作)
     *
     * @param outboundId 出库单id
     * @return 出库单
     */
    WmsOutboundDto getOutboundById(String outboundId);

    /**
     * 获取出库单列表(keeper操作)
     *
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 出库单列表
     */
    List<WmsOutboundDto> getOutbound(int pageNum, int pageSize);
}
