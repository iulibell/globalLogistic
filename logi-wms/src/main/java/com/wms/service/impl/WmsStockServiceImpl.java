package com.wms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.constant.AuthConstant;
import com.wms.dao.WmsStockDao;
import com.wms.dao.WmsWarehouseDao;
import com.wms.dto.WmsStockDto;
import com.wms.entity.WmsStock;
import com.wms.entity.WmsWarehouse;
import com.wms.service.WmsStockService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WmsStockServiceImpl implements WmsStockService {
    @Resource
    private WmsStockDao wmsStockDao;
    @Resource
    private WmsWarehouseDao wmsWarehouseDao;

    @Override
    public List<WmsStockDto> getStock(int pageNum, int pageSize) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        Set<Long> managedWarehouseIds = managedWarehouseIdsByLoginKeeper();
        if (managedWarehouseIds.isEmpty()) {
            return List.of();
        }
        IPage<WmsStock> page = new Page<>(pageNum,pageSize);
        wmsStockDao.selectPage(page,new LambdaQueryWrapper<WmsStock>()
                .in(WmsStock::getWarehouseId, managedWarehouseIds));
        return page.convert(wmsStock -> {
            WmsStockDto wmsStockDto = new WmsStockDto();
            BeanUtils.copyProperties(wmsStock,wmsStockDto);
            return wmsStockDto;
        }).getRecords();
    }

    @Override
    public WmsStockDto getStockById(String stockId) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        Set<Long> managedWarehouseIds = managedWarehouseIdsByLoginKeeper();
        if (managedWarehouseIds.isEmpty()) {
            return null;
        }
        WmsStock one = wmsStockDao.selectOne(new LambdaQueryWrapper<WmsStock>()
                .eq(WmsStock::getStockId,stockId)
                .in(WmsStock::getWarehouseId, managedWarehouseIds)
                .orderByDesc(WmsStock::getId)
                .last("limit 1"));
        if (one == null) {
            return null;
        }
        WmsStockDto wmsStockDto = new WmsStockDto();
        BeanUtils.copyProperties(one,wmsStockDto);
        return wmsStockDto;
    }

    @Override
    public void addStock(WmsStockDto wmsStockDto) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        if (!isWarehouseInManagedCity(wmsStockDto.getWarehouseId())) {
            return;
        }
        WmsStock wmsStock = new WmsStock();
        BeanUtils.copyProperties(wmsStockDto,wmsStock);
        wmsStockDao.insert(wmsStock);
    }

    @Override
    public void updateStock(WmsStockDto wmsStockDto) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        Set<Long> managedWarehouseIds = managedWarehouseIdsByLoginKeeper();
        if (managedWarehouseIds.isEmpty()) {
            return;
        }
        WmsStock wmsStock = new WmsStock();
        BeanUtils.copyProperties(wmsStockDto,wmsStock);
        wmsStockDao.update(wmsStock,new LambdaUpdateWrapper<WmsStock>()
                .eq(WmsStock::getStockId,wmsStockDto.getStockId())
                .in(WmsStock::getWarehouseId, managedWarehouseIds));
    }

    @Override
    public void deleteStock(String stockId) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        Set<Long> managedWarehouseIds = managedWarehouseIdsByLoginKeeper();
        if (managedWarehouseIds.isEmpty()) {
            return;
        }
        wmsStockDao.delete(new LambdaQueryWrapper<WmsStock>()
                .eq(WmsStock::getStockId,stockId)
                .in(WmsStock::getWarehouseId, managedWarehouseIds));
    }

    private boolean isWarehouseInManagedCity(Long warehouseId) {
        if (warehouseId == null) {
            return false;
        }
        return managedWarehouseIdsByLoginKeeper().contains(warehouseId);
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
