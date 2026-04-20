import { uiLangToDictLang } from '@/constants/dictLang.js'

/**
 * Spring Boot 默认错误 JSON 的 error 字段常为英文句子（含空格），不是业务消息键；
 * 映射为 api_message 中已有键后再走字典。
 */
const SPRING_ERROR_TO_KEY = {
  'internal server error': 'internal_error',
  'bad request': 'generic_error',
  'not found': 'generic_error',
  'unauthorized': 'generic_error',
  'forbidden': 'generic_error',
  'service unavailable': 'generic_error',
}

/**
 * 与 sql/dictionary_frontend_inserts_mysql.sql、dictionary_api_message_inserts_mysql.sql 中
 * dict_value + lang 对齐；当远程字典未加载完、接口失败或语言行缺失时兜底，避免界面显示裸键名。
 * key: dict_value → { '1': 中文, '2': 英文, '3': 俄文 }
 */
const KEY_FALLBACK = {
  // api_message（常用）
  generic_error: { '1': '操作失败', '2': 'Something went wrong', '3': 'Ошибка' },
  internal_error: { '1': '内部错误', '2': 'Internal error', '3': 'Внутренняя ошибка' },
  operation_success: { '1': '操作成功', '2': 'Success', '3': 'Успешно' },
  operation_failed: { '1': '操作失败', '2': 'Failed', '3': 'Не выполнено' },
  validation_failed: { '1': '参数校验失败', '2': 'Validation failed', '3': 'Ошибка проверки' },
  unauthorized: { '1': '未授权', '2': 'Unauthorized', '3': 'Нет доступа' },
  forbidden: { '1': '禁止访问', '2': 'Forbidden', '3': 'Запрещено' },
  redis_unavailable: {
    '1': '缓存服务不可用，请确认 Redis 已启动并可连接',
    '2': 'Cache unavailable. Check Redis.',
    '3': 'Кэш недоступен. Проверьте Redis.',
  },
  redis_error: { '1': '缓存异常，请稍后重试', '2': 'Cache error. Try again.', '3': 'Ошибка кэша' },
  data_access_error: {
    '1': '数据访问异常，请稍后重试或联系管理员',
    '2': 'Data access error. Try again or contact support.',
    '3': 'Ошибка доступа к данным',
  },
  db_access_error: {
    '1': '数据库访问异常，请检查服务与连接配置',
    '2': 'Database error. Check configuration.',
    '3': 'Ошибка базы данных',
  },
  gateway_internal_error: { '1': '网关内部错误', '2': 'Gateway error', '3': 'Ошибка шлюза' },
  gateway_response_serialize_failed: {
    '1': '响应序列化失败',
    '2': 'Response serialization failed',
    '3': 'Ошибка сериализации',
  },
  // api_auth 登录
  login_failed: { '1': '登录失败', '2': 'Sign-in failed', '3': 'Ошибка входа' },
  login_response_missing_token: {
    '1': '登录响应缺少 token',
    '2': 'Missing token in response',
    '3': 'В ответе нет токена',
  },
  login_account_or_password_empty: {
    '1': '账号或密码不能为空',
    '2': 'Username or password is required',
    '3': 'Укажите логин и пароль',
  },
  login_role_invalid: { '1': '登录入口角色无效', '2': 'Invalid sign-in role', '3': 'Неверная роль входа' },
  login_account_not_found: { '1': '账号不存在', '2': 'Account not found', '3': 'Аккаунт не найден' },
  login_account_data_error: {
    '1': '账号数据异常，请联系管理员',
    '2': 'Account data error. Contact support.',
    '3': 'Ошибка данных аккаунта',
  },
  login_password_wrong: { '1': '密码错误', '2': 'Wrong password', '3': 'Неверный пароль' },
  login_account_disabled: { '1': '账号已被禁用', '2': 'Account is disabled', '3': 'Аккаунт заблокирован' },
  login_account_type_invalid: {
    '1': '账号类型无效，请联系管理员',
    '2': 'Invalid account type',
    '3': 'Недопустимый тип аккаунта',
  },
  login_role_mismatch: {
    '1': '所选角色与账号不符，请在上方选择正确的角色后再登录',
    '2': 'Role does not match this account. Select the correct role.',
    '3': 'Роль не совпадает с аккаунтом',
  },
  login_stp_error: {
    '1': '发生了异常,请稍后再试!',
    '2': 'Something went wrong. Try again later.',
    '3': 'Ошибка. Попробуйте позже.',
  },
  // api_auth 注册
  validation_register_username_required: {
    '1': '用户名不能为空',
    '2': 'Username is required',
    '3': 'Укажите имя пользователя',
  },
  validation_register_password_required: {
    '1': '密码不能为空',
    '2': 'Password is required',
    '3': 'Укажите пароль',
  },
  register_phone_invalid: { '1': '手机号格式不正确', '2': 'Invalid phone number', '3': 'Неверный номер телефона' },
  register_captcha_rate_limit: {
    '1': '发送过于频繁，请稍后再试',
    '2': 'Too many requests. Try again later.',
    '3': 'Слишком часто. Подождите.',
  },
  register_captcha_sent: { '1': '验证码已发送', '2': 'Verification code sent', '3': 'Код отправлен' },
  register_required_fields_incomplete: {
    '1': '用户名或密码不能为空',
    '2': 'Username, password and phone are required',
    '3': 'Заполните имя, пароль и телефон',
  },
  register_verify_required: {
    '1': '请填写6位数字验证码',
    '2': 'Enter the 6-digit code',
    '3': 'Введите 6-значный код',
  },
  register_verify_wrong: {
    '1': '验证码错误或已过期，请重新获取',
    '2': 'Invalid or expired code',
    '3': 'Неверный или просроченный код',
  },
  register_user_exists: { '1': '该用户已存在!', '2': 'This user already exists', '3': 'Пользователь уже существует' },
  register_duplicate_apply: {
    '1': '申请失败,你今天已发起过申请!',
    '2': 'You already submitted an application today',
    '3': 'Заявка уже отправлена сегодня',
  },
  register_apply_success: {
    '1': '申请成功,请等待审核!',
    '2': 'Application submitted. Please wait for review.',
    '3': 'Заявка отправлена. Ожидайте проверки.',
  },
  register_failed: { '1': '注册申请失败', '2': 'Registration failed', '3': 'Не удалось зарегистрироваться' },
  reviewer_register_approved: {
    '1': '已通过注册申请',
    '2': 'Registration approved',
    '3': 'Регистрация одобрена',
  },
  reviewer_register_rejected: {
    '1': '已退回该注册申请',
    '2': 'Registration rejected',
    '3': 'Регистрация отклонена',
  },
  order_review_approved_ok: {
    '1': '订单审核已通过',
    '2': 'Order review approved',
    '3': 'Заказ одобрен',
  },
  order_review_rejected_ok: {
    '1': '订单已退回',
    '2': 'Order review rejected',
    '3': 'Заказ отклонён',
  },
  register_captcha_send_failed: {
    '1': '验证码发送失败',
    '2': 'Failed to send code',
    '3': 'Не удалось отправить код',
  },
  validation_usertype_invalid: {
    '1': '非本平台用户',
    '2': 'Invalid user type',
    '3': 'Недопустимый тип пользователя',
  },
  admin_user_updated: {
    '1': '用户信息已更新',
    '2': 'User updated',
    '3': 'Данные пользователя обновлены',
  },
  admin_user_deleted: {
    '1': '用户已删除',
    '2': 'User deleted',
    '3': 'Пользователь удалён',
  },
  wms_inbound_confirmed: {
    '1': '入库确认成功',
    '2': 'Inbound confirmed',
    '3': 'Приемка подтверждена',
  },
  wms_outbound_confirmed: {
    '1': '出库确认成功',
    '2': 'Outbound confirmed',
    '3': 'Отгрузка подтверждена',
  },
}

/**
 * @param {string} [raw]
 * @param {(dictType: string, dictValue: string, fallback?: string) => string} t useMultiDictionary 的 t
 * @param {string} [uiLang] useUiLang 的值，如 zh-CN / en / ru
 */
export function translateApiMessage(raw, t, uiLang = 'zh-CN') {
  const fb = () =>
    t('api_message', 'generic_error', '') ||
    t('api_auth', 'generic_error', '') ||
    fallbackLine('generic_error', uiLang) ||
    'Something went wrong'

  function fallbackLine(key, lang) {
    const row = KEY_FALLBACK[key]
    if (!row) return ''
    const code = uiLangToDictLang(lang)
    return row[code] || row['1'] || ''
  }

  if (raw == null || typeof raw !== 'string') {
    return fb()
  }
  let s = raw.trim()
  if (s === '') {
    return fb()
  }
  if (!/^[a-z][a-z0-9_]*$/i.test(s)) {
    const mapped = SPRING_ERROR_TO_KEY[s.toLowerCase()]
    if (mapped) {
      s = mapped
    } else {
      return s
    }
  }
  const tr = t('api_message', s, '') || t('api_auth', s, '')
  if (tr) return tr
  const offline = fallbackLine(s, uiLang)
  if (offline) return offline
  return s
}
