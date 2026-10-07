// ============== 🎤 语音播报工具 ==============
// 用 Web Speech API，零依赖！H5/小程序都支持
// 全局开关 + 冷却去重 + 方言切换

let _lastText = ''
let _lastTime = 0
let _voiceEnabled = true
let _currentLang = 'zh-CN'
let _volume = 1.0
let _rate = 1.0
let _pitch = 1.0

// 可用方言（浏览器支持的）
export const DIALECTS = [
  { code: 'zh-CN', name: '普通话 🇨🇳' },
  { code: 'zh-SG', name: '新加坡中文' },
  { code: 'zh-TW', name: '台湾腔' },
  { code: 'zh-HK', name: '粤语 🇭🇰' },
  { code: 'en-US', name: '英语 🇺🇸' },
]

// 农村主力方言预设（浏览器可能不完全支持，但会自动回退）
const RURAL_DIALECTS = {
  sichuan: { code: 'zh-CN', name: '四川话(近似)' },
  cantonese: { code: 'zh-HK', name: '粤语' },
  henan: { code: 'zh-CN', name: '河南话(近似)' },
  mandarin: { code: 'zh-CN', name: '普通话' },
}

/**
 * 说话！核心方法
 * @param {string} text 要说的话
 * @param {object} opts { volume, rate, pitch, force }
 */
export function speak(text, opts = {}) {
  if (!text) return
  
  // 检查开关
  if (!_voiceEnabled) return
  
  // H5 环境才支持
  if (typeof window === 'undefined' || !('speechSynthesis' in window)) {
    console.warn('⚠️ 当前环境不支持 Web Speech API')
    return
  }
  
  // 冷却去重（300ms 内同一句话不说两次）
  const now = Date.now()
  if (!opts.force && text === _lastText && now - _lastTime < 800) {
    return
  }
  _lastText = text
  _lastTime = now
  
  // 停止之前正在说的
  try { window.speechSynthesis.cancel() } catch(e) {}
  
  const utter = new SpeechSynthesisUtterance(text)
  utter.lang = opts.lang || _currentLang
  utter.volume = opts.volume ?? _volume
  utter.rate = opts.rate ?? _rate
  utter.pitch = opts.pitch ?? _pitch
  
  // 尝试匹配更好的声音
  try {
    const voices = window.speechSynthesis.getVoices()
    const zhVoice = voices.find(v => v.lang.startsWith('zh') && !v.lang.includes('TW') && !v.lang.includes('HK'))
    const targetVoice = voices.find(v => v.lang === _currentLang) || zhVoice
    if (targetVoice) utter.voice = targetVoice
  } catch(e) {}
  
  try { window.speechSynthesis.speak(utter) } catch(e) {}
}

// 快捷方法：肯定反馈
export function sayRight(text = '对了！很好！') { speak('✅ ' + text, { force: true, pitch: 1.2 }) }
// 快捷方法：错误反馈
export function sayWrong(text = '不对哦，再想想') { speak('❌ ' + text, { force: true, pitch: 0.9 }) }
// 快捷方法：进入某关
export function sayStep(stepNum, title) { speak(第关，, { force: true }) }
// 快捷方法：恭喜
export function sayCongrats(score) { speak(🎉 恭喜通关！得分  分！, { force: true, rate: 1.1 }) }
// 快捷方法：测试开始
export function sayTestStart(name = '') { speak(${name || ''}测试开始，加油！, { force: true }) }
// 快捷方法：按钮点击
export function sayClick(action) { speak(action, { force: true, pitch: 1.1, rate: 1.2 }) }

// 设置开关
export function setVoiceEnabled(on) { _voiceEnabled = !!on }
export function isVoiceEnabled() { return _voiceEnabled }

// 设置方言
export function setLang(code) { _currentLang = code }
export function getLang() { return _currentLang }

// 设置农村方言预设
export function setRuralDialect(key) {
  const d = RURAL_DIALECTS[key]
  if (d) _currentLang = d.code
}

// 停止说话
export function stopSpeak() {
  try { if (typeof window !== 'undefined' && 'speechSynthesis' in window) window.speechSynthesis.cancel() } catch(e) {}
}

// 预加载声音列表（浏览器需要异步加载）
export function preloadVoices() {
  if (typeof window === 'undefined' || !('speechSynthesis' in window)) return
  window.speechSynthesis.getVoices()
  window.speechSynthesis.onvoiceschanged = () => window.speechSynthesis.getVoices()
}

export default { speak, sayRight, sayWrong, sayStep, sayCongrats, sayTestStart, sayClick, setVoiceEnabled, isVoiceEnabled, setLang, getLang, stopSpeak, preloadVoices, DIALECTS }