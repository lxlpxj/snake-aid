<template>
  <view class="protocol-page">
    <!-- Tabs -->
    <view class="tab-bar">
      <view class="tab" :class="{ active: tab === 'serum' }" @click="switchTab('serum')">💉 血清使用规范</view>
      <view class="tab" :class="{ active: tab === 'complication' }" @click="switchTab('complication')">🆘 并发症处理</view>
    </view>

    <!-- 血清规范列表 -->
    <scroll-view v-if="tab === 'serum'" class="content" scroll-y>
      <view class="tip-banner">
        💡 以下规范来自《中国蛇伤急救指南 2023》，仅供医护人员参考。
      </view>
      <view v-if="serumList.length === 0" class="empty">暂无血清规范</view>
      <view v-for="s in serumList" :key="s.id" class="card" @click="openDetail('serum', s)">
        <view class="card-header">
          <text class="card-title">💉 {{ s.serumName }}</text>
          <text class="card-tag venom-tag">{{ venomLabel(s.venomType) }}</text>
        </view>
        <view class="quick-info">
          <text class="qi-item">📦 {{ s.dosage || '-' }}</text>
          <text class="qi-item">💧 {{ s.dilution || '-' }}</text>
        </view>
        <text class="card-sub">{{ s.reference || '' }}</text>
      </view>
    </scroll-view>

    <!-- 并发症列表 -->
    <scroll-view v-if="tab === 'complication'" class="content" scroll-y>
      <view class="tip-banner danger">
        ⚠️ 并发症处理流程（院内），适用于医护人员参考。
      </view>
      <view v-if="compList.length === 0" class="empty">暂无并发症流程</view>
      <view v-for="c in compList" :key="c.id" class="card" @click="openDetail('comp', c)">
        <view class="card-header">
          <text class="card-title">🆘 {{ c.title }}</text>
          <text class="card-tag comp-tag">{{ catLabel(c.category) }}</text>
        </view>
        <text class="card-sub">{{ truncate(c.triggers, 60) }}</text>
      </view>
    </scroll-view>

    <!-- 详情弹窗 -->
    <view v-if="detail" class="detail-mask" @click.self="detail = null">
      <scroll-view class="detail-panel" scroll-y>
        <view class="detail-header">
          <text class="detail-title">{{ detail.serumName || detail.title }}</text>
          <view class="close" @click="detail = null">✕</view>
        </view>
        <view class="detail-body">
          <!-- 血清详情 -->
          <template v-if="tab === 'serum'">
            <div class="section"><text class="sec-title">💉 剂量</text><text class="sec-body">{{ detail.dosage }}</text></div>
            <div class="section"><text class="sec-title">💧 稀释</text><text class="sec-body">{{ detail.dilution }}</text></div>
            <div class="section"><text class="sec-title">🛤️ 给药途径</text><text class="sec-body">{{ detail.route }}</text></div>
            <div class="section"><text class="sec-title">⏱️ 滴速</text><text class="sec-body">{{ detail.infusionRate }}</text></div>
            <div class="section"><text class="sec-title">🧪 皮试</text><text class="sec-body">{{ detail.skinTest }}</text></div>
            <div class="section"><text class="sec-title">🚨 过敏处理</text><text class="sec-body">{{ detail.allergyHandling }}</text></div>
            <div class="section"><text class="sec-title">⚠️ 注意事项</text><text class="sec-body">{{ detail.precautions }}</text></div>
          </template>
          <!-- 并发症详情 -->
          <template v-else>
            <div class="section"><text class="sec-title">🔔 触发条件</text><text class="sec-body">{{ detail.triggers }}</text></div>
            <div class="section"><text class="sec-title">👁️ 临床表现</text><text class="sec-body">{{ detail.symptoms }}</text></div>
            <div class="section"><text class="sec-title">🔍 诊断要点</text><text class="sec-body">{{ detail.diagnosis }}</text></div>
            <div class="section"><text class="sec-title">💊 处理步骤</text><text class="sec-body pre-wrap">{{ detail.treatmentSteps }}</text></div>
            <div class="section"><text class="sec-title">🛡️ 支持治疗</text><text class="sec-body">{{ detail.supportiveCare }}</text></div>
            <div class="section"><text class="sec-title">📞 会诊建议</text><text class="sec-body">{{ detail.consultation }}</text></div>
          </template>
          <view class="section ref"><text class="sec-title">📚 参考</text><text class="sec-body">{{ detail.reference }}</text></view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
const BASE = 'http://127.0.0.1:8080/api'

const tab = ref('serum')
const serumList = ref([])
const compList = ref([])
const detail = ref(null)

function switchTab(t) { tab.value = t; if (t === 'serum') loadSerum(); else loadComp() }
function openDetail(t, o) { detail.value = { ...o } }
function truncate(s, n) { return s && s.length > n ? s.substring(0, n) + '...' : s || '' }
function venomLabel(t) { return { neurotoxic: '神经毒', hemotoxic: '血循毒', cytotoxic: '细胞毒', mixed: '混合毒' }[t] || t }
function catLabel(c) { return { hemotoxic: '血循毒', neurotoxic: '神经毒', cytotoxic: '细胞毒', general: '综合' }[c] || c }

function api(url) {
  return new Promise(resolve => {
    uni.request({ url: BASE + url, success: r => resolve(r.data), fail: () => resolve({}) })
  })
}
async function loadSerum() { const j = await api('/app/serum-usage/list'); if (j.code === 200) serumList.value = j.data || [] }
async function loadComp() { const j = await api('/app/complication/list'); if (j.code === 200) compList.value = j.data || [] }

onMounted(() => { loadSerum(); loadComp() })
</script>

<style scoped>
.protocol-page { min-height: 100vh; background: #FAFAFA; }
.tab-bar { display: flex; background: #fff; border-bottom: 1rpx solid #eee; position: sticky; top: 0; z-index: 10; }
.tab { flex: 1; text-align: center; padding: 26rpx 0; font-size: 28rpx; color: #888; border-bottom: 6rpx solid transparent; }
.tab.active { color: #6A1B9A; font-weight: bold; border-bottom-color: #6A1B9A; }
.content { padding: 20rpx; height: calc(100vh - 100rpx); }

.tip-banner { background: #FFF8E1; border-left: 6rpx solid #FF9800; padding: 16rpx 20rpx; font-size: 24rpx; color: #E65100; border-radius: 8rpx; margin-bottom: 16rpx; }
.tip-banner.danger { background: #FFEBEE; border-color: #C62828; color: #C62828; }

.card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05); }
.card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12rpx; }
.card-title { font-size: 30rpx; font-weight: bold; color: #222; flex: 1; }
.card-tag { padding: 4rpx 16rpx; border-radius: 20rpx; font-size: 22rpx; }
.venom-tag { background: #F3E5F5; color: #7B1FA2; }
.comp-tag { background: #FFEBEE; color: #C62828; }
.quick-info { display: flex; gap: 20rpx; margin-bottom: 8rpx; }
.qi-item { font-size: 24rpx; color: #666; }
.card-sub { font-size: 24rpx; color: #999; }
.empty { text-align: center; padding: 60rpx; color: #aaa; font-size: 26rpx; }

.detail-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.6); z-index: 99; display: flex; align-items: flex-end; }
.detail-panel { background: #fff; border-radius: 24rpx 24rpx 0 0; width: 100%; max-height: 85vh; }
.detail-header { display: flex; justify-content: space-between; align-items: center; padding: 30rpx; border-bottom: 1rpx solid #eee; position: sticky; top: 0; background: #fff; }
.detail-title { font-size: 34rpx; font-weight: bold; color: #222; flex: 1; }
.close { font-size: 36rpx; color: #999; padding: 10rpx; }
.detail-body { padding: 20rpx 30rpx 40rpx; }
.section { margin-bottom: 24rpx; }
.sec-title { font-size: 26rpx; font-weight: bold; color: #6A1B9A; display: block; margin-bottom: 8rpx; }
.sec-body { font-size: 28rpx; color: #333; line-height: 1.6; white-space: pre-wrap; }
.sec-body.pre-wrap { white-space: pre-wrap; }
.section.ref { border-top: 1rpx solid #eee; padding-top: 20rpx; margin-top: 30rpx; }
</style>
