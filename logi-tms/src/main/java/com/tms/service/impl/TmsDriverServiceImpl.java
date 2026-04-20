package com.tms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.constant.RedisConstant;
import com.exception.Assert;
import com.service.RedisService;
import com.tms.dao.*;
import com.tms.dto.TmsDriverDto;
import com.tms.dto.TmsLineDto;
import com.tms.dto.TmsTransportOrderDto;
import com.tms.entity.*;
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
    private TmsLineDao tmsLineDao;
    @Resource
    private TmsVehicleDao tmsVehicleDao;
    @Resource
    private RedisService redisService;

    @Override
    public List<TmsTransportOrderDto> getTransportOrder(int pageNum, int pageSize, String currentCity) {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
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
    public List<TmsDriverDto> getAvailableDriver(int pageNum, int pageSize) {
        IPage<TmsDriver> page = new Page<>(pageNum,pageSize);
        tmsDriverDao.selectPage(page,new LambdaQueryWrapper<TmsDriver>()
                .eq(TmsDriver::getStatus,(short)0));
        return page.convert(tmsDriver -> {
            TmsDriverDto tmsDriverDto = new TmsDriverDto();
            BeanUtils.copyProperties(tmsDriver,tmsDriverDto);
            return tmsDriverDto;
        }).getRecords();
    }

    @Override
    @Transactional
    public boolean accessAssignment(String transportOrderId, String driverId) {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        if (tmsVehicleDao.selectOne(new LambdaQueryWrapper<TmsVehicle>()
                .eq(TmsVehicle::getDriverId, driverId)) == null) {
            Assert.fail("tms_vehicle_incomplete");
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
            Assert.fail("tms_driver_update_failed");
        }
        redisService.delete(RedisConstant.ASSIGN_KEY_PREFIX + transportOrderId);
        return true;
    }

    @Override
    public void confirmDeparture(String transportOrderId, String city) {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        updateOrderStatusIf(transportOrderId, (short) 1, (short) 2, "tms_status_departure_invalid");
        TmsTransportOrder order = tmsTransportOrderDao.selectOne(new LambdaQueryWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId));
        if (order == null) {
            Assert.fail("tms_order_not_found");
        }
        TmsLogistic tmsLogistic = new TmsLogistic();
        BeanUtils.copyProperties(order, tmsLogistic);
        tmsLogistic.setCity(city);
        tmsLogisticDao.insert(tmsLogistic);
    }

    @Override
    public void confirmArrived(String transportOrderId) {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        updateOrderStatusIf(transportOrderId, (short) 2, (short) 3, "tms_status_arrived_invalid");
    }

    @Override
    public void confirmReceived(String transportOrderId) {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        updateOrderStatusIf(transportOrderId, (short) 3, (short) 4, "tms_status_received_invalid");
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
