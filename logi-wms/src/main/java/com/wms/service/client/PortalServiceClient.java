package com.wms.service.client;

import com.api.CommonResult;
import com.wms.dto.PortalGoodsNeededDto;
import com.wms.dto.PortalGoodsRemoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("mall-portal")
public interface PortalServiceClient {
    @PostMapping("/portal/goodsOnShelf")
    void goodsOnShelf(@RequestBody PortalGoodsNeededDto portalGoodsNeededDto);

    @GetMapping("/portal/sys/portalGoods/{goodsId}")
    CommonResult<PortalGoodsRemoteDto> sysPortalGoodsByGoodsId(@PathVariable("goodsId") String goodsId);

    @PostMapping("/portal/sys/markOffShelfCompleted")
    void markOffShelfCompleted(@RequestParam Long offShelfId);
}
