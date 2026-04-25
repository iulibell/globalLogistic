package com.oms.service.client;

import com.oms.dto.OmsOrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("logi-wms")
public interface WmsServiceClient {
    @PostMapping("/wms/sys/wmsStockLock")
    void stockLock(@RequestBody OmsOrderDto omsOrderDto);

    @PostMapping("/wms/sys/wmsStockUnlock")
    void stockUnlock(@RequestParam String orderId);
}
