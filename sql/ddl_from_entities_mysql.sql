-- =============================================================================
-- DDL generated from Java @TableName entities (MyBatis-Plus).
-- Column naming: camelCase -> snake_case (framework default).
-- Engine / charset: adjust if your deployment differs.
-- Excluded: com.entity.WebLog (logging DTO, no table); com.wms.entity.Keeper (empty POJO).
-- =============================================================================

SET NAMES utf8mb4;

-- -----------------------------------------------------------------------------
-- gl-system: sys_user
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` VARCHAR(255) NULL COMMENT '用户id',
  `username` VARCHAR(50) NULL COMMENT '用户名',
  `password` VARCHAR(255) NULL COMMENT '密码(加密)',
  `nickname` VARCHAR(50) NULL COMMENT '用户昵称',
  `phone` VARCHAR(11) NULL COMMENT '用户手机号',
  `user_type` VARCHAR(20) NULL COMMENT '用户身份:1 super,2 manager,3 keeper,4 driver,5 reviewer',
  `status` SMALLINT NULL COMMENT '用户状态:0 禁用,1 启用',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户';

-- -----------------------------------------------------------------------------
-- logi-admin: dictionary
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `dictionary` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dict_type` VARCHAR(50) NULL COMMENT '字典类型',
  `dict_name` VARCHAR(50) NULL COMMENT '字典名称',
  `dict_value` VARCHAR(50) NULL COMMENT '字典值',
  `sort` INT NULL COMMENT '排序',
  `status` SMALLINT NULL COMMENT '状态:0 禁用,1 启用',
  `lang` VARCHAR(255) NULL COMMENT '语言: 1 cn,2 en,3 ru',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典';

-- -----------------------------------------------------------------------------
-- logi-admin: register_application
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `register_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(20) NULL COMMENT '用户名',
  `password` VARCHAR(100) NULL COMMENT '密码(加密)',
  `nickname` VARCHAR(20) NULL COMMENT '用户昵称',
  `user_type` SMALLINT NULL COMMENT '用户类型:1-5',
  `phone` VARCHAR(11) NULL COMMENT '手机号',
  `status` SMALLINT NULL COMMENT '状态:0 待审核,1 已审核,2 未过审',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='注册申请';

-- -----------------------------------------------------------------------------
-- logi-oms: oms_order
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `oms_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` VARCHAR(255) NULL COMMENT '订单id',
  `merchant_id` VARCHAR(255) NULL COMMENT '商家id',
  `user_id` VARCHAR(255) NULL COMMENT '用户id',
  `goods_id` VARCHAR(255) NULL COMMENT '商品id(对应wms库存stockId)',
  `warehouse_id` BIGINT NULL COMMENT '仓库id',
  `location_id` BIGINT NULL COMMENT '库位id',
  `sku_name` VARCHAR(50) NULL COMMENT '商品名称',
  `sku_code` VARCHAR(50) NULL COMMENT '商品库存编号',
  `price` DECIMAL(10,2) NULL COMMENT '支付金额',
  `user_phone` VARCHAR(11) NULL COMMENT '用户手机号',
  `merchant_phone` VARCHAR(11) NULL COMMENT '商家手机号',
  `warehouse_city` VARCHAR(30) NULL COMMENT '仓库所属城市',
  `city` VARCHAR(30) NULL COMMENT '用户的城市',
  `category` SMALLINT NULL COMMENT '商品种类:0 普通,1 特殊',
  `type` VARCHAR(20) NULL COMMENT '商品类型',
  `status` SMALLINT NULL COMMENT '订单状态 0-5',
  `remark` VARCHAR(500) NULL COMMENT '备注',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单';

-- -----------------------------------------------------------------------------
-- logi-oms: oms_order_item
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `oms_order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` VARCHAR(255) NULL COMMENT '订单id',
  `sku_name` VARCHAR(50) NULL COMMENT '商品名称',
  `sku_code` VARCHAR(50) NULL COMMENT '商品编号',
  `price` DECIMAL(10,2) NULL COMMENT '商品单价',
  `quantity` INT NULL COMMENT '下订数量',
  `total_price` DECIMAL(10,2) NULL COMMENT '下单总价',
  `create_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项';

-- -----------------------------------------------------------------------------
-- logi-oms: oms_order_review
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `oms_order_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` VARCHAR(255) NULL COMMENT '订单id',
  `sku_name` VARCHAR(50) NULL COMMENT '商品名称',
  `sku_code` VARCHAR(50) NULL COMMENT '商品编号',
  `type` VARCHAR(20) NULL COMMENT '商品类型',
  `status` SMALLINT NULL COMMENT '审核状态:0 待审核,1 已审核',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单审核';

-- -----------------------------------------------------------------------------
-- logi-wms: wms_warehouse
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `wms_warehouse` (
  `warehouse_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '仓库主键',
  `city` VARCHAR(20) NULL COMMENT '所属城市',
  `country` VARCHAR(20) NULL COMMENT '所属国家',
  `address` VARCHAR(100) NULL COMMENT '具体地址',
  `status` SMALLINT NULL COMMENT '状态:0 禁用,1 启用',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓库';

-- -----------------------------------------------------------------------------
-- logi-wms: wms_stock
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `wms_stock` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stock_id` VARCHAR(255) NULL COMMENT '库存id',
  `warehouse_id` BIGINT NULL COMMENT '仓库id',
  `location_id` BIGINT NULL COMMENT '库位id',
  `sku_name` VARCHAR(50) NULL COMMENT '商品名称',
  `sku_code` VARCHAR(50) NULL COMMENT '商品库存编号',
  `stock_quantity` INT NULL COMMENT '库存数量',
  `lock_quantity` INT NULL COMMENT '锁库数量',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存';

-- -----------------------------------------------------------------------------
-- logi-wms: wms_stock_lock
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `wms_stock_lock` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `warehouse_id` BIGINT NULL COMMENT '仓库id',
  `order_id` VARCHAR(255) NULL COMMENT '关联orderId',
  `stock_id` VARCHAR(255) NULL COMMENT '库存id',
  `location_id` BIGINT NULL COMMENT '库位id',
  `sku_name` VARCHAR(50) NULL COMMENT '商品名称',
  `sku_code` VARCHAR(50) NULL COMMENT '商品库存编号',
  `user_phone` VARCHAR(11) NULL COMMENT '用户手机号',
  `merchant_phone` VARCHAR(11) NULL COMMENT '商家手机号',
  `city` VARCHAR(20) NULL COMMENT '用户城市',
  `lock_quantity` INT NULL COMMENT '锁库数量',
  `status` SMALLINT NULL COMMENT '状态:0 已解锁,1 上锁中',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存锁';

-- -----------------------------------------------------------------------------
-- logi-wms: wms_inbound_apply
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `wms_inbound_apply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goods_id` VARCHAR(255) NULL COMMENT '商品id(对接商城/portal)',
  `warehouse_id` BIGINT NULL COMMENT '申请入库的仓库id',
  `apply_id` VARCHAR(255) NULL COMMENT '申请入库id',
  `merchant_id` VARCHAR(255) NULL COMMENT '商家id',
  `merchant_phone` VARCHAR(11) NULL COMMENT '商家手机号',
  `sku_name` VARCHAR(50) NULL COMMENT '商品名称',
  `warehouse_city` VARCHAR(20) NULL COMMENT '申请仓库所属城市',
  `city` VARCHAR(20) NULL COMMENT '商家所在城市',
  `apply_quantity` INT NULL COMMENT '申请数量',
  `status` SMALLINT NULL COMMENT '状态 0-4',
  `merchant_remark` VARCHAR(500) NULL COMMENT '商家备注',
  `reject_remark` VARCHAR(500) NULL COMMENT '未通过审核备注',
  `fee` DECIMAL(10,2) NULL COMMENT '入库申请支付金额',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入库申请';

-- -----------------------------------------------------------------------------
-- logi-wms: wms_inbound
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `wms_inbound` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goods_id` VARCHAR(255) NULL COMMENT '商品id(对接商城/portal)',
  `warehouse_id` BIGINT NULL COMMENT '仓库id',
  `location_id` BIGINT NULL COMMENT '库位id',
  `inbound_id` VARCHAR(255) NULL COMMENT '入库关联id',
  `merchant_id` VARCHAR(255) NULL COMMENT '商家id',
  `quantity` INT NULL COMMENT '入存数量',
  `merchant_phone` VARCHAR(11) NULL COMMENT '商家手机号',
  `sku_name` VARCHAR(50) NULL COMMENT '商品名称',
  `sku_code` VARCHAR(50) NULL COMMENT '商品库存编号',
  `merchant_remark` VARCHAR(500) NULL COMMENT '商家备注',
  `status` SMALLINT NULL COMMENT '状态:0 待入库,1 已入库',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入库';

-- -----------------------------------------------------------------------------
-- logi-wms: wms_inbound_item
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `wms_inbound_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `inbound_id` VARCHAR(255) NULL COMMENT '入库关联id',
  `sku_name` VARCHAR(50) NULL COMMENT '商品名称',
  `sku_code` VARCHAR(50) NULL COMMENT '商品编号',
  `quantity` INT NULL COMMENT '入库数量',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入库明细';

-- -----------------------------------------------------------------------------
-- logi-wms: wms_outbound
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `wms_outbound` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `outbound_id` VARCHAR(255) NULL COMMENT '出库id',
  `order_id` VARCHAR(255) NULL COMMENT '关联订单id',
  `stock_id` VARCHAR(255) NULL COMMENT '库存id',
  `warehouse_id` BIGINT NULL COMMENT '仓库id',
  `location_id` BIGINT NULL COMMENT '库位id',
  `sku_name` VARCHAR(50) NULL COMMENT '商品名称',
  `sku_code` VARCHAR(50) NULL COMMENT '商品库存编号',
  `user_phone` VARCHAR(11) NULL COMMENT '用户手机号',
  `merchant_phone` VARCHAR(11) NULL COMMENT '商家手机号',
  `city` VARCHAR(30) NULL COMMENT '用户城市',
  `status` SMALLINT NULL COMMENT '状态:0 待出库,1 已出库',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出库';

-- -----------------------------------------------------------------------------
-- logi-wms: wms_outbound_item
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `wms_outbound_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `outbound_id` VARCHAR(255) NULL COMMENT '出库关联id',
  `sku_name` VARCHAR(50) NULL COMMENT '商品名称',
  `sku_code` VARCHAR(50) NULL COMMENT '商品编号',
  `quantity` INT NULL COMMENT '出库数量',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出库明细';

-- -----------------------------------------------------------------------------
-- logi-tms: tms_driver
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tms_driver` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` VARCHAR(255) NULL COMMENT '关联sys_user.user_id',
  `vehicle_no` VARCHAR(20) NULL COMMENT '车牌号',
  `current_city` VARCHAR(20) NULL COMMENT '负责城市',
  `weight` INT NULL COMMENT '权重',
  `status` SMALLINT NULL COMMENT '状态:0 空闲,1 运输中,2 休息,3 离职',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='司机';

-- -----------------------------------------------------------------------------
-- logi-tms: tms_vehicle
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tms_vehicle` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `driver_id` VARCHAR(255) NULL COMMENT '司机id',
  `vehicle_no` VARCHAR(20) NULL COMMENT '车牌号',
  `height` DECIMAL(10,2) NULL COMMENT '体高',
  `length` DECIMAL(10,2) NULL COMMENT '体长',
  `wide` DECIMAL(10,2) NULL COMMENT '体宽',
  `tonnage` DECIMAL(10,2) NULL COMMENT '吨位',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆';

-- -----------------------------------------------------------------------------
-- logi-tms: tms_transport_order
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tms_transport_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goods_id` VARCHAR(255) NULL COMMENT '商品id',
  `transport_order_id` VARCHAR(255) NULL COMMENT '运输订单id',
  `order_id` VARCHAR(255) NULL COMMENT '关联oms订单id',
  `warehouse_id` BIGINT NULL COMMENT '仓库id',
  `driver_id` VARCHAR(255) NULL COMMENT '司机id',
  `last_reject_driver_id` VARCHAR(255) NULL COMMENT '最近一次拒单司机',
  `origin` VARCHAR(20) NULL COMMENT '起点',
  `phone` VARCHAR(11) NULL COMMENT '电话',
  `dest` VARCHAR(20) NULL COMMENT '终点',
  `status` SMALLINT NULL COMMENT '运输状态 0-6',
  `fee` DECIMAL(10,2) NULL COMMENT '运输费用',
  `reject_count` INT NULL COMMENT '拒绝次数',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运输单';

-- -----------------------------------------------------------------------------
-- logi-tms: tms_logistic
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tms_logistic` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `transport_order_id` VARCHAR(255) NULL COMMENT '运输订单id',
  `driver_id` VARCHAR(255) NULL COMMENT '司机id',
  `city` VARCHAR(20) NULL COMMENT '实时位置/城市',
  `status` SMALLINT NULL COMMENT '物流状态:1 运输中,2 已送达',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流轨迹';

-- -----------------------------------------------------------------------------
-- logi-tms: tms_line
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tms_line` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `line_id` BIGINT NULL COMMENT '路线id',
  `origin` VARCHAR(20) NULL COMMENT '起点',
  `dest` VARCHAR(20) NULL COMMENT '终点',
  `status` SMALLINT NULL COMMENT '状态:0 禁用,1 启用',
  `estimation` DOUBLE NULL COMMENT '消耗时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路线';

-- -----------------------------------------------------------------------------
-- gl-message: mq_message_log
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `mq_message_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `message_id` VARCHAR(64) NULL COMMENT '消息id',
  `biz_id` VARCHAR(64) NULL COMMENT '关联id',
  `exchange` VARCHAR(255) NULL COMMENT '交换机名称',
  `routing_key` VARCHAR(255) NULL COMMENT '路由键',
  `message` VARCHAR(500) NULL COMMENT '具体信息',
  `status` SMALLINT NULL COMMENT '状态:1 成功,2 失败',
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='MQ消息日志';
