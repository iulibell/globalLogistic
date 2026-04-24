package com.wms.service.client;

import com.api.CommonResult;
import com.wms.dto.GoodsIndexRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mall-gl-search")
public interface MallSearchServiceClient {

    @PostMapping("/search/goods/addIndex")
    CommonResult<Void> addIndex(@Valid @RequestBody GoodsIndexRequest request);
}
