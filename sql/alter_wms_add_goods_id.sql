-- 与 logi-wms 实体 WmsInboundApply、WmsInbound 对齐（goodsId -> goods_id）
-- 适用于「库表已建好、后来代码才加字段」的情况；CREATE TABLE IF NOT EXISTS 不会自动补列。
-- 可重复执行：已存在 goods_id 时跳过。

-- wms_inbound_apply
SET @s := (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'wms_inbound_apply'
        AND COLUMN_NAME = 'goods_id'
    ),
    'SELECT 1',
    'ALTER TABLE `wms_inbound_apply` ADD COLUMN `goods_id` VARCHAR(255) NULL COMMENT ''商品id(对接商城/portal)'' AFTER `id`'
  )
);
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- wms_inbound
SET @s := (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'wms_inbound'
        AND COLUMN_NAME = 'goods_id'
    ),
    'SELECT 1',
    'ALTER TABLE `wms_inbound` ADD COLUMN `goods_id` VARCHAR(255) NULL COMMENT ''商品id(对接商城/portal)'' AFTER `id`'
  )
);
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
