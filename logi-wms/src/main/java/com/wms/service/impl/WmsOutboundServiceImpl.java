package com.wms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.api.CommonResult;
import com.api.ResultCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.constant.AuthConstant;
import com.exception.Assert;
import com.wms.dao.WmsOutboundDao;
import com.wms.dao.WmsStockDao;
import com.wms.dao.WmsStockLockDao;
import com.wms.dao.WmsWarehouseDao;
import com.wms.dto.TmsTransportOrderDto;
import com.wms.dto.WmsOutboundDto;
import com.wms.entity.WmsOutbound;
import com.wms.entity.WmsStock;
import com.wms.entity.WmsStockLock;
import com.wms.entity.WmsWarehouse;
import com.wms.service.WmsOutboundService;
import com.wms.service.client.MallSearchServiceClient;
import com.wms.service.client.PortalServiceClient;
import com.wms.service.client.TmsServiceClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WmsOutboundServiceImpl implements WmsOutboundService {

    @Resource
    private WmsOutboundDao wmsOutboundDao;
    @Resource
    private PortalServiceClient portalServiceClient;
    @Resource
    private WmsWarehouseDao wmsWarehouseDao;
    @Resource
    private WmsStockDao wmsStockDao;
    @Resource
    private WmsStockLockDao wmsStockLockDao;
    @Resource
    private TmsServiceClient tmsServiceClient;
    @Resource
    private MallSearchServiceClient mallSearchServiceClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOutbound(WmsOutboundDto wmsOutboundDto) {
        if (wmsOutboundDao.selectOne(new LambdaQueryWrapper<WmsOutbound>()
                .eq(WmsOutbound::getOrderId, wmsOutboundDto.getOrderId())) != null) {
            return;
        }
        WmsOutbound wmsOutbound = new WmsOutbound();
        BeanUtils.copyProperties(wmsOutboundDto,wmsOutbound);
        // 服务端兜底：新建记录默认待出库，避免上游错误状态透传。
        wmsOutbound.setStatus((short) 0);
        wmsOutboundDao.insert(wmsOutbound);

        String orderId = wmsOutboundDto.getOrderId();
        if (orderId != null && orderId.startsWith("OFF")) {
            createOffShelfTransportOrder(wmsOutboundDto);
        }
    }

    /**
     * 商城下架出库：仓库城市 → 用户城市，与入库支付后派单逻辑一致（司机费为实付的 30%）。
     */
    private void createOffShelfTransportOrder(WmsOutboundDto dto) {
        BigDecimal total = dto.getPaidFee();
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            Assert.fail("paidFee缺失或无效，无法创建运输单");
        }
        Long warehouseId = dto.getWarehouseId();
        if (warehouseId == null) {
            Assert.fail("warehouseId缺失，无法创建运输单");
        }
        WmsWarehouse warehouse = wmsWarehouseDao.selectOne(new LambdaQueryWrapper<WmsWarehouse>()
                .eq(WmsWarehouse::getWarehouseId, warehouseId)
                .last("LIMIT 1"));
        if (warehouse == null || warehouse.getCity() == null || warehouse.getCity().isBlank()) {
            Assert.fail("仓库或仓库城市不存在，无法创建运输单");
        }
        String destCity = dto.getCity();
        if (destCity == null || destCity.isBlank()) {
            Assert.fail("目的地城市为空，无法创建运输单");
        }
        BigDecimal driverFee = total.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
        if (driverFee.compareTo(BigDecimal.ZERO) <= 0) {
            driverFee = new BigDecimal("0.01");
        }
        TmsTransportOrderDto tmsDto = new TmsTransportOrderDto();
        tmsDto.setGoodsId(dto.getStockId());
        tmsDto.setOrderId(dto.getOrderId());
        tmsDto.setPhone(dto.getUserPhone());
        tmsDto.setTransportOrderId(dto.getOrderId());
        tmsDto.setOrigin(warehouse.getCity().trim());
        tmsDto.setDest(destCity.trim());
        tmsDto.setFee(driverFee);
        String transportOrderId = tmsServiceClient.driverAssignment(tmsDto);
        if (transportOrderId != null && !transportOrderId.isBlank()) {
            String orderId = dto.getOrderId();
            if (orderId != null && orderId.startsWith("OFF")) {
                try {
                    Long offShelfId = Long.parseLong(orderId.substring(3));
                    portalServiceClient.bindOffShelfTransportOrderId(offShelfId, transportOrderId.trim());
                } catch (NumberFormatException ignored) {
                    // ignore malformed OFF{offShelfId}
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOutbound(String outboundId) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        Set<Long> managedWarehouseIds = managedWarehouseIdsByLoginKeeper();
        if (managedWarehouseIds.isEmpty()) {
            return;
        }
        WmsOutbound wmsOutbound = wmsOutboundDao.selectOne(new LambdaQueryWrapper<WmsOutbound>()
                .eq(WmsOutbound::getOutboundId, outboundId)
                .in(WmsOutbound::getWarehouseId, managedWarehouseIds));
        if (wmsOutbound == null) {
            return;
        }
        int rows = wmsOutboundDao.update(new LambdaUpdateWrapper<WmsOutbound>()
                .eq(WmsOutbound::getOutboundId,outboundId)
                .in(WmsOutbound::getWarehouseId, managedWarehouseIds)
                .eq(WmsOutbound::getStatus, (short) 0)
                .set(WmsOutbound::getStatus,(short)1));
        if (rows == 0) {
            return;
        }
        String orderId = wmsOutbound.getOrderId();
        if (orderId != null && orderId.startsWith("OFF")) {
            removeOffShelfStockOrFail(wmsOutbound.getStockId());
            removeGoodsSearchIndex(wmsOutbound.getStockId());
            try {
                Long offShelfId = Long.parseLong(orderId.substring(3));
                portalServiceClient.markOffShelfCompleted(offShelfId);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    /**
     * 下架确认出库时删除库存记录；若此刻有用户正在购买（锁库中）则拒绝删除并回滚本次确认。
     */
    private void removeOffShelfStockOrFail(String stockId) {
        if (stockId == null || stockId.isBlank()) {
            Assert.fail("库存ID为空，无法执行下架库存删除");
        }
        long activeLocks = wmsStockLockDao.selectCount(new LambdaQueryWrapper<WmsStockLock>()
                .eq(WmsStockLock::getStockId, stockId.trim())
                .eq(WmsStockLock::getStatus, (short) 1));
        if (activeLocks > 0) {
            Assert.fail("当前有用户下单锁库中，请稍后再确认下架出库");
        }
        int rows = wmsStockDao.delete(new LambdaQueryWrapper<WmsStock>()
                .eq(WmsStock::getStockId, stockId.trim())
                .and(w -> w.eq(WmsStock::getLockQuantity, 0).or().isNull(WmsStock::getLockQuantity)));
        if (rows == 0) {
            Assert.fail("库存删除失败：可能存在并发下单，请稍后重试");
        }
    }

    /**
     * 下架确认出库后同步删除商城 ES 商品索引（失败仅记录日志，不阻断出库主流程）。
     */
    private void removeGoodsSearchIndex(String goodsId) {
        if (goodsId == null || goodsId.isBlank()) {
            return;
        }
        try {
            CommonResult<Void> result = mallSearchServiceClient.deleteGoodsDocument(goodsId.trim());
            if (result == null || result.getCode() != ResultCode.SUCCESS.getCode()) {
                log.warn("删除ES索引失败: code={} goodsId={}",
                        result != null ? result.getCode() : null, goodsId);
            }
        } catch (Exception e) {
            log.error("删除ES索引异常 goodsId={}", goodsId, e);
        }
    }

    @Override
    public WmsOutboundDto getOutboundById(String outboundId) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        Set<Long> managedWarehouseIds = managedWarehouseIdsByLoginKeeper();
        if (managedWarehouseIds.isEmpty()) {
            return null;
        }
        WmsOutboundDto wmsOutboundDto = new WmsOutboundDto();
        BeanUtils.copyProperties(wmsOutboundDao.selectOne(new LambdaQueryWrapper<WmsOutbound>()
                .eq(WmsOutbound::getOutboundId,outboundId)
                .in(WmsOutbound::getWarehouseId, managedWarehouseIds)),wmsOutboundDto);
        return wmsOutboundDto;
    }

    @Override
    public List<WmsOutboundDto> getOutbound(int pageNum, int pageSize) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        Set<Long> managedWarehouseIds = managedWarehouseIdsByLoginKeeper();
        if (managedWarehouseIds.isEmpty()) {
            return List.of();
        }
        IPage<WmsOutbound> page = new Page<>(pageNum,pageSize);
        wmsOutboundDao.selectPage(page,new LambdaQueryWrapper<WmsOutbound>()
                .eq(WmsOutbound::getStatus,(short)0)
                .in(WmsOutbound::getWarehouseId, managedWarehouseIds));
        return page.convert(wmsOutbound -> {
            WmsOutboundDto wmsOutboundDto = new WmsOutboundDto();
            BeanUtils.copyProperties(wmsOutbound,wmsOutboundDto);
            return wmsOutboundDto;
        }).getRecords();
    }

    private Set<Long> managedWarehouseIdsByLoginKeeper() {
        String city = loginUserCity();
        if (city == null || city.isBlank()) {
            return Set.of();
        }
        List<WmsWarehouse> warehouses = wmsWarehouseDao.selectList(new LambdaQueryWrapper<WmsWarehouse>()
                .eq(WmsWarehouse::getCity, city.trim())
                .eq(WmsWarehouse::getStatus, (short) 1));
        if (warehouses == null || warehouses.isEmpty()) {
            return Set.of();
        }
        return warehouses.stream()
                .map(WmsWarehouse::getWarehouseId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private String loginUserCity() {
        Object raw = StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
        if (!(raw instanceof Map<?, ?> map)) {
            return null;
        }
        Object city = map.get("city");
        return city == null ? null : String.valueOf(city);
    }
}
