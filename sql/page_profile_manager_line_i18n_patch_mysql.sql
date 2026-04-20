-- =============================================================================
-- page_profile: 经理端路线功能新增文案补丁
-- 包含：路线列表新增/更新/详情按钮、详情页标题与提示、手动派单司机弹窗文案
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
    'btn_confirm_assign'
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
('page_profile', 'Подтвердить', 'btn_confirm_assign', 9028, 1, '3');
