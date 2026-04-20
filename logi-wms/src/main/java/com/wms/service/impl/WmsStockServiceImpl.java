package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.dao.WmsStockDao;
import com.wms.dto.WmsStockDto;
import com.wms.entity.WmsStock;
import com.wms.service.WmsStockService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsStockServiceImpl implements WmsStockService {
    @Resource
    private WmsStockDao wmsStockDao;

    @Override
    public List<WmsStockDto> getStock(int pageNum, int pageSize) {
        IPage<WmsStock> page = new Page<>(pageNum,pageSize);
        wmsStockDao.selectPage(page,null);
        return page.convert(wmsStock -> {
            WmsStockDto wmsStockDto = new WmsStockDto();
            BeanUtils.copyProperties(wmsStock,wmsStockDto);
            return wmsStockDto;
        }).getRecords();
    }

    @Override
    public WmsStockDto getStockById(String stockId) {
        WmsStock one = wmsStockDao.selectOne(new LambdaQueryWrapper<WmsStock>()
                .eq(WmsStock::getStockId,stockId)
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
        WmsStock wmsStock = new WmsStock();
        BeanUtils.copyProperties(wmsStockDto,wmsStock);
        wmsStockDao.insert(wmsStock);
    }

    @Override
    public void updateStock(WmsStockDto wmsStockDto) {
        WmsStock wmsStock = new WmsStock();
        BeanUtils.copyProperties(wmsStockDto,wmsStock);
        wmsStockDao.update(wmsStock,new LambdaUpdateWrapper<WmsStock>()
                .eq(WmsStock::getStockId,wmsStockDto.getStockId()));
    }

    @Override
    public void deleteStock(String stockId) {
        wmsStockDao.delete(new LambdaQueryWrapper<WmsStock>()
                .eq(WmsStock::getStockId,stockId));
    }
}
