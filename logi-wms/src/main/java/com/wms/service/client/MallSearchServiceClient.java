package com.wms.service.client;

import com.api.CommonResult;
import com.wms.dto.GoodsIndexRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("mall-gl-search")
public interface MallSearchServiceClient {

    @PostMapping("/search/goods/addIndex")
    CommonResult<Void> addIndex(@Valid @RequestBody GoodsIndexRequest request);

    @PostMapping("/search/goods/deleteGoodsDocument")
    CommonResult<Void> deleteGoodsDocument(@RequestParam("goodsId") String goodsId);
}
