-- 与 logi-tms 实体 TmsTransportOrder 对齐（goodsId -> goods_id）
-- 适用于「库表已建好、后来代码才加字段」的情况；可重复执行。

SET @s := (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'tms_transport_order'
        AND COLUMN_NAME = 'goods_id'
    ),
    'SELECT 1',
    'ALTER TABLE `tms_transport_order` ADD COLUMN `goods_id` VARCHAR(255) NULL COMMENT ''商品id'' AFTER `id`'
  )
);
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
