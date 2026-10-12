<template>
  <view class="first-aid-anim" :class="['step-' + step, sizeClass]">
    <view class="anim-stage">
      
      <!-- Step 1: 脱离（跑开） -->
      <g v-if="step === 1" class="scene escape">
        <view class="snake-svg">🐍</view>
        <view class="person">
          <view class="body" />
          <view class="head">👤</view>
          <view class="run-line">💨💨</view>
        </view>
        <view class="arrow">➡️➡️➡️</view>
      </g>

      <!-- Step 2: 认蛇（对比头型） -->
      <g v-if="step === 2" class="scene identify">
        <view class="snake-compare">
          <view class="compare-item poison">
            <view class="shape triangle">🔺</view>
            <text>三角头⚠️有毒</text>
          </view>
          <view class="vs">VS</view>
          <view class="compare-item safe">
            <view class="shape circle">⭕</view>
            <text>椭圆头✅</text>
          </view>
        </view>
      </g>

      <!-- Step 3: 镇定（深呼吸） -->
      <g v-if="step === 3" class="scene calm">
        <view class="person-breath">
          <text class="bubble">😌</text>
          <view class="breath-ring"></view>
          <view class="breath-ring delay"></view>
        </view>
        <view class="calm-text">深呼吸 1-2-3</view>
      </g>

      <!-- Step 4: 呼救（拨120动画） -->
      <g v-if="step === 4" class="scene call">
        <view class="phone-dial">
          <view class="phone-screen">📱</view>
          <view class="dial-nums">
            <text v-for="(n, i) in ['1','2','0']" :key="i" class="num" :class="{ dialed: i < dialIdx }">{{ n }}</text>
          </view>
          <view class="call-wave" v-if="dialIdx >= 3">📞呼叫中...</view>
        </view>
      </g>

      <!-- Step 5: 解压（冲洗伤口） -->
      <g v-if="step === 5" class="scene clean">
        <view class="arm">🦿</view>
        <view class="water-drop">💧💧💧</view>
        <view class="wound">⚠️</view>
      </g>

      <!-- Step 6: 制动（伤肢平放） -->
      <g v-if="step === 6" class="scene immobilize">
        <view class="limb-flat">
          <text class="limb">🦾</text>
          <view class="arrow-flat">➡️ 平放</view>
        </view>
        <view class="support">🛠️ 夹板固定</view>
      </g>

      <!-- Step 7: 加压包扎（缠绕动画） -->
      <g v-if="step === 7" class="scene bandage">
        <view class="arm-bandage">
          <view class="arm-shape"></view>
          <view class="bandage-wrap" v-for="n in 5" :key="n" :style="{ animationDelay: (n * 0.2) + 's' }"></view>
          <text class="wound-mark">⚠️</text>
        </view>
      </g>

      <!-- Step 8: 止痛（禁止吃药） -->
      <g v-if="step === 8" class="scene pain">
        <view class="no-pills">
          <text class="pill">💊</text>
          <text class="cross">❌</text>
        </view>
        <view class="pain-text">❌ 不要吃止痛药！</view>
        <view class="pain-sub">❌ 不要喝酒！❌ 不要冰敷！</view>
      </g>

      <!-- Step 9: 禁忌清单 -->
      <g v-if="step === 9" class="scene forbidden">
        <view class="forbidden-list">
          <view class="for-item"><text class="x">❌</text><text>不要切开伤口</text></view>
          <view class="for-item"><text class="x">❌</text><text>不要用嘴吸毒</text></view>
          <view class="for-item"><text class="x">❌</text><text>不要敷草药</text></view>
          <view class="for-item"><text class="check">✅</text><text>保持制动+尽快送医</text></view>
        </view>
      </g>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  step: { type: Number, default: 1 },  // 1-9
  size: { type: String, default: 'normal' }
})

const sizeClass = computed(() => 'size-' + props.size)

// Step 4 拨号动画状态
const dialIdx = ref(0)
let dialTimer = null

watch(() => props.step, (s) => {
  if (dialTimer) { clearInterval(dialTimer); dialTimer = null }
  dialIdx.value = 0
  if (s === 4) {
    dialTimer = setInterval(() => {
      if (dialIdx.value < 3) dialIdx.value++
      else clearInterval(dialTimer)
    }, 600)
  }
}, { immediate: true })

onUnmounted(() => { if (dialTimer) clearInterval(dialTimer) })
</script>

<style scoped>
.first-aid-anim {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-height: 280rpx;
  background: linear-gradient(135deg, #E8F5E9, #FFF);
  border-radius: 20rpx;
  overflow: hidden;
}
.size-small { min-height: 160rpx; }
.size-large { min-height: 400rpx; }

.anim-stage {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* ===== Step 1: 脱离 ===== */
.escape {
  display: flex; align-items: center; gap: 20rpx;
  animation: sceneFloat 2s ease-in-out infinite;
}
.snake-svg { font-size: 80rpx; }
.person { display: flex; flex-direction: column; align-items: center; }
.head { font-size: 60rpx; animation: runBob 0.3s ease-in-out infinite alternate; }
.run-line { font-size: 32rpx; animation: windBlow 0.5s ease-in-out infinite; }
.arrow { font-size: 40rpx; animation: arrowBlink 0.8s ease-in-out infinite; }
@keyframes runBob { 0% { transform: translateY(0) rotate(-5deg); } 100% { transform: translateY(-6rpx) rotate(5deg); } }
@keyframes windBlow { 0%,100% { opacity: 0.4; transform: scaleX(0.8); } 50% { opacity: 1; transform: scaleX(1.2); } }
@keyframes arrowBlink { 0%,100% { opacity: 0.3; } 50% { opacity: 1; } }

/* ===== Step 2: 认蛇 ===== */
.identify { display: flex; gap: 30rpx; }
.snake-compare { display: flex; gap: 40rpx; align-items: center; }
.compare-item {
  display: flex; flex-direction: column; align-items: center;
  padding: 20rpx; border-radius: 16rpx;
}
.compare-item.poison { background: #FFEBEE; animation: poisonWarn 1s ease-in-out infinite; }
.compare-item.safe { background: #E8F5E9; }
.shape { font-size: 80rpx; }
.vs { font-size: 32rpx; font-weight: bold; color: #888; }
@keyframes poisonWarn { 0%,100% { box-shadow: 0 0 0 0 rgba(229,57,53,0.4); } 50% { box-shadow: 0 0 0 16rpx rgba(229,57,53,0); } }

/* ===== Step 3: 镇定 ===== */
.calm { display: flex; flex-direction: column; align-items: center; gap: 16rpx; }
.person-breath { position: relative; display: flex; align-items: center; justify-content: center; }
.bubble { font-size: 80rpx; z-index: 2; }
.breath-ring {
  position: absolute; width: 120rpx; height: 120rpx;
  border: 4rpx solid #4CAF50; border-radius: 50%;
  animation: breath 3s ease-in-out infinite;
}
.breath-ring.delay { animation-delay: 1.5s; }
@keyframes breath {
  0%, 100% { transform: scale(0.5); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0; }
}
.calm-text { font-size: 26rpx; color: #2E7D32; }

/* ===== Step 4: 呼救 ===== */
.call { display: flex; align-items: center; justify-content: center; }
.phone-dial { text-align: center; }
.phone-screen { font-size: 60rpx; animation: phoneRing 0.5s ease-in-out infinite alternate; }
@keyframes phoneRing { 0% { transform: rotate(-5deg); } 100% { transform: rotate(5deg); } }
.dial-nums { display: flex; gap: 10rpx; margin: 16rpx 0; }
.num {
  width: 56rpx; height: 56rpx; line-height: 56rpx;
  background: #F5F5F5; border-radius: 50%;
  font-size: 30rpx; font-weight: bold;
  transition: all 0.3s;
}
.num.dialed { background: #4CAF50; color: #fff; transform: scale(1.1); }
.call-wave {
  font-size: 26rpx; color: #C62828; font-weight: bold;
  animation: callPulse 1s ease-in-out infinite;
}
@keyframes callPulse { 0%,100% { opacity: 1; } 50% { opacity: 0.5; } }

/* ===== Step 5: 解压 ===== */
.clean { display: flex; flex-direction: column; align-items: center; gap: 16rpx; }
.arm { font-size: 100rpx; }
.water-drop {
  font-size: 32rpx;
  animation: dropFall 1.2s ease-in-out infinite;
}
@keyframes dropFall {
  0% { transform: translateY(-20rpx); opacity: 0; }
  50% { transform: translateY(0); opacity: 1; }
  100% { transform: translateY(20rpx); opacity: 0; }
}
.wound {
  font-size: 36rpx; color: #C62828;
  animation: woundBlink 0.8s ease-in-out infinite;
}
@keyframes woundBlink { 0%,100% { transform: scale(1); } 50% { transform: scale(1.3); } }

/* ===== Step 6: 制动 ===== */
.immobilize { display: flex; flex-direction: column; align-items: center; gap: 20rpx; }
.limb-flat { display: flex; align-items: center; gap: 16rpx; }
.limb { font-size: 80rpx; }
.arrow-flat { font-size: 28rpx; color: #2E7D32; }
.support { font-size: 24rpx; color: #666; background: #FFF9C4; padding: 8rpx 16rpx; border-radius: 12rpx; }

/* ===== Step 7: 包扎 ===== */
.bandage { display: flex; align-items: center; justify-content: center; }
.arm-bandage {
  position: relative; width: 200rpx; height: 80rpx;
}
.arm-shape {
  position: absolute; top: 10rpx; left: 0;
  width: 200rpx; height: 60rpx;
  background: #FFCC80; border-radius: 30rpx;
}
.bandage-wrap {
  position: absolute; top: 0;
  width: 30rpx; height: 80rpx;
  background: repeating-linear-gradient(45deg, #FFF, #FFF 6rpx, #E0E0E0 6rpx, #E0E0E0 12rpx);
  border-radius: 4rpx;
  animation: wrapSlide 1.5s ease-out infinite;
  opacity: 0;
}
.bandage-wrap:nth-child(1) { left: 20rpx; animation-delay: 0s; }
.bandage-wrap:nth-child(2) { left: 50rpx; animation-delay: 0.2s; }
.bandage-wrap:nth-child(3) { left: 80rpx; animation-delay: 0.4s; }
.bandage-wrap:nth-child(4) { left: 110rpx; animation-delay: 0.6s; }
.bandage-wrap:nth-child(5) { left: 140rpx; animation-delay: 0.8s; }
@keyframes wrapSlide {
  0% { opacity: 0; transform: scaleX(0); }
  30% { opacity: 1; transform: scaleX(1); }
  100% { opacity: 0.8; }
}
.wound-mark {
  position: absolute; top: 20rpx; left: 85rpx;
  font-size: 28rpx; z-index: 5;
}

/* ===== Step 8: 止痛 ===== */
.pain { display: flex; flex-direction: column; align-items: center; gap: 12rpx; }
.no-pills { display: flex; align-items: center; gap: 8rpx; }
.pill { font-size: 60rpx; }
.cross { font-size: 80rpx; color: #C62828; animation: shake 0.3s ease-in-out infinite; }
@keyframes shake { 0%,100% { transform: rotate(0); } 25% { transform: rotate(-10deg); } 75% { transform: rotate(10deg); } }
.pain-text { font-size: 28rpx; color: #C62828; font-weight: bold; }
.pain-sub { font-size: 22rpx; color: #666; }

/* ===== Step 9: 禁忌 ===== */
.forbidden { display: flex; align-items: center; justify-content: center; }
.forbidden-list { display: flex; flex-direction: column; gap: 12rpx; }
.for-item {
  display: flex; align-items: center; gap: 12rpx;
  font-size: 26rpx;
  background: #FFEBEE; padding: 10rpx 20rpx; border-radius: 12rpx;
}
.for-item:last-child { background: #E8F5E9; }
.x { color: #C62828; font-weight: bold; }
.check { color: #2E7D32; font-weight: bold; }

/* 通用浮动 */
@keyframes sceneFloat {
  0%,100% { transform: translateY(0); }
  50% { transform: translateY(-6rpx); }
}
</style>
