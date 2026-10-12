<template>
  <view class="snake-learn-container" :class="{ 'big-font': bigFont }">
    <!-- 🔊 语音测试大按钮 -->
    <view class="test-voice-bar">
      <view class="test-voice-btn" @click="speak('语音测试！如果你听到这段话，说明语音正常工作啦！')">
        <text class="tv-icon">🔊</text>
        <text class="tv-label">点我测试语音</text>
      </view>
      <view class="voice-status">状态: {{ voiceOn ? '🟢 已开启' : '🔴 已关闭' }}</view>
    </view>

    <!-- 顶部进度 -->
    <view class="top-bar">
      <view class="tabs">
        <view class="tab" :class="{ active: phase === 'play' }" @click="goPhase('play')">
          <text class="tab-emoji">🎮</text>
          <text class="tab-label">玩</text>
        </view>
        <view class="tab" :class="{ active: phase === 'learn' }" @click="goPhase('learn')">
          <text class="tab-emoji">🐍</text>
          <text class="tab-label">认</text>
        </view>
        <view class="tab" :class="{ active: phase === 'practice' }" @click="goPhase('practice')">
          <text class="tab-emoji">✍️</text>
          <text class="tab-label">练</text>
        </view>
        <view class="tab" :class="{ active: phase === 'test' }" @click="goPhase('test')">
          <text class="tab-emoji">📝</text>
          <text class="tab-label">测</text>
        </view>
      </view>
      <view class="top-actions">
        <text class="score-pill">🏆 {{ totalScore }}</text>
      </view>
    </view>

    <!-- ========== Phase 1: 玩（象形动画 + 危险直觉建立） ========== -->
    <view v-if="phase === 'play'" class="phase-play">
      <view class="phase-desc">
        <text class="phase-title">🎮 玩一玩 · 建立危险直觉</text>
        <text class="phase-sub">看动画，记住毒蛇长什么样！三角形头是关键！</text>
      </view>

      <!-- 象形动画区 -->
      <view class="animation-area">
        <!-- 蛇的动画 SVG -->
        <view class="snake-scene">
          <!-- 毒蛇（三角形头）动画 -->
          <view class="snake poisonous" @click="showSnake('poisonous')">
            <view class="snake-svg">
              <!-- 三角形头 -->
              <view class="head triangle">
                <view class="fangs">🦷🦷</view>
              </view>
              <!-- 蛇身 -->
              <view class="body-seg" v-for="n in 6" :key="n" :style="{ animationDelay: (n*0.1)+'s' }"></view>
              <!-- 尾巴 -->
              <view class="tail"></view>
            </view>
            <text class="snake-label dangerous">⚠️ 有毒！三角头！</text>
          </view>

          <!-- 无毒蛇（椭圆形头）动画 -->
          <view class="snake harmless" @click="showSnake('harmless')">
            <view class="snake-svg">
              <view class="head round"></view>
              <view class="body-seg green" v-for="n in 6" :key="n" :style="{ animationDelay: (n*0.1)+'s' }"></view>
              <view class="tail"></view>
            </view>
            <text class="snake-label safe">✅ 无毒！椭圆头！</text>
          </view>
        </view>

        <!-- 记忆挑战：快速点毒蛇 -->
        <view class="quick-challenge">
          <text class="challenge-title">🎯 快速识别挑战！</text>
          <text class="challenge-desc" v-if="!challengeStart">点击"开始"，快速点出所有毒蛇！</text>
          <text class="challenge-desc" v-else>还有 {{ challengeTime }} 秒！点出毒蛇！</text>
          <view class="challenge-grid" v-if="challengeStart">
            <view class="challenge-item" 
                  :class="{ 'is-poisonous': item.poisonous, wrong: item.wrong, right: item.right }"
                  v-for="(item, idx) in challengeItems" 
                  :key="idx"
                  @click="clickChallenge(idx)">
              <text class="c-emoji">{{ item.emoji }}</text>
              <text class="c-shape">{{ item.shape }}</text>
            </view>
          </view>
          <button v-if="!challengeStart" class="btn-start-challenge" @click="startChallenge">▶ 开始挑战</button>
          <view v-else class="challenge-result">
            <text>得分: {{ challengeScore }}/{{ challengeItems.length }}</text>
            <button class="btn-retry-small" @click="startChallenge">🔄 再来</button>
          </view>
        </view>
      </view>
    </view>

    <!-- ========== Phase 2: 认（特征认知） ========== -->
    <view v-if="phase === 'learn'" class="phase-learn">
      <view class="phase-desc">
        <text class="phase-title">🐍 认一认 · 记忆保命特征</text>
        <text class="phase-sub">滑动卡片，记住 12 种常见蛇的保命特征！</text>
      </view>

      <swiper class="snake-swiper" indicator-dots :current="learnIdx" @change="onSwiperChange">
        <swiper-item v-for="s in snakes" :key="s.id">
          <view class="learn-card" :class="{ poisonous: s.isPoisonous }">
            <view class="card-badge" :class="{ danger: s.isPoisonous }">
              {{ s.isPoisonous === 1 ? '⚠️ 毒蛇' : '✅ 无毒' }}
            </view>
            <view class="card-emoji">{{ getSnakeEmoji(s.name) }}</view>
            <text class="card-name">{{ s.name }}</text>
            <view class="card-features">
              <view class="feature-row">
                <text class="f-label">头型:</text>
                <text class="f-value">{{ s.headShape }}</text>
              </view>
              <view class="feature-row">
                <text class="f-label">花纹:</text>
                <text class="f-value">{{ s.pattern }}</text>
              </view>
              <view class="feature-row">
                <text class="f-label">颜色:</text>
                <text class="f-value">{{ s.color }}</text>
              </view>
              <view class="feature-row" v-if="s.venomType">
                <text class="f-label">毒性:</text>
                <text class="f-value venom">{{ s.venomType }}</text>
              </view>
            </view>
            <view class="card-tip">
              💡 保命记忆：{{ s.memoryTip || (s.headShape + ' + ' + s.pattern) }}
            </view>
          </view>
        </swiper-item>
      </swiper>

      <view class="swiper-controls">
        <button class="btn-nav" @click="learnPrev">← 上一条</button>
        <text class="swiper-count">{{ learnIdx + 1 }} / {{ snakes.length }}</text>
        <button class="btn-nav" @click="learnNext">下一条 →</button>
      </view>
    </view>

    <!-- ========== Phase 3: 练（场景练习） ========== -->
    <view v-if="phase === 'practice'" class="phase-practice">
      <view class="phase-desc">
        <text class="phase-title">✍️ 练一练 · 模拟真实决策</text>
        <text class="phase-sub">场景判断：遇到这种蛇该怎么办？</text>
      </view>

      <view class="practice-card" v-if="practiceIdx < practiceQuestions.length">
        <view class="scene-desc">
          <text class="scene-title">🏞️ 场景 {{ practiceIdx + 1 }}：</text>
          <text class="scene-text">{{ practiceQuestions[practiceIdx].scene }}</text>
        </view>
        <view class="snake-reveal">
          <text class="sr-emoji">{{ practiceQuestions[practiceIdx].snakeEmoji }}</text>
          <text class="sr-name">{{ practiceQuestions[practiceIdx].snakeName }}</text>
        </view>
        <view class="practice-options">
          <view class="p-option" 
                :class="{ right: practiceCorrect && opt.correct, wrong: practiceWrong && !opt.correct }"
                v-for="(opt, i) in practiceQuestions[practiceIdx].options" 
                :key="i"
                @click="answerPractice(opt)">
            <text class="p-letter">{{ String.fromCharCode(65 + i) }}</text>
            <text class="p-text">{{ opt.text }}</text>
          </view>
        </view>
        <view class="p-feedback" v-if="practiceFeedback">
          <text>{{ practiceFeedback }}</text>
        </view>
      </view>

      <view class="practice-controls" v-if="practiceIdx < practiceQuestions.length">
        <button v-if="practiceFeedback && practiceCanNext" class="btn-next" @click="nextPractice">
          {{ practiceIdx < practiceQuestions.length - 1 ? '下一题 →' : '开始测试 📝' }}
        </button>
      </view>
    </view>

    <!-- ========== Phase 4: 测（场景通关） ========== -->
    <view v-if="phase === 'test'" class="phase-test">
      <view class="phase-desc">
        <text class="phase-title">📝 测一测 · 场景通关</text>
        <text class="phase-sub">{{ testIdx < testQuestions.length ? '共 ' + testQuestions.length + ' 题，答对 ' + testScore + '/' + testIdx : '测试完成！' }}</text>
      </view>

      <view v-if="testIdx < testQuestions.length" class="test-card">
        <view class="test-progress">
          <view class="tp-fill" :style="{ width: (testIdx / testQuestions.length * 100) + '%' }"></view>
        </view>
        <view class="test-q-title">第 {{ testIdx + 1 }} 题：{{ testQuestions[testIdx].question }}</view>
        <view class="test-options">
          <view class="t-option"
                :class="{ right: testAnswered && i === correctTestIdx, wrong: testAnswered && selectedOpt === i && i !== correctTestIdx }"
                v-for="(opt, i) in testQuestions[testIdx].options"
                :key="i"
                @click="answerTest(i)">
            <text class="t-letter">{{ String.fromCharCode(65 + i) }}</text>
            <text class="t-text">{{ opt }}</text>
          </view>
        </view>
        <view class="test-feedback" v-if="testFeedback">
          <text>{{ testFeedback }}</text>
        </view>
        <button v-if="testAnswered" class="btn-next" @click="nextTest">
          {{ testIdx < testQuestions.length - 1 ? '下一题 →' : '查看成绩 🏆' }}
        </button>
      </view>

      <view v-else class="test-result">
        <text class="tr-emoji">🏆</text>
        <text class="tr-score">{{ testScore }} / {{ testQuestions.length }}</text>
        <view class="tr-stars">
          <text v-for="n in 3" :key="n" :class="{ dim: testScore < n * 2 }">⭐</text>
        </view>
        <text class="tr-desc">{{ testDesc }}</text>
        <button class="btn-replay" @click="resetTest">🔄 再测一次</button>
      </view>
    </view>

    <!-- 全局语音+主题按钮 -->
    <view class="float-tools">
      <view class="tool-btn" @click="toggleVoice">
        <text>{{ voiceOn ? '🔊' : '🔇' }}</text>
      </view>
      <view class="tool-btn" @click="toggleBigFont">
        <text>{{ bigFont ? '🔤' : 'A' }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { API_BASE } from '@/utils/api'
import { ref, reactive, computed, onMounted } from 'vue'
import voice from '../../utils/voice'

// ===== 状态 =====
const phase = ref('play') // play / learn / practice / test
const bigFont = ref(false)
const voiceOn = ref(true)
const totalScore = ref(0)
const backendLoaded = ref(false) // 是否已从后端拉到题目

// ===== 语音反馈 =====
// 初始化 voices（Chrome 需要等 voiceschanged 事件）
let voicesReady = false
// #ifdef H5
if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
  const tryInit = () => {
    const vs = window.speechSynthesis.getVoices()
    if (vs && vs.length > 0) voicesReady = true
  }
  tryInit()
  window.speechSynthesis.addEventListener('voiceschanged', tryInit)
}
// #endif

function speak(text) {
  if (!voiceOn.value || !text) return
  try {
    // #ifdef H5
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel()
      const u = new SpeechSynthesisUtterance(text)
      u.lang = 'zh-CN'
      u.rate = 0.9
      u.pitch = 1.0
      const voices = window.speechSynthesis.getVoices()
      const v = voices.find(v => v.lang && (v.lang.startsWith('zh') || v.lang.includes('CN')))
      if (v) u.voice = v
      window.speechSynthesis.speak(u)
      // fallback
      if (!v && !voicesReady) {
        setTimeout(() => {
          const v2 = window.speechSynthesis.getVoices().find(v => v.lang && v.lang.startsWith('zh'))
          if (v2) {
            const u2 = new SpeechSynthesisUtterance(text)
            u2.lang = 'zh-CN'; u2.voice = v2; u2.rate = 0.9
            window.speechSynthesis.speak(u2)
          }
        }, 500)
      }
    }
    // #endif
  } catch(e) { console.warn('speak err', e) }
}

// ===== 页面加载后欢迎语 + 后端拉题 =====
onMounted(() => {
  setTimeout(() => {
    speak('欢迎来到蛇类识别学习！玩一玩，认一认，练一练，测一测！')
  }, 800)
  loadQuestionsFromBackend()
})

// ===== 从后端拉 LEARN/PRACTICE 题目 =====
// Question 表格式 → 前端格式转换
function questionToPractice(q) {
  const letters = ['A', 'B', 'C', 'D']
  const opts = [q.optionA, q.optionB, q.optionC, q.optionD]
    .filter(o => o != null && o !== '')
    .map((text, idx) => ({
      text: letters[idx] + '. ' + text,
      correct: letters[idx] === q.correctAnswer
    }))
  return {
    scene: q.question,
    snakeName: '',
    snakeEmoji: '🐍',
    options: opts,
    answer: q.correctAnswer,
    explain: q.analysis || ''
  }
}
function questionToTest(q) {
  const opts = [q.optionA, q.optionB, q.optionC, q.optionD]
    .filter(o => o != null && o !== '')
  return {
    id: q.id,
    question: q.question,
    options: opts,
    correctAnswer: q.correctAnswer, // 'A'/'B'/'C'/'D'
    analysis: q.analysis || ''
  }
}

async function loadQuestionsFromBackend() {
  // #ifdef H5
  try {
    const base = API_BASE
    // PRACTICE 题
    const pRes = await fetch(base + '/app/quiz/pick?category=PRACTICE&count=5')
    const pJson = await pRes.json()
    if (pJson && pJson.code === 200 && pJson.data && pJson.data.length > 0) {
      const list = pJson.data.map(questionToPractice)
      practiceQuestions.splice(0, practiceQuestions.length, ...list)
    }
    // LEARN 题
    const lRes = await fetch(base + '/app/quiz/pick?category=LEARN&count=8')
    const lJson = await lRes.json()
    if (lJson && lJson.code === 200 && lJson.data && lJson.data.length > 0) {
      const list = lJson.data.map(questionToTest)
      testQuestions.splice(0, testQuestions.length, ...list)
    }
    backendLoaded.value = true
    console.log('[learn.vue] 后端题目加载成功：PRACTICE=' + practiceQuestions.length + ', LEARN=' + testQuestions.length)
  } catch (e) {
    console.warn('[learn.vue] 后端拉题失败，使用硬编码 fallback:', e)
  }
  // #endif
  // #ifndef H5
  backendLoaded.value = false
  // #endif
}

function toggleVoice() {
  voiceOn.value = !voiceOn.value
  speak(voiceOn.value ? '语音反馈已开启' : '语音反馈已关闭')
}
function toggleBigFont() { bigFont.value = !bigFont.value }

// ===== 蛇种数据（12种）=====
const snakes = reactive([
  { id:1, name:'银环蛇', isPoisonous:1, headShape:'椭圆形但较扁', pattern:'体背黑白相间环纹', color:'黑色+白色环', venomType:'神经毒', memoryTip:'银黑两色环，夜晚会发光，极毒！' },
  { id:2, name:'金环蛇', isPoisonous:1, headShape:'椭圆形', pattern:'体背黄黑相间环纹', color:'黑色+黄色环', venomType:'神经毒', memoryTip:'黄黑两色环，和银环蛇亲戚' },
  { id:3, name:'眼镜蛇', isPoisonous:1, headShape:'椭圆形可扩张成兜', pattern:'颈部有眼镜状斑纹', color:'黑褐/棕褐色', venomType:'混合毒', memoryTip:'会"竖起"展开颈部像眼镜！' },
  { id:4, name:'五步蛇', isPoisonous:1, headShape:'三角形', pattern:'体背有深色大斑块', color:'深褐色', venomType:'血液毒', memoryTip:'三角头+大斑块+尖吻，极毒！' },
  { id:5, name:'竹叶青', isPoisonous:1, headShape:'三角形', pattern:'通体绿色无明显花纹', color:'翠绿色', venomType:'血液毒', memoryTip:'翠绿色小蛇，三角头，常挂树枝上！' },
  { id:6, name:'短尾蝮', isPoisonous:1, headShape:'三角形', pattern:'两行深色圆斑交错', color:'棕褐色', venomType:'血液毒', memoryTip:'全国最常见毒蛇！三角头+圆斑' },
  { id:7, name:'烙铁头蛇', isPoisonous:1, headShape:'三角形如烙铁', pattern:'X或Y形斑纹', color:'棕褐带橙', venomType:'血液毒', memoryTip:'头大如烙铁，X/Y花纹' },
  { id:8, name:'圆斑蝰', isPoisonous:1, headShape:'三角形', pattern:'三行圆形大斑', color:'黄褐/灰褐', venomType:'血液毒', memoryTip:'圆斑+三角头，毒性剧烈！' },
  { id:9, name:'海蛇(青环)', isPoisonous:1, headShape:'椭圆形', pattern:'浅色窄环纹', color:'橄榄色+浅环', venomType:'神经毒', memoryTip:'海里的蛇！尾巴侧扁像桨！' },
  { id:10, name:'莽山烙铁头', isPoisonous:1, headShape:'三角形', pattern:'大片黄绿斑纹', color:'黑褐+黄绿', venomType:'血液毒', memoryTip:'极危！世界最大烙铁头，尾部白色！' },
  { id:11, name:'乌梢蛇', isPoisonous:0, headShape:'椭圆形', pattern:'体背有浅色纵纹', color:'绿褐/棕褐', venomType:'', memoryTip:'常见无毒蛇，椭圆形头，性情温和' },
  { id:12, name:'王锦蛇', isPoisonous:0, headShape:'椭圆形', pattern:'体背有黄色斑块如菜花', color:'黑色+黄色花斑', venomType:'', memoryTip:'菜花蛇！体型大，无毒还吃毒蛇！' }
])

function getSnakeEmoji(name) {
  const map = {
    '银环蛇':'🐍','金环蛇':'🐍','眼镜蛇':'🐍','五步蛇':'🐍',
    '竹叶青':'🐍','短尾蝮':'🐍','烙铁头蛇':'🐍','圆斑蝰':'🐍',
    '海蛇(青环)':'🌊','莽山烙铁头':'🐍','乌梢蛇':'🐍','王锦蛇':'🐍'
  }
  return map[name] || '🐍'
}

// ===== Phase 1: 玩 =====
const challengeStart = ref(false)
const challengeTime = ref(15)
const challengeScore = ref(0)
const challengeItems = reactive([])
let timer = null

function showSnake(type) {
  speak(type === 'poisonous' ? '毒蛇！三角头！有危险！' : '无毒蛇！椭圆头！安全！')
}

function startChallenge() {
  challengeItems.length = 0
  challengeScore.value = 0
  challengeTime.value = 15
  // 生成9个随机蛇（6毒3无毒）
  const shuffled = [...snakes].sort(() => Math.random() - 0.5).slice(0, 9)
  shuffled.forEach(s => {
    challengeItems.push({
      emoji: getSnakeEmoji(s.name),
      shape: s.headShape.includes('三角') ? '🔺' : '⭕',
      poisonous: s.isPoisonous === 1,
      clicked: false,
      right: false,
      wrong: false
    })
  })
  challengeStart.value = true
  if (timer) clearInterval(timer)
  timer = setInterval(() => {
    challengeTime.value--
    if (challengeTime.value <= 0) {
      clearInterval(timer)
      challengeStart.value = false
    }
  }, 1000)
}

function clickChallenge(idx) {
  const item = challengeItems[idx]
  if (item.clicked) return
  item.clicked = true
  if (item.poisonous) {
    item.right = true
    challengeScore.value++
    speak('对了！这是毒蛇！')
  } else {
    item.wrong = true
    speak('错了！这是无毒蛇！')
  }
}

// ===== Phase 2: 认（swiper）=====
const learnIdx = ref(0)
function onSwiperChange(e) { learnIdx.value = e.detail.current }
function learnPrev() { learnIdx.value = Math.max(0, learnIdx.value - 1) }
function learnNext() { learnIdx.value = Math.min(snakes.length - 1, learnIdx.value + 1) }
function goPhase(p) { phase.value = p }

// ===== Phase 3: 练 =====
const practiceQuestions = reactive([
  {
    scene: '你在菜园里看到一条小蛇，身体翠绿色挂在菜叶上，头是三角形的...',
    snakeName: '竹叶青',
    snakeEmoji: '🐍',
    options: [
      { text: 'A. 无毒蛇，抓来泡酒', correct: false },
      { text: 'B. 有毒！立刻离开并报120', correct: true },
      { text: 'C. 用棍子打死后丢', correct: false },
      { text: 'D. 拍照发朋友圈', correct: false }
    ],
    answer: 'B',
    explain: '翠绿色+三角头=竹叶青！剧毒血液毒！立刻远离！'
  },
  {
    scene: '晚上走路踩到一条蛇，蛇头是黑色椭圆形，身体有黑白环纹...',
    snakeName: '银环蛇',
    snakeEmoji: '🐍',
    options: [
      { text: 'A. 无毒，没事继续走', correct: false },
      { text: 'B. 有毒！神经毒！立刻送医！', correct: true },
      { text: 'C. 用嘴吸出毒液', correct: false },
      { text: 'D. 敷草药止血', correct: false }
    ],
    answer: 'B',
    explain: '银环蛇！神经毒之王！咬后不疼但会窒息！必须立刻送医院！'
  },
  {
    scene: '夏天田埂上看到一条蛇，头大呈三角形，体背有深色圆斑交错...',
    snakeName: '短尾蝮',
    snakeEmoji: '🐍',
    options: [
      { text: 'A. 无毒菜花蛇', correct: false },
      { text: 'B. 有毒！保持冷静，制动伤肢', correct: true },
      { text: 'C. 赶紧跑回家', correct: false },
      { text: 'D. 切开伤口排毒', correct: false }
    ],
    answer: 'B',
    explain: '短尾蝮！全国最常见毒蛇！三角头+圆斑特征！别跑！制动！'
  },
  {
    scene: '河边游泳被蛇咬，伤口不痛但肌肉开始疼，蛇尾巴像桨一样侧扁...',
    snakeName: '海蛇',
    snakeEmoji: '🌊',
    options: [
      { text: 'A. 无毒水蛇，不管它', correct: false },
      { text: 'B. 海蛇！神经毒！立刻上岸报120！', correct: true },
      { text: 'C. 冰敷伤口止痛', correct: false },
      { text: 'D. 继续游，回家再说', correct: false }
    ],
    answer: 'B',
    explain: '海蛇！剧毒神经毒！尾巴侧扁是标志！咬后不痛但会全身瘫痪！'
  },
  {
    scene: '看到一条大蛇，头椭圆形，身体有黄色花斑...',
    snakeName: '王锦蛇(菜花蛇)',
    snakeEmoji: '🐍',
    options: [
      { text: 'A. 有毒！三角头要注意！', correct: false },
      { text: 'B. 无毒菜花蛇！可以安全走开', correct: true },
      { text: 'C. 剧毒！立刻开枪', correct: false },
      { text: 'D. 抓来炖汤喝', correct: false }
    ],
    answer: 'B',
    explain: '王锦蛇=菜花蛇！椭圆形头=无毒标志！还会吃毒蛇呢！'
  }
])

const practiceIdx = ref(0)
const practiceFeedback = ref('')
const practiceCorrect = ref(false)
const practiceWrong = ref(false)
const practiceCanNext = ref(false)

function answerPractice(opt) {
  if (practiceFeedback.value) return
  if (opt.correct) {
    practiceCorrect.value = true
    practiceFeedback.value = '✅ 答对了！' + practiceQuestions[practiceIdx.value].explain
    practiceCanNext.value = true
    totalScore.value += 20
    speak('答对了！')
  } else {
    practiceWrong.value = true
    practiceFeedback.value = '❌ 不对！' + practiceQuestions[practiceIdx.value].explain
    practiceCanNext.value = false
    speak('答错了！')
  }
}

function nextPractice() {
  practiceIdx.value++
  practiceFeedback.value = ''
  practiceCorrect.value = false
  practiceWrong.value = false
  practiceCanNext.value = false
}

// ===== Phase 4: 测 =====
const testQuestions = reactive([
  { question: '毒蛇最典型的头部形状是？', options: ['圆形','三角形','正方形','扁形'] },
  { question: '银环蛇和金环蛇属于什么毒？', options: ['血液毒','神经毒','混合毒','无毒'] },
  { question: '被毒蛇咬伤后应该？', options: ['立刻跑回家','保持冷静制动伤肢','用嘴吸毒','敷草药'] },
  { question: '哪个是无毒蛇？', options: ['五步蛇','竹叶青','乌梢蛇','圆斑蝰'] },
  { question: '蛇伤急救三大禁忌不包括？', options: ['不切伤口','不吸毒','不敷草药','不吃鸡蛋'] },
  { question: '竹叶青蛇的典型颜色是？', options: ['黑色','绿色','红色','白色'] },
  { question: '海蛇的尾巴有什么特征？', options: ['尖细','侧扁像桨','分叉','短粗'] },
  { question: '下面哪种不是毒蛇？', options: ['眼镜蛇','王锦蛇','烙铁头','莽山烙铁头'] }
])

const testIdx = ref(0)
const testScore = ref(0)
const testAnswered = ref(false)
const testFeedback = ref('')
const selectedOpt = ref(-1)
const correctTestIdx = ref(1) // 默认正确答案在 index 1（大部分题都对）

// 计算当前题正确答案 index（直接用 correctAnswer 字段，兼容旧硬编码）
function getCorrectIdx() {
  const q = testQuestions[testIdx.value]
  // 后端加载的新格式：有 correctAnswer 字段
  if (q.correctAnswer) {
    const letterToIdx = { 'A': 0, 'B': 1, 'C': 2, 'D': 3 }
    return letterToIdx[q.correctAnswer] ?? 0
  }
  // 旧硬编码 fallback：根据题目匹配
  const question = q.question
  if (question.includes('毒蛇最典型')) return 1
  if (question.includes('什么毒')) return 1
  if (question.includes('咬伤后')) return 1
  if (question.includes('哪个是无毒')) return 2
  if (question.includes('不包括')) return 3
  if (question.includes('竹叶青')) return 1
  if (question.includes('尾巴')) return 1
  if (question.includes('哪种不是')) return 1
  return 1
}

function answerTest(i) {
  if (testAnswered.value) return
  selectedOpt.value = i
  correctTestIdx.value = getCorrectIdx()
  const q = testQuestions[testIdx.value]
  const letter = String.fromCharCode(65 + i) // i=0→'A', i=1→'B'...
  let isCorrect = false
  if (q.correctAnswer) {
    // 后端加载：直接比较 correctAnswer
    isCorrect = letter === q.correctAnswer
  } else {
    // 旧硬编码 fallback
    isCorrect = i === correctTestIdx.value
  }
  if (isCorrect) {
    testScore.value++
    testFeedback.value = '✅ 答对了！' + (q.analysis || '')
  } else {
    const correctLetter = q.correctAnswer || String.fromCharCode(65 + correctTestIdx.value)
    testFeedback.value = '❌ 不对！正确答案是 ' + correctLetter + '！' + (q.analysis || '')
  }
  testAnswered.value = true
  speak(isCorrect ? '答对了！' : '答错了！')
}

function nextTest() {
  testIdx.value++
  testAnswered.value = false
  testFeedback.value = ''
  selectedOpt.value = -1
}

function resetTest() {
  testIdx.value = 0
  testScore.value = 0
  testAnswered.value = false
  testFeedback.value = ''
  selectedOpt.value = -1
}

const testDesc = computed(() => {
  const s = testScore.value
  if (s >= 7) return '🏆 太棒了！你是蛇伤急救达人！'
  if (s >= 5) return '👍 不错！急救知识掌握良好！'
  if (s >= 3) return '💪 继续努力！多看几遍吧！'
  return '📚 别灰心！再练练就好了！'
})
</script>

<style scoped>
.snake-learn-container {
  min-height: 100vh;
  background: #FFF8E1;
  padding-bottom: 120rpx;
}
.snake-learn-container.big-font {
  font-size: 36rpx;
}
.snake-learn-container.big-font .tab-label { font-size: 32rpx; }
.snake-learn-container.big-font .phase-title { font-size: 40rpx; }

/* 顶部 */
.top-bar {
  position: sticky; top: 0; z-index: 20;
  background: linear-gradient(135deg, #FF6F00, #E65100);
  padding: 20rpx;
}
.tabs { display: flex; justify-content: space-around; }
.tab {
  display: flex; flex-direction: column; align-items: center;
  color: rgba(255,255,255,0.7);
  padding: 10rpx 20rpx;
  border-radius: 16rpx;
  transition: all 0.2s;
}
.tab.active { background: rgba(255,255,255,0.25); color: #fff; }
.tab-emoji { font-size: 36rpx; }
.tab-label { font-size: 26rpx; font-weight: bold; margin-top: 4rpx; }
.top-actions { text-align: right; padding-right: 10rpx; }
.score-pill {
  background: rgba(255,255,255,0.3);
  color: #fff; font-size: 24rpx; padding: 6rpx 16rpx; border-radius: 20rpx;
}

.phase-desc {
  padding: 30rpx; text-align: center;
}
.phase-title { font-size: 34rpx; font-weight: bold; color: #E65100; display: block; }
.phase-sub { font-size: 26rpx; color: #888; margin-top: 8rpx; display: block; }

/* ===== Phase 1: 玩 ===== */
.snake-scene {
  display: flex; justify-content: space-around; padding: 30rpx;
}
.snake { text-align: center; }
.snake-svg { display: flex; flex-direction: column; align-items: center; }
.head {
  width: 60rpx; height: 50rpx;
  background: #4E342E;
  margin-bottom: 8rpx;
}
.head.triangle { clip-path: polygon(50% 0%, 0% 100%, 100% 100%); }
.head.round { border-radius: 50%; background: #8D6E63; }
.fangs { font-size: 16rpx; }
.body-seg {
  width: 40rpx; height: 20rpx;
  background: #4E342E;
  border-radius: 10rpx;
  margin: 2rpx 0;
  animation: wiggle 0.6s ease-in-out infinite alternate;
}
.body-seg.green { background: #66BB6A; }
@keyframes wiggle {
  0% { transform: translateX(-8rpx) scaleX(1); }
  100% { transform: translateX(8rpx) scaleX(1.05); }
}
.tail {
  width: 20rpx; height: 16rpx;
  background: #4E342E;
  border-radius: 0 0 10rpx 10rpx;
}
.snake-label { font-size: 24rpx; margin-top: 10rpx; display: block; }
.snake-label.dangerous { color: #C62828; font-weight: bold; }
.snake-label.safe { color: #2E7D32; }

.quick-challenge {
  background: #fff; border-radius: 20rpx;
  padding: 30rpx; margin: 0 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.08);
}
.challenge-title { font-size: 30rpx; font-weight: bold; color: #E65100; }
.challenge-desc { font-size: 26rpx; color: #666; margin-top: 8rpx; display: block; }
.challenge-grid {
  display: grid; grid-template-columns: repeat(3,1fr); gap: 16rpx;
  margin: 20rpx 0;
}
.challenge-item {
  background: #FFF3E0; padding: 24rpx 10rpx; border-radius: 12rpx;
  text-align: center; border: 3rpx solid transparent;
}
.challenge-item.right { background: #E8F5E9; border-color: #2E7D32; }
.challenge-item.wrong { background: #FFEBEE; border-color: #C62828; }
.c-emoji { font-size: 40rpx; display: block; }
.c-shape { font-size: 24rpx; color: #666; }
.btn-start-challenge {
  background: #E65100; color: #fff; border-radius: 30rpx; font-size: 28rpx;
}
.challenge-result {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16rpx; background: #FFF3E0; border-radius: 12rpx;
}
.btn-retry-small { background: #E65100; color: #fff; font-size: 24rpx; border-radius: 20rpx; }

/* ===== Phase 2: 认 ===== */
.snake-swiper { height: 800rpx; margin: 0 20rpx; }
.learn-card {
  height: 780rpx; margin: 20rpx; padding: 40rpx 30rpx;
  background: #fff; border-radius: 24rpx;
  display: flex; flex-direction: column; align-items: center;
  box-shadow: 0 8rpx 24rpx rgba(0,0,0,0.1);
}
.learn-card.poisonous { border: 6rpx solid #E53935; }
.card-badge {
  align-self: flex-start; font-size: 24rpx;
  padding: 6rpx 16rpx; border-radius: 16rpx; background: #E8F5E9; color: #2E7D32;
}
.card-badge.danger { background: #FFEBEE; color: #C62828; }
.card-emoji { font-size: 120rpx; margin: 20rpx 0; }
.card-name { font-size: 40rpx; font-weight: bold; color: #222; }
.card-features { width: 100%; margin-top: 20rpx; }
.feature-row { display: flex; padding: 10rpx 0; border-bottom: 1rpx solid #F0F0F0; }
.f-label { width: 120rpx; color: #888; font-size: 28rpx; }
.f-value { flex: 1; color: #333; font-size: 28rpx; }
.f-value.venom { color: #E53935; font-weight: bold; }
.card-tip {
  margin-top: auto; padding: 20rpx;
  background: #FFF3E0; border-radius: 12rpx;
  font-size: 26rpx; color: #E65100; line-height: 1.6;
}
.swiper-controls {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20rpx 60rpx;
}
.btn-nav { background: #E65100; color: #fff; font-size: 26rpx; border-radius: 30rpx; }
.swiper-count { font-size: 26rpx; color: #666; }

/* ===== Phase 3: 练 ===== */
.practice-card {
  margin: 20rpx 30rpx; padding: 30rpx;
  background: #fff; border-radius: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.08);
}
.scene-title { font-weight: bold; color: #333; }
.scene-text { display: block; margin-top: 10rpx; color: #555; font-size: 28rpx; line-height: 1.6; }
.snake-reveal { display: flex; align-items: center; gap: 20rpx; margin: 20rpx 0; padding: 20rpx; background: #FFF3E0; border-radius: 12rpx; }
.sr-emoji { font-size: 48rpx; }
.sr-name { font-size: 30rpx; font-weight: bold; color: #E65100; }
.practice-options { margin-top: 20rpx; }
.p-option {
  display: flex; align-items: center;
  padding: 20rpx; margin-bottom: 12rpx;
  background: #F5F5F5; border-radius: 12rpx;
  border-left: 6rpx solid transparent;
}
.p-option.right { background: #E8F5E9; border-left-color: #2E7D32; }
.p-option.wrong { background: #FFEBEE; border-left-color: #C62828; }
.p-letter { width: 48rpx; height: 48rpx; line-height: 48rpx; background: #9E9E9E; color: #fff; border-radius: 50%; text-align: center; margin-right: 16rpx; }
.p-text { flex: 1; font-size: 28rpx; color: #333; }
.p-feedback { padding: 20rpx; background: #E3F2FD; border-radius: 12rpx; margin-top: 20rpx; font-size: 26rpx; color: #1565C0; line-height: 1.6; }
.practice-controls { padding: 0 30rpx; }

/* ===== Phase 4: 测 ===== */
.test-card {
  margin: 20rpx 30rpx; padding: 30rpx;
  background: #fff; border-radius: 20rpx;
}
.test-progress { height: 12rpx; background: #F0F0F0; border-radius: 6rpx; margin-bottom: 20rpx; overflow: hidden; }
.tp-fill { height: 100%; background: linear-gradient(90deg, #4CAF50, #2E7D32); transition: width 0.4s; }
.test-q-title { font-size: 30rpx; font-weight: bold; color: #222; margin-bottom: 20rpx; }
.test-options { display: flex; flex-direction: column; gap: 12rpx; }
.t-option {
  display: flex; align-items: center;
  padding: 20rpx; background: #F5F5F5; border-radius: 12rpx; font-size: 28rpx;
  border-left: 6rpx solid transparent;
}
.t-option.right { background: #E8F5E9; color: #2E7D32; border-left-color: #2E7D32; }
.t-option.wrong { background: #FFEBEE; color: #C62828; border-left-color: #C62828; }
.t-letter { width: 48rpx; height: 48rpx; line-height: 48rpx; background: #9E9E9E; color: #fff; border-radius: 50%; text-align: center; margin-right: 16rpx; font-size: 26rpx; font-weight: bold; flex-shrink: 0; }
.t-option.right .t-letter { background: #2E7D32; }
.t-option.wrong .t-letter { background: #C62828; }
.t-text { flex: 1; font-size: 28rpx; color: #333; line-height: 1.5; }
.t-option.right .t-text { color: #2E7D32; }
.t-option.wrong .t-text { color: #C62828; }
.test-feedback { padding: 20rpx; background: #E3F2FD; border-radius: 12rpx; margin-top: 20rpx; font-size: 26rpx; }

.test-result { text-align: center; padding: 60rpx 30rpx; }
.tr-emoji { font-size: 160rpx; display: block; }
.tr-score { font-size: 60rpx; font-weight: bold; color: #E65100; display: block; margin: 20rpx 0; }
.tr-stars { font-size: 60rpx; letter-spacing: 20rpx; margin-bottom: 20rpx; }
.tr-stars .dim { opacity: 0.2; }
.tr-desc { font-size: 28rpx; color: #666; margin-bottom: 40rpx; display: block; }
.btn-next, .btn-replay {
  background: #E65100; color: #fff; font-size: 30rpx; border-radius: 30rpx;
  padding: 20rpx 60rpx; margin-top: 20rpx;
}

/* 浮动工具 */
.float-tools {
  position: fixed; right: 20rpx; bottom: 200rpx;
  display: flex; flex-direction: column; gap: 16rpx;
  z-index: 100;
}
.tool-btn {
  width: 80rpx; height: 80rpx;
  background: #fff; border-radius: 50%;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.2);
  display: flex; align-items: center; justify-content: center;
  font-size: 36rpx;
}

/* 🔊 语音测试大按钮 */
.test-voice-bar {
  background: linear-gradient(135deg, #FF6F00, #E65100);
  padding: 16rpx 24rpx;
  display: flex; align-items: center; justify-content: space-between; gap: 16rpx;
}
.test-voice-btn {
  display: flex; align-items: center; gap: 12rpx;
  background: #FFF; color: #E65100;
  padding: 16rpx 28rpx; border-radius: 40rpx;
  font-size: 32rpx; font-weight: bold;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,.2);
}
.tv-icon { font-size: 40rpx; }
.voice-status { color: #FFF; font-size: 26rpx; font-weight: bold; }
</style>
