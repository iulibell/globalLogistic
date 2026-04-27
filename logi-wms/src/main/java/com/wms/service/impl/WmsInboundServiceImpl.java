package com.wms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.api.CommonResult;
import com.api.ResultCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.constant.AuthConstant;
import com.wms.dao.WmsInboundDao;
import com.wms.dao.WmsStockDao;
import com.wms.dao.WmsWarehouseDao;
import com.wms.dto.GoodsIndexRequest;
import com.wms.dto.PortalGoodsNeededDto;
import com.wms.dto.PortalGoodsRemoteDto;
import com.wms.dto.WmsInboundDto;
import com.wms.entity.WmsInbound;
import com.wms.entity.WmsStock;
import com.wms.service.WmsInboundService;
import com.wms.service.client.MallSearchServiceClient;
import com.wms.service.client.PortalServiceClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WmsInboundServiceImpl implements WmsInboundService {

    @Resource
    private PortalServiceClient portalServiceClient;
    @Resource
    private MallSearchServiceClient mallSearchServiceClient;
    @Resource
    private WmsInboundDao wmsInboundDao;
    @Resource
    private WmsStockDao wmsStockDao;
    @Resource
    private WmsWarehouseDao wmsWarehouseDao;

    @Override
    public List<WmsInboundDto> getInbound(int pageNum, int pageSize) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        Set<Long> managedWarehouseIds = managedWarehouseIdsByLoginKeeper();
        if (managedWarehouseIds.isEmpty()) {
            return List.of();
        }
        IPage<WmsInbound> page = new Page<>(pageNum,pageSize);
        wmsInboundDao.selectPage(page,new LambdaQueryWrapper<WmsInbound>()
                .eq(WmsInbound::getStatus,(short)0)
                .in(WmsInbound::getWarehouseId, managedWarehouseIds));
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
        Set<Long> managedWarehouseIds = managedWarehouseIdsByLoginKeeper();
        if (managedWarehouseIds.isEmpty()) {
            return null;
        }
        WmsInboundDto wmsInboundDto = new WmsInboundDto();
        BeanUtils.copyProperties(wmsInboundDao.selectOne(new LambdaQueryWrapper<WmsInbound>()
                .eq(WmsInbound::getInboundId,inbound)
                .in(WmsInbound::getWarehouseId, managedWarehouseIds)),wmsInboundDto);
        return wmsInboundDto;
    }

    @Override
    public void confirmInbound(String inboundId, String skuCode, Long locationId, Short category) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        Set<Long> managedWarehouseIds = managedWarehouseIdsByLoginKeeper();
        if (managedWarehouseIds.isEmpty()) {
            return;
        }
        short normalizedCategory = (category != null && category == (short) 1) ? (short) 1 : (short) 0;

        wmsInboundDao.update(new LambdaUpdateWrapper<WmsInbound>()
                .eq(WmsInbound::getInboundId,inboundId)
                .in(WmsInbound::getWarehouseId, managedWarehouseIds)
                .set(WmsInbound::getStatus,(short)1)
                .set(WmsInbound::getLocationId, locationId)
                .set(WmsInbound::getSkuCode,skuCode));

        WmsInbound wmsInbound = wmsInboundDao.selectOne(new LambdaQueryWrapper<WmsInbound>()
                .eq(WmsInbound::getInboundId,inboundId)
                .in(WmsInbound::getWarehouseId, managedWarehouseIds));
        if (wmsInbound == null) {
            return;
        }
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
        wmsStock.setStockId(wmsInbound.getGoodsId());
        wmsStock.setWarehouseId(wmsInbound.getWarehouseId());
        wmsStock.setLocationId(locationId);
        wmsStock.setSkuName(wmsInbound.getSkuName());
        wmsStock.setSkuCode(skuCode);
        wmsStock.setStockQuantity(wmsInbound.getQuantity() == null ? 0 : wmsInbound.getQuantity());
        wmsStock.setLockQuantity(0);
        wmsStock.setCreateTime(new Date());
        wmsStock.setUpdateTime(new Date());
        wmsStockDao.insert(wmsStock);

        PortalGoodsNeededDto portalGoodsNeededDto = new PortalGoodsNeededDto();
        portalGoodsNeededDto.setWarehouseId(wmsInbound.getWarehouseId());
        portalGoodsNeededDto.setGoodsId(wmsInbound.getGoodsId());
        portalGoodsNeededDto.setLocationId(locationId);
        portalGoodsNeededDto.setSkuCode(skuCode);
        portalGoodsNeededDto.setCategory(normalizedCategory);
        portalServiceClient.goodsOnShelf(portalGoodsNeededDto);
        syncGoodsSearchIndex(wmsInbound);
    }

    /**
     * 确认入库且门户状态已更新为可售后，将商品快照写入商城 ES（失败仅打日志，不阻断入库主流程）。
     */
    private void syncGoodsSearchIndex(WmsInbound wmsInbound) {
        try {
            GoodsIndexRequest goodsIndexRequest = buildGoodsIndexRequest(wmsInbound);
            CommonResult<Void> searchResult = mallSearchServiceClient.addIndex(goodsIndexRequest);
            if (searchResult == null || searchResult.getCode() != ResultCode.SUCCESS.getCode()) {
                log.warn("添加ES索引失败:code={} goodsId={}",
                        searchResult != null ? searchResult.getCode() : null, wmsInbound.getGoodsId());
            }
        } catch (Exception e) {
            log.error("异步添加ES索引异常inboundId={} goodsId={}", wmsInbound.getInboundId(), wmsInbound.getGoodsId(), e);
        }
    }

    private GoodsIndexRequest buildGoodsIndexRequest(WmsInbound wmsInbound) {
        CommonResult<PortalGoodsRemoteDto> portalRes =
                portalServiceClient.sysPortalGoodsByGoodsId(wmsInbound.getGoodsId());
        if (portalRes != null && portalRes.getCode() ==
                ResultCode.SUCCESS.getCode() && portalRes.getData() != null) {
            return fromPortalGoods(portalRes.getData());
        }
        return fromInboundFallback(wmsInbound);
    }

    private static GoodsIndexRequest fromPortalGoods(PortalGoodsRemoteDto portalGoodsRemoteDto) {
        GoodsIndexRequest goodsIndexRequest = new GoodsIndexRequest();
        goodsIndexRequest.setGoodsId(portalGoodsRemoteDto.getGoodsId());
        goodsIndexRequest.setMerchantId(portalGoodsRemoteDto.getMerchantId());
        goodsIndexRequest.setSkuName(portalGoodsRemoteDto.getSkuName());
        goodsIndexRequest.setPicture(StringUtils.hasText(portalGoodsRemoteDto.getPicture())
                ? portalGoodsRemoteDto.getPicture() : "");
        goodsIndexRequest.setCategory(portalGoodsRemoteDto.getCategory() != null
                ? portalGoodsRemoteDto.getCategory().intValue() : 0);
        goodsIndexRequest.setType(StringUtils.hasText(portalGoodsRemoteDto.getType())
                ? portalGoodsRemoteDto.getType() : "GENERAL");
        goodsIndexRequest.setDescription(portalGoodsRemoteDto.getDescription());
        goodsIndexRequest.setPrice(portalGoodsRemoteDto.getPrice());
        goodsIndexRequest.setStatus(1);
        return goodsIndexRequest;
    }

    private static GoodsIndexRequest fromInboundFallback(WmsInbound wmsInbound) {
        GoodsIndexRequest goodsIndexRequest = new GoodsIndexRequest();
        goodsIndexRequest.setGoodsId(wmsInbound.getGoodsId());
        goodsIndexRequest.setMerchantId(wmsInbound.getMerchantId());
        goodsIndexRequest.setSkuName(wmsInbound.getSkuName());
        goodsIndexRequest.setPicture("");
        goodsIndexRequest.setCategory(0);
        goodsIndexRequest.setType("GENERAL");
        goodsIndexRequest.setDescription(wmsInbound.getMerchantRemark());
        goodsIndexRequest.setPrice(null);
        goodsIndexRequest.setStatus(1);
        return goodsIndexRequest;
    }

    private Set<Long> managedWarehouseIdsByLoginKeeper() {
        String city = loginUserCity();
        if (city == null || city.isBlank()) {
            return Set.of();
        }
        List<com.wms.entity.WmsWarehouse> warehouses = wmsWarehouseDao.selectList(new LambdaQueryWrapper<com.wms.entity.WmsWarehouse>()
                .eq(com.wms.entity.WmsWarehouse::getCity, city.trim())
                .eq(com.wms.entity.WmsWarehouse::getStatus, (short) 1));
        if (warehouses == null || warehouses.isEmpty()) {
            return Set.of();
        }
        return warehouses.stream()
                .map(com.wms.entity.WmsWarehouse::getWarehouseId)
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
