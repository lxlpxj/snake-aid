<template>
  <view class="video-page">
    <!-- 顶部标题 -->
    <view class="page-header">
      <text class="page-title">🎬 科普视频</text>
      <text class="page-sub">急救教学 · 专家讲解 · 一看就会</text>
    </view>

    <!-- 分类筛选 -->
    <scroll-view class="cat-tabs" scroll-x>
      <view class="cat-tab" :class="{ active: !filterCategory }" @click="filterCategory = ''">全部</view>
      <view class="cat-tab" :class="{ active: filterCategory === c }"
            v-for="c in categories" :key="c" @click="filterCategory = c">{{ c }}</view>
    </scroll-view>

    <!-- 视频列表 -->
    <view v-if="list.length > 0" class="video-list">
      <view class="video-card" v-for="v in list" :key="v.id" @click="playVideo(v)">
        <!-- 封面 -->
        <view class="video-cover-wrap">
          <image v-if="v.coverUrl" :src="fullUrl(v.coverUrl)" class="video-cover" mode="aspectFill" />
          <view v-else class="video-cover video-placeholder">
            <text class="placeholder-emoji">🎬</text>
          </view>
          <view class="play-btn">▶</view>
          <text v-if="v.duration" class="duration-badge">{{ fmtDuration(v.duration) }}</text>
        </view>
        <!-- 信息 -->
        <view class="video-info">
          <text class="video-title">{{ v.title }}</text>
          <view class="video-meta">
            <text class="cat-tag">{{ v.category }}</text>
            <text v-if="v.author" class="author">👤 {{ v.author }}</text>
            <text class="plays">▶ {{ v.playCount || 0 }}</text>
          </view>
          <text v-if="v.summary" class="video-summary">{{ v.summary }}</text>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else-if="!loading" class="empty-state">
      <text class="empty-emoji">📺</text>
      <text class="empty-text">暂无视频内容</text>
      <text class="empty-hint">请联系内容管理员上传视频</text>
    </view>

    <!-- 加载中 -->
    <view v-else class="loading">加载中...</view>

    <!-- 播放弹窗 -->
    <view v-if="playingVideo" class="player-mask" @click.self="playingVideo = null">
      <view class="player-wrap">
        <video
          :src="fullUrl(playingVideo.videoUrl)"
          controls
          autoplay
          class="player-video"
          object-fit="contain" />
        <view class="player-info">
          <text class="player-title">{{ playingVideo.title }}</text>
          <text v-if="playingVideo.author" class="player-author">主讲：{{ playingVideo.author }}</text>
          <text v-if="playingVideo.summary" class="player-summary">{{ playingVideo.summary }}</text>
        </view>
        <view class="close-btn" @click="playingVideo = null">✕ 关闭</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { API_BASE } from '@/utils/api'
import { ref, onMounted, watch } from 'vue'

const categories = ['急救操作', '蛇种识别', '案例讲解', '其他']
const filterCategory = ref('')
const list = ref([])
const loading = ref(true)
const playingVideo = ref(null)

const BASE = API_BASE

function fullUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return BASE + url
}

function fmtDuration(sec) {
  if (!sec) return ''
  const m = Math.floor(sec / 60), s = sec % 60
  return m + ':' + String(s).padStart(2, '0')
}

async function load() {
  loading.value = true
  try {
    const params = filterCategory.value ? '?category=' + encodeURIComponent(filterCategory.value) : ''
    const res = await fetch(BASE + '/app/video/list' + params)
    const json = await res.json()
    if (json && json.code === 200 && json.data) {
      list.value = json.data
    } else {
      list.value = []
    }
  } catch (e) {
    console.warn('视频加载失败:', e)
    list.value = []
  }
  loading.value = false
}

async function playVideo(v) {
  // 触发后端播放数 +1（GET /app/video/{id} 会自动 incPlayCount）
  try {
    const res = await fetch(BASE + '/app/video/' + v.id)
    const json = await res.json()
    if (json && json.code === 200 && json.data) {
      // 用最新数据更新列表中的播放数显示
      const idx = list.value.findIndex(x => x.id === v.id)
      if (idx >= 0) {
        list.value[idx].playCount = json.data.playCount
      }
      playingVideo.value = json.data
    } else {
      playingVideo.value = v
    }
  } catch (e) {
    console.warn('播放数上报失败:', e)
    playingVideo.value = v
  }
}

watch(filterCategory, load)
onMounted(load)
</script>

<style scoped>
.video-page {
  min-height: 100vh;
  background: #F5F7FA;
  padding-bottom: 40rpx;
}

.page-header {
  background: linear-gradient(135deg, #6A1B9A, #9C27B0);
  padding: 40rpx 30rpx;
}
.page-title { font-size: 40rpx; font-weight: bold; color: #fff; display: block; }
.page-sub { font-size: 26rpx; color: rgba(255,255,255,0.8); margin-top: 8rpx; display: block; }

.cat-tabs {
  white-space: nowrap;
  padding: 20rpx 30rpx;
  background: #fff;
  border-bottom: 1rpx solid #eee;
}
.cat-tab {
  display: inline-block;
  padding: 12rpx 28rpx;
  margin-right: 16rpx;
  background: #F0F0F0;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #666;
}
.cat-tab.active {
  background: #6A1B9A;
  color: #fff;
}

.video-list {
  padding: 20rpx;
}
.video-card {
  background: #fff;
  border-radius: 20rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}
.video-cover-wrap {
  position: relative;
  width: 100%;
  height: 360rpx;
  background: #000;
}
.video-cover { width: 100%; height: 100%; }
.video-placeholder {
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #424242, #616161);
}
.placeholder-emoji { font-size: 80rpx; }
.play-btn {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  width: 100rpx; height: 100rpx;
  background: rgba(0,0,0,0.6);
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 48rpx;
  border: 4rpx solid rgba(255,255,255,0.5);
}
.duration-badge {
  position: absolute;
  bottom: 16rpx; right: 16rpx;
  background: rgba(0,0,0,0.7);
  color: #fff;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
}

.video-info { padding: 24rpx; }
.video-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #222;
  display: block;
  margin-bottom: 12rpx;
  line-height: 1.4;
}
.video-meta {
  display: flex;
  align-items: center;
  gap: 16rpx;
  font-size: 24rpx;
  color: #888;
  margin-bottom: 10rpx;
}
.cat-tag {
  background: #F3E5F5;
  color: #7B1FA2;
  padding: 4rpx 14rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
}
.video-summary {
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.empty-state {
  text-align: center;
  padding: 120rpx 40rpx;
}
.empty-emoji { font-size: 120rpx; display: block; }
.empty-text { font-size: 32rpx; color: #666; margin-top: 20rpx; display: block; }
.empty-hint { font-size: 26rpx; color: #aaa; margin-top: 10rpx; display: block; }
.loading { text-align: center; padding: 80rpx; color: #999; }

/* 播放弹窗 */
.player-mask {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.85);
  z-index: 999;
  display: flex; align-items: center; justify-content: center;
  padding: 20rpx;
}
.player-wrap {
  width: 100%;
  max-width: 800rpx;
  background: #000;
  border-radius: 16rpx;
  overflow: hidden;
}
.player-video {
  width: 100%;
  height: 420rpx;
  background: #000;
}
.player-info {
  background: #fff;
  padding: 24rpx;
}
.player-title { font-size: 30rpx; font-weight: bold; color: #222; display: block; margin-bottom: 10rpx; }
.player-author { font-size: 24rpx; color: #666; display: block; margin-bottom: 8rpx; }
.player-summary { font-size: 26rpx; color: #555; line-height: 1.5; }
.close-btn {
  text-align: center;
  padding: 20rpx;
  background: #F5F5F5;
  font-size: 28rpx;
  color: #666;
}
</style>
