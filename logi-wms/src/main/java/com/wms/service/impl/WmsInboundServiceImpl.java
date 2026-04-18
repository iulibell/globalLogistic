package com.wms.service.impl;

import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.dao.WmsInboundDao;
import com.wms.dao.WmsStockDao;
import com.wms.dto.WmsInboundDto;
import com.wms.entity.WmsInbound;
import com.wms.entity.WmsStock;
import com.wms.service.WmsInboundService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsInboundServiceImpl implements WmsInboundService {

    @Resource
    private WmsInboundDao wmsInboundDao;
    @Resource
    private WmsStockDao wmsStockDao;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public List<WmsInboundDto> getInbound(int pageNum, int pageSize) {
        IPage<WmsInbound> page = new Page<>(pageNum,pageSize);
        wmsInboundDao.selectPage(page,new LambdaQueryWrapper<WmsInbound>()
                .eq(WmsInbound::getStatus,(short)0));
        return page.convert(wmsInbound -> {
            WmsInboundDto wmsInboundDto = new WmsInboundDto();
            BeanUtils.copyProperties(wmsInbound,wmsInboundDto);
            return wmsInboundDto;
        }).getRecords();
    }

    @Override
    public WmsInboundDto getInboundById(String inbound) {
        WmsInboundDto wmsInboundDto = new WmsInboundDto();
        BeanUtils.copyProperties(wmsInboundDao.selectOne(new LambdaQueryWrapper<WmsInbound>()
                .eq(WmsInbound::getInboundId,inbound)),wmsInboundDto);
        return wmsInboundDto;
    }

    @Override
    public void confirmInbound(String inboundId,String skuCode) {
        String stockId = snowflakeIdGenerator.nextId() + skuCode;

        wmsInboundDao.update(new LambdaUpdateWrapper<WmsInbound>()
                .eq(WmsInbound::getInboundId,inboundId)
                .set(WmsInbound::getStatus,(short)1)
                .set(WmsInbound::getSkuCode,skuCode));

        WmsInbound wmsInbound = wmsInboundDao.selectOne(new LambdaQueryWrapper<WmsInbound>()
                .eq(WmsInbound::getInboundId,inboundId));
        WmsStock wmsStock = new WmsStock();
        BeanUtils.copyProperties(wmsInbound,wmsStock);
        wmsStock.setStockId(stockId);
        wmsStockDao.insert(wmsStock);
    }
}
