-- =============================================================================
-- page_profile: 经理端路线功能新增文案补丁
-- 包含：
-- 1) 经理端路线列表新增/更新/详情按钮、详情页标题与提示
-- 2) 手动派单司机弹窗文案
-- 3) 仓管端入库列表 / 入库申请文案
-- 执行方式：在目标库直接执行本文件（MySQL）
-- =============================================================================

SET NAMES utf8mb4;

-- 为避免重复，先删除本批 keys 的历史数据
DELETE FROM `dictionary`
WHERE `dict_type` = 'page_profile'
  AND `dict_value` IN (
    'btn_manager_add_line',
    'btn_manager_update_line',
    'btn_manager_line_detail',
    'manager_line_dialog_add_title',
    'manager_line_dialog_update_title',
    'manager_line_dialog_detail_title',
    'manager_line_submit_ok',
    'manager_line_submit_fail',
    'manager_line_detail_load_error',
    'manager_line_need_line_id',
    'manager_line_need_origin',
    'manager_line_need_dest',
    'manager_line_need_estimation',
    'manager_line_need_status',
    'doc_title_manager_line_detail',
    'section_manager_line_detail',
    'lead_manager_line_detail',
    'manager_line_detail_back',
    'manager_line_detail_missing_params',
    'manager_manual_assign_modal_title',
    'manager_manual_assign_modal_lead',
    'manager_manual_assign_pick_driver',
    'manager_manual_assign_drivers_empty',
    'manager_manual_assign_drivers_load_error',
    'col_driver_vehicle_no',
    'col_driver_current_city',
    'col_driver_status',
    'btn_confirm_assign',
    'nav_keeper_inbound_list',
    'nav_keeper_inbound_apply',
    'doc_title_keeper_inbound_list',
    'doc_title_keeper_inbound_apply',
    'section_keeper_inbound_list',
    'lead_keeper_inbound_list',
    'col_keeper_inbound_id',
    'col_keeper_sku_code',
    'col_keeper_sku_name',
    'btn_keeper_confirm_inbound',
    'keeper_inbound_empty',
    'keeper_inbound_load_error',
    'keeper_inbound_need_fields',
    'keeper_inbound_confirm_ok',
    'keeper_inbound_confirm_fail',
    'section_keeper_inbound_apply',
    'lead_keeper_inbound_apply',
    'col_keeper_apply_id',
    'btn_keeper_apply_pass',
    'btn_keeper_apply_reject',
    'keeper_apply_dialog_pass',
    'keeper_apply_dialog_reject',
    'keeper_apply_fee_label',
    'keeper_apply_remark_label',
    'keeper_apply_empty',
    'keeper_apply_load_error',
    'keeper_apply_need_fee',
    'keeper_apply_need_remark',
    'keeper_apply_pass_ok',
    'keeper_apply_reject_ok',
    'keeper_apply_action_fail',
    'nav_keeper_inbound_query',
    'doc_title_keeper_inbound_query',
    'section_keeper_inbound_query',
    'lead_keeper_inbound_query',
    'keeper_apply_not_found'
  );

INSERT INTO `dictionary` (`dict_type`, `dict_name`, `dict_value`, `sort`, `status`, `lang`) VALUES
('page_profile', '添加线路', 'btn_manager_add_line', 9001, 1, '1'),
('page_profile', 'Add route', 'btn_manager_add_line', 9001, 1, '2'),
('page_profile', 'Добавить маршрут', 'btn_manager_add_line', 9001, 1, '3'),

('page_profile', '更新', 'btn_manager_update_line', 9002, 1, '1'),
('page_profile', 'Update', 'btn_manager_update_line', 9002, 1, '2'),
('page_profile', 'Обновить', 'btn_manager_update_line', 9002, 1, '3'),

('page_profile', '详情', 'btn_manager_line_detail', 9003, 1, '1'),
('page_profile', 'Detail', 'btn_manager_line_detail', 9003, 1, '2'),
('page_profile', 'Детали', 'btn_manager_line_detail', 9003, 1, '3'),

('page_profile', '添加线路', 'manager_line_dialog_add_title', 9004, 1, '1'),
('page_profile', 'Add route', 'manager_line_dialog_add_title', 9004, 1, '2'),
('page_profile', 'Добавить маршрут', 'manager_line_dialog_add_title', 9004, 1, '3'),

('page_profile', '更新线路', 'manager_line_dialog_update_title', 9005, 1, '1'),
('page_profile', 'Update route', 'manager_line_dialog_update_title', 9005, 1, '2'),
('page_profile', 'Обновить маршрут', 'manager_line_dialog_update_title', 9005, 1, '3'),

('page_profile', '线路详情', 'manager_line_dialog_detail_title', 9006, 1, '1'),
('page_profile', 'Route detail', 'manager_line_dialog_detail_title', 9006, 1, '2'),
('page_profile', 'Детали маршрута', 'manager_line_dialog_detail_title', 9006, 1, '3'),

('page_profile', '提交成功', 'manager_line_submit_ok', 9007, 1, '1'),
('page_profile', 'Saved', 'manager_line_submit_ok', 9007, 1, '2'),
('page_profile', 'Сохранено', 'manager_line_submit_ok', 9007, 1, '3'),

('page_profile', '提交失败', 'manager_line_submit_fail', 9008, 1, '1'),
('page_profile', 'Save failed', 'manager_line_submit_fail', 9008, 1, '2'),
('page_profile', 'Не удалось сохранить', 'manager_line_submit_fail', 9008, 1, '3'),

('page_profile', '详情加载失败', 'manager_line_detail_load_error', 9009, 1, '1'),
('page_profile', 'Failed to load detail', 'manager_line_detail_load_error', 9009, 1, '2'),
('page_profile', 'Ошибка загрузки деталей', 'manager_line_detail_load_error', 9009, 1, '3'),

('page_profile', '请填写业务路线 ID', 'manager_line_need_line_id', 9010, 1, '1'),
('page_profile', 'Enter route ID', 'manager_line_need_line_id', 9010, 1, '2'),
('page_profile', 'Введите ID маршрута', 'manager_line_need_line_id', 9010, 1, '3'),

('page_profile', '请填写起点', 'manager_line_need_origin', 9011, 1, '1'),
('page_profile', 'Enter origin', 'manager_line_need_origin', 9011, 1, '2'),
('page_profile', 'Введите пункт отправления', 'manager_line_need_origin', 9011, 1, '3'),

('page_profile', '请填写终点', 'manager_line_need_dest', 9012, 1, '1'),
('page_profile', 'Enter destination', 'manager_line_need_dest', 9012, 1, '2'),
('page_profile', 'Введите пункт назначения', 'manager_line_need_dest', 9012, 1, '3'),

('page_profile', '请填写预计耗时（数字）', 'manager_line_need_estimation', 9013, 1, '1'),
('page_profile', 'Enter estimation', 'manager_line_need_estimation', 9013, 1, '2'),
('page_profile', 'Введите время в пути', 'manager_line_need_estimation', 9013, 1, '3'),

('page_profile', '请填写状态（0/1）', 'manager_line_need_status', 9014, 1, '1'),
('page_profile', 'Enter status (0/1)', 'manager_line_need_status', 9014, 1, '2'),
('page_profile', 'Введите статус (0/1)', 'manager_line_need_status', 9014, 1, '3'),

('page_profile', '路线详情', 'doc_title_manager_line_detail', 9015, 1, '1'),
('page_profile', 'Route detail', 'doc_title_manager_line_detail', 9015, 1, '2'),
('page_profile', 'Детали маршрута', 'doc_title_manager_line_detail', 9015, 1, '3'),

('page_profile', '路线详情', 'section_manager_line_detail', 9016, 1, '1'),
('page_profile', 'Route detail', 'section_manager_line_detail', 9016, 1, '2'),
('page_profile', 'Детали маршрута', 'section_manager_line_detail', 9016, 1, '3'),

('page_profile', '上半部分展示线路地图，下半部分展示线路基础信息。', 'lead_manager_line_detail', 9017, 1, '1'),
('page_profile', 'Top panel shows route map, bottom panel shows route metadata.', 'lead_manager_line_detail', 9017, 1, '2'),
('page_profile', 'Сверху карта маршрута, снизу карточка с параметрами маршрута.', 'lead_manager_line_detail', 9017, 1, '3'),

('page_profile', '返回路线列表', 'manager_line_detail_back', 9018, 1, '1'),
('page_profile', 'Back to route list', 'manager_line_detail_back', 9018, 1, '2'),
('page_profile', 'Назад к списку маршрутов', 'manager_line_detail_back', 9018, 1, '3'),

('page_profile', '缺少起点或终点参数，请从路线列表进入详情。', 'manager_line_detail_missing_params', 9019, 1, '1'),
('page_profile', 'Missing origin/destination. Open detail from route list.', 'manager_line_detail_missing_params', 9019, 1, '2'),
('page_profile', 'Не хватает параметров origin/dest. Откройте детали из списка.', 'manager_line_detail_missing_params', 9019, 1, '3'),

('page_profile', '选择司机', 'manager_manual_assign_modal_title', 9020, 1, '1'),
('page_profile', 'Choose driver', 'manager_manual_assign_modal_title', 9020, 1, '2'),
('page_profile', 'Выберите водителя', 'manager_manual_assign_modal_title', 9020, 1, '3'),

('page_profile', '运输单：', 'manager_manual_assign_modal_lead', 9021, 1, '1'),
('page_profile', 'Shipment: ', 'manager_manual_assign_modal_lead', 9021, 1, '2'),
('page_profile', 'Перевозка: ', 'manager_manual_assign_modal_lead', 9021, 1, '3'),

('page_profile', '请先在列表中点选一名司机', 'manager_manual_assign_pick_driver', 9022, 1, '1'),
('page_profile', 'Select a driver from the list', 'manager_manual_assign_pick_driver', 9022, 1, '2'),
('page_profile', 'Выберите водителя в списке', 'manager_manual_assign_pick_driver', 9022, 1, '3'),

('page_profile', '暂无空闲司机', 'manager_manual_assign_drivers_empty', 9023, 1, '1'),
('page_profile', 'No available drivers', 'manager_manual_assign_drivers_empty', 9023, 1, '2'),
('page_profile', 'Нет свободных водителей', 'manager_manual_assign_drivers_empty', 9023, 1, '3'),

('page_profile', '司机列表加载失败', 'manager_manual_assign_drivers_load_error', 9024, 1, '1'),
('page_profile', 'Failed to load drivers', 'manager_manual_assign_drivers_load_error', 9024, 1, '2'),
('page_profile', 'Не удалось загрузить список', 'manager_manual_assign_drivers_load_error', 9024, 1, '3'),

('page_profile', '车牌', 'col_driver_vehicle_no', 9025, 1, '1'),
('page_profile', 'Plate', 'col_driver_vehicle_no', 9025, 1, '2'),
('page_profile', 'Номер', 'col_driver_vehicle_no', 9025, 1, '3'),

('page_profile', '所在城市', 'col_driver_current_city', 9026, 1, '1'),
('page_profile', 'City', 'col_driver_current_city', 9026, 1, '2'),
('page_profile', 'Город', 'col_driver_current_city', 9026, 1, '3'),

('page_profile', '状态', 'col_driver_status', 9027, 1, '1'),
('page_profile', 'Status', 'col_driver_status', 9027, 1, '2'),
('page_profile', 'Статус', 'col_driver_status', 9027, 1, '3'),

('page_profile', '确认指派', 'btn_confirm_assign', 9028, 1, '1'),
('page_profile', 'Confirm assign', 'btn_confirm_assign', 9028, 1, '2'),
('page_profile', 'Подтвердить', 'btn_confirm_assign', 9028, 1, '3'),

('page_profile', '入库列表', 'nav_keeper_inbound_list', 9029, 1, '1'),
('page_profile', 'Inbound list', 'nav_keeper_inbound_list', 9029, 1, '2'),
('page_profile', 'Список приемки', 'nav_keeper_inbound_list', 9029, 1, '3'),

('page_profile', '入库申请', 'nav_keeper_inbound_apply', 9030, 1, '1'),
('page_profile', 'Inbound applications', 'nav_keeper_inbound_apply', 9030, 1, '2'),
('page_profile', 'Заявки на приемку', 'nav_keeper_inbound_apply', 9030, 1, '3'),

('page_profile', '入库列表', 'doc_title_keeper_inbound_list', 9031, 1, '1'),
('page_profile', 'Inbound list', 'doc_title_keeper_inbound_list', 9031, 1, '2'),
('page_profile', 'Список приемки', 'doc_title_keeper_inbound_list', 9031, 1, '3'),

('page_profile', '入库申请', 'doc_title_keeper_inbound_apply', 9032, 1, '1'),
('page_profile', 'Inbound applications', 'doc_title_keeper_inbound_apply', 9032, 1, '2'),
('page_profile', 'Заявки на приемку', 'doc_title_keeper_inbound_apply', 9032, 1, '3'),

('page_profile', '入库列表', 'section_keeper_inbound_list', 9033, 1, '1'),
('page_profile', 'Inbound list', 'section_keeper_inbound_list', 9033, 1, '2'),
('page_profile', 'Список приемки', 'section_keeper_inbound_list', 9033, 1, '3'),

('page_profile', '分页查看入库单并执行确认入库。', 'lead_keeper_inbound_list', 9034, 1, '1'),
('page_profile', 'Browse inbound orders and confirm inbound.', 'lead_keeper_inbound_list', 9034, 1, '2'),
('page_profile', 'Постраничный список приемки и подтверждение приемки.', 'lead_keeper_inbound_list', 9034, 1, '3'),

('page_profile', '入库单号', 'col_keeper_inbound_id', 9035, 1, '1'),
('page_profile', 'Inbound ID', 'col_keeper_inbound_id', 9035, 1, '2'),
('page_profile', 'ID приемки', 'col_keeper_inbound_id', 9035, 1, '3'),

('page_profile', 'SKU 编码', 'col_keeper_sku_code', 9036, 1, '1'),
('page_profile', 'SKU code', 'col_keeper_sku_code', 9036, 1, '2'),
('page_profile', 'Код SKU', 'col_keeper_sku_code', 9036, 1, '3'),

('page_profile', 'SKU 名称', 'col_keeper_sku_name', 9037, 1, '1'),
('page_profile', 'SKU name', 'col_keeper_sku_name', 9037, 1, '2'),
('page_profile', 'Название SKU', 'col_keeper_sku_name', 9037, 1, '3'),

('page_profile', '确认入库', 'btn_keeper_confirm_inbound', 9038, 1, '1'),
('page_profile', 'Confirm inbound', 'btn_keeper_confirm_inbound', 9038, 1, '2'),
('page_profile', 'Подтвердить приемку', 'btn_keeper_confirm_inbound', 9038, 1, '3'),

('page_profile', '暂无入库单', 'keeper_inbound_empty', 9039, 1, '1'),
('page_profile', 'No inbound orders', 'keeper_inbound_empty', 9039, 1, '2'),
('page_profile', 'Нет записей приемки', 'keeper_inbound_empty', 9039, 1, '3'),

('page_profile', '入库列表加载失败', 'keeper_inbound_load_error', 9040, 1, '1'),
('page_profile', 'Failed to load inbound list', 'keeper_inbound_load_error', 9040, 1, '2'),
('page_profile', 'Ошибка загрузки списка приемки', 'keeper_inbound_load_error', 9040, 1, '3'),

('page_profile', '缺少 inboundId 或 skuCode，无法确认入库', 'keeper_inbound_need_fields', 9041, 1, '1'),
('page_profile', 'Missing inboundId or skuCode', 'keeper_inbound_need_fields', 9041, 1, '2'),
('page_profile', 'Не хватает inboundId или skuCode', 'keeper_inbound_need_fields', 9041, 1, '3'),

('page_profile', '确认入库成功', 'keeper_inbound_confirm_ok', 9042, 1, '1'),
('page_profile', 'Inbound confirmed', 'keeper_inbound_confirm_ok', 9042, 1, '2'),
('page_profile', 'Приемка подтверждена', 'keeper_inbound_confirm_ok', 9042, 1, '3'),

('page_profile', '确认入库失败', 'keeper_inbound_confirm_fail', 9043, 1, '1'),
('page_profile', 'Confirm inbound failed', 'keeper_inbound_confirm_fail', 9043, 1, '2'),
('page_profile', 'Не удалось подтвердить приемку', 'keeper_inbound_confirm_fail', 9043, 1, '3'),

('page_profile', '入库申请', 'section_keeper_inbound_apply', 9044, 1, '1'),
('page_profile', 'Inbound applications', 'section_keeper_inbound_apply', 9044, 1, '2'),
('page_profile', 'Заявки на приемку', 'section_keeper_inbound_apply', 9044, 1, '3'),

('page_profile', '分页查看入库申请并执行通过/退回。', 'lead_keeper_inbound_apply', 9045, 1, '1'),
('page_profile', 'Review inbound applications with approve/reject actions.', 'lead_keeper_inbound_apply', 9045, 1, '2'),
('page_profile', 'Постраничный просмотр заявок с действиями одобрить/отклонить.', 'lead_keeper_inbound_apply', 9045, 1, '3'),

('page_profile', '申请单号', 'col_keeper_apply_id', 9046, 1, '1'),
('page_profile', 'Apply ID', 'col_keeper_apply_id', 9046, 1, '2'),
('page_profile', 'ID заявки', 'col_keeper_apply_id', 9046, 1, '3'),

('page_profile', '通过', 'btn_keeper_apply_pass', 9047, 1, '1'),
('page_profile', 'Approve', 'btn_keeper_apply_pass', 9047, 1, '2'),
('page_profile', 'Одобрить', 'btn_keeper_apply_pass', 9047, 1, '3'),

('page_profile', '退回', 'btn_keeper_apply_reject', 9048, 1, '1'),
('page_profile', 'Reject', 'btn_keeper_apply_reject', 9048, 1, '2'),
('page_profile', 'Отклонить', 'btn_keeper_apply_reject', 9048, 1, '3'),

('page_profile', '通过入库申请', 'keeper_apply_dialog_pass', 9049, 1, '1'),
('page_profile', 'Approve inbound application', 'keeper_apply_dialog_pass', 9049, 1, '2'),
('page_profile', 'Одобрить заявку', 'keeper_apply_dialog_pass', 9049, 1, '3'),

('page_profile', '退回入库申请', 'keeper_apply_dialog_reject', 9050, 1, '1'),
('page_profile', 'Reject inbound application', 'keeper_apply_dialog_reject', 9050, 1, '2'),
('page_profile', 'Отклонить заявку', 'keeper_apply_dialog_reject', 9050, 1, '3'),

('page_profile', '费用', 'keeper_apply_fee_label', 9051, 1, '1'),
('page_profile', 'Fee', 'keeper_apply_fee_label', 9051, 1, '2'),
('page_profile', 'Стоимость', 'keeper_apply_fee_label', 9051, 1, '3'),

('page_profile', '退回备注', 'keeper_apply_remark_label', 9052, 1, '1'),
('page_profile', 'Reject remark', 'keeper_apply_remark_label', 9052, 1, '2'),
('page_profile', 'Комментарий отклонения', 'keeper_apply_remark_label', 9052, 1, '3'),

('page_profile', '暂无入库申请', 'keeper_apply_empty', 9053, 1, '1'),
('page_profile', 'No inbound applications', 'keeper_apply_empty', 9053, 1, '2'),
('page_profile', 'Нет заявок на приемку', 'keeper_apply_empty', 9053, 1, '3'),

('page_profile', '入库申请加载失败', 'keeper_apply_load_error', 9054, 1, '1'),
('page_profile', 'Failed to load applications', 'keeper_apply_load_error', 9054, 1, '2'),
('page_profile', 'Ошибка загрузки заявок', 'keeper_apply_load_error', 9054, 1, '3'),

('page_profile', '请填写合法费用', 'keeper_apply_need_fee', 9055, 1, '1'),
('page_profile', 'Enter a valid fee', 'keeper_apply_need_fee', 9055, 1, '2'),
('page_profile', 'Укажите корректную стоимость', 'keeper_apply_need_fee', 9055, 1, '3'),

('page_profile', '请填写退回备注', 'keeper_apply_need_remark', 9056, 1, '1'),
('page_profile', 'Enter reject remark', 'keeper_apply_need_remark', 9056, 1, '2'),
('page_profile', 'Введите комментарий отклонения', 'keeper_apply_need_remark', 9056, 1, '3'),

('page_profile', '已通过入库申请', 'keeper_apply_pass_ok', 9057, 1, '1'),
('page_profile', 'Application approved', 'keeper_apply_pass_ok', 9057, 1, '2'),
('page_profile', 'Заявка одобрена', 'keeper_apply_pass_ok', 9057, 1, '3'),

('page_profile', '已退回入库申请', 'keeper_apply_reject_ok', 9058, 1, '1'),
('page_profile', 'Application rejected', 'keeper_apply_reject_ok', 9058, 1, '2'),
('page_profile', 'Заявка отклонена', 'keeper_apply_reject_ok', 9058, 1, '3'),

('page_profile', '操作失败', 'keeper_apply_action_fail', 9059, 1, '1'),
('page_profile', 'Action failed', 'keeper_apply_action_fail', 9059, 1, '2'),
('page_profile', 'Операция не выполнена', 'keeper_apply_action_fail', 9059, 1, '3'),

('page_profile', '入库查询', 'nav_keeper_inbound_query', 9060, 1, '1'),
('page_profile', 'Inbound query', 'nav_keeper_inbound_query', 9060, 1, '2'),
('page_profile', 'Поиск приемки', 'nav_keeper_inbound_query', 9060, 1, '3'),

('page_profile', '入库查询', 'doc_title_keeper_inbound_query', 9061, 1, '1'),
('page_profile', 'Inbound query', 'doc_title_keeper_inbound_query', 9061, 1, '2'),
('page_profile', 'Поиск приемки', 'doc_title_keeper_inbound_query', 9061, 1, '3'),

('page_profile', '入库查询', 'section_keeper_inbound_query', 9062, 1, '1'),
('page_profile', 'Inbound query', 'section_keeper_inbound_query', 9062, 1, '2'),
('page_profile', 'Поиск приемки', 'section_keeper_inbound_query', 9062, 1, '3'),

('page_profile', '输入申请单号查询单条入库申请。', 'lead_keeper_inbound_query', 9063, 1, '1'),
('page_profile', 'Enter apply ID to query one inbound application.', 'lead_keeper_inbound_query', 9063, 1, '2'),
('page_profile', 'Введите ID заявки для поиска одной записи.', 'lead_keeper_inbound_query', 9063, 1, '3'),

('page_profile', '未找到该入库申请', 'keeper_apply_not_found', 9064, 1, '1'),
('page_profile', 'Inbound application not found', 'keeper_apply_not_found', 9064, 1, '2'),
('page_profile', 'Заявка не найдена', 'keeper_apply_not_found', 9064, 1, '3');
