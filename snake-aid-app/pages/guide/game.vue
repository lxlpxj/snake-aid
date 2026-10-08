<template>
  <view class="game-container">
  <!-- 语音开关 -->
  <view class="voice-switch" @click="toggleVoice">
    <text class="voice-icon">{{ voiceOn ? '🔊' : '🔇' }}</text>
    <text class="voice-label">{{ voiceOn ? '语音开' : '语音关' }}</text>
  </view>
    <!-- 顶部进度条 -->
    <view class="progress-bar">
      <view class="progress-fill" :style="{ width: ((currentStep) / steps.length * 100) + '%' }"></view>
      <text class="progress-text">第 {{ currentStep + 1 }} / {{ steps.length }} 关</text>
    </view>

    <!-- 开始界面 -->
    <view v-if="gameState === 'start'" class="start-screen">
      <view class="start-icon">🏥</view>
      <view class="start-title">急救闯关大挑战</view>
      <view class="start-desc">
        根据《中国蛇伤救治指南(2024版)》<br>
        完成9步现场急救流程<br>
        每一步都有即时反馈！
      </view>
      <view class="start-steps">
        <view class="step-item" v-for="(s, i) in steps" :key="i">
          <text class="step-num">{{ i + 1 }}</text>
          <text class="step-label">{{ s.title }}</text>
        </view>
      </view>
      <button class="btn-start" @click="startGame">🚑 开始闯关</button>
    </view>

    <!-- 闯关界面 -->
    <view v-else-if="gameState === 'playing'" class="play-screen">
      <!-- 当前关卡 -->
      <view class="level-card" :class="feedbackType ? ('card-' + feedbackType) : ''">
        <view class="level-header">
          <text class="level-emoji">{{ steps[currentStep].emoji }}</text>
          <view class="level-info">
            <view class="level-title">{{ steps[currentStep].title }}</view>
            <view class="level-hint">{{ steps[currentStep].hint }}</view>
          </view>
        </view>

<!-- 🎬 每关专属 SVG 场景插图（仅选择题关显示） -->
<view v-if="!steps[currentStep]?.isInteractive" class="scene-stage">
<!-- 第1关：田间遇蛇 -->
<view v-if="currentStep === 0" class="scene-wrap" :class="feedbackType ? ('scene-' + feedbackType) : ''">
<svg viewBox="0 0 400 200" class="scene-svg">
<!-- 天空渐变 -->
<defs>
<linearGradient id="sky" x1="0" y1="0" x2="0" y2="1">
<stop offset="0" stop-color="#87CEEB"/><stop offset="1" stop-color="#B3E5FC"/>
</linearGradient>
</defs>
<rect width="400" height="200" fill="url(#sky)"/>
<!-- 云 3朵 -->
<g class="anim-cloud1"><ellipse cx="80" cy="30" rx="25" ry="10" fill="#FFF"/><ellipse cx="100" cy="25" rx="20" ry="12" fill="#FFF"/></g>
<g class="anim-cloud2"><ellipse cx="220" cy="50" rx="22" ry="9" fill="#FFF"/><ellipse cx="235" cy="46" rx="18" ry="11" fill="#FFF"/></g>
<!-- 太阳 -->
<circle cx="340" cy="40" r="25" fill="#FFD54F" class="anim-sun"/>
<!-- 草地 -->
<rect y="140" width="400" height="60" fill="#8BC34A"/>
<!-- 小路 -->
<path d="M30 150 Q45 120 60 150 Q75 180 90 150" fill="#F5DEB3" stroke="#A1887F" stroke-width="2"/>
<!-- 草丛 3丛 -->
<g stroke="#2E7D32" stroke-width="2" fill="#4CAF50">
<path d="M20 160 Q25 150 30 160 Q35 148 40 160" class="anim-grass"/>
<path d="M160 160 Q165 150 170 160 Q175 148 180 160" class="anim-grass2"/>
<path d="M350 162 Q355 152 360 162 Q365 150 370 162" class="anim-grass"/>
</g>

<!-- ====== 人物（嵌套 g 锚定关节！）====== -->
<g transform="translate(280,120)">
<!-- 身体（整体 bob） -->
<g class="anim-body">
<rect x="8" y="25" width="14" height="25" rx="3" fill="#1565C0"/>
</g>

<!-- 左腿：关节在(11.5, 50) → 外层 g 锚定，内层 g 旋转 -->
<g transform="translate(11.5, 50)">
<g class="anim-leg-back" style="transform-box: fill-box; transform-origin: top center;">
<rect x="-2.5" y="0" width="5" height="18" rx="2" fill="#795548"/>
</g>
</g>

<!-- 右腿：关节在(17.5, 50) -->
<g transform="translate(17.5, 50)">
<g class="anim-leg-front" style="transform-box: fill-box; transform-origin: top center;">
<rect x="-2.5" y="0" width="5" height="18" rx="2" fill="#5D4037"/>
</g>
</g>

<!-- 右臂：关节在(24, 28) 肩 -->
<g transform="translate(24, 28)">
<g class="anim-arm-right" style="transform-box: fill-box; transform-origin: top center;">
<rect x="-2" y="0" width="4" height="14" rx="2" fill="#1565C0"/>
</g>
</g>

<!-- 左臂：关节在(6, 28) 肩 -->
<g transform="translate(6, 28)">
<g class="anim-arm-left" style="transform-box: fill-box; transform-origin: top center;">
<rect x="-2" y="0" width="4" height="14" rx="2" fill="#1565C0"/>
</g>
</g>

<!-- 头：关节在(15, 25) 脖子底部 -->
<g transform="translate(15, 25)">
<g class="anim-head" style="transform-box: fill-box; transform-origin: bottom center;">
<!-- 头发 -->
<path d="M-10 -17 Q0 -27 10 -17 Q10 -11 -10 -11 Z" fill="#3E2723"/>
<!-- 脸 -->
<circle cx="0" cy="-13" r="10" fill="#FFCCBC"/>
<!-- 眼睛（紧张睁圆） -->
<circle cx="-4" cy="-13" r="1.5" fill="#333"/>
<circle cx="4" cy="-13" r="1.5" fill="#333"/>
<!-- 嘴巴（O型害怕） -->
<ellipse cx="0" cy="-8" rx="2" ry="2.5" fill="#B71C1C"/>
<!-- 汗珠 -->
<circle cx="12" cy="-17" r="2" fill="#4FC3F7" class="anim-sweat"/>
</g>
</g>
</g>

<!-- ====== 蛇 ====== -->
<g transform="translate(40,-10)">
<!-- 蛇身 S形 -->
<path d="M0 40 Q20 10 40 30 Q60 50 80 20" fill="none" stroke="#558B2F" stroke-width="10" stroke-linecap="round" class="anim-snake-body"/>
<!-- 蛇头 -->
<ellipse cx="80" cy="20" rx="10" ry="7" fill="#33691E" class="anim-snake-head"/>
<!-- 眼睛 -->
<circle cx="84" cy="18" r="2" fill="#FFEB3B" class="anim-snake-eye"/>
<!-- 毒牙 -->
<path d="M86 22 L88 28 M82 22 L80 28" stroke="#FFF" stroke-width="1.5"/>
<!-- 吐信 -->
<path d="M88 20 L96 18 M88 20 L96 22" stroke="#D32F2F" stroke-width="2" class="anim-tongue"/>
<!-- 花纹 -->
<circle cx="20" cy="25" r="2" fill="#1B5E20"/>
<circle cx="45" cy="30" r="2" fill="#1B5E20"/>
<circle cx="65" cy="22" r="2" fill="#1B5E20"/>
</g>

<!-- ⚠ 警告气泡弹出 -->
<g transform="translate(310,95)" class="anim-warn-bubble">
<ellipse cx="25" cy="0" rx="30" ry="18" fill="#FFEB3B" stroke="#F57F17" stroke-width="2"/>
<text x="25" y="5" text-anchor="middle" font-size="14" fill="#D84315" font-weight="bold">⚠ 有蛇！</text>
<path d="M0 5 L-8 10 L0 12 Z" fill="#F57F17"/>
</g>
</svg>
</view>
<!-- 第2关：辨认蛇种 -->
<view v-if="currentStep === 1" class="scene-wrap" :class="feedbackType ? ('scene-' + feedbackType) : ''">
<svg viewBox="0 0 400 200" class="scene-svg">
<rect width="400" height="200" fill="#ECEFF1"/>
<rect x="40" y="30" width="140" height="150" rx="12" fill="#FFEBEE" stroke="#E53935" stroke-width="3"/>
<rect x="220" y="30" width="140" height="150" rx="12" fill="#E8F5E9" stroke="#43A047" stroke-width="3"/>
<g transform="translate(70,60)">
<polygon points="30,5 55,30 30,55" fill="#B71C1C" class="anim-tri"/>
<circle cx="50" cy="25" r="4" fill="#FFEB3B" class="anim-pulse"/>
<text y="90" font-size="14" fill="#C62828" text-anchor="middle">⚠️ 毒蛇 · 三角头</text>
</g>
<g transform="translate(250,60)">
<ellipse cx="35" cy="30" rx="25" ry="20" fill="#388E3C" class="anim-oval"/>
<circle cx="50" cy="28" r="4" fill="#FFF"/>
<text y="90" font-size="14" fill="#2E7D32" text-anchor="middle">✅ 无毒 · 椭圆头</text>
</g>
</svg>
</view>
<!-- 第3关：保持镇定 -->
<view v-if="currentStep === 2" class="scene-wrap" :class="feedbackType ? ('scene-' + feedbackType) : ''">
<svg viewBox="0 0 400 200" class="scene-svg">
<rect width="400" height="200" fill="#E0F2F1"/>
<rect y="160" width="400" height="40" fill="#8D6E63"/>
<g transform="translate(170,80)" class="anim-calm">
<circle cx="30" cy="10" r="15" fill="#FFCCBC"/>
<path d="M20 35 L40 35 L45 70 L15 70 Z" fill="#5C6BC0"/>
<path d="M20 50 Q30 55 40 50" fill="none" stroke="#3949AB" stroke-width="2"/>
<circle cx="10" cy="5" r="3" fill="#FF8A65"/>
<circle cx="50" cy="5" r="3" fill="#FF8A65"/>
</g>
<g transform="translate(200,100)">
<circle r="25" fill="none" stroke="#4DD0E1" stroke-width="2" class="anim-ring"/>
<circle r="30" fill="none" stroke="#26C6DA" stroke-width="2" class="anim-ring2"/>
<circle r="35" fill="none" stroke="#00BCD4" stroke-width="2" class="anim-ring3"/>
</g>
<text x="300" y="50" font-size="14" fill="#00695C">🌿 深呼吸...</text>
</svg>
</view>
<!-- 第5关：伤口解压（冲洗） -->
<view v-if="currentStep === 4" class="scene-wrap" :class="feedbackType ? ('scene-' + feedbackType) : ''">
<svg viewBox="0 0 400 200" class="scene-svg">
<rect width="400" height="200" fill="#E3F2FD"/>
<rect y="170" width="400" height="30" fill="#90A4AE"/>
<rect x="100" y="40" width="30" height="80" fill="#78909C"/>
<circle cx="115" cy="125" r="8" fill="#455A64" class="anim-handle"/>
<path d="M115 130 L115 170" stroke="#4FC3F7" stroke-width="4" stroke-dasharray="8 4" class="anim-water"/>
<g transform="translate(200,110)">
<rect x="0" y="0" width="80" height="25" rx="6" fill="#FFCCBC"/>
<circle cx="40" cy="12" r="6" fill="#D32F2F" class="anim-wound"/>
</g>
</svg>
</view>
<!-- 第8关：止痛处理 -->
<view v-if="currentStep === 7" class="scene-wrap" :class="feedbackType ? ('scene-' + feedbackType) : ''">
<svg viewBox="0 0 400 200" class="scene-svg">
<rect width="400" height="200" fill="#FFF3E0"/>
<g transform="translate(60,60)" class="anim-card">
<rect width="80" height="100" rx="12" fill="#FFEBEE" stroke="#E53935" stroke-width="3"/>
<text x="40" y="60" text-anchor="middle" font-size="40">💊</text>
<text x="40" y="85" text-anchor="middle" font-size="14" fill="#C62828">别吃药</text>
<text x="40" y="115" text-anchor="middle" font-size="30" fill="#D32F2F" class="anim-cross">✗</text>
</g>
<g transform="translate(160,60)" class="anim-card2">
<rect width="80" height="100" rx="12" fill="#FFEBEE" stroke="#E53935" stroke-width="3"/>
<text x="40" y="60" text-anchor="middle" font-size="40">🍺</text>
<text x="40" y="85" text-anchor="middle" font-size="14" fill="#C62828">别喝酒</text>
<text x="40" y="115" text-anchor="middle" font-size="30" fill="#D32F2F" class="anim-cross">✗</text>
</g>
<g transform="translate(260,60)" class="anim-card3">
<rect width="80" height="100" rx="12" fill="#FFEBEE" stroke="#E53935" stroke-width="3"/>
<text x="40" y="60" text-anchor="middle" font-size="40">🧊</text>
<text x="40" y="85" text-anchor="middle" font-size="14" fill="#C62828">别冰敷</text>
<text x="40" y="115" text-anchor="middle" font-size="30" fill="#D32F2F" class="anim-cross">✗</text>
</g>
</svg>
</view>
<!-- 第9关：禁忌清单 -->
<view v-if="currentStep === 8" class="scene-wrap" :class="feedbackType ? ('scene-' + feedbackType) : ''">
<svg viewBox="0 0 400 200" class="scene-svg">
<rect width="400" height="200" fill="#FFF8E1"/>
<rect x="30" y="30" width="160" height="140" rx="12" fill="#FFEBEE" stroke="#E53935" stroke-width="2"/>
<rect x="210" y="30" width="160" height="140" rx="12" fill="#E8F5E9" stroke="#43A047" stroke-width="2"/>
<text x="110" y="55" text-anchor="middle" font-size="14" fill="#C62828" font-weight="bold">❌ 这些别做！</text>
<text x="290" y="55" text-anchor="middle" font-size="14" fill="#2E7D32" font-weight="bold">✅ 这样才对！</text>
<text x="50" y="80" font-size="12" fill="#B71C1C">✗ 别用嘴吸伤口</text>
<text x="50" y="100" font-size="12" fill="#B71C1C">✗ 别喝酒吃止痛药</text>
<text x="50" y="120" font-size="12" fill="#B71C1C">✗ 别冰敷别敷草药</text>
<text x="50" y="140" font-size="12" fill="#B71C1C">✗ 别切开伤口</text>
<text x="50" y="160" font-size="12" fill="#B71C1C">✗ 别高举伤肢</text>
<text x="230" y="80" font-size="12" fill="#1B5E20">✓ 冷静！慢慢退开</text>
<text x="230" y="100" font-size="12" fill="#1B5E20">✓ 立刻打 120</text>
<text x="230" y="120" font-size="12" fill="#1B5E20">✓ 伤肢平放制动</text>
<text x="230" y="140" font-size="12" fill="#1B5E20">✓ 保持清醒别进食</text>
<text x="230" y="160" font-size="12" fill="#1B5E20">✓ 送医抗蛇毒血清</text>
</svg>
</view>
</view>


        <!-- 第4关：模拟拨120 -->
        <view v-if="steps[currentStep]?.isInteractive && steps[currentStep]?.interactionType === 'phone'" class="dial-120">
          <view class="phone-screen">
            <text class="dialing-text" v-if="!phoneCalled">请点击下方按键拨打 120</text>
            <text class="connected-text" v-else>📞 已接通！请说："蛇咬伤！需要急救！"</text>
          </view>
          <view class="phone-keypad">
            <view class="key" v-for="n in ['1','2','0']" :key="n">{{ n }}</view>
            <view class="key-call" @click="call120">📞</view>
          </view>
        </view>

        <!-- 第6关：伤肢摆放 -->
        <view v-if="steps[currentStep]?.isInteractive && steps[currentStep]?.interactionType === 'limb'" class="limb-placement">
          <text class="limb-q">蛇咬伤后，伤肢应该放在哪个位置？</text>
          <view class="limb-options">
            <view class="limb-opt" @click="answerLimb('up')">
              <view class="limb-emoji">⬆️</view>
              <text>抬高(高于心脏)</text>
            </view>
            <view class="limb-opt correct" @click="answerLimb('flat')">
              <view class="limb-emoji">➡️</view>
              <text>平放(与心脏同高) ✓</text>
            </view>
            <view class="limb-opt" @click="answerLimb('down')">
              <view class="limb-emoji">⬇️</view>
              <text>放低(低于心脏)</text>
            </view>
          </view>
        </view>

        <!-- 第7关：拖拽包扎带 -->
        <view v-if="steps[currentStep]?.isInteractive && steps[currentStep]?.interactionType === 'drag'" class="bandage-area">
          <view class="bandage-scene">
            <view class="arm-svg">
              <text class="arm-emoji">🦾</text>
              <view class="wound-mark">⚠️</view>
            </view>
            <view class="bandage-target" :class="{ done: bandageDone }" @click="dragBandage">
              <text v-if="!bandageDone">👆 拖拽包扎带缠绕伤口</text>
              <text v-else>✅ 包扎完成！</text>
            </view>
            <view v-if="!bandageDone" class="bandage-strip" @click="dragBandage">
              🩹 包扎带(点击使用)
            </view>
          </view>
          <view class="bandage-hint">提示：包扎要从伤口远心端向近心端缠绕，松紧适度！</view>
        </view>

        <!-- 通用选择题 -->
        <view v-if="!steps[currentStep]?.isInteractive && steps[currentStep]?.options?.length" class="choice-area">
          <view class="choice-option" 
                :class="{ wrong: wrongChoice, right: rightChoice && option.correct, tapped: selectedOpt === option.correct }"
                v-for="(option, idx) in steps[currentStep].options" 
                :key="idx"
                @click="selectOption(option); selectedOpt = option.correct">
            <text class="option-letter">{{ String.fromCharCode(65 + idx) }}</text>
            <text class="option-text">{{ option.text }}</text>
            <text class="option-mark" v-if="rightChoice && option.correct">✓</text>
            <text class="option-mark wrong" v-if="wrongChoice && !option.correct">✗</text>
          </view>
        </view>

        <!-- 即时反馈 -->
        <view class="feedback" :class="feedbackType" v-if="feedbackText">
          <text class="feedback-emoji">{{ feedbackType === 'right' ? '🎉' : '⚠️' }}</text>
          <text class="feedback-text">{{ feedbackText }}</text>
        </view>
      </view>

      <!-- 底部按钮 -->
      <view class="action-btns">
        <button v-if="feedbackText && canNext" class="btn-next" @click="nextStep">
          {{ currentStep < steps.length - 1 ? '进入下一关 →' : '查看成绩 🏆' }}
        </button>
        <button v-else-if="feedbackText && !canNext" class="btn-retry" @click="retryStep">
          再试一次
        </button>
        <button v-else class="btn-hint" @click="showHint">💡 提示</button>
      </view>

      <!-- 🎆 交互反馈特效层 -->
      <view class="fx-overlay right-fx" v-if="feedbackType === 'right'">
        <view class="fx-star" v-for="n in 10" :key="'s'+n">⭐</view>
        <view class="fx-plus">+10</view>
      </view>
      <view class="fx-overlay wrong-fx" v-if="feedbackType === 'wrong'">
        <view class="fx-boo">❌ 答错了！</view>
      </view>
    </view>

    <!-- 完成界面 -->
    <view v-else-if="gameState === 'done'" class="done-screen">
      <view class="done-icon">🏆</view>
      <view class="done-title">闯关完成！</view>
      <view class="done-score">得分：{{ score }} / {{ steps.length * 10 }}</view>
      <view class="done-stars">
        <text v-for="n in 3" :key="n" :class="{ dim: score < n * 30 }">⭐</text>
      </view>
      <view class="done-desc">
        <text v-if="score >= 70">太棒了！你已经掌握了蛇伤急救的核心技能！</text>
        <text v-else-if="score >= 40">不错！建议你复习一下薄弱环节。</text>
        <text v-else>还需要多加练习！别担心，急救知识就是练出来的。</text>
      </view>
      <view class="done-btns">
        <button class="btn-replay" @click="resetGame">🔄 再闯一次</button>
        <button class="btn-back" @click="goBack">← 返回首页</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import voice from '../../utils/voice'

// ===== 语音系统 =====
const voiceOn = ref(true)
// ===== Web Speech API 初始化（等 voices 加载）=====
let voicesReady = false;
if (typeof window !== 'undefined' && window.speechSynthesis) {
  const tryInit = () => {
    const vs = window.speechSynthesis.getVoices();
    if (vs && vs.length > 0) voicesReady = true;
  };
  tryInit();
  window.speechSynthesis.addEventListener('voiceschanged', tryInit);
}

function speak(text) {
  if (!voiceOn.value || !text) return;
  try {
    if (typeof window !== 'undefined' && window.speechSynthesis) {
      window.speechSynthesis.cancel();
      const u = new SpeechSynthesisUtterance(text);
      u.lang = 'zh-CN';
      u.rate = 1.0;
      u.pitch = 1.0;
      const voices = window.speechSynthesis.getVoices();
      const v = voices.find(v => v.lang && (v.lang.startsWith('zh') || v.lang.includes('CN')));
      if (v) u.voice = v;
      u.onstart = () => console.log('🔊', text.substring(0, 20));
      u.onerror = (e) => console.warn('🔊 err:', e.error);
      window.speechSynthesis.speak(u);
      // fallback: 如果 voice 还没加载，500ms 后再试
      if (!v && !voicesReady) {
        setTimeout(() => {
          const v2 = window.speechSynthesis.getVoices().find(v => v.lang && v.lang.startsWith('zh'));
          if (v2) {
            const u2 = new SpeechSynthesisUtterance(text);
            u2.lang = 'zh-CN'; u2.voice = v2;
            window.speechSynthesis.speak(u2);
          }
        }, 500);
      }
    }
  } catch(e) { console.warn('speak err', e); }
}
function toggleVoice() {
  voiceOn.value = !voiceOn.value
  speak(voiceOn.value ? '语音已开启' : '语音已关闭')
}
import { sfxClick, sfxCorrect, sfxWrong, sfxWin, sfxEmergency, sfxDone } from '@/utils/audio.js'

const API = 'http://127.0.0.1:8080/api'
const hardcodedStepsBackup = []

// 从后端拉关卡数据，失败用硬编码 fallback
onMounted(async () => {
  // 先备份硬编码 steps（push 进去有 isInteractive + interactionType）
  steps.forEach(s => hardcodedStepsBackup.push({ ...s }))

  try {
    const res = await new Promise((resolve, reject) => {
      uni.request({
        url: API + '/app/first-aid/steps',
        timeout: 3000,
        success: r => resolve(r.data),
        fail: reject
      })
    })
    if (res.code === 200 && res.data?.length) {
      steps.length = 0
      res.data.forEach(s => {
        let stepData = {
          emoji: s.emoji || '❓',
          title: s.title,
          hint: s.hint,
          feedbackRight: s.feedbackRight,
          feedbackWrong: s.feedbackWrong,
          score: s.score || 10,
          // 关键！模板依赖这两个字段
          isInteractive: s.interactionType !== 'choice',
          interactionType: s.interactionType || 'choice'
        }
        if (s.interactionType === 'limb') stepData.correctAnswer = 'flat'
        if (s.interactionType === 'choice') {
          const wrongs = (s.wrongAnswer || '').split('/').map(w => w.trim()).filter(w => w)
          stepData.options = [
            { text: s.correctAnswer, correct: true },
            ...wrongs.slice(0, 3).map(w => ({ text: w, correct: false }))
          ]
        }
        steps.push(stepData)
      })
      console.log('✅ 从后端拉了 ' + steps.length + ' 步急救关卡')
    }
  } catch(e) {
    console.log('⚠️ 后端拉关卡失败，用硬编码 fallback')
    // 恢复备份
    steps.length = 0
    hardcodedStepsBackup.forEach(s => steps.push(s))
  }
  // 语音欢迎
  setTimeout(() => speak('欢迎来到急救闯关！开始学习蛇咬伤急救知识！'), 500)
})

function playSFX(type) {
  try {
    const map = {
      click: sfxClick, correct: sfxCorrect, wrong: sfxWrong,
      win: sfxWin, emergency: sfxEmergency, done: sfxDone
    }
    if (map[type]) map[type]()
  } catch(e) {}
}

// 9步急救流程（合同要求：脱离—认蛇—镇定—呼救—解压—制动—包扎—止痛—禁忌）
const steps = reactive([
  {
    emoji: '🏃',
    title: '脱离险境',
    hint: '刚被蛇咬了，首先应该做什么？',
    options: [
      { text: '赶紧跑开，远离毒蛇！', correct: true },
      { text: '站着不动看蛇会不会走', correct: false },
      { text: '捡起棍子打蛇报仇', correct: false },
      { text: '拍照发朋友圈先', correct: false }
    ],
    feedbackRight: '对！立刻远离蛇！不要试图抓蛇或拍照，安全第一！',
    feedbackWrong: '❌ 错误！毒蛇可能还在攻击范围内，必须立刻退后 3-5 米远离！'
  },
  {
    emoji: '🐍',
    title: '辨认蛇种',
    hint: '远离后怎么判断是不是毒蛇？',
    options: [
      { text: '三角形头 + 明显毒牙 + 彩色花纹', correct: true },
      { text: '圆头圆脑的就是毒蛇', correct: false },
      { text: '小蛇无毒 大蛇有毒', correct: false },
      { text: '看不清，不管它了', correct: false }
    ],
    feedbackRight: '✓ 好眼力！记住：三角形头是毒蛇典型特征！另外看伤口：2个深牙印是毒蛇，一排浅牙印是无毒蛇。',
    feedbackWrong: '❌ 错误！毒蛇典型特征：三角形头、有2个毒牙（咬伤有2个深牙印）、花纹艳丽。但有些毒蛇例外，看到就当有毒处理！'
  },
  {
    emoji: '😌',
    title: '保持镇定',
    hint: '确认是毒蛇咬伤后，最重要的是？',
    options: [
      { text: '保持冷静，不要慌，记住蛇的特征', correct: true },
      { text: '嚎啕大哭，等着有人来救', correct: false },
      { text: '立刻跑回家找药', correct: false },
      { text: '用嘴把毒吸出来', correct: false }
    ],
    feedbackRight: '✓ 对！紧张会让心跳加快，蛇毒扩散更快！深呼吸，保持镇静，回忆蛇的样子告诉医生。',
    feedbackWrong: '❌ 错误！千万不要跑！运动会加速血液循环，蛇毒扩散更快！也别用嘴吸，口腔有溃疡反而害你！'
  },
  // 第4关：模拟拨120
  {
    emoji: '📞',
    title: '紧急呼救',
    hint: '现在立刻拨打 120！',
    isInteractive: true,
    interactionType: 'phone',
    feedbackRight: '✓ 好样的！打通后说清楚："蛇咬伤！在XX路XX地方！人还清醒/不清醒！" 让急救室准备抗蛇毒血清！',
    feedbackWrong: ''
  },
  {
    emoji: '💧',
    title: '伤口解压',
    hint: '伤口怎么处理？',
    options: [
      { text: '用清水或肥皂水冲洗伤口周围', correct: true },
      { text: '用酒精/碘酒涂伤口', correct: false },
      { text: '用刀切开伤口排毒', correct: false },
      { text: '敷上泥土/草药', correct: false }
    ],
    feedbackRight: '✓ 对！用流动清水冲洗伤口周围即可，不要用酒精碘酒刺激伤口！也绝对不能切开排毒！',
    feedbackWrong: '❌ 错误！千万别用刀切开伤口！会损伤组织造成感染！别用酒精碘酒涂伤口，只用清水轻轻冲洗周围就好！'
  },
  // 第6关：伤肢摆放
  {
    emoji: '🦿',
    title: '伤肢制动',
    hint: '咬伤的手/腿应该怎么放？',
    isInteractive: true,
    interactionType: 'limb',
    correctAnswer: 'flat',
    feedbackRight: '✓ 对！伤肢要放平放低，与心脏同高！抬高会加速毒扩散，放低也会！',
    feedbackWrong: ''
  },
  // 第7关：拖拽包扎
  {
    emoji: '🩹',
    title: '加压包扎',
    hint: '用绷带在伤口上方（靠近心脏一侧）8-10cm处绑紧',
    isInteractive: true,
    interactionType: 'drag',
    feedbackRight: '✓ 完美！包扎位置正确！松紧合适能减缓淋巴回流但不影响动脉供血。记住：每绑1小时要松15分钟！',
    feedbackWrong: ''
  },
  {
    emoji: '💊',
    title: '止痛处理',
    hint: '伤者痛得厉害，怎么处理？',
    options: [
      { text: '绝对不要用止痛药！会加速毒扩散', correct: true },
      { text: '吃两片止痛药缓解下', correct: false },
      { text: '喝白酒麻痹一下', correct: false },
      { text: '用冰敷伤口止痛', correct: false }
    ],
    feedbackRight: '✓ 对！止痛药（特别是阿司匹林）和酒精会影响凝血功能，加重出血！冰敷会让血管收缩，反而加重局部组织坏死！',
    feedbackWrong: '❌ 错误！绝对不能吃止痛药！绝对不能喝酒！绝对不能冰敷！这些都可能致命！疼就忍着，去医院让医生处理！'
  },
  {
    emoji: '🚫',
    title: '禁忌清单',
    hint: '下面哪个是**允许**做的？',
    options: [
      { text: '保持伤口清洁，等待就医', correct: true },
      { text: '切开伤口排毒', correct: false },
      { text: '用嘴吸毒液', correct: false },
      { text: '敷草药/蜂蜜', correct: false }
    ],
    feedbackRight: '✓ 答对了！再强调一下：❌ 别切伤口 ❌ 别用嘴吸 ❌ 别敷草药蜂蜜 ❌ 别喝酒 ❌ 别跑！ 正确做法：制动+包扎+冲洗+尽快送医！',
    feedbackWrong: '❌ 错误！记住蛇伤三大禁忌：1.不切开伤口 2.不用嘴吸毒 3.不敷任何草药。保持干净等医生来！'
  }
])

const gameState = ref('start') // start / playing / done
const currentStep = ref(0)
const score = ref(0)
const wrongCount = ref(0)

const feedbackText = ref('')
const selectedOpt = ref(null)
const feedbackType = ref('')
const rightChoice = ref(false)
const wrongChoice = ref(false)
const canNext = ref(false)

// 交互关卡状态
const phoneCalled = ref(false)
const bandageDone = ref(false)
const limbAnswer = ref('')

function isInteractiveStep(idx) {
  return [3, 5, 6].includes(idx) // 拨号、伤肢摆放、包扎
}

function startGame() {
  speak('开始闯关！第 1 关，脱离险境！')
  gameState.value = 'playing'
  currentStep.value = 0
  score.value = 0
  wrongCount.value = 0
  feedbackText.value = ''
  phoneCalled.value = false
  bandageDone.value = false
  limbAnswer.value = ''
}

function showHint() {
  feedbackText.value = '💡 ' + steps[currentStep.value].hint
  feedbackType.value = 'hint'
  canNext.value = false
}

function selectOption(option) {
  if (feedbackText.value) return // 已选过
  if (option.correct) {
    rightChoice.value = true
    feedbackText.value = steps[currentStep.value].feedbackRight
    feedbackType.value = 'right'
    score.value += 10
    canNext.value = true
    speak('答对了！好样的！' + (steps[currentStep.value].feedbackRight || ''))
  } else {
    wrongChoice.value = true
    wrongCount.value++
    feedbackText.value = steps[currentStep.value].feedbackWrong
    feedbackType.value = 'wrong'
    canNext.value = false
    const correctOpt = steps[currentStep.value].options?.find(o => o.correct)?.text || ''
    speak('答错了！危险！正确答案是：' + correctOpt)
  }
}

// 第4关：拨120
function call120() {
  if (phoneCalled.value) return
  phoneCalled.value = true
  score.value += 10
  feedbackText.value = steps[3].feedbackRight
  feedbackType.value = 'right'
  canNext.value = true
  speak('拨打 120！蛇咬伤需要急救！')
}

// 第6关：伤肢摆放
function answerLimb(ans) {
  limbAnswer.value = ans
  const correct = steps[5].correctAnswer
  if (ans === correct) {
    score.value += 10
    feedbackText.value = steps[5].feedbackRight
    feedbackType.value = 'right'
    canNext.value = true
    speak('伤肢平放制动！正确！')
  } else {
    wrongCount.value++
    feedbackText.value = '❌ 这个位置不对！' + steps[5].feedbackRight
    feedbackType.value = 'wrong'
    canNext.value = false
    speak('位置不对！伤肢应该平放！')
  }
}

// 第7关：包扎
function dragBandage() {
  if (bandageDone.value) return
  bandageDone.value = true
  score.value += 10
  feedbackText.value = steps[6].feedbackRight
  feedbackType.value = 'right'
  canNext.value = true
  speak('加压包扎完成！太棒了！')
}

function retryStep() {
  feedbackText.value = ''
  feedbackType.value = ''
  rightChoice.value = false
  wrongChoice.value = false
  canNext.value = false
  // 重置交互关卡
  phoneCalled.value = false
  bandageDone.value = false
  limbAnswer.value = ''
}

function nextStep() {
  if (currentStep.value < steps.length - 1) {
    
    speak('第 ' + (currentStep.value + 2) + ' 关，' + (steps[currentStep.value + 1]?.title || ''))
    currentStep.value++
    feedbackText.value = ''
    feedbackType.value = ''
    rightChoice.value = false
    wrongChoice.value = false
    canNext.value = false
    phoneCalled.value = false
    bandageDone.value = false
    limbAnswer.value = ''
  } else {
    gameState.value = 'done'
    speak('🎉 恭喜通关！你已经完成了全部 9 关急救知识！')
  }
}

function resetGame() {
  startGame()
}

function goBack() {
  uni.navigateBack()
}
</script>

<style>
.game-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #FFEBEE 0%, #FFF 100%);
  padding-bottom: 180rpx;
}

/* 🔊 语音开关 */
.voice-switch {
  position: fixed;
  top: 16rpx;
  right: 16rpx;
  z-index: 999;
  display: flex;
  align-items: center;
  gap: 6rpx;
  background: #FFF;
  border: 2px solid #FF7043;
  border-radius: 40rpx;
  padding: 10rpx 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(255,112,67,.2);
}
.voice-icon { font-size: 28rpx; }
.voice-label { font-size: 22rpx; color: #FF7043; font-weight: bold; }

/* 进度条 */
.progress-bar {
  position: sticky;
  top: 0;
  z-index: 10;
  background: #fff;
  padding: 20rpx 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.08);
}
.progress-fill {
  height: 16rpx;
  background: linear-gradient(90deg, #E53935, #FF7043);
  border-radius: 8rpx;
  transition: width 0.4s ease;
}
.progress-text {
  display: block;
  text-align: center;
  font-size: 28rpx;
  color: #555;
  margin-top: 10rpx;
}

/* 开始界面 */
.start-screen {
  padding: 60rpx 40rpx;
  text-align: center;
}
.start-icon { font-size: 120rpx; margin-bottom: 20rpx; }
.start-title { font-size: 48rpx; font-weight: bold; color: #E53935; margin-bottom: 16rpx; }
.start-desc { font-size: 30rpx; color: #666; line-height: 1.8; margin-bottom: 40rpx; }
.start-steps {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
  margin-bottom: 50rpx;
}
.step-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx 10rpx;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.06);
}
.step-num {
  display: block;
  width: 48rpx; height: 48rpx;
  line-height: 48rpx;
  background: #E53935;
  color: #fff;
  border-radius: 50%;
  font-size: 24rpx;
  margin: 0 auto 8rpx;
}
.step-label { font-size: 22rpx; color: #555; }

.btn-start {
  background: linear-gradient(135deg, #E53935, #FF5252);
  color: #fff;
  font-size: 36rpx;
  font-weight: bold;
  padding: 24rpx 100rpx;
  border-radius: 50rpx;
  box-shadow: 0 8rpx 24rpx rgba(229,57,53,0.4);
}

/* 闯关界面 */
.play-screen { padding: 30rpx; }
.level-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx 30rpx;
  box-shadow: 0 8rpx 24rpx rgba(0,0,0,0.08);
}
.level-header { display: flex; align-items: center; margin-bottom: 30rpx; }
.level-emoji { font-size: 80rpx; margin-right: 20rpx; }
.level-title { font-size: 40rpx; font-weight: bold; color: #222; }
.level-hint { font-size: 28rpx; color: #888; margin-top: 8rpx; }

/* 选择选项 */
.choice-area { margin-top: 20rpx; }
.choice-option {
  display: flex;
  align-items: center;
  padding: 28rpx 24rpx;
  margin-bottom: 16rpx;
  background: #F8F9FA;
  border-radius: 16rpx;
  border-left: 8rpx solid #E0E0E0;
  transition: all 0.2s;
}
.choice-option.wrong { background: #FFEBEE; border-left-color: #E53935; }
.choice-option.right { background: #E8F5E9; border-left-color: #2E7D32; }
.option-letter {
  width: 48rpx; height: 48rpx;
  line-height: 48rpx;
  background: #9E9E9E;
  color: #fff;
  border-radius: 50%;
  text-align: center;
  font-size: 26rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
}
.choice-option.wrong .option-letter { background: #E53935; }
.choice-option.right .option-letter { background: #2E7D32; }
.option-text { flex: 1; font-size: 30rpx; color: #333; line-height: 1.6; }
.option-mark { font-size: 40rpx; color: #2E7D32; margin-left: 10rpx; }
.option-mark.wrong { color: #E53935; }

/* 反馈 */
.feedback {
  margin-top: 30rpx;
  padding: 24rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: flex-start;
}
.feedback.right { background: #E8F5E9; }
.feedback.wrong { background: #FFEBEE; }
.feedback.hint { background: #FFF9C4; }
.feedback-emoji { font-size: 36rpx; margin-right: 12rpx; flex-shrink: 0; }
.feedback-text { flex: 1; font-size: 28rpx; color: #333; line-height: 1.7; }
.feedback.wrong .feedback-text { color: #B71C1C; }

/* 模拟拨120 */
.dial-120 { margin-top: 30rpx; }
.phone-screen {
  background: #1A1A1A;
  border-radius: 24rpx;
  height: 180rpx;
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 24rpx;
}
.dialing-text { color: #FFC107; font-size: 30rpx; }
.connected-text { color: #4CAF50; font-size: 28rpx; }
.phone-keypad {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
  margin-bottom: 24rpx;
}
.key {
  background: #F5F5F5;
  border-radius: 16rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  font-size: 36rpx;
  font-weight: bold;
}
.key-call {
  background: #4CAF50;
  color: #fff;
  border-radius: 16rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  font-size: 48rpx;
}

/* 伤肢摆放 */
.limb-placement { margin-top: 30rpx; }
.limb-q { font-size: 32rpx; font-weight: bold; color: #333; margin-bottom: 20rpx; display: block; }
.limb-options { display: flex; gap: 16rpx; }
.limb-opt {
  flex: 1;
  background: #F5F5F5;
  border-radius: 16rpx;
  padding: 24rpx 12rpx;
  text-align: center;
}
.limb-opt.correct { background: #E8F5E9; }
.limb-emoji { font-size: 48rpx; display: block; margin-bottom: 8rpx; }
.limb-opt text { font-size: 24rpx; color: #333; }

/* 包扎 */
.bandage-area { margin-top: 30rpx; }
.bandage-scene {
  background: #F5F5F5;
  border-radius: 24rpx;
  padding: 40rpx;
  text-align: center;
  margin-bottom: 20rpx;
}
.arm-emoji { font-size: 100rpx; }
.wound-mark { font-size: 32rpx; margin-top: 8rpx; }
.bandage-target {
  margin-top: 20rpx;
  padding: 20rpx;
  border: 4rpx dashed #E53935;
  border-radius: 16rpx;
  color: #E53935;
  font-size: 26rpx;
}
.bandage-target.done {
  border-style: solid;
  background: #E8F5E9;
  border-color: #2E7D32;
  color: #2E7D32;
}
.bandage-strip {
  margin-top: 20rpx;
  padding: 20rpx;
  background: #FFCDD2;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #E53935;
  font-weight: bold;
}
.bandage-hint { font-size: 24rpx; color: #888; text-align: center; }

/* 底部按钮 */
.action-btns { margin-top: 40rpx; padding: 0 30rpx; }
.btn-next, .btn-retry, .btn-hint {
  width: 100%;
  padding: 28rpx;
  border-radius: 50rpx;
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}
.btn-next { background: linear-gradient(135deg, #2E7D32, #4CAF50); color: #fff; }
.btn-retry { background: #FFCDD2; color: #C62828; }
.btn-hint { background: #FFF9C4; color: #F57F17; }

/* 完成界面 */
.done-screen { padding: 80rpx 40rpx; text-align: center; }
.done-icon { font-size: 160rpx; margin-bottom: 20rpx; }
.done-title { font-size: 52rpx; font-weight: bold; color: #2E7D32; }
.done-score { font-size: 36rpx; color: #666; margin: 20rpx 0; }
.done-stars { font-size: 60rpx; letter-spacing: 20rpx; margin-bottom: 30rpx; }
.done-stars .dim { opacity: 0.2; }
.done-desc {
  background: #fff;
  padding: 30rpx;
  border-radius: 20rpx;
  font-size: 28rpx;
  color: #555;
  line-height: 1.8;
  margin-bottom: 50rpx;
}
.done-btns { display: flex; flex-direction: column; gap: 20rpx; }
.btn-replay {
  background: linear-gradient(135deg, #E53935, #FF5252);
  color: #fff;
  padding: 28rpx;
  border-radius: 50rpx;
  font-size: 32rpx;
}
.btn-back {
  background: #F5F5F5;
  color: #666;
  padding: 28rpx;
  border-radius: 50rpx;
  font-size: 32rpx;
}

/* ===== SVG 场景区动画 ===== */
.scene-stage { margin: 12rpx 0; border-radius: 16rpx; overflow: hidden; background: #FFF; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.06); }
.scene-svg { width: 100%; height: auto; display: block; }
.scene-wrap { background: #F5F5F5; }
.scene-wrap .scene-svg * { transform-box: fill-box; }
/* transform-origin 各 .anim-* 类自己定义，避免全局 center 覆盖腿/臂/草等的 top/bottom/left center */
.scene-wrap .scene-svg .anim-leg-back, .scene-wrap .scene-svg .anim-leg-front,
.scene-wrap .scene-svg .anim-body, .scene-wrap .scene-svg .anim-arm-right, .scene-wrap .scene-svg .anim-arm-left
  { transform-origin: top center; }
.scene-wrap .scene-svg .anim-head { transform-origin: bottom center; }
.scene-wrap .scene-svg .anim-grass, .scene-wrap .scene-svg .anim-grass2,
.scene-wrap .scene-svg .anim-tri { transform-origin: bottom center; }
.scene-wrap .scene-svg .anim-snake-body, .scene-wrap .scene-svg .anim-snake-head,
.scene-wrap .scene-svg .anim-warn-bubble { transform-origin: left center; }

/* 第1关：田间遇蛇 — 多部件动画 */
.anim-sun { animation: sunGlow 2s ease-in-out infinite; transform-origin: center; }
@keyframes sunGlow { 0%,100% { opacity: .9; } 50% { opacity: 1; transform: scale(1.1); } }

/* 云飘 */
.anim-cloud1 { animation: cloudMove 20s linear infinite; }
.anim-cloud2 { animation: cloudMove 30s linear infinite reverse; }
@keyframes cloudMove { 0% { transform: translateX(-50px); } 100% { transform: translateX(450px); } }

/* 草晃 */
.anim-grass { animation: grassSway 2.5s ease-in-out infinite; transform-origin: bottom center; }
.anim-grass2 { animation: grassSway 2.5s ease-in-out infinite .5s; transform-origin: bottom center; }
@keyframes grassSway { 0%,100% { transform: rotate(-3deg); } 50% { transform: rotate(3deg); } }

/* ===== 人走路动画（6部件联动）===== */
.anim-leg-back { animation: legBack 0.6s ease-in-out infinite; transform-origin: top center; }
@keyframes legBack { 0%,100% { transform: rotate(-20deg); } 50% { transform: rotate(20deg); } }
.anim-leg-front { animation: legFront 0.6s ease-in-out infinite; transform-origin: top center; }
@keyframes legFront { 0%,100% { transform: rotate(20deg); } 50% { transform: rotate(-20deg); } }
.anim-body { animation: bodyBob 0.6s ease-in-out infinite; transform-origin: top center; }
@keyframes bodyBob { 0%,100% { transform: translateY(0) rotate(0deg); } 25% { transform: translateY(-1px) rotate(-2deg); } 75% { transform: translateY(-1px) rotate(2deg); } }
.anim-arm-right { animation: armSwing 0.6s ease-in-out infinite; transform-origin: top center; }
@keyframes armSwing { 0%,100% { transform: rotate(25deg); } 50% { transform: rotate(-25deg); } }
.anim-arm-left { animation: armSwing 0.6s ease-in-out infinite reverse; transform-origin: top center; }
.anim-head { animation: headBob 0.6s ease-in-out infinite; transform-origin: bottom center; }
@keyframes headBob { 0%,100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(-2px) rotate(1deg); } }
.anim-sweat { animation: sweatFall 1.2s ease-in infinite; }
@keyframes sweatFall { 0% { cy: 8; opacity: 1; } 100% { cy: 20; opacity: 0; } }

/* ===== 蛇动画 ===== */
.anim-snake-body { animation: snakeWiggle 1.2s ease-in-out infinite; transform-origin: left center; }
@keyframes snakeWiggle { 0%,100% { transform: rotate(-4deg) scaleX(1); } 50% { transform: rotate(4deg) scaleX(1.02); } }
.anim-snake-head { animation: snakeHead 1.2s ease-in-out infinite; transform-origin: left center; }
@keyframes snakeHead { 0%,100% { transform: rotate(-5deg); } 50% { transform: rotate(5deg); } }
.anim-snake-eye { animation: eyePulse 1s ease-in-out infinite; transform-origin: center; }
@keyframes eyePulse { 0%,100% { transform: scale(1); } 50% { transform: scale(1.4); } }
.anim-tongue { animation: tongueFlick 0.5s ease-in-out infinite; }
@keyframes tongueFlick { 0%,100% { opacity: 1; transform: scaleX(1); } 50% { opacity: 0.3; transform: scaleX(.3); } }

/* 警告气泡弹出 */
.anim-warn-bubble { animation: warnPop 1.2s ease-in-out infinite; transform-origin: left center; }
@keyframes warnPop { 0%,100% { transform: scale(1); } 50% { transform: scale(1.1); } }

/* 第2关：辨认蛇种 */
.anim-tri { animation: triWarn 1s ease-in-out infinite; transform-origin: bottom center; }
@keyframes triWarn { 0%,100% { filter: brightness(1); } 50% { filter: brightness(1.4); transform: scale(1.05); } }
.anim-pulse { animation: eyePulse 1.5s ease-in-out infinite; transform-origin: center; }
@keyframes eyePulse { 0%,100% { transform: scale(1); } 50% { transform: scale(1.3); } }
.anim-oval { animation: ovalFloat 2.5s ease-in-out infinite; transform-origin: center; }
@keyframes ovalFloat { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-4px); } }

/* 第3关：保持镇定 */
.anim-calm { animation: calmBreathe 4s ease-in-out infinite; transform-origin: center; }
@keyframes calmBreathe { 0%,100% { transform: scale(1); } 50% { transform: scale(1.04); } }
.anim-ring { animation: ringExpand 3s ease-out infinite; transform-origin: center; }
.anim-ring2 { animation: ringExpand 3s ease-out infinite 1s; transform-origin: center; }
.anim-ring3 { animation: ringExpand 3s ease-out infinite 2s; transform-origin: center; }
@keyframes ringExpand { 0% { opacity: .9; } 50% { opacity: .3; } 100% { opacity: 0; } }

/* 第5关：伤口冲洗 */
.anim-handle { animation: handleTurn 3s ease-in-out infinite; transform-origin: center; }
@keyframes handleTurn { 0%,100% { transform: rotate(0deg); } 50% { transform: rotate(15deg); } }
.anim-water { animation: waterFlow 1.2s linear infinite; }
@keyframes waterFlow { 0% { stroke-dashoffset: 0; } 100% { stroke-dashoffset: 24; } }
.anim-wound { animation: woundPulse 1.2s ease-in-out infinite; transform-origin: center; }
@keyframes woundPulse { 0%,100% { transform: scale(1); opacity: .8; } 50% { transform: scale(1.2); opacity: 1; } }

/* 第8关：止痛处理 */
.anim-card { animation: cardSway 2.5s ease-in-out infinite; transform-origin: center; }
.anim-card2 { animation: cardSway 2.5s ease-in-out infinite .3s; transform-origin: center; }
.anim-card3 { animation: cardSway 2.5s ease-in-out infinite .6s; transform-origin: center; }
@keyframes cardSway { 0%,100% { transform: rotate(-2deg); } 50% { transform: rotate(2deg); } }
.anim-cross { animation: crossShake 0.5s ease-in-out infinite; transform-origin: center; }
@keyframes crossShake { 0%,100% { transform: scale(1); } 25% { transform: scale(1.2); } 75% { transform: scale(.9); } }

/* 第9关：禁忌清单 */
.scene-wrap svg rect[stroke="#E53935"] { animation: cardFloat 3s ease-in-out infinite; }
.scene-wrap svg rect[stroke="#43A047"] { animation: cardFloat 3s ease-in-out infinite 1s; }
@keyframes cardFloat { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-4px); } }

/* ===== ✅ 交互反馈特效 ===== */

/* level-card 答对/答错 整体联动 */
.level-card.card-right { animation: cardPulse .6s ease-out; box-shadow: 0 0 30px #4CAF50; }
.level-card.card-wrong { animation: cardShake .5s ease-in-out; box-shadow: 0 0 30px #E53935; }
@keyframes cardPulse { 0% { transform: scale(1); } 50% { transform: scale(1.02); } 100% { transform: scale(1); } }
@keyframes cardShake { 0%,100% { transform: translateX(0); } 20%,60% { transform: translateX(-8px); } 40%,80% { transform: translateX(8px); } }

/* 选项按钮点击反馈 */
.choice-option { transition: transform .15s, box-shadow .15s; }
.choice-option:active { transform: scale(.96); box-shadow: 0 2rpx 8rpx rgba(0,0,0,.15); }
.choice-option.right { animation: optionRight .5s ease-out; }
.choice-option.wrong { animation: optionWrong .5s ease-out; }
.choice-option.tapped { transform: scale(.97); }
@keyframes optionRight { 0% { background: #E8F5E9; } 50% { background: #66BB6A; transform: scale(1.05); } 100% { background: #C8E6C9; } }
@keyframes optionWrong { 0% { transform: translateX(0); } 25% { transform: translateX(-6px); } 75% { transform: translateX(6px); } 100% { transform: translateX(0); } }

/* feedback 弹出动画 */
.feedback { animation: feedbackPop .4s ease-out; }
@keyframes feedbackPop { 0% { opacity: 0; transform: translateY(20px) scale(.95); } 100% { opacity: 1; transform: translateY(0) scale(1); } }

/* 特效遮罩层 */
.fx-overlay { position: absolute; inset: 0; pointer-events: none; z-index: 50; }
.right-fx { animation: rightFlash .3s ease-out; }
.wrong-fx { animation: wrongFlash .4s ease-out; }
@keyframes rightFlash { 0% { background: rgba(76,175,80,0); } 30% { background: rgba(76,175,80,.15); } 100% { background: transparent; } }
@keyframes wrongFlash { 0% { background: rgba(229,57,53,0); } 30% { background: rgba(229,57,53,.2); } 100% { background: transparent; } }

/* 星星撒花 */
.fx-star { position: absolute; font-size: 40rpx; animation: starFall 1.5s ease-out forwards; }
.fx-star:nth-child(1) { left: 10%; animation-delay: .0s; }
.fx-star:nth-child(2) { left: 25%; animation-delay: .1s; }
.fx-star:nth-child(3) { left: 40%; animation-delay: .2s; }
.fx-star:nth-child(4) { left: 55%; animation-delay: .3s; }
.fx-star:nth-child(5) { left: 70%; animation-delay: .15s; }
.fx-star:nth-child(6) { left: 85%; animation-delay: .25s; }
.fx-star:nth-child(7) { left: 15%; animation-delay: .35s; }
.fx-star:nth-child(8) { left: 60%; animation-delay: .05s; }
.fx-star:nth-child(9) { left: 35%; animation-delay: .4s; }
.fx-star:nth-child(10){ left: 78%; animation-delay: .18s; }
@keyframes starFall {
  0% { top: 20%; opacity: 1; transform: scale(.5) rotate(0deg); }
  60% { opacity: 1; }
  100% { top: 90%; opacity: 0; transform: scale(1.5) rotate(360deg); }
}

/* +10 飘分 */
.fx-plus { position: absolute; top: 30%; left: 50%; transform: translateX(-50%); font-size: 80rpx; font-weight: bold; color: #4CAF50; text-shadow: 0 4rpx 8rpx rgba(0,0,0,.2); animation: plusFloat 1.2s ease-out forwards; }
@keyframes plusFloat { 0% { top: 35%; opacity: 0; transform: translateX(-50%) scale(.5); } 30% { opacity: 1; transform: translateX(-50%) scale(1.2); } 100% { top: 10%; opacity: 0; transform: translateX(-50%) scale(1); } }

/* ❌ 答错大字 */
.fx-boo { position: absolute; top: 40%; left: 50%; transform: translateX(-50%); font-size: 72rpx; font-weight: bold; color: #E53935; animation: booPop .6s ease-out; }
@keyframes booPop { 0% { opacity: 0; transform: translateX(-50%) scale(.3); } 50% { opacity: 1; transform: translateX(-50%) scale(1.2); } 100% { opacity: 0; transform: translateX(-50%) scale(1); } }

/* ========================================================
   ✨ 场景联动反馈：选对/选错 SVG 跟着变！
   ======================================================== */

/* ===== 第1关：脱离险境 ===== */
.scene-right .anim-body { animation: personEscape 1s ease-out forwards; }
.scene-right .anim-head { animation: personEscapeHead 1s ease-out forwards; }
.scene-right .anim-leg-back, .scene-right .anim-leg-front { animation: personRun 0.4s linear infinite; }
@keyframes personEscape { 0% { transform: translateX(0); } 100% { transform: translateX(-80px); opacity: .6; } }
@keyframes personEscapeHead { 0% { transform: translateX(0); } 100% { transform: translateX(-80px) translateY(-5px); opacity: .6; } }
@keyframes personRun { 0%,100% { transform: rotate(-35deg); } 50% { transform: rotate(35deg); } }
.scene-right .anim-snake-body, .scene-right .anim-snake-head { animation: snakeFade 1s ease-out forwards; }
.scene-right .anim-warn-bubble { animation: warnDisappear .8s ease-out forwards; }
@keyframes snakeFade { 0% { opacity: 1; } 100% { opacity: 0; transform: scale(.3) translateX(-30px); } }
@keyframes warnDisappear { 0% { opacity: 1; } 100% { opacity: 0; transform: scale(.3); } }
.scene-wrong .anim-snake-body { animation: snakeChase 1.5s ease-in forwards; }
.scene-wrong .anim-snake-head { animation: snakeBite 1.5s ease-in forwards; }
.scene-wrong .anim-body rect, .scene-wrong .anim-body { fill: #E53935 !important; }
.scene-wrong .anim-head circle { fill: #FFCDD2 !important; }
.scene-wrong .anim-sweat { animation-duration: 0.6s; }
@keyframes snakeChase { 0% { transform: translateX(0); } 100% { transform: translateX(220px); } }
@keyframes snakeBite { 0% { transform: translateX(0) rotate(0); } 100% { transform: translateX(220px) rotate(45deg); } }

/* ===== 第2关：辨认蛇种 ===== */
.scene-right .anim-tri { animation: triGood 1s ease-out forwards; filter: none; }
@keyframes triGood { 0% { filter: brightness(1); } 100% { filter: drop-shadow(0 0 15px #4CAF50); } }
.scene-right .anim-oval { animation: ovalFade 1s ease-out forwards; }
@keyframes ovalFade { to { opacity: .4; transform: scale(.9); } }
.scene-wrong .anim-tri { animation: triBad 0.3s ease-in-out infinite; }
@keyframes triBad { 0%,100% { filter: drop-shadow(0 0 5px #E53935); } 50% { filter: brightness(1.6) drop-shadow(0 0 20px #FF0000); } }
.scene-wrong .anim-oval { animation: ovalJump 0.5s ease-out; }
@keyframes ovalJump { 0% { transform: translateY(0); } 50% { transform: translateY(-15px) scale(1.1); } 100% { transform: translateY(0); } }

/* ===== 第3关：保持镇定 ===== */
.scene-right .anim-ring, .scene-right .anim-ring2, .scene-right .anim-ring3 { animation: ringCalm 2s ease-out forwards; }
@keyframes ringCalm { 0% { opacity: .9; } 100% { opacity: 0; stroke-width: 5; } }
.scene-right .anim-calm { animation: calmSuccess 1s ease-out forwards; }
@keyframes calmSuccess { 0% { transform: scale(1); } 50% { transform: scale(1.15); } 100% { transform: scale(1); } }
.scene-wrong .anim-ring, .scene-wrong .anim-ring2, .scene-wrong .anim-ring3 { animation: ringPanic 0.4s ease-in-out infinite; }
@keyframes ringPanic { 0%,100% { opacity: .9; transform: scale(1); } 50% { opacity: .3; transform: scale(.7); } }

/* ===== 第5关：伤口冲洗 ===== */
.scene-right .anim-water { stroke: #2196F3 !important; animation-duration: 0.8s; }
.scene-right .anim-wound { animation: woundHeal 1.5s ease-out forwards; }
@keyframes woundHeal { 0% { fill: #D32F2F; transform: scale(1); } 60% { fill: #FFCDD2; } 100% { fill: #81C784; transform: scale(.6); opacity: .3; } }
.scene-wrong .anim-water { stroke: #B71C1C !important; animation-duration: 2s; }
.scene-wrong .anim-wound { animation: woundWorsen 1s ease-in-out infinite; }
@keyframes woundWorsen { 0%,100% { fill: #D32F2F; transform: scale(1); } 50% { fill: #7F0000; transform: scale(1.4); } }

/* ===== 第7关：止痛处理 ===== */
.scene-right .anim-card, .scene-right .anim-card2, .scene-right .anim-card3 { animation: cardAccept 1s ease-out forwards; }
@keyframes cardAccept { to { filter: grayscale(1) opacity(.5); transform: scale(.9); } }
.scene-right .anim-cross { animation: crossFade .8s ease-out forwards; }
@keyframes crossFade { to { opacity: .2; transform: scale(.5); } }
.scene-wrong .anim-card, .scene-wrong .anim-card2, .scene-wrong .anim-card3 { animation: cardWarn .5s ease-in-out infinite; }
@keyframes cardWarn { 0%,100% { filter: brightness(1); } 50% { filter: drop-shadow(0 0 15px #FF0000); } }
.scene-wrong .anim-cross { animation: crossScare 0.3s ease-in-out infinite; }
@keyframes crossScare { 0%,100% { transform: scale(1); } 50% { transform: scale(1.6); } }

/* ===== 第8关：禁忌清单 ===== */
.scene-right svg rect[stroke="#E53935"] { animation: redDim 1.5s ease-out forwards; }
@keyframes redDim { to { opacity: .3; filter: grayscale(1); } }
.scene-right svg rect[stroke="#43A047"] { animation: greenGlow 1.5s ease-out forwards; }
@keyframes greenGlow { to { filter: drop-shadow(0 0 15px #4CAF50); transform: translateY(-5px) scale(1.02); } }
.scene-wrong svg rect[stroke="#E53935"] { animation: redBlink 0.4s ease-in-out infinite; }
@keyframes redBlink { 0%,100% { filter: brightness(1); } 50% { filter: brightness(1.5) drop-shadow(0 0 15px #FF0000); } }
.scene-wrong svg rect[stroke="#43A047"] { opacity: .4; }
</style>