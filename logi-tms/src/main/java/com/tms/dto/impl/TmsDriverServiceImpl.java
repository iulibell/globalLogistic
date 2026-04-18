package com.tms.dto.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.constant.RedisConstant;
import com.exception.Assert;
import com.service.RedisService;
import com.tms.dao.TmsDriverDao;
import com.tms.dao.TmsLogisticDao;
import com.tms.dao.TmsTransportOrderDao;
import com.tms.dao.TmsVehicleDao;
import com.tms.dto.TmsTransportOrderDto;
import com.tms.entity.TmsDriver;
import com.tms.entity.TmsLogistic;
import com.tms.entity.TmsTransportOrder;
import com.tms.entity.TmsVehicle;
import com.tms.service.TmsDriverService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TmsDriverServiceImpl implements TmsDriverService {

    @Resource
    private TmsTransportOrderDao tmsTransportOrderDao;
    @Resource
    private TmsLogisticDao tmsLogisticDao;
    @Resource
    private TmsDriverDao tmsDriverDao;
    @Resource
    private TmsVehicleDao tmsVehicleDao;
    @Resource
    private RedisService redisService;

    @Override
    public List<TmsTransportOrderDto> getTransportOrder(int pageNum, int pageSize, String currentCity) {
        IPage<TmsTransportOrder> page = new Page<>(pageNum, pageSize);
        tmsTransportOrderDao.selectPage(page, new LambdaQueryWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getOrigin, currentCity)
                .eq(TmsTransportOrder::getStatus, (short) 0));
        return page.convert(tmsTransportOrder -> {
            TmsTransportOrderDto tmsTransportOrderDto = new TmsTransportOrderDto();
            BeanUtils.copyProperties(tmsTransportOrder, tmsTransportOrderDto);
            return tmsTransportOrderDto;
        }).getRecords();
    }

    @Override
    @Transactional
    public boolean accessAssignment(String transportOrderId, String driverId) {
        if (tmsVehicleDao.selectOne(new LambdaQueryWrapper<TmsVehicle>()
                .eq(TmsVehicle::getDriverId, driverId)) == null) {
            Assert.fail("请完善货车的信息");
        }
        int grabbed = tmsTransportOrderDao.update(new LambdaUpdateWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId)
                .eq(TmsTransportOrder::getStatus, (short) 0)
                .set(TmsTransportOrder::getDriverId, driverId)
                .set(TmsTransportOrder::getStatus, (short) 1));
        if (grabbed == 0) {
            return false;
        }
        int driverUpdated = tmsDriverDao.update(new LambdaUpdateWrapper<TmsDriver>()
                .eq(TmsDriver::getUserId, driverId)
                .set(TmsDriver::getStatus, (short) 1)
                .setSql("weight = COALESCE(weight, 0) + 1"));
        if (driverUpdated == 0) {
            Assert.fail("司机不存在或状态更新失败");
        }
        redisService.delete(RedisConstant.ASSIGN_KEY_PREFIX + transportOrderId);
        return true;
    }

    @Override
    public void confirmDeparture(String transportOrderId, String city) {
        updateOrderStatusIf(transportOrderId, (short) 1, (short) 2, "仅已接单状态可确认发车");
        TmsTransportOrder order = tmsTransportOrderDao.selectOne(new LambdaQueryWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId));
        if (order == null) {
            Assert.fail("运输单不存在");
        }
        TmsLogistic tmsLogistic = new TmsLogistic();
        BeanUtils.copyProperties(order, tmsLogistic);
        tmsLogistic.setCity(city);
        tmsLogisticDao.insert(tmsLogistic);
    }

    @Override
    public void confirmArrived(String transportOrderId) {
        updateOrderStatusIf(transportOrderId, (short) 2, (short) 3, "仅运输中状态可确认送达");
    }

    @Override
    public void confirmReceived(String transportOrderId) {
        updateOrderStatusIf(transportOrderId, (short) 3, (short) 4, "仅已送达状态可确认签收");
    }

    private void updateOrderStatusIf(String transportOrderId, short expectedStatus, short newStatus, String illegalMessage) {
        int rows = tmsTransportOrderDao.update(new LambdaUpdateWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId)
                .eq(TmsTransportOrder::getStatus, expectedStatus)
                .set(TmsTransportOrder::getStatus, newStatus));
        if (rows == 0) {
            Assert.fail(illegalMessage);
        }
    }
}
