package com.wms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.dao.WmsWarehouseDao;
import com.wms.dto.WmsWarehouseDto;
import com.wms.entity.WmsWarehouse;
import com.wms.service.WmsWarehouseService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsWarehouseServiceImpl implements WmsWarehouseService {
    @Resource
    private WmsWarehouseDao wmsWarehouseDao;

    @Override
    public List<WmsWarehouseDto> getAvailableWarehouse(int pageNum, int pageSize) {
        IPage<WmsWarehouse> page = new Page<>(pageNum,pageSize);
        wmsWarehouseDao.selectPage(page,new LambdaQueryWrapper<WmsWarehouse>().eq(WmsWarehouse::getStatus,(short)1));
        return page.convert(wmsWarehouse -> {
            WmsWarehouseDto wmsWarehouseDto = new WmsWarehouseDto();
            BeanUtils.copyProperties(wmsWarehouse,wmsWarehouseDto);
            return wmsWarehouseDto;
        }).getRecords();
    }

    @Override
    public List<WmsWarehouseDto> getWarehouse(int pageNum, int pageSize) {
        StpUtil.checkLogin();
        StpUtil.checkPermission("manager");
        IPage<WmsWarehouse> page = new Page<>(pageNum,pageSize);
        wmsWarehouseDao.selectPage(page,null);
        return page.convert(wmsWarehouse -> {
            WmsWarehouseDto wmsWarehouseDto = new WmsWarehouseDto();
            BeanUtils.copyProperties(wmsWarehouse,wmsWarehouseDto);
            return wmsWarehouseDto;
        }).getRecords();
    }

    @Override
    public void addWarehouse(WmsWarehouseDto wmsWarehouseDto) {
        StpUtil.checkLogin();
        StpUtil.checkPermission("manager");
        WmsWarehouse wmsWarehouse = new WmsWarehouse();
        BeanUtils.copyProperties(wmsWarehouseDto,wmsWarehouse);
        wmsWarehouseDao.insert(wmsWarehouse);
    }

    @Override
    public void updateWarehouse(WmsWarehouseDto wmsWarehouseDto) {
        StpUtil.checkLogin();
        StpUtil.checkPermission("manager");
        WmsWarehouse wmsWarehouse = new WmsWarehouse();
        BeanUtils.copyProperties(wmsWarehouseDto,wmsWarehouse);
        wmsWarehouseDao.update(new LambdaUpdateWrapper<WmsWarehouse>()
                .eq(WmsWarehouse::getWarehouseId,wmsWarehouseDto.getWarehouseId()));
    }

    @Override
    public void deleteWarehouse(Long warehouseId) {
        StpUtil.checkLogin();
        StpUtil.checkPermission("manager");
        wmsWarehouseDao.delete(new LambdaQueryWrapper<WmsWarehouse>()
                .eq(WmsWarehouse::getWarehouseId,warehouseId));
    }
}
