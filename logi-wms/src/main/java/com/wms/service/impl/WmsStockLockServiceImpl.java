package com.wms.service.impl;

import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.exception.Assert;
import com.wms.dao.WmsStockDao;
import com.wms.dao.WmsStockLockDao;
import com.wms.dto.OmsOrderDto;
import com.wms.dto.WmsOutboundDto;
import com.wms.entity.WmsStock;
import com.wms.entity.WmsStockLock;
import com.wms.service.WmsOutboundService;
import com.wms.service.WmsStockLockService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class WmsStockLockServiceImpl implements WmsStockLockService {
    @Resource
    private WmsStockDao wmsStockDao;
    @Resource
    private WmsStockLockDao wmsStockLockDao;
    @Resource
    private WmsOutboundService wmsOutboundService;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public void WmsStockLock(OmsOrderDto omsOrderDto) {
        WmsStock stock = wmsStockDao.selectOne(new LambdaQueryWrapper<WmsStock>()
                .eq(WmsStock::getStockId, omsOrderDto.getGoodsId()));
        if (stock == null) {
            Assert.fail("wms_stock_not_found");
        }
        if (stock.getStockQuantity() == null || stock.getStockQuantity() - omsOrderDto.getQuantity() < 0)
            Assert.fail("wms_stock_insufficient");

        int rows = wmsStockDao.update(new LambdaUpdateWrapper<WmsStock>()
                .eq(WmsStock::getStockId, omsOrderDto.getGoodsId())
                .setSql("lock_quantity = lock_quantity + {0}", omsOrderDto.getQuantity())
                .setSql("stock_quantity = stock_quantity - {0}", omsOrderDto.getQuantity()));
        if (rows == 0) {
            Assert.fail("wms_stock_lock_failed");
        }

        WmsStockLock wmsStockLock = new WmsStockLock();
        BeanUtils.copyProperties(omsOrderDto, wmsStockLock);
        wmsStockLock.setLockQuantity(omsOrderDto.getQuantity());
        wmsStockLock.setOrderId(omsOrderDto.getOrderId());
        wmsStockLock.setStockId(omsOrderDto.getGoodsId());
        wmsStockLockDao.insert(wmsStockLock);
    }

    @Override
    public void WmsStockUnlock(String orderId) {
        String outboundId = String.valueOf(snowflakeIdGenerator.nextId()
                + orderId.indexOf(1,5));

        WmsStockLock wmsStockLock = wmsStockLockDao.selectOne(new LambdaQueryWrapper<WmsStockLock>()
                .eq(WmsStockLock::getOrderId,orderId));

        wmsStockLockDao.update(new LambdaUpdateWrapper<WmsStockLock>()
                .eq(WmsStockLock::getOrderId,orderId)
                .set(WmsStockLock::getStatus,(short)0));

        wmsStockDao.update(new LambdaUpdateWrapper<WmsStock>()
                .eq(WmsStock::getStockId,wmsStockLock.getStockId())
                .setSql("lock_quantity = lock_quantity - {0}",wmsStockLock.getLockQuantity()));

        WmsOutboundDto wmsOutboundDto = new WmsOutboundDto();
        BeanUtils.copyProperties(wmsStockLock,wmsOutboundDto);
        wmsOutboundDto.setOutboundId(outboundId);
        // 新建出库单一律置为待出库，必须由仓管确认后才能完成出库。
        wmsOutboundDto.setStatus((short) 0);

        wmsOutboundService.createOutbound(wmsOutboundDto);
    }
}
