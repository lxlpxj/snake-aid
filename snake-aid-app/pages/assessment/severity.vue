<template>
  <view class="assessment-page">
    <!-- 顶部进度 -->
    <view class="progress-bar">
      <view class="progress-fill" :style="{ width: (step / 4 * 100) + '%' }"></view>
    </view>
    <view class="progress-text">第 {{ step }} / 4 步</view>

    <!-- 步骤 1：蛇毒类型 -->
    <view v-if="step === 1" class="step">
      <text class="step-title">🐍 蛇的类型（毒力）</text>
      <text class="step-desc">如果不确定蛇种，选"未知"即可</text>
      <view class="option-list">
        <view class="option-card" :class="{ active: form.venomType === 'neurotoxic' }" @click="form.venomType = 'neurotoxic'">
          <text class="opt-icon">⚡</text>
          <text class="opt-title">神经毒</text>
          <text class="opt-sub">呼吸麻痹 · 银环蛇/金环蛇</text>
        </view>
        <view class="option-card" :class="{ active: form.venomType === 'hemotoxic' }" @click="form.venomType = 'hemotoxic'">
          <text class="opt-icon">🩸</text>
          <text class="opt-title">血循毒</text>
          <text class="opt-sub">凝血/出血 · 五步蛇/蝰蛇</text>
        </view>
        <view class="option-card" :class="{ active: form.venomType === 'cytotoxic' }" @click="form.venomType = 'cytotoxic'">
          <text class="opt-icon">🔥</text>
          <text class="opt-title">细胞毒</text>
          <text class="opt-sub">局部坏死 · 眼镜蛇</text>
        </view>
        <view class="option-card" :class="{ active: form.venomType === 'mixed' }" @click="form.venomType = 'mixed'">
          <text class="opt-icon">☠️</text>
          <text class="opt-title">混合毒</text>
          <text class="opt-sub">最危险 · 眼镜王蛇</text>
        </view>
        <view class="option-card" :class="{ active: form.venomType === 'unknown' }" @click="form.venomType = 'unknown'">
          <text class="opt-icon">❓</text>
          <text class="opt-title">不确定 / 无毒蛇</text>
          <text class="opt-sub">先按症状评估</text>
        </view>
      </view>
    </view>

    <!-- 步骤 2：咬伤部位 -->
    <view v-if="step === 2" class="step">
      <text class="step-title">📍 咬伤部位</text>
      <text class="step-desc">越靠近躯干/头部越危险</text>
      <view class="option-list">
        <view class="option-card" :class="{ active: form.location === 'head' || form.location === 'neck' }" @click="form.location = 'neck'">
          <text class="opt-icon">🧠</text>
          <text class="opt-title">头部 / 颈部</text>
          <text class="opt-sub">极危险</text>
        </view>
        <view class="option-card" :class="{ active: form.location === 'trunk' }" @click="form.location = 'trunk'">
          <text class="opt-icon">🫁</text>
          <text class="opt-title">躯干（胸/腹）</text>
          <text class="opt-sub">危险</text>
        </view>
        <view class="option-card" :class="{ active: form.location === 'upper_arm' }" @click="form.location = 'upper_arm'">
          <text class="opt-icon">💪</text>
          <text class="opt-title">上臂 / 大腿</text>
          <text class="opt-sub">较危险</text>
        </view>
        <view class="option-card" :class="{ active: form.location === 'forearm' || form.location === 'hand' }" @click="form.location = 'forearm'">
          <text class="opt-icon">✋</text>
          <text class="opt-title">前臂 / 手</text>
          <text class="opt-sub">中等</text>
        </view>
        <view class="option-card" :class="{ active: form.location === 'calf' || form.location === 'foot' }" @click="form.location = 'calf'">
          <text class="opt-icon">🦶</text>
          <text class="opt-title">小腿 / 足</text>
          <text class="opt-sub">相对安全</text>
        </view>
      </view>
    </view>

    <!-- 步骤 3：局部症状 -->
    <view v-if="step === 3" class="step">
      <text class="step-title">👁️ 伤口周围症状</text>
      <text class="step-desc">选最符合的一项</text>
      <view class="option-list">
        <view class="option-card" :class="{ active: form.localSymptom === 'necrosis' }" @click="form.localSymptom = 'necrosis'">
          <text class="opt-icon">💀</text>
          <text class="opt-title">组织坏死 / 麻木</text>
          <text class="opt-sub">皮肤发黑、感觉丧失</text>
        </view>
        <view class="option-card" :class="{ active: form.localSymptom === 'severe' }" @click="form.localSymptom = 'severe'">
          <text class="opt-icon">😣</text>
          <text class="opt-title">剧痛 + 明显肿胀</text>
          <text class="opt-sub">肿过肘关节/膝关节，有水泡</text>
        </view>
        <view class="option-card" :class="{ active: form.localSymptom === 'moderate' }" @click="form.localSymptom = 'moderate'">
          <text class="opt-icon">😟</text>
          <text class="opt-title">中度疼痛 + 轻度肿胀</text>
          <text class="opt-sub">肿未过关节</text>
        </view>
        <view class="option-card" :class="{ active: form.localSymptom === 'mild' }" @click="form.localSymptom = 'mild'">
          <text class="opt-icon">🙂</text>
          <text class="opt-title">轻微疼痛 / 无肿胀</text>
          <text class="opt-sub">只有牙印</text>
        </view>
      </view>
    </view>

    <!-- 步骤 4：全身症状 + 时间 -->
    <view v-if="step === 4" class="step">
      <text class="step-title">⚠️ 全身症状 + 咬伤时间</text>
      <text class="step-desc">可多选全身症状</text>

      <text class="sub-label">全身症状（多选）</text>
      <view class="chip-list">
        <view class="chip" :class="{ active: form.systemicSymptoms.includes('dizziness') }" @click="toggle('dizziness')">头晕/乏力</view>
        <view class="chip" :class="{ active: form.systemicSymptoms.includes('nausea') }" @click="toggle('nausea')">恶心/呕吐</view>
        <view class="chip" :class="{ active: form.systemicSymptoms.includes('palpitation') }" @click="toggle('palpitation')">心悸/胸闷</view>
        <view class="chip" :class="{ active: form.systemicSymptoms.includes('hypotension') }" @click="toggle('hypotension')">血压下降</view>
        <view class="chip danger" :class="{ active: form.systemicSymptoms.includes('dyspnea') }" @click="toggle('dyspnea')">呼吸困难</view>
        <view class="chip danger" :class="{ active: form.systemicSymptoms.includes('confusion') }" @click="toggle('confusion')">意识模糊</view>
        <view class="chip danger" :class="{ active: form.systemicSymptoms.includes('seizure') }" @click="toggle('seizure')">抽搐</view>
        <view class="chip danger" :class="{ active: form.systemicSymptoms.includes('respiratory_arrest') }" @click="toggle('respiratory_arrest')">呼吸停止</view>
      </view>

      <text class="sub-label">咬伤时间</text>
      <view class="option-list">
        <view class="option-card sm" :class="{ active: form.hours < 1 }" @click="form.hours = 0.5">
          <text class="opt-title">< 1 小时</text>
        </view>
        <view class="option-card sm" :class="{ active: form.hours >= 1 && form.hours <= 6 }" @click="form.hours = 3">
          <text class="opt-title">1-6 小时</text>
        </view>
        <view class="option-card sm" :class="{ active: form.hours > 6 }" @click="form.hours = 12">
          <text class="opt-title">> 6 小时</text>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="btn-bar" v-if="step < 5">
      <view class="btn ghost" v-if="step > 1" @click="step--">上一步</view>
      <view class="btn primary" :class="{ disabled: !canNext }" @click="next">
        {{ step === 4 ? '开始评估' : '下一步' }}
      </view>
    </view>

    <!-- 结果页 -->
    <view v-if="step === 5" class="result" :style="{ borderColor: result?.color }">
      <view class="result-header" :style="{ background: result?.color }">
        <text class="result-icon">{{ levelEmoji }}</text>
        <text class="result-level">{{ result?.levelName }}</text>
        <text class="result-score">评分 {{ result?.totalScore }}</text>
      </view>

      <view class="result-body">
        <view class="advice-box">
          <text class="advice-title">📋 医疗建议</text>
          <text class="advice-text">{{ result?.advice }}</text>
        </view>

        <view class="action-box">
          <text class="advice-title">🚨 立即行动</text>
          <view class="action-list">
            <view class="action-item" v-for="(a, i) in result?.actions" :key="i">
              <text class="action-num">{{ i + 1 }}</text>
              <text class="action-text">{{ a }}</text>
            </view>
          </view>
        </view>

        <view class="breakdown-box">
          <text class="advice-title">📊 评分明细</text>
          <view class="breakdown-row" v-for="(v, k) in result?.breakdown" :key="k">
            <text class="bd-label">{{ v.label }}</text>
            <view class="bd-bar-bg">
              <view class="bd-bar-fill" :style="{ width: (v.score / v.max * 100) + '%', background: result.color }"></view>
            </view>
            <text class="bd-score">{{ v.score }}/{{ v.max }}</text>
          </view>
        </view>
      </view>

      <view class="result-btns">
        <view class="btn ghost" @click="reset">重新评估</view>
        <view class="btn primary" @click="call120">🚑 拨打 120</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'

const BASE = '/api'

const step = ref(1)
const form = reactive({
  venomType: 'unknown',
  location: 'calf',
  localSymptom: 'mild',
  systemicSymptoms: [],
  hours: 0.5
})
const result = ref(null)

const levelEmoji = computed(() => {
  if (!result.value) return ''
  return { mild: '✅', moderate: '⚠️', severe: '🚨', critical: '☠️' }[result.value.level] || '❓'
})

const canNext = computed(() => {
  if (step.value === 1) return !!form.venomType
  if (step.value === 2) return !!form.location
  if (step.value === 3) return !!form.localSymptom
  return true
})

function toggle(s) {
  const i = form.systemicSymptoms.indexOf(s)
  if (i >= 0) form.systemicSymptoms.splice(i, 1)
  else form.systemicSymptoms.push(s)
}

async function next() {
  if (step.value < 4) {
    step.value++
    return
  }
  return new Promise(() => {}).then(async () => {
    uni.request({
      url: BASE + '/app/assessment/severity',
      method: 'POST',
      data: form,
      success: res => {
        const j = res.data
        if (j.code === 200) { result.value = j.data; step.value = 5 }
        else uni.showToast({ title: '评估失败', icon: 'none' })
      },
      fail: () => uni.showToast({ title: '网络错误', icon: 'none' })
    })
  })
}

function reset() {
  step.value = 1
  form.venomType = 'unknown'
  form.location = 'calf'
  form.localSymptom = 'mild'
  form.systemicSymptoms = []
  form.hours = 0.5
  result.value = null
}

function call120() {
  uni.makePhoneCall({ phoneNumber: '120' })
}
</script>

<style scoped>
.assessment-page {
  min-height: 100vh; background: #FAFAFA; padding-bottom: 40rpx;
}

.progress-bar {
  height: 8rpx; background: #E0E0E0;
}
.progress-fill {
  height: 100%; background: linear-gradient(90deg, #6A1B9A, #9C27B0);
  transition: width 0.3s;
}
.progress-text {
  text-align: center; font-size: 26rpx; color: #888; padding: 16rpx;
}

.step { padding: 20rpx 30rpx; }
.step-title { font-size: 36rpx; font-weight: bold; color: #222; display: block; }
.step-desc { font-size: 26rpx; color: #888; margin-top: 8rpx; display: block; margin-bottom: 30rpx; }
.sub-label { font-size: 28rpx; color: #444; font-weight: 500; display: block; margin: 24rpx 0 16rpx; }

.option-list { display: flex; flex-direction: column; gap: 16rpx; }
.option-card {
  background: #fff; border: 2rpx solid #E0E0E0; border-radius: 16rpx;
  padding: 24rpx; display: flex; align-items: center; gap: 20rpx;
  transition: all 0.15s;
}
.option-card.active {
  border-color: #6A1B9A; background: #F3E5F5;
  box-shadow: 0 4rpx 12rpx rgba(106,27,154,0.15);
}
.option-card.sm { justify-content: center; }
.opt-icon { font-size: 40rpx; width: 60rpx; text-align: center; }
.opt-title { font-size: 30rpx; font-weight: 500; color: #222; }
.opt-sub { font-size: 24rpx; color: #888; margin-top: 4rpx; display: block; }

.chip-list { display: flex; flex-wrap: wrap; gap: 14rpx; margin-bottom: 20rpx; }
.chip {
  padding: 14rpx 28rpx; background: #F5F5F5; border-radius: 30rpx;
  font-size: 26rpx; color: #555; border: 2rpx solid transparent;
}
.chip.active { background: #6A1B9A; color: #fff; }
.chip.danger { background: #FFEBEE; color: #C62828; }
.chip.danger.active { background: #C62828; color: #fff; }

.btn-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #fff; padding: 20rpx 30rpx; padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  display: flex; gap: 20rpx; box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.06);
}
.btn { flex: 1; text-align: center; padding: 24rpx; border-radius: 16rpx; font-size: 30rpx; font-weight: 500; }
.btn.primary { background: #6A1B9A; color: #fff; }
.btn.primary.disabled { background: #BDBDBD; }
.btn.ghost { background: #F5F5F5; color: #555; }

/* 结果页 */
.result {
  margin: 20rpx; border-radius: 24rpx; background: #fff;
  overflow: hidden; box-shadow: 0 6rpx 24rpx rgba(0,0,0,0.1);
  border-left: 12rpx solid;
}
.result-header {
  padding: 40rpx 30rpx; text-align: center;
  color: #fff;
}
.result-icon { font-size: 80rpx; display: block; }
.result-level { font-size: 44rpx; font-weight: bold; display: block; margin-top: 8rpx; }
.result-score { font-size: 28rpx; opacity: 0.9; margin-top: 4rpx; display: block; }

.result-body { padding: 20rpx 30rpx; }
.advice-box, .action-box, .breakdown-box {
  background: #FAFAFA; border-radius: 16rpx; padding: 20rpx; margin-top: 16rpx;
}
.advice-title { font-size: 28rpx; font-weight: bold; color: #333; display: block; margin-bottom: 12rpx; }
.advice-text { font-size: 28rpx; color: #444; line-height: 1.6; }

.action-list { display: flex; flex-direction: column; gap: 12rpx; }
.action-item { display: flex; gap: 16rpx; align-items: flex-start; }
.action-num {
  width: 40rpx; height: 40rpx; background: #6A1B9A; color: #fff;
  border-radius: 50%; text-align: center; line-height: 40rpx;
  font-size: 24rpx; flex-shrink: 0;
}
.action-text { font-size: 28rpx; color: #444; line-height: 1.5; flex: 1; }

.breakdown-row {
  display: flex; align-items: center; gap: 12rpx; margin-top: 10rpx;
}
.bd-label { font-size: 24rpx; color: #666; width: 180rpx; flex-shrink: 0; }
.bd-bar-bg { flex: 1; height: 12rpx; background: #E0E0E0; border-radius: 6rpx; overflow: hidden; }
.bd-bar-fill { height: 100%; border-radius: 6rpx; transition: width 0.4s; }
.bd-score { font-size: 24rpx; color: #333; width: 60rpx; text-align: right; }

.result-btns {
  display: flex; gap: 20rpx; padding: 20rpx 30rpx 40rpx;
}
</style>
