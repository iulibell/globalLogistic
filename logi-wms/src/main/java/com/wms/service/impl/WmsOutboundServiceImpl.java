package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.dao.WmsOutboundDao;
import com.wms.dto.WmsOutboundDto;
import com.wms.entity.WmsOutbound;
import com.wms.service.WmsOutboundService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsOutboundServiceImpl implements WmsOutboundService {

    @Resource
    private WmsOutboundDao wmsOutboundDao;

    @Override
    public void createOutbound(WmsOutboundDto wmsOutboundDto) {
        WmsOutbound wmsOutbound = new WmsOutbound();
        BeanUtils.copyProperties(wmsOutboundDto,wmsOutbound);
        wmsOutboundDao.insert(wmsOutbound);
    }

    @Override
    public void confirmOutbound(String outboundId) {
        wmsOutboundDao.update(new LambdaUpdateWrapper<WmsOutbound>()
                .eq(WmsOutbound::getOutboundId,outboundId)
                .set(WmsOutbound::getStatus,(short)1));
    }

    @Override
    public WmsOutboundDto getOutboundById(String outboundId) {
        WmsOutboundDto wmsOutboundDto = new WmsOutboundDto();
        BeanUtils.copyProperties(wmsOutboundDao.selectOne(new LambdaQueryWrapper<WmsOutbound>()
                .eq(WmsOutbound::getOutboundId,outboundId)),wmsOutboundDto);
        return wmsOutboundDto;
    }

    @Override
    public List<WmsOutboundDto> getOutbound(int pageNum, int pageSize) {
        IPage<WmsOutbound> page = new Page<>(pageNum,pageSize);
        wmsOutboundDao.selectPage(page,new LambdaQueryWrapper<WmsOutbound>()
                .eq(WmsOutbound::getStatus,(short)0));
        return page.convert(wmsOutbound -> {
            WmsOutboundDto wmsOutboundDto = new WmsOutboundDto();
            BeanUtils.copyProperties(wmsOutbound,wmsOutboundDto);
            return wmsOutboundDto;
        }).getRecords();
    }
}
