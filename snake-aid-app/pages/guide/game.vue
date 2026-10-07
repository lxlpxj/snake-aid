<template>
  <view class="game-container">
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
      <view class="level-card">
        <view class="level-header">
          <text class="level-emoji">{{ steps[currentStep].emoji }}</text>
          <view class="level-info">
            <view class="level-title">{{ steps[currentStep].title }}</view>
            <view class="level-hint">{{ steps[currentStep].hint }}</view>
          </view>
        </view>

        <!-- 第4关：模拟拨120 -->
        <view v-if="currentStep === 3" class="dial-120">
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
        <view v-if="currentStep === 5" class="limb-placement">
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
        <view v-if="currentStep === 6" class="bandage-area">
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
        <view v-if="!isInteractiveStep(currentStep)" class="choice-area">
          <view class="choice-option" 
                :class="{ wrong: wrongChoice, right: rightChoice && option.correct }"
                v-for="(option, idx) in steps[currentStep].options" 
                :key="idx"
                @click="selectOption(option)">
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
import { sfxClick, sfxCorrect, sfxWrong, sfxWin, sfxEmergency, sfxDone } from '@/utils/audio.js'

const API = 'http://127.0.0.1:8080/api'

// 从后端拉关卡数据，失败用下面的硬编码 fallback
onMounted(async () => {
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
        // choice 类型需要 options
        let stepData = {
          emoji: s.emoji || '❓',
          title: s.title,
          hint: s.hint,
          feedbackRight: s.feedbackRight,
          feedbackWrong: s.feedbackWrong,
          score: s.score || 10,
          isInteractive: s.interactionType !== 'choice'
        }
        if (s.interactionType === 'phone') stepData.isInteractive = true
        if (s.interactionType === 'limb') { stepData.isInteractive = true; stepData.correctAnswer = 'flat' }
        if (s.interactionType === 'drag') stepData.isInteractive = true
        if (s.interactionType === 'choice') {
          // 从 correctAnswer 构造 options：正确选项 + 3 个常见错误
          const wrongs = (s.wrongAnswer || '').split('/').map(w => w.trim()).filter(w => w)
          const opts = [
            { text: s.correctAnswer, correct: true },
            ...wrongs.slice(0, 3).map(w => ({ text: w, correct: false }))
          ]
          stepData.options = opts
        }
        steps.push(stepData)
      })
      console.log('✅ 从后端拉了 ' + steps.length + ' 步急救关卡')
    }
  } catch(e) {
    console.log('⚠️ 后端拉关卡失败，用硬编码 fallback')
  }
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
  } else {
    wrongChoice.value = true
    wrongCount.value++
    feedbackText.value = steps[currentStep.value].feedbackWrong
    feedbackType.value = 'wrong'
    canNext.value = false
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
  } else {
    wrongCount.value++
    feedbackText.value = '❌ 这个位置不对！' + steps[5].feedbackRight
    feedbackType.value = 'wrong'
    canNext.value = false
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
  }
}

function resetGame() {
  startGame()
}

function goBack() {
  uni.navigateBack()
}
</script>

<style scoped>
.game-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #FFEBEE 0%, #FFF 100%);
  padding-bottom: 180rpx;
}

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
</style>


