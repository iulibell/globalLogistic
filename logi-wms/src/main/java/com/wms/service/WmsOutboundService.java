package com.wms.service;

import com.wms.dto.WmsOutboundDto;

import java.util.List;

public interface WmsOutboundService {
    /**
     * 创建出库单
     *
     * @param wmsOutboundDto 出库dto
     */
    void createOutbound(WmsOutboundDto wmsOutboundDto);

    /**
     * 确认出库(keeper操作)
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
