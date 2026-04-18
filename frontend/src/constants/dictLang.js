/**
 * 与 dictionary 表 lang 及 sql/dictionary_frontend_inserts_mysql.sql 注释一致：
 * 1=中文 2=英文 3=俄文
 */
export function uiLangToDictLang(uiLang) {
  if (uiLang === 'en') return '2'
  if (uiLang === 'ru') return '3'
  if (uiLang === 'zh-CN') return '1'
  return '1'
}
