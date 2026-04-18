import { ref } from 'vue'

function readStoredLang() {
  try {
    return localStorage.getItem('gl_ui_lang') || 'zh-CN'
  } catch {
    return 'zh-CN'
  }
}

const uiLang = ref(typeof localStorage !== 'undefined' ? readStoredLang() : 'zh-CN')

/** 站点界面语言（与 LanguageDropdown、字典 lang 过滤一致） */
export function useUiLang() {
  return { uiLang }
}
