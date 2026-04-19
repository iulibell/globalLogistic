package com.tms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.constant.RabbitConstant;
import com.constant.RedisConstant;
import com.service.RedisService;
import com.tms.dao.TmsDriverDao;
import com.tms.dao.TmsTransportOrderDao;
import com.tms.dto.TmsTransportOrderDto;
import com.tms.entity.TmsDriver;
import com.tms.entity.TmsTransportOrder;
import com.tms.service.TmsTransportOrderService;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TmsTransportOrderServiceImpl implements TmsTransportOrderService {

    private static final int MANUAL_ASSIGN_REJECT_WEIGHT_DELTA = 5;

    @Resource
    TmsTransportOrderDao tmsTransportOrderDao;
    @Resource
    private TmsDriverDao tmsDriverDao;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Resource
    private RedisService redisService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void driverAssignment(TmsTransportOrderDto tmsTransportOrderDto) {
        String transportOrderId = String.valueOf(snowflakeIdGenerator.nextId());

        tmsTransportOrderDto.setTransportOrderId(transportOrderId);

        TmsTransportOrder tmsTransportOrder = new TmsTransportOrder();
        BeanUtils.copyProperties(tmsTransportOrderDto,tmsTransportOrder);
        tmsTransportOrder.setStatus((short) 0);
        tmsTransportOrderDao.insert(tmsTransportOrder);

        scheduleAssignWindow(transportOrderId);
    }

    @Override
    public List<TmsTransportOrderDto> getManualAssignment(int pageNum, int pageSize) {
        StpUtil.checkPermission("manager");
        StpUtil.checkLogin();
        IPage<TmsTransportOrder> page = new Page<>(pageNum,pageSize);
        tmsTransportOrderDao.selectPage(page, new LambdaQueryWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getStatus, (short) 6));
        return page.convert(tmsTransportOrder -> {
            TmsTransportOrderDto tmsTransportOrderDto = new TmsTransportOrderDto();
            BeanUtils.copyProperties(tmsTransportOrder,tmsTransportOrderDto);
            return tmsTransportOrderDto;
        }).getRecords();
    }

    @Override
    public boolean handleAssignWindowExpired(String transportOrderId) {
        int rows = tmsTransportOrderDao.update(null, new LambdaUpdateWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId)
                .eq(TmsTransportOrder::getStatus, (short) 0)
                .setSql("fee = COALESCE(fee, 0) + {0}", 3));
        if (rows > 0) {
            scheduleAssignWindow(transportOrderId);
        }
        return rows > 0;
    }

    @Override
    @Transactional
    public boolean recordDriverReject(String transportOrderId, String driverId) {
        TmsTransportOrder current = tmsTransportOrderDao.selectOne(new LambdaQueryWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId));
        if (current == null || current.getStatus() == null) {
            return false;
        }
        if (current.getStatus() == (short) 1) {
            return recordManualAssignReject(current, transportOrderId, driverId);
        }
        if (current.getStatus() != (short) 0) {
            return false;
        }
        if (driverId != null && !driverId.isBlank()) {
            return false;
        }
        int updated = tmsTransportOrderDao.update(null, new LambdaUpdateWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId)
                .eq(TmsTransportOrder::getStatus, (short) 0)
                .setSql("reject_count = COALESCE(reject_count, 0) + 1"));
        if (updated == 0) {
            return false;
        }
        TmsTransportOrder fresh = tmsTransportOrderDao.selectOne(new LambdaQueryWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId));
        int newRc = fresh.getRejectCount() == null ? 0 : fresh.getRejectCount();
        if (newRc == 1) {
            scheduleAssignWindow(transportOrderId);
            return true;
        }
        if (newRc == 2 || newRc == 3) {
            tmsTransportOrderDao.update(null, new LambdaUpdateWrapper<TmsTransportOrder>()
                    .eq(TmsTransportOrder::getTransportOrderId, transportOrderId)
                    .setSql("fee = COALESCE(fee, 0) + {0}", 3));
            scheduleAssignWindow(transportOrderId);
            return true;
        }
        if (newRc >= 4) {
            int toManual = tmsTransportOrderDao.update(null, new LambdaUpdateWrapper<TmsTransportOrder>()
                    .eq(TmsTransportOrder::getTransportOrderId, transportOrderId)
                    .eq(TmsTransportOrder::getStatus, (short) 0)
                    .eq(TmsTransportOrder::getRejectCount, newRc)
                    .set(TmsTransportOrder::getStatus, (short) 6));
            if (toManual == 0) {
                throw new IllegalStateException("转入待人工分配失败，请重试");
            }
            redisService.delete(RedisConstant.ASSIGN_KEY_PREFIX + transportOrderId);
            return true;
        }
        return true;
    }

    @Override
    public boolean manualAssignDriver(String transportOrderId, String driverId) {
        StpUtil.checkPermission("manager");
        StpUtil.checkLogin();
        int n = tmsTransportOrderDao.update(null, new LambdaUpdateWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId)
                .eq(TmsTransportOrder::getStatus, (short) 6)
                .set(TmsTransportOrder::getDriverId, driverId)
                .set(TmsTransportOrder::getStatus, (short) 1));
        if (n > 0) {
            redisService.delete(RedisConstant.ASSIGN_KEY_PREFIX + transportOrderId);
        }
        return n > 0;
    }

    /**
     * 人工指派后司机拒单：记录拒单司机、降低权重，运输单回到待人工分配。
     */
    private boolean recordManualAssignReject(TmsTransportOrder current, String transportOrderId, String driverId) {
        if (driverId == null || driverId.isBlank()) {
            return false;
        }
        if (current.getDriverId() == null || !driverId.equals(current.getDriverId())) {
            return false;
        }
        TmsDriver driver = tmsDriverDao.selectOne(new LambdaQueryWrapper<TmsDriver>()
                .eq(TmsDriver::getUserId, driverId));
        if (driver != null) {
            int w = driver.getWeight() == null ? 100 : driver.getWeight();
            int next = Math.max(0, w - MANUAL_ASSIGN_REJECT_WEIGHT_DELTA);
            tmsDriverDao.update(null, new LambdaUpdateWrapper<TmsDriver>()
                    .eq(TmsDriver::getUserId, driverId)
                    .set(TmsDriver::getWeight, next));
        }
        int n = tmsTransportOrderDao.update(null, new LambdaUpdateWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportOrderId)
                .eq(TmsTransportOrder::getStatus, (short) 1)
                .eq(TmsTransportOrder::getDriverId, driverId)
                .set(TmsTransportOrder::getStatus, (short) 6)
                .set(TmsTransportOrder::getLastRejectDriverId, driverId)
                .setSql("driver_id = NULL"));
        if (n > 0) {
            redisService.delete(RedisConstant.ASSIGN_KEY_PREFIX + transportOrderId);
        }
        return n > 0;
    }

    private void scheduleAssignWindow(String transportOrderId) {
        redisService.set(RedisConstant.ASSIGN_KEY_PREFIX + transportOrderId,
                "",
                RedisConstant.ASSIGN_EXPIRE_TIME,
                TimeUnit.MINUTES);
        rabbitTemplate.convertAndSend(RabbitConstant.ASSIGN_TTL_EXCHANGE,
                RabbitConstant.ASSIGN_TTL_ROUTING_KEY,
                transportOrderId);
    }

}
