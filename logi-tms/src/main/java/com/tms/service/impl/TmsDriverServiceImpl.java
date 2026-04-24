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
import com.tms.dto.TmsAssignedOrderDto;
import com.tms.dto.TmsCurrentAssignmentDto;
import com.tms.dto.TmsDriverDto;
import com.tms.dto.TmsTransportOrderDto;
import com.tms.entity.TmsAssignedOrder;
import com.tms.entity.TmsDriver;
import com.tms.entity.TmsLogistic;
import com.tms.entity.TmsTransportOrder;
import com.tms.entity.TmsVehicle;
import com.tms.service.TmsDriverService;
import com.tms.service.TmsTransportOrderService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    private TmsAssignedOrderDao tmsAssignedOrderDao;
    @Resource
    private RedisService redisService;
    @Resource
    private TmsTransportOrderService tmsTransportOrderService;

    @Override
    public List<TmsTransportOrderDto> getTransportOrder(int pageNum, int pageSize) {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        String driverId = StpUtil.getLoginIdAsString();
        TmsDriver currentDriver = tmsDriverDao.selectOne(new LambdaQueryWrapper<TmsDriver>()
                .eq(TmsDriver::getUserId, driverId)
                .last("limit 1"));
        String currentCity = currentDriver == null ? null : currentDriver.getCurrentCity();
        if (currentCity == null || currentCity.trim().isEmpty()) {
            return Collections.emptyList();
        }
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
    public TmsDriverDto getCurrentDriverInfo() {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        String driverId = StpUtil.getLoginIdAsString();
        TmsDriver currentDriver = tmsDriverDao.selectOne(new LambdaQueryWrapper<TmsDriver>()
                .eq(TmsDriver::getUserId, driverId)
                .orderByDesc(TmsDriver::getId)
                .last("limit 1"));
        if (currentDriver == null) {
            return null;
        }
        TmsDriverDto dto = new TmsDriverDto();
        BeanUtils.copyProperties(currentDriver, dto);
        return dto;
    }

    @Override
    public List<TmsAssignedOrderDto> getPendingAssignedOrders() {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        String driverId = StpUtil.getLoginIdAsString();
        IPage<TmsAssignedOrder> page = new Page<>(1, 20);
        tmsAssignedOrderDao.selectPage(page, new LambdaQueryWrapper<TmsAssignedOrder>()
                .eq(TmsAssignedOrder::getDriverId, driverId)
                .eq(TmsAssignedOrder::getStatus, (short) 0)
                .orderByDesc(TmsAssignedOrder::getId));
        return page.convert(assignedOrder -> {
            TmsAssignedOrderDto dto = new TmsAssignedOrderDto();
            BeanUtils.copyProperties(assignedOrder, dto);
            return dto;
        }).getRecords();
    }

    @Override
    public List<TmsAssignedOrderDto> getCurrentAssignedOrders() {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        String driverId = StpUtil.getLoginIdAsString();
        IPage<TmsAssignedOrder> page = new Page<>(1, 50);
        tmsAssignedOrderDao.selectPage(page, new LambdaQueryWrapper<TmsAssignedOrder>()
                .eq(TmsAssignedOrder::getDriverId, driverId)
                .eq(TmsAssignedOrder::getStatus, (short) 2)
                .orderByDesc(TmsAssignedOrder::getId));
        return page.convert(assignedOrder -> {
            TmsAssignedOrderDto dto = new TmsAssignedOrderDto();
            BeanUtils.copyProperties(assignedOrder, dto);
            return dto;
        }).getRecords();
    }

    @Override
    public TmsCurrentAssignmentDto getCurrentAssignmentDetail() {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        String driverId = StpUtil.getLoginIdAsString();
        TmsTransportOrder order = tmsTransportOrderDao.selectOne(new LambdaQueryWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getDriverId, driverId)
                .in(TmsTransportOrder::getStatus, (short) 1, (short) 2, (short) 3)
                .orderByDesc(TmsTransportOrder::getId)
                .last("limit 1"));
        if (order == null) {
            return null;
        }
        TmsCurrentAssignmentDto dto = new TmsCurrentAssignmentDto();
        BeanUtils.copyProperties(order, dto);
        return dto;
    }

    @Override
    public boolean acceptAssignedOrder(String transportId) {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        String driverId = StpUtil.getLoginIdAsString();
        int n = tmsAssignedOrderDao.update(null, new LambdaUpdateWrapper<TmsAssignedOrder>()
                .eq(TmsAssignedOrder::getTransportId, transportId)
                .eq(TmsAssignedOrder::getDriverId, driverId)
                .eq(TmsAssignedOrder::getStatus, (short) 0)
                .set(TmsAssignedOrder::getStatus, (short) 2));
        return n > 0;
    }

    @Override
    @Transactional
    public boolean rejectAssignedOrder(String transportId) {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        String driverId = StpUtil.getLoginIdAsString();
        boolean ok = tmsTransportOrderService.recordDriverReject(transportId, driverId);
        if (!ok) {
            return false;
        }
        tmsAssignedOrderDao.update(null, new LambdaUpdateWrapper<TmsAssignedOrder>()
                .eq(TmsAssignedOrder::getTransportId, transportId)
                .eq(TmsAssignedOrder::getDriverId, driverId)
                .eq(TmsAssignedOrder::getStatus, (short) 0)
                .set(TmsAssignedOrder::getStatus, (short) 1));
        return true;
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
        String loginDriverId = StpUtil.getLoginIdAsString();
        String actualDriverId = loginDriverId;
        if (driverId != null && !driverId.isBlank() && !driverId.equals(loginDriverId)) {
            Assert.fail("tms_driver_mismatch");
        }
        Long vehicleCount = tmsVehicleDao.selectCount(new LambdaQueryWrapper<TmsVehicle>()
                .eq(TmsVehicle::getDriverId, actualDriverId));
        if (vehicleCount == null || vehicleCount == 0) {
            Assert.fail("tms_vehicle_incomplete");
        }
        int grabbed = tmsTransportOrderDao.update(new LambdaUpdateWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId)
                .eq(TmsTransportOrder::getStatus, (short) 0)
                .set(TmsTransportOrder::getDriverId, actualDriverId)
                .set(TmsTransportOrder::getStatus, (short) 1));
        if (grabbed == 0) {
            return false;
        }
        int driverUpdated = tmsDriverDao.update(new LambdaUpdateWrapper<TmsDriver>()
                .eq(TmsDriver::getUserId, actualDriverId)
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
        insertLogisticNode(transportOrderId, (short) 2, city);
    }

    @Override
    public void confirmArrived(String transportOrderId) {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        updateOrderStatusIf(transportOrderId, (short) 2, (short) 3, "tms_status_arrived_invalid");
        insertLogisticNode(transportOrderId, (short) 3, null);
    }

    @Override
    public void confirmReceived(String transportOrderId) {
        StpUtil.checkPermission("driver");
        StpUtil.checkLogin();
        updateOrderStatusIf(transportOrderId, (short) 3, (short) 4, "tms_status_received_invalid");
        insertLogisticNode(transportOrderId, (short) 4, null);
    }

    @Override
    public void updateStatus(String driverId, Short status) {
        tmsDriverDao.update(new LambdaUpdateWrapper<TmsDriver>()
                .eq(TmsDriver::getUserId,driverId)
                .set(TmsDriver::getStatus,status));
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

    private TmsTransportOrder getTransportOrderOrFail(String transportOrderId) {
        TmsTransportOrder order = tmsTransportOrderDao.selectOne(new LambdaQueryWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId)
                .orderByDesc(TmsTransportOrder::getId)
                .last("limit 1"));
        if (order == null) {
            Assert.fail("tms_order_not_found");
        }
        return order;
    }

    private void insertLogisticNode(String transportOrderId, short status, String city) {
        TmsTransportOrder order = getTransportOrderOrFail(transportOrderId);
        TmsLogistic logistic = new TmsLogistic();
        logistic.setTransportOrderId(order.getTransportOrderId());
        logistic.setDriverId(order.getDriverId());
        String nodeCity = (city != null && !city.isBlank()) ? city : order.getDest();
        logistic.setCity(nodeCity);
        logistic.setStatus(status);
        tmsLogisticDao.insert(logistic);
        redisService.delete(RedisConstant.LOGI_KEY_PREFIX + transportOrderId);
    }
}
