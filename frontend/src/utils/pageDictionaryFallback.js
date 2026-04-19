import { uiLangToDictLang } from '@/constants/dictLang.js'

/**
 * 与 sql/dictionary_frontend_inserts_mysql.sql 中 page_login / page_register 各 lang 行对齐。
 * 远程字典未同步或缺行时，按界面语言兜底，避免校验提示始终为中文。
 * dict_lang: 1=中文 2=英文 3=俄文
 */
const BY_TYPE = {
  page_login: {
    err_role_required: {
      '1': '请选择身份',
      '2': 'Please choose a role',
      '3': 'Выберите роль',
    },
    err_username_required: {
      '1': '请填写用户名',
      '2': 'Please enter your username',
      '3': 'Введите имя пользователя',
    },
    err_password_required: {
      '1': '请填写密码',
      '2': 'Please enter your password',
      '3': 'Введите пароль',
    },
  },
  page_register: {
    err_must_agree: {
      '1': '请先阅读并勾选同意服务条款与隐私政策',
      '2': 'Please read and accept the terms and privacy policy',
      '3': 'Примите условия и политику конфиденциальности',
    },
    err_username_required: {
      '1': '请填写用户名',
      '2': 'Please enter a username',
      '3': 'Введите имя пользователя',
    },
    err_password_required: {
      '1': '请填写密码',
      '2': 'Please enter a password',
      '3': 'Введите пароль',
    },
    err_phone_required: {
      '1': '请填写手机号',
      '2': 'Please enter your mobile number',
      '3': 'Введите номер телефона',
    },
    err_phone_invalid: {
      '1': '请输入正确的11位手机号',
      '2': 'Enter a valid 11-digit mobile number',
      '3': 'Введите корректный 11-значный номер',
    },
    err_verify_required: {
      '1': '请填写验证码',
      '2': 'Please enter the verification code',
      '3': 'Введите код подтверждения',
    },
    err_verify_format: {
      '1': '请输入6位数字验证码',
      '2': 'Enter the 6-digit code',
      '3': 'Введите 6 цифр кода',
    },
    err_user_type_required: {
      '1': '请选择身份',
      '2': 'Please choose a role',
      '3': 'Выберите роль',
    },
  },
  page_profile: {
    sidebar_welcome: {
      '1': '欢迎回来，{name}{role}！',
      '2': 'Welcome back, {name}{role}!',
      '3': 'С возвращением, {name}{role}!',
    },
    doc_title: {
      '1': '个人中心',
      '2': 'Profile',
      '3': 'Профиль',
    },
    value_empty: {
      '1': '—',
      '2': '—',
      '3': '—',
    },
    aria_sidebar_nav: {
      '1': '个人中心侧栏导航',
      '2': 'Profile navigation',
      '3': 'Навигация профиля',
    },
    nav_register_application: {
      '1': '注册申请',
      '2': 'Registration requests',
      '3': 'Заявки на регистрацию',
    },
    section_register_application: {
      '1': '注册申请',
      '2': 'Registration requests',
      '3': 'Заявки на регистрацию',
    },
    lead_register_application: {
      '1': '查看并处理用户提交的账号注册申请。',
      '2': 'Review and process account registration requests.',
      '3': 'Просмотр и обработка заявок на регистрацию аккаунтов.',
    },
    doc_title_register_apps: {
      '1': '注册申请',
      '2': 'Registration requests',
      '3': 'Заявки на регистрацию',
    },
    col_username: { '1': '用户名', '2': 'Username', '3': 'Имя пользователя' },
    col_nickname: { '1': '昵称', '2': 'Nickname', '3': 'Никнейм' },
    col_phone: { '1': '手机号', '2': 'Phone', '3': 'Телефон' },
    col_user_type: { '1': '申请身份', '2': 'Requested role', '3': 'Запрошенная роль' },
    col_status: { '1': '状态', '2': 'Status', '3': 'Статус' },
    status_pending: { '1': '待审核', '2': 'Pending', '3': 'На проверке' },
    status_approved: { '1': '已通过', '2': 'Approved', '3': 'Одобрено' },
    status_rejected: { '1': '未通过', '2': 'Rejected', '3': 'Отклонено' },
    register_apps_empty: { '1': '暂无申请记录', '2': 'No requests yet', '3': 'Нет заявок' },
    register_apps_load_error: { '1': '加载失败', '2': 'Failed to load', '3': 'Не удалось загрузить' },
    register_apps_loading: { '1': '加载中…', '2': 'Loading…', '3': 'Загрузка…' },
    col_actions: { '1': '操作', '2': 'Actions', '3': 'Действия' },
    btn_approve: { '1': '通过', '2': 'Approve', '3': 'Одобрить' },
    btn_reject: { '1': '退回', '2': 'Reject', '3': 'Отклонить' },
    nav_order_review: { '1': '商品审核', '2': 'Product review', '3': 'Проверка товаров' },
    section_order_review: {
      '1': '商品审核',
      '2': 'Product order review',
      '3': 'Проверка заказов на товары',
    },
    lead_order_review: {
      '1': '待审核的特殊商品订单将显示在下方列表中。',
      '2': 'Special product orders pending review are listed below.',
      '3': 'Ниже — заказы на особые товары, ожидающие проверки.',
    },
    doc_title_order_review: { '1': '商品审核', '2': 'Product review', '3': 'Проверка товаров' },
    col_order_id: { '1': '订单号', '2': 'Order ID', '3': 'Номер заказа' },
    col_sku_name: { '1': '商品名称', '2': 'Product name', '3': 'Название товара' },
    col_sku_code: { '1': '商品编号', '2': 'SKU code', '3': 'Артикул' },
    col_product_type: { '1': '商品类型', '2': 'Product type', '3': 'Тип товара' },
    col_quantity: { '1': '数量', '2': 'Quantity', '3': 'Кол-во' },
    col_review_status: { '1': '审核状态', '2': 'Review status', '3': 'Статус проверки' },
    col_remark: { '1': '备注', '2': 'Remark', '3': 'Примечание' },
    order_review_status_pending: { '1': '待审核', '2': 'Pending', '3': 'На проверке' },
    order_review_status_done: { '1': '已审核', '2': 'Reviewed', '3': 'Проверено' },
    order_review_empty: { '1': '暂无待审核订单', '2': 'No orders to review', '3': 'Нет заказов для проверки' },
    order_review_load_error: { '1': '加载失败', '2': 'Failed to load', '3': 'Ошибка загрузки' },
    nav_super_users: { '1': '用户列表', '2': 'User list', '3': 'Список пользователей' },
    nav_super_by_type: { '1': '身份查询', '2': 'Search by role', '3': 'Поиск по роли' },
    nav_super_by_id: { '1': 'ID 查询', '2': 'Search by ID', '3': 'Поиск по ID' },
    section_super_users: { '1': '用户列表', '2': 'User list', '3': 'Список пользователей' },
    section_super_by_type: { '1': '按身份查询用户', '2': 'Users by role', '3': 'Пользователи по роли' },
    section_super_by_id: { '1': '按用户 ID 查询', '2': 'User by ID', '3': 'Пользователь по ID' },
    lead_super_users: {
      '1': '分页查看全部系统用户，可更新或删除。',
      '2': 'Browse all users by page. Update or delete accounts.',
      '3': 'Постраничный список пользователей; можно изменить или удалить.',
    },
    lead_super_by_type: {
      '1': '选择身份类型后分页查看该类型用户，可更新或删除。',
      '2': 'Choose a role, browse matching users, update or delete.',
      '3': 'Выберите роль и просматривайте пользователей этой роли.',
    },
    lead_super_by_id: {
      '1': '输入用户 ID 查询单个用户，可更新或删除。',
      '2': 'Enter a user ID to load one account, then update or delete.',
      '3': 'Введите ID пользователя для поиска одной записи.',
    },
    doc_title_super_users: { '1': '用户列表', '2': 'User list', '3': 'Список пользователей' },
    doc_title_super_by_type: { '1': '身份查询', '2': 'Search by role', '3': 'Поиск по роли' },
    doc_title_super_by_id: { '1': 'ID 查询', '2': 'Search by ID', '3': 'Поиск по ID' },
    col_sys_user_id: { '1': '用户 ID', '2': 'User ID', '3': 'ID пользователя' },
    super_col_user_type: { '1': '身份', '2': 'Role', '3': 'Роль' },
    btn_update_user: { '1': '更新', '2': 'Update', '3': 'Обновить' },
    btn_delete_user: { '1': '删除', '2': 'Delete', '3': 'Удалить' },
    super_btn_search: { '1': '查询', '2': 'Search', '3': 'Найти' },
    super_btn_prev: { '1': '上一页', '2': 'Previous', '3': 'Назад' },
    super_btn_next: { '1': '下一页', '2': 'Next', '3': 'Далее' },
    super_filter_user_type: { '1': '身份', '2': 'Role', '3': 'Роль' },
    super_filter_user_id: { '1': '用户 ID', '2': 'User ID', '3': 'ID пользователя' },
    dialog_edit_user_title: { '1': '编辑用户', '2': 'Edit user', '3': 'Редактировать' },
    btn_save: { '1': '保存', '2': 'Save', '3': 'Сохранить' },
    btn_cancel: { '1': '取消', '2': 'Cancel', '3': 'Отмена' },
    super_empty: { '1': '暂无用户', '2': 'No users', '3': 'Нет пользователей' },
    super_load_error: { '1': '加载失败', '2': 'Failed to load', '3': 'Ошибка загрузки' },
    super_by_id_hint: {
      '1': '请输入用户 ID 后点击查询。',
      '2': 'Enter a user ID and click Search.',
      '3': 'Введите ID и нажмите «Найти».',
    },
    super_user_not_found: {
      '1': '该用户不存在',
      '2': 'No user found for this ID',
      '3': 'Пользователь с таким ID не найден',
    },
    super_confirm_delete: {
      '1': '确定删除用户 {userId} 吗？此操作不可恢复。',
      '2': 'Delete user {userId}? This cannot be undone.',
      '3': 'Удалить пользователя {userId}? Действие необратимо.',
    },
  },
}

/**
 * @param {string} dictType
 * @param {string} dictValue
 * @param {string} uiLang 来自 useUiLang（zh-CN | en | ru）
 */
export function pageDictFallback(dictType, dictValue, uiLang) {
  const lang = uiLangToDictLang(uiLang)
  const row = BY_TYPE[dictType]?.[dictValue]
  if (!row) return ''
  return row[lang] ?? row['1'] ?? ''
}
