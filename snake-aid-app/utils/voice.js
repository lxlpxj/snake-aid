// ============== Voice Tool (Web Speech API, zero dep) ==============

let _lastText = ''
let _lastTime = 0
let _voiceEnabled = true
let _currentLang = 'zh-CN'

export const DIALECTS = [
  { code: 'zh-CN', name: '普通话' },
  { code: 'zh-HK', name: '粤语' },
  { code: 'zh-TW', name: '台湾腔' },
  { code: 'zh-SG', name: '新加坡中文' },
]

function getVoices() {
  if (typeof window === 'undefined' || !('speechSynthesis' in window)) return []
  return window.speechSynthesis.getVoices() || []
}

function pickVoice(lang) {
  const voices = getVoices()
  return voices.find(v => v.lang === lang) || voices.find(v => v.lang.startsWith('zh')) || null
}

export function speak(text, opts) {
  if (!text || !_voiceEnabled) return
  if (typeof window === 'undefined' || !('speechSynthesis' in window)) return

  opts = opts || {}
  const now = Date.now()
  if (!opts.force && text === _lastText && now - _lastTime < 800) return
  _lastText = text
  _lastTime = now

  try { window.speechSynthesis.cancel() } catch(e) {}

  const utter = new SpeechSynthesisUtterance(text)
  utter.lang = opts.lang || _currentLang
  utter.volume = opts.volume ?? 1.0
  utter.rate = opts.rate ?? 1.0
  utter.pitch = opts.pitch ?? 1.0

  const v = pickVoice(utter.lang)
  if (v) utter.voice = v

  try { window.speechSynthesis.speak(utter) } catch(e) {}
}

export function sayRight(text) {
  speak('对了！' + (text || ''), { force: true, pitch: 1.2 })
}

export function sayWrong(text) {
  speak('不对哦！' + (text || ''), { force: true, pitch: 0.9 })
}

export function sayStep(stepNum, title) {
  speak('第' + stepNum + '关，' + (title || ''), { force: true })
}

export function sayCongrats(score) {
  speak('恭喜通关！得分' + score + '分！', { force: true, rate: 1.1 })
}

export function sayTestStart(name) {
  speak((name || '') + '测试开始，加油！', { force: true })
}

export function sayClick(action) {
  speak(action || '点击', { force: true, pitch: 1.1, rate: 1.2 })
}

export function setVoiceEnabled(on) { _voiceEnabled = !!on }
export function isVoiceEnabled() { return _voiceEnabled }
export function setLang(code) { _currentLang = code }
export function getLang() { return _currentLang }
export function stopSpeak() {
  try { if (typeof window !== 'undefined' && 'speechSynthesis' in window) window.speechSynthesis.cancel() } catch(e) {}
}
export function preloadVoices() {
  if (typeof window === 'undefined' || !('speechSynthesis' in window)) return
  window.speechSynthesis.getVoices()
  window.speechSynthesis.onvoiceschanged = function() { window.speechSynthesis.getVoices() }
}

export default {
  speak: speak, sayRight: sayRight, sayWrong: sayWrong,
  sayStep: sayStep, sayCongrats: sayCongrats,
  sayTestStart: sayTestStart, sayClick: sayClick,
  setVoiceEnabled: setVoiceEnabled, isVoiceEnabled: isVoiceEnabled,
  setLang: setLang, getLang: getLang,
  stopSpeak: stopSpeak, preloadVoices: preloadVoices,
  DIALECTS: DIALECTS
}
