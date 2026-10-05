<template>
  <view class="page">
    <!-- 顶部提示 -->
    <view class="hero">
      <view class="hero-emoji">📸</view>
      <view class="hero-title">AI 蛇种识别</view>
      <view class="hero-sub">上传蛇的照片，一键识别蛇种</view>
    </view>

    <!-- 上传区域 -->
    <view class="upload-card" @click="chooseImage">
      <image v-if="imagePath" :src="imagePath" mode="aspectFill" class="preview" />
      <view v-else class="upload-placeholder">
        <view class="upload-icon">🐍</view>
        <view class="upload-text">点击拍照 / 选择图片</view>
        <view class="upload-hint">支持 JPG / PNG，最大 5MB</view>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-row">
      <button class="btn-secondary" v-if="imagePath" @click="clearImage">重新选择</button>
      <button class="btn-primary" :disabled="!imagePath || loading" @click="recognize">
        <text v-if="loading">识别中...</text>
        <text v-else>🔍 开始识别</text>
      </button>
    </view>

    <!-- 结果区域 -->
    <view v-if="result" class="result-area">
      <!-- 使用 AI 视觉 -->
      <view v-if="result.usingAI" class="result-card ai">
        <view class="result-header">
          <text class="badge ai-badge">🤖 AI 视觉识别</text>
          <text v-if="result.confidence" class="confidence">置信度 {{ (result.confidence * 100).toFixed(0) }}%</text>
        </view>
        <view v-if="result.aiReply" class="ai-reply">{{ result.aiReply }}</view>
        <view v-if="result.matchedSnakes && result.matchedSnakes.length" class="matched-list">
          <view class="matched-title">📚 匹配到的蛇种：</view>
          <view v-for="s in result.matchedSnakes" :key="s.id" class="matched-item" @click="goDetail(s.id)">
            <image v-if="s.imageUrl" :src="baseUrl + s.imageUrl" class="matched-img" />
            <view class="matched-info">
              <view class="matched-name">{{ s.name }}</view>
              <view class="matched-sub">
                <text :class="['venom-tag', s.isPoisonous ? 'venom' : 'safe']">
                  {{ s.isPoisonous ? '有毒' : '无毒' }}
                </text>
                <text v-if="s.venomType" class="venom-type">{{ s.venomType }}</text>
              </view>
            </view>
            <text class="arrow">›</text>
          </view>
        </view>
      </view>

      <!-- 降级：让用户描述特征 -->
      <view v-else class="result-card fallback">
        <view class="result-header">
          <text class="badge fallback-badge">💡 特征匹配模式</text>
        </view>
        <view class="fallback-msg">{{ result.message }}</view>
        <view class="prompt-list">
          <view v-for="(p, i) in result.prompt" :key="i" class="prompt-item">{{ p }}</view>
        </view>
        <button class="btn-feature" @click="goFeaturePage">🧩 去特征匹配 →</button>

        <view v-if="result.referenceSnakes && result.referenceSnakes.length" class="ref-snakes">
          <view class="ref-title">📚 常见蛇种参考：</view>
          <scroll-view scroll-x class="ref-scroll">
            <view v-for="s in result.referenceSnakes" :key="s.id" class="ref-card" @click="goDetail(s.id)">
              <image v-if="s.imageUrl" :src="baseUrl + s.imageUrl" class="ref-img" />
              <view v-else class="ref-img placeholder">🐍</view>
              <view class="ref-name">{{ s.name }}</view>
              <text :class="['venom-tag sm', s.isPoisonous ? 'venom' : 'safe']">
                {{ s.isPoisonous ? '毒' : '安' }}
              </text>
            </view>
          </scroll-view>
        </view>
      </view>
    </view>

    <!-- 安全提示 -->
    <view class="safety-tip">
      ⚠️ 识别结果仅供参考。如果被蛇咬伤，**立即拨打 120** 并前往有抗蛇毒血清的医院！
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const baseUrl = 'http://192.168.2.15:8080/api';
const imagePath = ref('');
const loading = ref(false);
const result = ref(null);

function chooseImage() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success(res) {
      imagePath.value = res.tempFilePaths[0];
      result.value = null;
    }
  });
}

function clearImage() {
  imagePath.value = '';
  result.value = null;
}

function recognize() {
  if (!imagePath.value) return;
  loading.value = true;

  const headers = {};
  const token = uni.getStorageSync('token');
  if (token) headers['Authorization'] = 'Bearer ' + token;

  uni.uploadFile({
    url: baseUrl + '/app/snake/recognize-image',
    filePath: imagePath.value,
    name: 'file',
    header: headers,
    timeout: 120000,
    success(res) {
      try {
        const data = JSON.parse(res.data);
        if (data.code === 200) {
          result.value = data.data;
        } else {
          uni.showToast({ title: data.message || '识别失败', icon: 'none' });
        }
      } catch (e) {
        console.error('parse error:', e, 'raw:', res.data);
        uni.showToast({ title: '解析失败', icon: 'none' });
      }
    },
    fail(err) {
      console.error('uploadFile fail:', err);
      uni.showToast({ title: '网络错误：' + (err.errMsg || err.message || ''), icon: 'none', duration: 3000 });
    },
    complete() {
      loading.value = false;
    }
  });
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/snake/detail?id=${id}` });
}

function goFeaturePage() {
  uni.switchTab({ url: '/pages/snake/list' });
}
</script>

<style scoped>
.page { padding: 24rpx; background: #f5f5f5; min-height: 100vh; }

.hero { text-align: center; padding: 40rpx 0 30rpx; }
.hero-emoji { font-size: 72rpx; }
.hero-title { font-size: 40rpx; font-weight: bold; color: #6A1B9A; margin-top: 10rpx; }
.hero-sub { font-size: 26rpx; color: #888; margin-top: 8rpx; }

.upload-card {
  background: white;
  border-radius: 20rpx;
  border: 3rpx dashed #ccc;
  height: 480rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.preview { width: 100%; height: 100%; }
.upload-placeholder { text-align: center; }
.upload-icon { font-size: 80rpx; }
.upload-text { font-size: 30rpx; color: #333; margin-top: 12rpx; }
.upload-hint { font-size: 22rpx; color: #999; margin-top: 8rpx; }

.action-row { display: flex; gap: 20rpx; margin-top: 30rpx; }
.btn-primary {
  flex: 1;
  background: linear-gradient(135deg, #6A1B9A, #AB47BC);
  color: white;
  border-radius: 50rpx;
  font-size: 30rpx;
  padding: 24rpx;
  border: none;
}
.btn-primary[disabled] { opacity: 0.6; }
.btn-secondary {
  background: white;
  color: #666;
  border-radius: 50rpx;
  font-size: 28rpx;
  padding: 24rpx 30rpx;
  border: 2rpx solid #ddd;
}

.result-area { margin-top: 30rpx; }
.result-card {
  background: white;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}
.result-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.badge {
  padding: 6rpx 20rpx;
  border-radius: 30rpx;
  font-size: 24rpx;
}
.ai-badge { background: #EDE7F6; color: #6A1B9A; }
.fallback-badge { background: #FFF3E0; color: #E65100; }
.confidence { font-size: 24rpx; color: #666; }

.ai-reply { font-size: 28rpx; color: #333; line-height: 1.8; padding: 16rpx; background: #FAFAFA; border-radius: 12rpx; margin-bottom: 20rpx; }

.matched-title, .ref-title { font-size: 26rpx; color: #666; margin-bottom: 12rpx; }
.matched-item {
  display: flex;
  align-items: center;
  padding: 16rpx;
  background: #FAFAFA;
  border-radius: 12rpx;
  margin-bottom: 12rpx;
}
.matched-img { width: 100rpx; height: 100rpx; border-radius: 10rpx; margin-right: 16rpx; }
.matched-info { flex: 1; }
.matched-name { font-size: 30rpx; font-weight: bold; color: #333; }
.matched-sub { display: flex; align-items: center; gap: 10rpx; margin-top: 6rpx; }
.venom-tag {
  font-size: 22rpx;
  padding: 2rpx 14rpx;
  border-radius: 20rpx;
}
.venom-tag.sm { padding: 2rpx 10rpx; font-size: 20rpx; }
.venom { background: #FFEBEE; color: #C62828; }
.safe { background: #E8F5E9; color: #2E7D32; }
.venom-type { font-size: 22rpx; color: #666; }
.arrow { font-size: 40rpx; color: #ccc; }

.fallback-msg { font-size: 28rpx; color: #E65100; margin-bottom: 16rpx; }
.prompt-list { padding-left: 10rpx; }
.prompt-item { font-size: 26rpx; color: #555; line-height: 2; }
.btn-feature {
  margin-top: 20rpx;
  background: #E65100;
  color: white;
  border-radius: 50rpx;
  font-size: 28rpx;
  padding: 20rpx;
  border: none;
}

.ref-snakes { margin-top: 24rpx; }
.ref-scroll { white-space: nowrap; }
.ref-card {
  display: inline-block;
  width: 180rpx;
  margin-right: 16rpx;
  background: #FAFAFA;
  border-radius: 14rpx;
  padding: 14rpx;
  text-align: center;
}
.ref-img { width: 150rpx; height: 150rpx; border-radius: 10rpx; }
.ref-img.placeholder { background: #E0E0E0; font-size: 60rpx; }
.ref-name { font-size: 24rpx; color: #333; margin: 8rpx 0 6rpx; }

.safety-tip {
  margin-top: 40rpx;
  padding: 24rpx;
  background: #FFEBEE;
  border-radius: 14rpx;
  font-size: 24rpx;
  color: #C62828;
  line-height: 1.6;
}
</style>
