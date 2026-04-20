package com.wms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
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

import java.util.Date;
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
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
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
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        WmsInboundDto wmsInboundDto = new WmsInboundDto();
        BeanUtils.copyProperties(wmsInboundDao.selectOne(new LambdaQueryWrapper<WmsInbound>()
                .eq(WmsInbound::getInboundId,inbound)),wmsInboundDto);
        return wmsInboundDto;
    }

    @Override
    public void confirmInbound(String inboundId,String skuCode) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();

        wmsInboundDao.update(new LambdaUpdateWrapper<WmsInbound>()
                .eq(WmsInbound::getInboundId,inboundId)
                .set(WmsInbound::getStatus,(short)1)
                .set(WmsInbound::getSkuCode,skuCode));

        WmsInbound wmsInbound = wmsInboundDao.selectOne(new LambdaQueryWrapper<WmsInbound>()
                .eq(WmsInbound::getInboundId,inboundId));
        WmsStock existedStock = wmsStockDao.selectOne(new LambdaQueryWrapper<WmsStock>()
                .eq(WmsStock::getWarehouseId, wmsInbound.getWarehouseId())
                .eq(WmsStock::getLocationId, wmsInbound.getLocationId())
                .eq(WmsStock::getSkuCode, skuCode));

        if (existedStock != null) {
            int oldQty = existedStock.getStockQuantity() == null ? 0 : existedStock.getStockQuantity();
            int inboundQty = wmsInbound.getQuantity() == null ? 0 : wmsInbound.getQuantity();
            wmsStockDao.update(new LambdaUpdateWrapper<WmsStock>()
                    .eq(WmsStock::getId, existedStock.getId())
                    .set(WmsStock::getStockQuantity, oldQty + inboundQty)
                    .set(WmsStock::getUpdateTime, new Date()));
            return;
        }

        WmsStock wmsStock = new WmsStock();
        // 只拷贝业务字段，避免将 wms_inbound 的主键 id 误拷贝到 wms_stock。
        wmsStock.setStockId(snowflakeIdGenerator.nextId() + skuCode);
        wmsStock.setWarehouseId(wmsInbound.getWarehouseId());
        wmsStock.setLocationId(wmsInbound.getLocationId());
        wmsStock.setSkuName(wmsInbound.getSkuName());
        wmsStock.setSkuCode(skuCode);
        wmsStock.setStockQuantity(wmsInbound.getQuantity() == null ? 0 : wmsInbound.getQuantity());
        wmsStock.setLockQuantity(0);
        wmsStock.setCreateTime(new Date());
        wmsStock.setUpdateTime(new Date());
        wmsStockDao.insert(wmsStock);
    }
}
