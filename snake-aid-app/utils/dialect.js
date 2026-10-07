// ==========================================
// dialect.js — 方言语音适配方案
// 合同要求：至少一种农村主力方言，可切换
// 支持：普通话（默认）/ 四川话（农民工主力）/ 粤语 / 河南话
// 技术实现：Web Speech API lang 参数切换 + 方言短语包
// ==========================================

const DIALECT_KEY = 'appDialect'

export const dialects = {
  mandarin: { code: 'zh-CN', name: '普通话', desc: '默认' },
  sichuan:  { code: 'zh-CN', name: '四川话', desc: '农民工主力方言', phrases: true },
  cantonese:{ code: 'zh-HK', name: '粤语', desc: '南方农村' },
  henan:    { code: 'zh-CN', name: '河南话', desc: '中部农村' }
}

// ===== 方言短语包（关键急救词翻译）=====
// 四川话：用谐音/方言表达
export const phrasePacks = {
  sichuan: {
    // 界面关键词
    '急救': '急救',
    '蛇咬伤': '遭蛇咬了',
    '毒蛇': '毒蛇',
    '无毒': '没得毒',
    '立刻': '搞快',
    '远离': '走远点',
    '保持冷静': '莫慌，稳住',
    '制动伤肢': '莫动，把手脚绑到',
    '拨打120': '赶紧打幺二零',
    '加压包扎': '用绷带缠紧',
    '不要切开伤口': '千万莫用刀划',
    '不要用嘴吸毒': '莫用嘴巴吸哈',
    '不要敷草药': '莫乱敷草药',
    '送到有血清的医院': '送有血清的医院去',
    '三角头': '三角脑壳',
    '椭圆形头': '圆脑壳',
    '特征': '样子'
  },
  cantonese: {
    '急救': '急救',
    '蛇咬伤': '俾蛇咬咗',
    '毒蛇': '毒蛇',
    '无毒': '冇毒',
    '立刻': '即刻',
    '远离': '行开啲',
    '保持冷静': '唔好惊，定啲嚟',
    '制动伤肢': '唔好喐，绑好佢',
    '拨打120': '快啲打一二零',
    '加压包扎': '用绷带缠紧',
    '不要切开伤口': '千祈唔好用刀划',
    '不要用嘴吸毒': '唔好用嘴吸',
    '不要敷草药': '唔好乱敷草药',
    '三角头': '三角头',
    '椭圆形头': '圆头',
    '特征': '特征'
  },
  henan: {
    '蛇咬伤': '叫蛇咬着了',
    '毒蛇': '毒蛇',
    '无毒': '没毒',
    '立刻': '赶紧嘞',
    '远离': '离远点',
    '保持冷静': '别慌，稳住气',
    '制动伤肢': '别动，绑上',
    '拨打120': '赶紧打一百二十',
    '不要切开伤口': '千万别用刀拉',
    '不要用嘴吸毒': '别用嘴吸啊',
    '不要敷草药': '别瞎敷草药',
    '三角头': '三角头',
    '椭圆形头': '圆头'
  }
}

let currentDialect = loadDialect()
const listeners = []

function loadDialect() {
  try {
    const s = uni.getStorageSync(DIALECT_KEY)
    if (s && dialects[s]) return s
  } catch (e) {}
  return 'mandarin'
}

export function getDialect() { return currentDialect }
export function getDialectConfig() { return dialects[currentDialect] }

export function setDialect(name) {
  if (!dialects[name]) return false
  currentDialect = name
  try { uni.setStorageSync(DIALECT_KEY, name) } catch (e) {}
  listeners.forEach(fn => { try { fn(dialects[name]) } catch (e) {} })
  return true
}

export function onDialectChange(fn) { listeners.push(fn) }

// 翻译短语（如果当前方言有对应短语）
export function t(key, fallback = '') {
  if (currentDialect === 'mandarin') return fallback || key
  const pack = phrasePacks[currentDialect]
  if (pack && pack[key]) return pack[key]
  return fallback || key
}

// ===== 语音合成（用 Web Speech API）=====
let speechOn = false

export function isSpeechOn() { return speechOn }
export function toggleSpeech() { speechOn = !speechOn; return speechOn }

export function speak(text) {
  if (!speechOn) return
  // #ifdef H5
  try {
    if (!('speechSynthesis' in window)) return
    const u = new SpeechSynthesisUtterance(text)
    u.lang = dialects[currentDialect].code
    u.rate = 0.85
    u.pitch = 1.0
    window.speechSynthesis.cancel()
    window.speechSynthesis.speak(u)
  } catch (e) {}
  // #endif
}

// 直接说方言短语
export function speakKey(key) { speak(t(key, key)) }
