<template>
  <view class="global-tools" :class="{ expanded }">
    <!-- 触发按钮 -->
    <view class="tools-fab" @click="toggle">
      <text class="fab-icon">{{ expanded ? '✕' : '🛠️' }}</text>
    </view>
    
    <!-- 展开面板 -->
    <view class="tools-panel" v-if="expanded">
      <text class="panel-title">🛠️ 全局设置</text>
      
      <!-- 主题切换 -->
      <view class="tool-group">
        <text class="group-label">👁️ 显示模式</text>
        <view class="btn-row">
          <view class="tool-btn" :class="{ active: currentTheme==='normal' }" @click="setTheme('normal')">标准</view>
          <view class="tool-btn" :class="{ active: currentTheme==='bigFont' }" @click="setTheme('bigFont')">大字</view>
          <view class="tool-btn elder" :class="{ active: currentTheme==='elder' }" @click="setTheme('elder')">👴老人</view>
        </view>
        <text class="hint-text">字号 {{ themeConfig.fontScale }}x · 对比度 {{ themeConfig.contrastRatio }}</text>
      </view>
      
      <!-- 方言切换 -->
      <view class="tool-group">
        <text class="group-label">🗣️ 语音方言</text>
        <view class="btn-row">
          <view class="tool-btn" :class="{ active: currentDialect==='mandarin' }" @click="setDialect('mandarin')">普通话</view>
          <view class="tool-btn" :class="{ active: currentDialect==='sichuan' }" @click="setDialect('sichuan')">四川话</view>
          <view class="tool-btn" :class="{ active: currentDialect==='cantonese' }" @click="setDialect('cantonese')">粤语</view>
          <view class="tool-btn" :class="{ active: currentDialect==='henan' }" @click="setDialect('henan')">河南话</view>
        </view>
      </view>
      
      <!-- 开关 -->
      <view class="tool-group">
        <text class="group-label">🔊 音效 / 语音</text>
        <view class="toggle-row">
          <text>操作音效</text>
          <view class="toggle" :class="{ on: sfxOn }" @click="toggleSFX">
            <view class="knob"></view>
          </view>
        </view>
        <view class="toggle-row">
          <text>语音朗读</text>
          <view class="toggle" :class="{ on: speechOn }" @click="toggleSpeech">
            <view class="knob"></view>
          </view>
        </view>
        <view class="toggle-row">
          <text>背景音</text>
          <view class="toggle" :class="{ on: bgmOn }" @click="toggleBGM">
            <view class="knob"></view>
          </view>
        </view>
      </view>
      
      <button class="test-voice" @click="testVoice">🔈 测试语音（{{ dialectName }}）</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getTheme, setTheme as _setTheme, getThemeConfig, onThemeChange } from '@/utils/theme.js'
import { getDialect, setDialect as _setDialect, dialects, onDialectChange } from '@/utils/dialect.js'
import { playBGM, stopBGM, toggleBGM as audioToggleBGM, sfxClick, sfxCorrect, sfxWrong, isBGMPlaying } from '@/utils/audio.js'
import { isSpeechOn, toggleSpeech as dialectToggleSpeech, speak } from '@/utils/dialect.js'

const expanded = ref(false)
const currentTheme = ref(getTheme())
const currentDialect = ref(getDialect())
const sfxOn = ref(true)
const speechOn = ref(isSpeechOn())
const bgmOn = ref(false)

const themeConfig = computed(() => getThemeConfig())
const dialectName = computed(() => dialects[currentDialect.value].name)

function toggle() { expanded.value = !expanded.value; sfxClick() }
function setTheme(name) { currentTheme.value = name; _setTheme(name); sfxClick(); }
function setDialect(name) { currentDialect.value = name; _setDialect(name); sfxClick(); }

function toggleSFX() { sfxOn.value = !sfxOn.value; if (sfxOn.value) sfxCorrect(); }
function toggleSpeech() { 
  dialectToggleSpeech(); 
  speechOn.value = !speechOn.value; 
  if (speechOn.value) { speak('语音已开启'); }
}
function toggleBGM() {
  audioToggleBGM()
  bgmOn.value = isBGMPlaying()
}

function testVoice() {
  sfxClick()
  const phrases = {
    mandarin: '您好！蛇咬伤后请保持冷静，立刻远离毒蛇！',
    sichuan: '遭蛇咬到了莫慌！搞快打幺二零！',
    cantonese: '俾蛇咬咗唔好惊，即刻打一二零！',
    henan: '叫蛇咬着了别慌，赶紧嘞打一百二十！'
  }
  speechOn.value = true
  dialectToggleSpeech()
  setTimeout(() => speak(phrases[currentDialect.value]), 300)
}

onMounted(() => {
  onThemeChange(() => { currentTheme.value = getTheme() })
  onDialectChange(() => { currentDialect.value = getDialect() })
})
</script>

<style scoped>
.global-tools { position: fixed; right: 20rpx; bottom: 200rpx; z-index: 999; }
.tools-fab {
  width: 100rpx; height: 100rpx; border-radius: 50%;
  background: linear-gradient(135deg, #FF6F00, #E65100);
  box-shadow: 0 8rpx 24rpx rgba(230,81,0,0.4);
  display: flex; align-items: center; justify-content: center;
  font-size: 48rpx;
  position: relative; z-index: 1000;
}
.tools-fab:active { transform: scale(0.95); }

.tools-panel {
  position: absolute; right: 110rpx; bottom: 0;
  width: 520rpx; max-height: 70vh;
  background: #fff; border-radius: 24rpx;
  box-shadow: 0 16rpx 48rpx rgba(0,0,0,0.2);
  padding: 24rpx;
  overflow-y: auto;
}
.panel-title { font-size: 30rpx; font-weight: bold; color: #333; display: block; margin-bottom: 16rpx; }

.tool-group { margin-bottom: 20rpx; padding: 16rpx; background: #F5F5F5; border-radius: 16rpx; }
.group-label { font-size: 26rpx; color: #555; display: block; margin-bottom: 12rpx; }
.btn-row { display: flex; gap: 10rpx; flex-wrap: wrap; }
.tool-btn {
  padding: 10rpx 20rpx;
  background: #fff; border-radius: 10rpx;
  font-size: 24rpx; color: #666;
  border: 2rpx solid #E0E0E0;
}
.tool-btn.active { background: #FF6F00; color: #fff; border-color: #FF6F00; }
.tool-btn.elder { background: #FFEBEE; border-color: #E53935; color: #C62828; }
.tool-btn.elder.active { background: #C62828; color: #fff; }
.hint-text { font-size: 22rpx; color: #999; margin-top: 8rpx; display: block; }

.toggle-row { display: flex; justify-content: space-between; align-items: center; padding: 8rpx 0; font-size: 26rpx; color: #333; }
.toggle {
  width: 80rpx; height: 44rpx; background: #E0E0E0;
  border-radius: 22rpx; position: relative;
  transition: background 0.3s;
}
.toggle.on { background: #4CAF50; }
.toggle .knob {
  position: absolute; top: 4rpx; left: 4rpx;
  width: 36rpx; height: 36rpx; background: #fff;
  border-radius: 50%;
  transition: left 0.3s;
}
.toggle.on .knob { left: 40rpx; }

.test-voice {
  width: 100%; background: #FF6F00; color: #fff;
  font-size: 26rpx; border-radius: 30rpx; margin-top: 10rpx;
}
</style>

