package com.wms.controller;

import com.wms.dto.OmsOrderDto;
import com.wms.service.WmsStockLockService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wms")
public class WmsStockController {
    @Resource
    private WmsStockLockService wmsStockLockService;

    @PostMapping("/wmsStockLock")
    public void stockLock(@RequestBody OmsOrderDto omsOrderDto){
        wmsStockLockService.WmsStockLock(omsOrderDto);
    }

    @PostMapping("/wmsStockUnlock")
    public void stockUnlock(@RequestParam String orderId){
        wmsStockLockService.WmsStockUnlock(orderId);
    }
}
