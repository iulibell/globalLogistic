package com.tms.service;

import com.tms.dto.TmsLineDto;

import java.util.List;

public interface TmsLineService {
    /**
     * 通过id获取单条运输路线(manager操作)
     *
     * @param id 运输路线id
     * @return 路线dto
     */
    TmsLineDto getLineById(Long id);

    /**
     * 获取路线列表(manager操作)
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 路线列表
     */
    List<TmsLineDto> getLine(int pageNum, int pageSize);

    /**
     * 添加路线(manager操作)
     * @param tmsLineDto 路线dto
     */
    void addLine(TmsLineDto tmsLineDto);

    /**
     * 更新路线(manager操作)
     * @param tmsLineDto 路线dto
     */
    void updateLine(TmsLineDto tmsLineDto);
}
