package com.wms.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.api.CommonResult;
import com.wms.dto.PortalOffShelfPendingDto;
import com.wms.service.client.PortalServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商城下架申请：待审核列表与核定费用由<strong>物流仓管 keeper</strong>在 WMS 侧处理，经 Feign 回写 mall-portal。
 */
@RestController
@RequestMapping("/wms")
@Tag(name = "WmsPortalOffShelfController", description = "下架申请：仓管拉单与核定费用（对接商城 portal）。")
public class WmsPortalOffShelfController {

    @Resource
    private PortalServiceClient portalServiceClient;

    @GetMapping("/keeper/portalOffShelf/pending")
    @Operation(summary = "下架申请待审核分页", description = "拉取商城 portal 状态为待审核的下架申请。")
    public CommonResult<List<PortalOffShelfPendingDto>> pending(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        StpUtil.checkLogin();
        StpUtil.checkPermission("keeper");
        return portalServiceClient.offShelfPendingList(pageNum, pageSize);
    }

    @PostMapping("/keeper/portalOffShelf/setFee")
    @Operation(summary = "核定下架费用", description = "写入费用并将申请置为待支付，开启商家支付时效。")
    public CommonResult<Boolean> setFee(@RequestParam Long offShelfId, @RequestParam BigDecimal fee) {
        StpUtil.checkLogin();
        StpUtil.checkPermission("keeper");
        return portalServiceClient.setOffShelfFee(offShelfId, fee);
    }
}
