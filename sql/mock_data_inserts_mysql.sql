-- =============================================================================
-- 模拟数据 INSERT（与 ddl_from_entities_mysql.sql 表结构一致）
-- 不包含：sys_user、dictionary、register_application、tms_driver、tms_vehicle
-- 执行前请确认库名；若已有主键冲突可先 TRUNCATE 对应表（勿动上述排除表）。
-- =============================================================================

SET NAMES utf8mb4;

-- -----------------------------------------------------------------------------
-- logi-oms: oms_order（与订单项、审核、出库、运输单等共用 order_id）
-- -----------------------------------------------------------------------------
INSERT INTO `oms_order` (`id`, `order_id`, `merchant_id`, `user_id`, `goods_id`, `warehouse_id`, `location_id`, `sku_name`, `sku_code`, `price`, `user_phone`, `merchant_phone`, `warehouse_city`, `city`, `category`, `type`, `status`, `remark`, `create_time`, `update_time`) VALUES
(1, 'ORD202604001', 'MCH001', 'U10001', 'STK-SKU001', 1, 1001, '加厚纸箱 60*40', 'SKU001', 128.00, '13900001001', '13800001001', '上海', '杭州', 0, '标品', 2, '普通订单示例', '2026-04-01 10:00:00', '2026-04-01 10:30:00'),
(2, 'ORD202604002', 'MCH001', 'U10002', 'STK-SKU002', 1, 1002, '冷链保温箱 小号', 'SKU002', 899.00, '13900001002', '13800001001', '上海', '苏州', 1, '冷链', 0, '特殊商品待审示例', '2026-04-02 11:00:00', '2026-04-02 11:00:00'),
(3, 'ORD202604003', 'MCH002', 'U10001', 'STK-SKU001', 2, 2001, '加厚纸箱 60*40', 'SKU001', 99.50, '13900001001', '13900002001', '北京', '北京', 0, '标品', 1, '已支付待履约', '2026-04-03 09:15:00', '2026-04-03 09:20:00');

INSERT INTO `oms_order_item` (`id`, `order_id`, `sku_name`, `sku_code`, `price`, `quantity`, `total_price`, `create_time`) VALUES
(1, 'ORD202604001', '加厚纸箱 60*40', 'SKU001', 64.00, 2, 128.00, '2026-04-01 10:00:00'),
(2, 'ORD202604002', '冷链保温箱 小号', 'SKU002', 899.00, 1, 899.00, '2026-04-02 11:00:00'),
(3, 'ORD202604003', '加厚纸箱 60*40', 'SKU001', 99.50, 1, 99.50, '2026-04-03 09:15:00');

INSERT INTO `oms_order_review` (`id`, `order_id`, `sku_name`, `sku_code`, `type`, `status`, `create_time`, `update_time`) VALUES
(1, 'ORD202604002', '冷链保温箱 小号', 'SKU002', '冷链', 0, '2026-04-02 11:05:00', '2026-04-02 11:05:00'),
(2, 'ORD202604001', '加厚纸箱 60*40', 'SKU001', '标品', 1, '2026-04-01 12:00:00', '2026-04-01 12:30:00');

-- -----------------------------------------------------------------------------
-- logi-wms: wms_warehouse / wms_stock / wms_stock_lock
-- -----------------------------------------------------------------------------
INSERT INTO `wms_warehouse` (`warehouse_id`, `city`, `country`, `address`, `status`, `create_time`, `update_time`) VALUES
(1, '上海', '中国', '浦东新区临港物流园 A3', 1, '2026-01-01 08:00:00', '2026-01-01 08:00:00'),
(2, '北京', '中国', '大兴区京南物流基地 B1', 1, '2026-01-01 08:00:00', '2026-01-01 08:00:00');

INSERT INTO `wms_stock` (`id`, `stock_id`, `warehouse_id`, `location_id`, `sku_name`, `sku_code`, `stock_quantity`, `lock_quantity`, `create_time`, `update_time`) VALUES
(1, 'STK-SKU001', 1, 1001, '加厚纸箱 60*40', 'SKU001', 500, 0, '2026-01-10 10:00:00', '2026-04-01 10:00:00'),
(2, 'STK-SKU002', 1, 1002, '冷链保温箱 小号', 'SKU002', 80, 10, '2026-01-10 10:00:00', '2026-04-02 11:00:00'),
(3, 'STK-SKU001', 2, 2001, '加厚纸箱 60*40', 'SKU001', 200, 0, '2026-01-15 10:00:00', '2026-04-03 09:15:00');

INSERT INTO `wms_stock_lock` (`id`, `warehouse_id`, `order_id`, `stock_id`, `location_id`, `sku_name`, `sku_code`, `user_phone`, `merchant_phone`, `city`, `lock_quantity`, `status`, `create_time`, `update_time`) VALUES
(1, 1, 'ORD202604002', 'STK-SKU002', 1002, '冷链保温箱 小号', 'SKU002', '13900001002', '13800001001', '苏州', 1, 1, '2026-04-02 11:10:00', '2026-04-02 11:10:00');

-- -----------------------------------------------------------------------------
-- logi-wms: 入库申请 / 入库 / 明细
-- -----------------------------------------------------------------------------
INSERT INTO `wms_inbound_apply` (`id`, `goods_id`, `warehouse_id`, `apply_id`, `merchant_id`, `merchant_phone`, `sku_name`, `warehouse_city`, `city`, `apply_quantity`, `status`, `merchant_remark`, `reject_remark`, `fee`, `create_time`, `update_time`) VALUES
(1, NULL, 1, 'INB-APPLY-001', 'MCH001', '13800001001', '加厚纸箱 60*40', '上海', '杭州', 100, 1, '首批补货', NULL, 50.00, '2026-04-05 14:00:00', '2026-04-05 14:30:00'),
(2, NULL, 2, 'INB-APPLY-002', 'MCH002', '13900002001', '加厚纸箱 60*40', '北京', '北京', 50, 0, '北京仓补货', NULL, 30.00, '2026-04-06 09:00:00', '2026-04-06 09:00:00');

INSERT INTO `wms_inbound` (`id`, `goods_id`, `warehouse_id`, `location_id`, `inbound_id`, `merchant_id`, `quantity`, `merchant_phone`, `sku_name`, `sku_code`, `merchant_remark`, `status`, `create_time`, `update_time`) VALUES
(1, NULL, 1, 1001, 'INB-202604001', 'MCH001', 100, '13800001001', '加厚纸箱 60*40', 'SKU001', '首批到货', 1, '2026-04-05 16:00:00', '2026-04-05 17:00:00'),
(2, NULL, 2, 2001, 'INB-202604002', 'MCH002', 50, '13900002001', '加厚纸箱 60*40', 'SKU001', '北京仓到货', 0, '2026-04-06 10:00:00', '2026-04-06 10:00:00');

INSERT INTO `wms_inbound_item` (`id`, `inbound_id`, `sku_name`, `sku_code`, `quantity`, `create_time`, `update_time`) VALUES
(1, 'INB-202604001', '加厚纸箱 60*40', 'SKU001', 100, '2026-04-05 16:00:00', '2026-04-05 17:00:00'),
(2, 'INB-202604002', '加厚纸箱 60*40', 'SKU001', 50, '2026-04-06 10:00:00', '2026-04-06 10:00:00');

-- -----------------------------------------------------------------------------
-- logi-wms: 出库 / 明细
-- -----------------------------------------------------------------------------
INSERT INTO `wms_outbound` (`id`, `outbound_id`, `order_id`, `stock_id`, `warehouse_id`, `location_id`, `sku_name`, `sku_code`, `user_phone`, `merchant_phone`, `city`, `status`, `create_time`, `update_time`) VALUES
(1, 'OUB-202604001', 'ORD202604001', 'STK-SKU001', 1, 1001, '加厚纸箱 60*40', 'SKU001', '13900001001', '13800001001', '杭州', 1, '2026-04-01 14:00:00', '2026-04-01 15:00:00'),
(2, 'OUB-202604002', 'ORD202604003', 'STK-SKU001', 2, 2001, '加厚纸箱 60*40', 'SKU001', '13900001001', '13900002001', '北京', 0, '2026-04-03 10:00:00', '2026-04-03 10:00:00');

INSERT INTO `wms_outbound_item` (`id`, `outbound_id`, `sku_name`, `sku_code`, `quantity`, `create_time`, `update_time`) VALUES
(1, 'OUB-202604001', '加厚纸箱 60*40', 'SKU001', 2, '2026-04-01 14:00:00', '2026-04-01 15:00:00'),
(2, 'OUB-202604002', '加厚纸箱 60*40', 'SKU001', 1, '2026-04-03 10:00:00', '2026-04-03 10:00:00');

-- -----------------------------------------------------------------------------
-- logi-tms: tms_line（status=1 才会出现在管理员分页线路列表）
-- -----------------------------------------------------------------------------
INSERT INTO `tms_line` (`id`, `line_id`, `origin`, `dest`, `status`, `estimation`) VALUES
(1, 500001, '上海', '杭州', 1, 3.5),
(2, 500002, '上海', '苏州', 1, 2.0),
(3, 500003, '北京', '天津', 1, 1.5),
(4, 500004, '广州', '深圳', 0, 2.0);

-- -----------------------------------------------------------------------------
-- logi-tms: tms_transport_order（含 status=6 待人工派单示例）
-- -----------------------------------------------------------------------------
INSERT INTO `tms_transport_order` (`id`, `transport_order_id`, `order_id`, `warehouse_id`, `user_id`, `driver_id`, `last_reject_driver_id`, `origin`, `phone`, `dest`, `status`, `fee`, `reject_count`, `create_time`, `update_time`) VALUES
(1, 'TMS202604001', 'ORD202604001', 1, 'U10001', 'DRV001', NULL, '上海', '13900001001', '杭州', 2, 120.00, 0, '2026-04-01 11:00:00', '2026-04-01 13:00:00'),
(2, 'TMS202604002', 'ORD202604003', 2, 'U10001', NULL, NULL, '北京', '13900001001', '北京', 1, 80.00, 0, '2026-04-03 09:30:00', '2026-04-03 09:45:00'),
(3, 'TMS202604003', 'ORD202604002', 1, 'U10002', NULL, NULL, '上海', '13900001002', '苏州', 6, 200.00, 4, '2026-04-02 12:00:00', '2026-04-02 14:00:00');

INSERT INTO `tms_logistic` (`id`, `transport_order_id`, `driver_id`, `city`, `status`, `create_time`, `update_time`) VALUES
(1, 'TMS202604001', 'DRV001', '嘉兴', 1, '2026-04-01 12:00:00', '2026-04-01 12:30:00'),
(2, 'TMS202604001', 'DRV001', '杭州', 2, '2026-04-01 13:00:00', '2026-04-01 13:00:00'),
(3, 'TMS202604002', NULL, '北京', 1, '2026-04-03 10:00:00', '2026-04-03 10:00:00');

-- -----------------------------------------------------------------------------
-- gl-message: mq_message_log
-- -----------------------------------------------------------------------------
INSERT INTO `mq_message_log` (`id`, `message_id`, `biz_id`, `exchange`, `routing_key`, `message`, `status`, `create_time`, `update_time`) VALUES
(1, 'msg-uuid-001', 'ORD202604001', 'order.ttl.exchange', 'order.ttl.key', '{"orderId":"ORD202604001","event":"created"}', 1, '2026-04-01 10:05:00', '2026-04-01 10:05:00'),
(2, 'msg-uuid-002', 'TMS202604001', 'order.ttl.exchange', 'order.ttl.key', '{"transportOrderId":"TMS202604001","event":"assigned"}', 1, '2026-04-01 11:10:00', '2026-04-01 11:10:00'),
(3, 'msg-uuid-003', 'ORD202604002', 'order.ttl.exchange', 'order.ttl.key', '{"orderId":"ORD202604002","event":"review_pending"}', 2, '2026-04-02 11:20:00', '2026-04-02 11:20:00');
