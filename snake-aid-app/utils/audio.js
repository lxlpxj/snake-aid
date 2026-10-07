// ==========================================
// audio.js — Web Audio API 音效合成系统
// 合同要求：操作音效、提示音、背景音
// 全部用浏览器原生 API 合成，**无需录音文件！**
// ==========================================

let ctx = null
let bgmNode = null
let bgmPlaying = false

// 懒加载 AudioContext
function getCtx() {
  if (!ctx) {
    try {
      ctx = new (window.AudioContext || window.webkitAudioContext)()
    } catch (e) { ctx = null }
  }
  return ctx
}

// 通用：播放一个纯音调
function playTone(freq, duration, type = 'sine', volume = 0.3) {
  const c = getCtx()
  if (!c) return
  try {
    const osc = c.createOscillator()
    const gain = c.createGain()
    osc.type = type
    osc.frequency.value = freq
    gain.gain.setValueAtTime(volume, c.currentTime)
    gain.gain.exponentialRampToValueAtTime(0.001, c.currentTime + duration)
    osc.connect(gain).connect(c.destination)
    osc.start()
    osc.stop(c.currentTime + duration)
  } catch (e) {}
}

// ===== 合同要求的音效 =====

// 操作音：点按钮
export function sfxClick() { playTone(800, 0.08, 'sine', 0.2) }

// 正确反馈音（上升三连音）
export function sfxCorrect() {
  playTone(523, 0.12, 'sine', 0.25)  // C5
  setTimeout(() => playTone(659, 0.12, 'sine', 0.25), 120)  // E5
  setTimeout(() => playTone(784, 0.18, 'sine', 0.25), 240)  // G5
}

// 错误反馈音（下降两连音）
export function sfxWrong() {
  playTone(220, 0.15, 'square', 0.2)
  setTimeout(() => playTone(150, 0.2, 'square', 0.2), 150)
}

// 警告音（蛇出现）
export function sfxSnake() {
  playTone(300, 0.1, 'sawtooth', 0.15)
  setTimeout(() => playTone(250, 0.15, 'sawtooth', 0.15), 100)
  setTimeout(() => playTone(200, 0.2, 'sawtooth', 0.15), 250)
}

// 通关/庆祝音
export function sfxWin() {
  [523, 659, 784, 1047].forEach((f, i) => {
    setTimeout(() => playTone(f, 0.2, 'triangle', 0.3), i * 150)
  })
}

// 紧急呼救音（120 警报声）
export function sfxEmergency() {
  playTone(880, 0.25, 'sine', 0.3)
  setTimeout(() => playTone(660, 0.25, 'sine', 0.3), 250)
  setTimeout(() => playTone(880, 0.25, 'sine', 0.3), 500)
  setTimeout(() => playTone(660, 0.25, 'sine', 0.3), 750)
}

// 包扎完成音
export function sfxDone() {
  [440, 554, 659].forEach((f, i) => {
    setTimeout(() => playTone(f, 0.15, 'sine', 0.25), i * 80)
  })
}

// ===== 背景音（轻松环境音：模拟森林/乡村） =====
export function playBGM() {
  if (bgmPlaying) return
  const c = getCtx()
  if (!c) return
  
  // 用简单的和弦循环做背景
  const notes = [262, 330, 392, 523] // C5 E5 G5 C6
  let i = 0
  bgmPlaying = true
  
  bgmNode = setInterval(() => {
    if (!bgmPlaying) return
    const freq = notes[i % notes.length]
    playTone(freq, 1.5, 'triangle', 0.06) // 低音量
    i++
  }, 1200)
}

export function stopBGM() {
  bgmPlaying = false
  if (bgmNode) { clearInterval(bgmNode); bgmNode = null }
}

export function toggleBGM() {
  if (bgmPlaying) stopBGM(); else playBGM()
  return bgmPlaying
}

export function isBGMPlaying() { return bgmPlaying }
