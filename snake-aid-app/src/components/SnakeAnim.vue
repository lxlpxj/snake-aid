<template>
  <view class="snake-anim" :class="wrapClass">
    <view class="snake-wrap">
      <!-- SVG 蛇身（弯曲路径） -->
      <svg class="snake-svg" viewBox="0 0 120 60" xmlns="http://www.w3.org/2000/svg">
        <!-- 蛇身主体（wave 路径） -->
        <path :d="bodyPath" 
              :stroke="bodyColor" 
              stroke-width="8" 
              fill="none" 
              stroke-linecap="round"
              class="snake-body-path" />
        <!-- 蛇头 -->
        <g :transform="`translate(${headX},${headY})`">
          <!-- 毒蛇：三角形头 + 毒牙 -->
          <g v-if="snake.isPoisonous" class="head-group triangle-head">
            <polygon points="18,0 0,-12 0,12" :fill="headColor" class="head-shape" />
            <text class="fangs" x="-2" y="4">🦷</text>
          </g>
          <!-- 无毒蛇：椭圆形头 -->
          <g v-else class="head-group round-head">
            <ellipse cx="8" cy="0" rx="14" ry="10" :fill="headColor" class="head-shape" />
            <text class="eye" x="4" y="-4">●</text>
          </g>
          <!-- 吐信动画 -->
          <g class="tongue-group">
            <path d="M20,0 L32,-2 M20,0 L32,2" :stroke="tongueColor" stroke-width="2" class="tongue" />
          </g>
        </g>
        <!-- 花纹 -->
        <path v-for="(m, i) in markings" 
              :key="i" 
              :d="m" 
              :stroke="markColor"
              stroke-width="3" 
              fill="none" 
              stroke-linecap="round"
              class="marking" 
              :style="{ animationDelay: (i * 0.15) + 's' }" />
      </svg>
      
      <!-- 警告闪烁（毒蛇专属） -->
      <view v-if="snake.isPoisonous && dangerous" class="danger-flash">⚠️</view>
    </view>
    
    <!-- 标签 -->
    <view class="snake-label" :class="{ danger: snake.isPoisonous }">
      <text class="label-name">{{ snake.name }}</text>
      <text class="label-type">{{ snake.isPoisonous ? '⚠️ 毒蛇' : '✅ 无毒' }}</text>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  snake: { type: Object, required: true },
  size: { type: String, default: 'normal' },  // small/normal/large
  animated: { type: Boolean, default: true },
  dangerous: { type: Boolean, default: false }
})

// 蛇的配色方案（每种蛇独特）
const snakeThemes = {
  '银环蛇':  { body: '#1A1A1A', head: '#2A2A2A', mark: '#FFFFFF', tongue: '#C62828' },
  '金环蛇':  { body: '#1A1A1A', head: '#2A2A2A', mark: '#FFD700', tongue: '#C62828' },
  '眼镜蛇':  { body: '#424242', head: '#212121', mark: '#FFFFFF', tongue: '#C62828' },
  '五步蛇':  { body: '#5D4037', head: '#3E2723', mark: '#8D6E63', tongue: '#C62828' },
  '竹叶青':  { body: '#4CAF50', head: '#388E3C', mark: '#2E7D32', tongue: '#C62828' },
  '短尾蝮':  { body: '#6D4C41', head: '#4E342E', mark: '#3E2723', tongue: '#C62828' },
  '烙铁头蛇':{ body: '#5D4037', head: '#3E2723', mark: '#FF6F00', tongue: '#C62828' },
  '圆斑蝰':  { body: '#8D6E63', head: '#5D4037', mark: '#FFEB3B', tongue: '#C62828' },
  '海蛇(青环)':{ body: '#607D8B', head: '#455A64', mark: '#E0E0E0', tongue: '#C62828' },
  '莽山烙铁头':{ body: '#212121', head: '#1A1A1A', mark: '#CDDC39', tongue: '#C62828' },
  '乌梢蛇':  { body: '#558B2F', head: '#33691E', mark: '#33691E', tongue: '#2E7D32' },
  '王锦蛇':  { body: '#212121', head: '#1A1A1A', mark: '#FFD700', tongue: '#F57F17' }
}

const theme = computed(() => snakeThemes[props.snake.name] || { body: '#666', head: '#444', mark: '#888', tongue: '#F00' })

const bodyColor = computed(() => theme.value.body)
const headColor = computed(() => theme.value.head)
const markColor = computed(() => theme.value.mark)
const tongueColor = computed(() => theme.value.tongue)

// 弯曲蛇身路径
const bodyPath = computed(() => {
  return "M 10,30 Q 30,10 50,30 T 90,30 T 110,30"
})
const headX = computed(() => 112)
const headY = computed(() => 26)

// 花纹（每种蛇独特花纹路径）
const markings = computed(() => {
  const name = props.snake.name
  if (name.includes('银环')) {
    // 银环：白色环纹
    return [
      "M 25,20 Q 25,40 25,60",
      "M 45,10 Q 45,50 45,70",
      "M 65,10 Q 65,50 65,70",
      "M 85,10 Q 85,50 85,70"
    ]
  }
  if (name.includes('金环')) {
    return [
      "M 25,20 Q 25,40 25,60",
      "M 50,10 Q 50,50 50,70",
      "M 75,10 Q 75,50 75,70"
    ]
  }
  if (name.includes('竹叶青')) {
    // 竹叶青：绿色无明显花纹
    return []
  }
  if (name.includes('眼镜蛇')) {
    // 眼镜蛇：颈部斑纹
    return ["M 15,20 Q 15,40 15,60"]
  }
  if (name.includes('海蛇')) {
    // 海蛇：浅色环
    return [
      "M 30,15 Q 30,45 30,60",
      "M 55,15 Q 55,45 55,60",
      "M 80,15 Q 80,45 80,60"
    ]
  }
  if (name.includes('莽山')) {
    // 莽山烙铁头：大片黄绿斑
    return [
      "M 20,10 Q 20,50 20,70",
      "M 60,10 Q 60,50 60,70"
    ]
  }
  if (name.includes('圆斑')) {
    // 圆斑蝰：圆形斑点
    return [
      "M 30,20 A 5 5 0 1 0 30,20",
      "M 55,35 A 5 5 0 1 0 55,35",
      "M 80,20 A 5 5 0 1 0 80,20"
    ]
  }
  // 默认：小斑点
  return [
    "M 30,20 A 3 3 0 1 0 30,20",
    "M 55,35 A 3 3 0 1 0 55,35",
    "M 80,20 A 3 3 0 1 0 80,20"
  ]
})

const wrapClass = computed(() => [
  `size-${props.size}`,
  { animated: props.animated, dangerous: props.dangerous }
])
</script>

<style scoped>
.snake-anim {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx;
}
.snake-wrap {
  position: relative;
  width: 240rpx;
  height: 120rpx;
  display: flex;
  align-items: center;
}
.size-small .snake-wrap { width: 140rpx; height: 70rpx; }
.size-large .snake-wrap { width: 320rpx; height: 160rpx; }

.snake-svg { width: 100%; height: 100%; overflow: visible; }

/* 蛇身扭动动画（合同要求：象形动画建立危险直觉） */
.snake-body-path {
  animation: bodyWave 2s ease-in-out infinite;
  transform-origin: left center;
}
@keyframes bodyWave {
  0%, 100% { transform: translateX(0) scaleX(1); }
  25% { transform: translateX(4rpx) scaleX(1.02); }
  50% { transform: translateX(-4rpx) scaleX(1); }
  75% { transform: translateX(2rpx) scaleX(1.01); }
}

/* 花纹蠕动 */
.marking {
  animation: markCrawl 3s ease-in-out infinite;
}
@keyframes markCrawl {
  0%, 100% { stroke-dasharray: 4 2; }
  50% { stroke-dasharray: 6 1; }
}

/* 三角头：毒蛇闪烁危险色 */
.triangle-head {
  animation: headGlow 1.5s ease-in-out infinite;
  transform-origin: left center;
}
@keyframes headGlow {
  0%, 100% { filter: drop-shadow(0 0 0 transparent); }
  50% { filter: drop-shadow(0 0 8rpx rgba(255,0,0,0.5)); }
}

.round-head {
  animation: headBob 2s ease-in-out infinite;
  transform-origin: left center;
}
@keyframes headBob {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-2rpx); }
}

/* 吐信动画 */
.tongue {
  stroke-dasharray: 8 4;
  animation: tongueFlick 0.8s ease-in-out infinite;
}
@keyframes tongueFlick {
  0%, 100% { stroke-dashoffset: 12; opacity: 1; }
  50% { stroke-dashoffset: 0; opacity: 0.7; }
}

/* 毒牙闪烁 */
.fangs {
  animation: fangBlink 1s ease-in-out infinite;
}
@keyframes fangBlink {
  0%, 100% { opacity: 1; transform: translateY(0); }
  50% { opacity: 0.5; transform: translateY(2rpx); }
}

/* 危险警告闪烁 */
.danger-flash {
  position: absolute;
  top: -20rpx; right: -10rpx;
  font-size: 40rpx;
  animation: dangerFlash 0.6s ease-in-out infinite;
}
@keyframes dangerFlash {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.3); }
}

/* 标签 */
.snake-label {
  margin-top: 16rpx;
  text-align: center;
  display: flex; flex-direction: column;
  align-items: center;
}
.label-name { font-size: 28rpx; font-weight: bold; color: #333; }
.label-type { font-size: 22rpx; color: #666; margin-top: 4rpx; }
.snake-label.danger .label-type { color: #C62828; font-weight: bold; }

/* 非动画模式 */
.snake-anim:not(.animated) .snake-body-path,
.snake-anim:not(.animated) .marking,
.snake-anim:not(.animated) .tongue,
.snake-anim:not(.animated) .fangs,
.snake-anim:not(.animated) .triangle-head,
.snake-anim:not(.animated) .round-head {
  animation: none !important;
}
</style>
