package com.wms.service;

import com.wms.dto.WmsInboundDto;

import java.util.List;

public interface WmsInboundService {
    /**
     * 获取待入库单列表(keeper操作)
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 入库单列表
     */
    List<WmsInboundDto> getInbound(int pageNum, int pageSize);

    /**
     * 获取单个入库单
     * @param inbound 入库单id
     * @return 入库单
     */
    WmsInboundDto getInboundById(String inbound);

    /**
     * 确认入库(keeper操作)
     * @param inboundId 入库单id
     * @param skuCode 商品编号(需keeper手动录入)
     */
    void confirmInbound(String inboundId, String skuCode, Long locationId, Short category);
}
