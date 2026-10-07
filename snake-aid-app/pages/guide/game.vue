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
          <view class="level-emoji-wrap">
            <text class="level-emoji level-emoji-anim">{{ steps[currentStep].emoji }}</text>
            <view class="level-emoji-ring"></view>
          </view>
          <view class="level-info">
            <view class="level-title">{{ steps[currentStep].title }}</view>
            <view class="level-hint">{{ steps[currentStep].hint }}</view>
          </view>
        </view>

                <!-- 🎬 每关专属 SVG 场景插图 -->
        <view class="scene-stage">
          <!-- 第1关：田间小路遇蛇 - 超详细场景 -->
          <view v-if="currentStep === 0" class="scene-wrap">
            <svg viewBox="0 0 400 280" class="scene-svg">
              <!-- 天空渐变 -->
              <defs>
                <linearGradient id="sky1" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="#87CEEB"/>
                  <stop offset="100%" stop-color="#E8F5E9"/>
                </linearGradient>
              </defs>
              <rect width="400" height="160" fill="url(#sky1)"/>
              <!-- 太阳 -->
              <circle cx="350" cy="40" r="25" fill="#FFD54F" class="anim-sun"/>
              <g stroke="#FFD54F" stroke-width="3">
                <line x1="350" y1="5" x2="350" y2="15"/>
                <line x1="350" y1="65" x2="350" y2="75"/>
                <line x1="315" y1="40" x2="325" y2="40"/>
                <line x1="375" y1="40" x2="385" y2="40"/>
                <line x1="325" y1="15" x2="332" y2="22"/>
                <line x1="368" y1="55" x2="375" y2="62"/>
                <line x1="368" y1="25" x2="375" y2="18"/>
                <line x1="325" y1="65" x2="332" y2="58"/>
              </g>
              <!-- 云 -->
              <g fill="white" opacity="0.9">
                <ellipse cx="80" cy="50" rx="30" ry="12"/>
                <ellipse cx="100" cy="45" rx="25" ry="15"/>
                <ellipse cx="120" cy="52" rx="22" ry="10"/>
              </g>
              <g fill="white" opacity="0.85">
                <ellipse cx="230" cy="35" rx="25" ry="10"/>
                <ellipse cx="250" cy="30" rx="20" ry="13"/>
              </g>
              <!-- 远山 -->
              <path d="M0 160 Q50 120 100 140 Q150 115 200 135 Q250 120 300 140 Q350 125 400 150 L400 160 Z" fill="#81C784"/>
              <!-- 草地 -->
              <rect y="160" width="400" height="120" fill="#A5D6A7"/>
              <rect y="175" width="400" height="105" fill="#81C784"/>
              <!-- 小路 -->
              <path d="M180 280 L200 170 L220 170 L240 280 Z" fill="#D7CCC8" stroke="#BCAAA4" stroke-width="1"/>
              <path d="M195 280 L205 170 M220 170 L225 280" stroke="#BCAAA4" stroke-width="0.5" stroke-dasharray="3 5"/>
              <!-- 小草 -->
              <g stroke="#558B2F" stroke-width="2" stroke-linecap="round">
                <path d="M20 200 Q22 190 20 180"/>
                <path d="M25 205 Q28 195 25 185"/>
                <path d="M60 220 Q63 210 60 200"/>
                <path d="M340 210 Q343 200 340 190"/>
                <path d="M365 230 Q368 220 365 210"/>
                <path d="M380 215 Q383 205 380 195"/>
              </g>
              <!-- 野花 -->
              <g class="anim-flower1">
                <circle cx="45" cy="195" r="3" fill="#E91E63"/>
                <circle cx="45" cy="190" r="3" fill="#E91E63"/>
                <circle cx="45" cy="200" r="3" fill="#E91E63"/>
                <circle cx="40" cy="195" r="3" fill="#E91E63"/>
                <circle cx="50" cy="195" r="3" fill="#E91E63"/>
                <circle cx="45" cy="195" r="2" fill="#FFEB3B"/>
              </g>
              <g class="anim-flower2">
                <circle cx="370" cy="225" r="3" fill="#FF9800"/>
                <circle cx="370" cy="220" r="3" fill="#FF9800"/>
                <circle cx="370" cy="230" r="3" fill="#FF9800"/>
                <circle cx="365" cy="225" r="3" fill="#FF9800"/>
                <circle cx="375" cy="225" r="3" fill="#FF9800"/>
                <circle cx="370" cy="225" r="2" fill="#FFEB3B"/>
              </g>
              <!-- 树 -->
              <g transform="translate(15,120)">
                <rect x="18" y="40" width="8" height="40" fill="#6D4C41"/>
                <circle cx="22" cy="35" r="28" fill="#388E3C"/>
                <circle cx="10" cy="30" r="20" fill="#43A047"/>
                <circle cx="35" cy="28" r="22" fill="#4CAF50"/>
              </g>
              <g transform="translate(325,125)">
                <rect x="18" y="40" width="8" height="35" fill="#6D4C41"/>
                <circle cx="22" cy="35" r="25" fill="#388E3C"/>
                <circle cx="35" cy="30" r="18" fill="#43A047"/>
              </g>
              <!-- 人物（后退 + 惊恐表情） -->
              <g class="anim-person-run" transform="translate(70,140)">
                <!-- 头 -->
                <circle cx="18" cy="20" r="14" fill="#FFCCBC"/>
                <path d="M4 16 Q18 5 32 16 Q32 12 18 10 Q4 12 4 16" fill="#5D4037"/>
                <!-- 惊恐眼睛 -->
                <circle cx="13" cy="20" r="3" fill="white"/>
                <circle cx="23" cy="20" r="3" fill="white"/>
                <circle cx="13" cy="20" r="1.5" fill="#1A1A1A"/>
                <circle cx="23" cy="20" r="1.5" fill="#1A1A1A"/>
                <!-- 惊恐嘴巴 O -->
                <ellipse cx="18" cy="27" rx="3" ry="4" fill="#5D4037"/>
                <!-- 身体 -->
                <rect x="10" y="32" width="16" height="22" rx="4" fill="#1565C0"/>
                <!-- 后退的脚（一前一后） -->
                <rect x="10" y="54" width="6" height="16" rx="2" fill="#795548" class="anim-leg1"/>
                <rect x="20" y="54" width="6" height="16" rx="2" fill="#795548" class="anim-leg2"/>
                <!-- 手臂向后摆 -->
                <rect x="2" y="35" width="8" height="5" rx="2" fill="#1976D2" transform="rotate(-20 6 37)"/>
                <rect x="26" y="35" width="8" height="5" rx="2" fill="#1976D2" transform="rotate(20 30 37)"/>
                <!-- 汗滴 -->
                <text x="35" y="15" font-size="14">💦</text>
              </g>
              <!-- 蛇（S形 + 抬头警告 + 花纹） -->
              <g class="anim-snake-warn" transform="translate(260,190)">
                <path d="M0 30 Q15 5 35 20 Q55 40 75 10 Q95 -15 115 5 Q125 15 130 0" 
                      fill="none" stroke="#558B2F" stroke-width="11" stroke-linecap="round"/>
                <!-- 花纹 -->
                <g fill="#33691E">
                  <circle cx="20" cy="18" r="4"/>
                  <circle cx="45" cy="28" r="4"/>
                  <circle cx="65" cy="12" r="4"/>
                  <circle cx="90" cy="-3" r="4"/>
                </g>
                <!-- 头 -->
                <ellipse cx="128" cy="2" rx="10" ry="8" fill="#558B2F"/>
                <!-- 眼睛 -->
                <circle cx="132" cy="-1" r="2.5" fill="#FFEB3B"/>
                <circle cx="132" cy="-1" r="1" fill="#1A1A1A"/>
                <!-- 吐信 -->
                <g class="anim-tongue">
                  <path d="M138 2 L148 0 M138 2 L148 4 M138 2 L145 2" stroke="#D32F2F" stroke-width="2" fill="none"/>
                </g>
                <!-- 警告气泡 -->
                <g transform="translate(60,-40)" class="anim-warn-bubble">
                  <ellipse rx="28" ry="20" fill="#FFEB3B" stroke="#F57F17" stroke-width="2"/>
                  <path d="M45 0 L55 12 L50 -8 Z" fill="#FFEB3B" stroke="#F57F17" stroke-width="2"/>
                  <text text-anchor="middle" y="5" font-size="18" fill="#D84315" font-weight="bold">⚠ 危险！</text>
                </g>
              </g>
              <!-- 后退箭头 + 提示 -->
              <g class="anim-back-arrow">
                <path d="M140 130 L115 145 L140 160" stroke="#D32F2F" stroke-width="5" fill="none" stroke-linecap="round"/>
                <text x="175" y="150" font-size="14" fill="#D32F2F" font-weight="bold" class="anim-arrow-text">慢慢后退！</text>
              </g>
              <!-- 小鸟飞 -->
              <g class="anim-bird" transform="translate(180,60)">
                <path d="M0 0 Q5 -5 10 0 Q15 -5 20 0" stroke="#5D4037" stroke-width="2" fill="none"/>
              </g>
              <g class="anim-bird2" transform="translate(210,75)">
                <path d="M0 0 Q4 -4 8 0 Q12 -4 16 0" stroke="#5D4037" stroke-width="2" fill="none"/>
              </g>
            </svg>
          </view>

          <!-- 第2关：辨认蛇 - 两种蛇+细节花纹+毒牙+安全卡 -->
          <view v-if="currentStep === 1" class="scene-wrap">
            <svg viewBox="0 0 400 280" class="scene-svg">
              <rect width="400" height="280" fill="#FFF8E1"/>
              <!-- 标题 -->
              <text x="200" y="30" text-anchor="middle" font-size="18" fill="#3E2723" font-weight="bold">🔍 一眼分辨：毒蛇 vs 无毒蛇</text>
              <!-- 毒蛇卡片 -->
              <g transform="translate(15,50)">
                <rect width="175" height="210" rx="15" fill="#FFEBEE" stroke="#EF5350" stroke-width="3"/>
                <rect x="0" y="0" width="175" height="35" rx="15" fill="#C62828"/>
                <rect x="0" y="20" width="175" height="15" fill="#C62828"/>
                <text x="87" y="25" text-anchor="middle" font-size="15" fill="white" font-weight="bold">⚠️ 有 毒 蛇</text>
                <!-- 蛇身 -->
                <g transform="translate(17,50)">
                  <path d="M0 80 Q25 60 50 75 Q75 90 100 60 Q125 30 145 50" 
                        fill="none" stroke="#424242" stroke-width="14" stroke-linecap="round"/>
                  <!-- 花纹 -->
                  <g fill="#D32F2F">
                    <path d="M20 73 A7 7 0 1 1 20 73.1"/>
                    <path d="M50 75 A8 8 0 1 1 50 75.1"/>
                    <path d="M80 62 A7 7 0 1 1 80 62.1"/>
                    <path d="M110 45 A6 6 0 1 1 110 45.1"/>
                  </g>
                  <!-- 三角头 + 脖子细 -->
                  <path d="M140 48 L170 30 L170 70 L140 58 Z" fill="#B71C1C" class="anim-tri-warn"/>
                  <rect x="163" y="48" width="10" height="6" fill="#1A1A1A"/>
                  <rect x="163" y="52" width="10" height="6" fill="#1A1A1A"/>
                  <!-- 立起的瞳孔 -->
                  <ellipse cx="175" cy="45" rx="3" ry="5" fill="#FFEB3B"/>
                  <ellipse cx="175" cy="45" rx="1" ry="4" fill="#1A1A1A"/>
                  <!-- 毒牙 -->
                  <polygon points="165,58 160,75 170,70" fill="#FFEB3B" class="anim-tooth1"/>
                  <polygon points="175,58 180,75 170,70" fill="#FFEB3B" class="anim-tooth2"/>
                  <!-- 毒汁滴 -->
                  <circle cx="170" cy="80" r="3" fill="#4CAF50" class="anim-venom-drop"/>
                </g>
                <text x="87" y="170" text-anchor="middle" font-size="12" fill="#333">🔺 三角头形</text>
                <text x="87" y="188" text-anchor="middle" font-size="12" fill="#333">👁 竖瞳 + 毒牙</text>
                <text x="87" y="206" text-anchor="middle" font-size="13" fill="#C62828" font-weight="bold">→ 立 刻 后 退！</text>
              </g>
              <!-- 无毒蛇卡片 -->
              <g transform="translate(210,50)">
                <rect width="175" height="210" rx="15" fill="#E8F5E9" stroke="#66BB6A" stroke-width="3"/>
                <rect x="0" y="0" width="175" height="35" rx="15" fill="#2E7D32"/>
                <rect x="0" y="20" width="175" height="15" fill="#2E7D32"/>
                <text x="87" y="25" text-anchor="middle" font-size="15" fill="white" font-weight="bold">✅ 无 毒 蛇</text>
                <!-- 蛇身 -->
                <g transform="translate(17,50)">
                  <path d="M0 80 Q25 60 50 75 Q75 90 100 60 Q125 30 145 50" 
                        fill="none" stroke="#2E7D32" stroke-width="14" stroke-linecap="round"/>
                  <!-- 花纹 -->
                  <g fill="#1B5E20">
                    <ellipse cx="25" cy="70" rx="6" ry="4"/>
                    <ellipse cx="55" cy="80" rx="6" ry="4"/>
                    <ellipse cx="85" cy="65" rx="6" ry="4"/>
                    <ellipse cx="115" cy="48" rx="6" ry="4"/>
                  </g>
                  <!-- 椭圆头 -->
                  <ellipse cx="165" cy="50" rx="18" ry="14" fill="#4CAF50" class="anim-round-float"/>
                  <path d="M150 50 L165 50" stroke="#558B2F" stroke-width="3"/>
                  <!-- 圆瞳孔 -->
                  <circle cx="165" cy="45" r="4" fill="white"/>
                  <circle cx="165" cy="45" r="2.5" fill="#1A1A1A"/>
                  <!-- 舌头 -->
                  <path d="M183 50 L193 48 M183 50 L193 52 M183 50 L190 50" stroke="#FF5722" stroke-width="1.5" fill="none"/>
                </g>
                <text x="87" y="170" text-anchor="middle" font-size="12" fill="#333">⭕ 椭圆/圆头</text>
                <text x="87" y="188" text-anchor="middle" font-size="12" fill="#333">👁 圆瞳 + 无毒牙</text>
                <text x="87" y="206" text-anchor="middle" font-size="13" fill="#2E7D32" font-weight="bold">→ 一般安全</text>
              </g>
            </svg>
          </view>

          <!-- 第3关：保持镇定 - 打坐+呼吸环+自然风 -->
          <view v-if="currentStep === 2" class="scene-wrap">
            <svg viewBox="0 0 400 280" class="scene-svg">
              <defs>
                <radialGradient id="calmSky" cx="50%" cy="30%">
                  <stop offset="0%" stop-color="#C8E6C9"/>
                  <stop offset="100%" stop-color="#A5D6A7"/>
                </radialGradient>
              </defs>
              <rect width="400" height="280" fill="url(#calmSky)"/>
              <!-- 飘浮的叶子 -->
              <g class="anim-leaf1" transform="translate(50,40)">
                <ellipse cx="0" cy="0" rx="8" ry="4" fill="#66BB6A" transform="rotate(30)"/>
              </g>
              <g class="anim-leaf2" transform="translate(320,60)">
                <ellipse cx="0" cy="0" rx="7" ry="3" fill="#81C784" transform="rotate(-20)"/>
              </g>
              <g class="anim-leaf3" transform="translate(80,80)">
                <ellipse cx="0" cy="0" rx="6" ry="3" fill="#A5D6A7" transform="rotate(45)"/>
              </g>
              <!-- 草地 -->
              <path d="M0 230 Q200 200 400 230 L400 280 L0 280 Z" fill="#81C784"/>
              <!-- 石头 -->
              <ellipse cx="60" cy="240" rx="25" ry="10" fill="#9E9E9E"/>
              <ellipse cx="340" cy="245" rx="22" ry="9" fill="#9E9E9E"/>
              <!-- 打坐人物 -->
              <g transform="translate(170,110)">
                <!-- 盘腿 -->
                <ellipse cx="35" cy="95" rx="55" ry="15" fill="#388E3C"/>
                <!-- 身体 -->
                <path d="M15 55 Q35 45 55 55 L60 95 Q35 100 10 95 Z" fill="#4CAF50"/>
                <!-- 头 -->
                <circle cx="35" cy="30" r="25" fill="#FFCCBC"/>
                <!-- 闭眼睛 -->
                <path d="M20 28 Q25 32 30 28" fill="none" stroke="#5D4037" stroke-width="2"/>
                <path d="M40 28 Q45 32 50 28" fill="none" stroke="#5D4037" stroke-width="2"/>
                <!-- 微笑 -->
                <path d="M28 38 Q35 42 42 38" fill="none" stroke="#5D4037" stroke-width="2"/>
                <!-- 头发 -->
                <path d="M18 15 Q35 0 52 15 Q50 8 35 5 Q20 8 18 15" fill="#3E2723"/>
                <!-- 手放膝盖 -->
                <circle cx="18" cy="75" r="8" fill="#FFCCBC"/>
                <circle cx="52" cy="75" r="8" fill="#FFCCBC"/>
                <!-- 呼吸光环 -->
                <circle cx="35" cy="40" r="60" fill="none" stroke="#81C784" stroke-width="2" class="anim-ring1"/>
                <circle cx="35" cy="40" r="80" fill="none" stroke="#A5D6A7" stroke-width="2" class="anim-ring2"/>
                <circle cx="35" cy="40" r="100" fill="none" stroke="#C8E6C9" stroke-width="2" class="anim-ring3"/>
                <circle cx="35" cy="40" r="120" fill="none" stroke="#E8F5E9" stroke-width="1.5" class="anim-ring4"/>
              </g>
              <!-- 提示气泡 -->
              <g transform="translate(200,60)">
                <ellipse rx="60" ry="22" fill="white" opacity="0.95" stroke="#81C784" stroke-width="2"/>
                <path d="M55 15 L45 25 L50 10" fill="white" stroke="#81C784" stroke-width="2"/>
                <text text-anchor="middle" y="5" font-size="13" fill="#2E7D32" class="anim-breathe-text">深 呼 吸 ~ 吸 气 ~</text>
              </g>
              <!-- 正念提示 -->
              <text x="200" y="270" text-anchor="middle" font-size="11" fill="#558B2F">🧘 慌了吗？深呼吸3次，冷静下来才能救自己</text>
            </svg>
          </view>

          <!-- 第4关：紧急呼救 - 手机+拨号+救护车+信号塔 -->
          <view v-if="currentStep === 3" class="scene-wrap">
            <svg viewBox="0 0 400 280" class="scene-svg">
              <rect width="400" height="280" fill="#E3F2FD"/>
              <!-- 紧急红条 -->
              <g transform="translate(200,20)">
                <rect x="-80" y="0" width="160" height="32" rx="16" fill="#D32F2F" class="anim-emergency-pulse"/>
                <text text-anchor="middle" y="22" font-size="16" fill="white" font-weight="bold">🚨 紧 急 拨 打 120 🚨</text>
              </g>
              <!-- 人物拿手机 -->
              <g transform="translate(50,100)">
                <!-- 身体 -->
                <ellipse cx="25" cy="70" rx="22" ry="25" fill="#FFCCBC"/>
                <!-- 头 -->
                <circle cx="25" cy="25" r="20" fill="#FFCCBC"/>
                <!-- 表情紧张 -->
                <circle cx="19" cy="23" r="2.5" fill="#1A1A1A"/>
                <circle cx="31" cy="23" r="2.5" fill="#1A1A1A"/>
                <path d="M20 33 Q25 30 30 33" fill="none" stroke="#5D4037" stroke-width="2"/>
                <!-- 手举手机 -->
                <rect x="40" y="45" width="15" height="25" rx="3" fill="#FFCCBC" transform="rotate(-15 47 57)"/>
              </g>
              <!-- 大屏手机 -->
              <g transform="translate(140,65)" class="anim-phone-vibrate">
                <rect width="130" height="190" rx="18" fill="#263238" stroke="#455A64" stroke-width="2"/>
                <!-- 顶部听筒 -->
                <rect x="50" y="12" width="30" height="5" rx="2" fill="#1A1A1A"/>
                <!-- 屏幕 -->
                <rect x="10" y="25" width="110" height="130" rx="5" fill="#0D47A1"/>
                <!-- 120 大号显示 -->
                <text x="65" y="70" text-anchor="middle" font-size="44" fill="#00E676" font-family="monospace" font-weight="bold" class="anim-digits">120</text>
                <text x="65" y="95" text-anchor="middle" font-size="11" fill="#90CAF9" class="anim-calling-text">📡 正在接通急救中心...</text>
                <!-- 信号格 -->
                <g transform="translate(15,35)">
                  <rect width="4" height="6" fill="#00E676"/>
                  <rect x="7" width="4" height="10" fill="#00E676"/>
                  <rect x="14" width="4" height="14" fill="#00E676"/>
                  <rect x="21" width="4" height="18" fill="#00E676"/>
                </g>
                <!-- 按键 -->
                <g fill="#37474F">
                  <circle cx="35" cy="170" r="10"/>
                  <circle cx="65" cy="170" r="10"/>
                  <circle cx="95" cy="170" r="10"/>
                  <circle cx="65" cy="170" r="13" fill="#4CAF50"/>
                </g>
                <text x="35" y="174" text-anchor="middle" font-size="12" fill="white">1</text>
                <text x="65" y="174" text-anchor="middle" font-size="12" fill="white">2</text>
                <text x="95" y="174" text-anchor="middle" font-size="12" fill="white">0</text>
                <text x="65" y="174" text-anchor="middle" font-size="14" fill="white">📞</text>
              </g>
              <!-- 信号波 -->
              <g class="anim-signal-wave" transform="translate(205,160)">
                <circle r="25" fill="none" stroke="#1976D2" stroke-width="2" opacity="0.7"/>
                <circle r="45" fill="none" stroke="#1976D2" stroke-width="1.5" opacity="0.4"/>
                <circle r="65" fill="none" stroke="#1976D2" stroke-width="1" opacity="0.2"/>
              </g>
              <!-- 远处救护车 -->
              <g transform="translate(300,180)" class="anim-ambulance">
                <rect width="80" height="35" rx="4" fill="#F5F5F5" stroke="#BDBDBD" stroke-width="1"/>
                <rect x="0" y="10" width="80" height="12" fill="#D32F2F"/>
                <text x="40" y="20" text-anchor="middle" font-size="12" fill="white" font-weight="bold">🚑 120</text>
                <!-- 灯 -->
                <circle cx="10" cy="8" r="4" fill="#FFEB3B" class="anim-siren"/>
                <circle cx="70" cy="8" r="4" fill="#FFEB3B" class="anim-siren"/>
                <!-- 轮子 -->
                <circle cx="15" cy="35" r="6" fill="#424242"/>
                <circle cx="65" cy="35" r="6" fill="#424242"/>
              </g>
              <!-- 救护车车灯 -->
              <g class="anim-flash" transform="translate(310,180)">
                <path d="M-20 0 L-5 0 L-20 5 L-20 -5 Z" fill="#FFEB3B" opacity="0.6"/>
              </g>
            </svg>
          </view>

          <!-- 第5关：伤口解压 - 完整冲洗流程 -->
          <view v-if="currentStep === 4" class="scene-wrap">
            <svg viewBox="0 0 400 280" class="scene-svg">
              <defs>
                <linearGradient id="waterBg" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="#E0F7FA"/>
                  <stop offset="100%" stop-color="#B2EBF2"/>
                </linearGradient>
              </defs>
              <rect width="400" height="280" fill="url(#waterBg)"/>
              <!-- 瓷砖墙 -->
              <g stroke="#80DEEA" stroke-width="1" opacity="0.4">
                <line x1="0" y1="0" x2="0" y2="180"/>
                <line x1="60" y1="0" x2="60" y2="180"/>
                <line x1="120" y1="0" x2="120" y2="180"/>
                <line x1="180" y1="0" x2="180" y2="180"/>
                <line x1="240" y1="0" x2="240" y2="180"/>
                <line x1="300" y1="0" x2="300" y2="180"/>
                <line x1="360" y1="0" x2="360" y2="180"/>
                <line x1="0" y1="60" x2="400" y2="60"/>
                <line x1="0" y1="120" x2="400" y2="120"/>
              </g>
              <!-- 水龙头底座 -->
              <rect x="40" y="50" width="40" height="80" rx="5" fill="#B0BEC5"/>
              <rect x="45" y="40" width="30" height="15" rx="3" fill="#78909C"/>
              <!-- 水龙头把手 -->
              <g transform="translate(60,50)" class="anim-handle">
                <rect x="-4" y="-15" width="8" height="20" rx="2" fill="#455A64"/>
                <circle cx="0" cy="-18" r="6" fill="#90A4AE"/>
              </g>
              <!-- 出水嘴 -->
              <rect x="75" y="120" width="50" height="10" rx="3" fill="#78909C"/>
              <rect x="120" y="118" width="8" height="14" rx="2" fill="#546E7A"/>
              <!-- 水滴 -->
              <g class="anim-water-drop1" transform="translate(125,132)">
                <path d="M0 0 Q8 18 0 30 Q-8 18 0 0" fill="#42A5F5"/>
              </g>
              <g class="anim-water-drop2" transform="translate(135,132)">
                <path d="M0 0 Q8 18 0 30 Q-8 18 0 0" fill="#64B5F6"/>
              </g>
              <g class="anim-water-drop3" transform="translate(145,132)">
                <path d="M0 0 Q8 18 0 30 Q-8 18 0 0" fill="#42A5F5"/>
              </g>
              <!-- 主水流 -->
              <path d="M145 145 Q160 200 240 200" stroke="#2196F3" stroke-width="8" fill="none" stroke-linecap="round" class="anim-water-flow"/>
              <path d="M145 145 Q160 200 240 200" stroke="#64B5F6" stroke-width="4" fill="none" stroke-linecap="round"/>
              <!-- 手臂 -->
              <g transform="translate(180,170)">
                <ellipse cx="80" cy="30" rx="90" ry="22" fill="#FFCCBC"/>
                <!-- 手腕 -->
                <rect x="160" y="20" width="25" height="22" rx="5" fill="#FFAB91"/>
                <!-- 手掌 -->
                <g transform="translate(185,12)">
                  <ellipse cx="12" cy="10" rx="14" ry="18" fill="#FFCCBC"/>
                  <!-- 手指 -->
                  <rect x="-2" y="0" width="6" height="12" rx="3" fill="#FFCCBC"/>
                  <rect x="6" y="-4" width="6" height="14" rx="3" fill="#FFCCBC"/>
                  <rect x="14" y="-4" width="6" height="14" rx="3" fill="#FFCCBC"/>
                  <rect x="22" y="-2" width="6" height="13" rx="3" fill="#FFCCBC"/>
                </g>
                <!-- 伤口 -->
                <circle cx="100" cy="30" r="28" fill="#FFCDD2" stroke="#E57373" stroke-width="3" stroke-dasharray="5 3"/>
                <path d="M90 25 Q100 35 110 25 Q115 35 110 40 Q100 45 90 40 Q85 35 90 25" fill="#C62828" class="anim-wound-pulse"/>
                <!-- 牙印 -->
                <circle cx="95" cy="28" r="3" fill="#FF5252"/>
                <circle cx="108" cy="32" r="3" fill="#FF5252"/>
              </g>
              <!-- 水花 -->
              <g class="anim-splash">
                <circle cx="240" cy="200" r="15" fill="white" opacity="0.7"/>
                <circle cx="235" cy="198" r="5" fill="white"/>
                <circle cx="248" cy="202" r="4" fill="white"/>
              </g>
              <!-- 提示文字 -->
              <g transform="translate(200,240)">
                <rect x="-90" y="-15" width="180" height="30" rx="15" fill="white" opacity="0.9" stroke="#4FC3F7" stroke-width="1"/>
                <text text-anchor="middle" y="5" font-size="12" fill="#0277BD">💧 流动清水冲洗 15-20 分钟</text>
              </g>
            </svg>
          </view>

          <!-- 第6关：伤肢制动 - 医院病床+三角巾夹板 -->
          <view v-if="currentStep === 5" class="scene-wrap">
            <svg viewBox="0 0 400 280" class="scene-svg">
              <rect width="400" height="280" fill="#FFF3E0"/>
              <!-- 地板 -->
              <rect y="220" width="400" height="60" fill="#8D6E63"/>
              <g y="220" stroke="#5D4037" stroke-width="0.5" opacity="0.4">
                <line x1="0" y1="240" x2="400" y2="240"/>
                <line x1="0" y1="260" x2="400" y2="260"/>
              </g>
              <!-- 床 -->
              <g transform="translate(40,120)">
                <!-- 床垫 -->
                <rect width="320" height="50" rx="5" fill="white" stroke="#BDBDBD" stroke-width="1"/>
                <!-- 床单 -->
                <rect width="320" height="10" rx="5" fill="#E3F2FD"/>
                <!-- 枕头 -->
                <rect x="10" y="-15" width="80" height="25" rx="5" fill="white" stroke="#E0E0E0" stroke-width="1"/>
                <rect x="12" y="-13" width="76" height="5" fill="#BBDEFB"/>
                <!-- 床腿 -->
                <rect x="10" y="50" width="8" height="50" fill="#6D4C41"/>
                <rect x="300" y="50" width="8" height="50" fill="#6D4C41"/>
                <!-- 床头 -->
                <rect x="-10" y="-25" width="15" height="90" rx="3" fill="#8D6E63"/>
              </g>
              <!-- 躺下的人 -->
              <g transform="translate(60,95)">
                <!-- 头 -->
                <circle cx="25" cy="15" r="18" fill="#FFCCBC"/>
                <path d="M12 12 Q25 3 38 12" fill="#3E2723"/>
                <path d="M15 18 Q20 22 25 18" fill="none" stroke="#5D4037" stroke-width="2"/>
                <path d="M30 18 Q35 22 40 18" fill="none" stroke="#5D4037" stroke-width="2"/>
                <path d="M20 25 Q25 28 30 25" fill="none" stroke="#5D4037" stroke-width="1.5"/>
                <!-- 身体 -->
                <ellipse cx="140" cy="35" rx="110" ry="20" fill="#1976D2"/>
                <!-- 平放的伤肢（关键！） -->
                <g transform="translate(160,30)" class="anim-limb-flat">
                  <rect x="0" y="0" width="130" height="22" rx="11" fill="#FFCCBC"/>
                  <!-- 夹板 -->
                  <rect x="10" y="-8" width="110" height="8" rx="2" fill="#8D6E63" stroke="#5D4037" stroke-width="0.5"/>
                  <rect x="10" y="22" width="110" height="8" rx="2" fill="#8D6E63" stroke="#5D4037" stroke-width="0.5"/>
                  <!-- 三角巾绑扎 -->
                  <g fill="#FFCDD2" stroke="#E57373" stroke-width="1">
                    <path d="M40 -8 L40 30 L35 30 L35 -8"/>
                    <path d="M70 -8 L70 30 L65 30 L65 -8"/>
                    <path d="M100 -8 L100 30 L95 30 L95 -8"/>
                  </g>
                  <!-- 伤口标记 -->
                  <circle cx="65" cy="11" r="10" fill="#FFCDD2" stroke="#D32F2F" stroke-width="2"/>
                  <text x="65" y="15" text-anchor="middle" font-size="12">⚠️</text>
                  <!-- 手 -->
                  <ellipse cx="135" cy="11" rx="12" ry="9" fill="#FFCCBC"/>
                </g>
                <!-- 正常腿 -->
                <rect x="40" y="55" width="18" height="35" rx="5" fill="#FFCCBC"/>
                <rect x="80" y="55" width="18" height="35" rx="5" fill="#FFCCBC"/>
              </g>
              <!-- 心脏高度标记 -->
              <g transform="translate(40,60)">
                <line x1="0" y1="0" x2="320" y2="0" stroke="#E53935" stroke-width="1.5" stroke-dasharray="6 4"/>
                <path d="M-10 -5 L0 0 L-10 5" fill="#E53935"/>
                <path d="M330 -5 L320 0 L330 5" fill="#E53935"/>
                <text x="160" y="-8" text-anchor="middle" font-size="11" fill="#E53935">❤ 心脏水平线</text>
              </g>
              <!-- 绿色勾 -->
              <g transform="translate(330,160)" class="anim-check">
                <circle r="20" fill="#4CAF50"/>
                <path d="M-8 0 L-2 8 L10 -6" stroke="white" stroke-width="4" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
              </g>
              <!-- 提示 -->
              <g transform="translate(200,265)">
                <text text-anchor="middle" font-size="12" fill="#2E7D32" font-weight="bold">✓ 伤肢与心脏同高，平放制动！</text>
              </g>
            </svg>
          </view>

          <!-- 第7关：加压包扎 - 3 层螺旋绷带 -->
          <view v-if="currentStep === 6" class="scene-wrap">
            <svg viewBox="0 0 400 280" class="scene-svg">
              <rect width="400" height="280" fill="#FBE9E7"/>
              <!-- 背景条纹 -->
              <g fill="#FFCCBC" opacity="0.2">
                <rect x="0" y="0" width="400" height="40"/>
                <rect x="0" y="80" width="400" height="40"/>
                <rect x="0" y="160" width="400" height="40"/>
                <rect x="0" y="240" width="400" height="40"/>
              </g>
              <!-- 手臂（带绷带） -->
              <g transform="translate(30,130)">
                <!-- 上臂 -->
                <rect x="0" y="35" width="150" height="45" rx="22" fill="#FFCCBC" stroke="#E57373" stroke-width="2"/>
                <!-- 肘部 -->
                <ellipse cx="150" cy="57" rx="15" ry="22" fill="#FFCCBC" stroke="#E57373" stroke-width="2"/>
                <!-- 前臂 -->
                <path d="M150 35 L300 55 L300 85 L150 79 Z" fill="#FFCCBC" stroke="#E57373" stroke-width="2"/>
                <!-- 手腕 -->
                <ellipse cx="300" cy="70" rx="12" ry="17" fill="#FFCCBC" stroke="#E57373" stroke-width="2"/>
                <!-- 手掌 -->
                <g transform="translate(310,55)">
                  <ellipse cx="12" cy="15" rx="14" ry="18" fill="#FFCCBC"/>
                  <rect x="-2" y="2" width="6" height="14" rx="3" fill="#FFCCBC"/>
                  <rect x="6" y="-2" width="6" height="16" rx="3" fill="#FFCCBC"/>
                  <rect x="14" y="-2" width="6" height="16" rx="3" fill="#FFCCBC"/>
                  <rect x="22" y="0" width="6" height="15" rx="3" fill="#FFCCBC"/>
                </g>
                <!-- 伤口位置（在肘部上方） -->
                <circle cx="100" cy="30" r="18" fill="#FFCDD2" stroke="#D32F2F" stroke-width="2" stroke-dasharray="4 2"/>
                <path d="M92 25 Q100 32 108 25 Q110 35 105 38 Q100 42 95 38 Q90 35 92 25" fill="#C62828"/>
                <!-- ⭐ 重点：3 层螺旋绷带缠绕动画 -->
                <g class="anim-bandage-group">
                  <!-- 第1层 - 松 -->
                  <path d="M60 45 Q90 30 120 45 Q150 60 180 45 Q210 30 240 45" 
                        stroke="#EF9A9A" stroke-width="14" fill="none" stroke-linecap="round" opacity="0.7" class="anim-bandage1"/>
                  <!-- 第2层 - 中 -->
                  <path d="M55 55 Q85 70 115 55 Q145 40 175 55 Q205 70 235 55" 
                        stroke="#E57373" stroke-width="12" fill="none" stroke-linecap="round" opacity="0.85" class="anim-bandage2"/>
                  <!-- 第3层 - 紧 -->
                  <path d="M50 65 Q80 50 110 65 Q140 80 170 65 Q200 50 230 65" 
                        stroke="#C62828" stroke-width="10" fill="none" stroke-linecap="round" opacity="0.9" class="anim-bandage3"/>
                  <!-- 螺旋线 -->
                  <path d="M50 35 Q80 55 110 35 Q140 15 170 35 Q200 55 230 35 Q260 15 290 35" 
                        stroke="#FFCDD2" stroke-width="3" fill="none" class="anim-bandage-spiral"/>
                  <!-- 绷带收尾 -->
                  <rect x="230" y="35" width="35" height="30" rx="3" fill="#FFCDD2" stroke="#E57373" stroke-width="1"/>
                  <text x="247" y="55" text-anchor="middle" font-size="10">🩹</text>
                </g>
                <!-- 箭头指示方向 -->
                <g class="anim-direction-arrow">
                  <path d="M30 80 L30 110 L20 100 M30 110 L40 100" stroke="#2196F3" stroke-width="3" fill="none" stroke-linecap="round"/>
                  <text x="15" y="125" font-size="10" fill="#2196F3">远</text>
                  <path d="M280 80 L280 110 L270 100 M280 110 L290 100" stroke="#2196F3" stroke-width="3" fill="none" stroke-linecap="round"/>
                  <text x="265" y="125" font-size="10" fill="#2196F3">近</text>
                </g>
              </g>
              <!-- 绷带卷 -->
              <g transform="translate(340,50)">
                <ellipse cx="0" cy="0" rx="28" ry="40" fill="#FFCDD2" stroke="#E57373" stroke-width="2"/>
                <ellipse cx="-5" cy="0" rx="22" ry="35" fill="#FFCCBC"/>
                <rect x="-33" y="-5" width="35" height="10" rx="3" fill="#EF9A9A"/>
                <text x="0" y="5" text-anchor="middle" font-size="22">🩹</text>
              </g>
              <!-- 加压指示线 -->
              <g transform="translate(50,120)" class="anim-pulse-line">
                <line x1="0" y1="0" x2="340" y2="0" stroke="#F06292" stroke-width="1" stroke-dasharray="4 3"/>
              </g>
              <text x="200" y="270" text-anchor="middle" font-size="11" fill="#C62828" font-weight="bold">🩹 从远心端向近心端螺旋缠绕，松紧适度</text>
            </svg>
          </view>

          <!-- 第8关：止痛处理 - 三个绝对禁止 -->
          <view v-if="currentStep === 7" class="scene-wrap">
            <svg viewBox="0 0 400 280" class="scene-svg">
              <rect width="400" height="280" fill="#FFEBEE"/>
              <!-- 标题条 -->
              <g transform="translate(200,25)">
                <rect x="-140" y="-20" width="280" height="40" rx="20" fill="#C62828"/>
                <text text-anchor="middle" y="5" font-size="15" fill="white" font-weight="bold">🚫 蛇咬伤后绝对不能做的 3 件事！</text>
              </g>
              <!-- 卡片1：止痛药 -->
              <g transform="translate(25,65)">
                <rect width="110" height="190" rx="12" fill="white" stroke="#EF5350" stroke-width="2" class="anim-card-shake"/>
                <rect x="0" y="0" width="110" height="25" rx="12" fill="#FFCDD2"/>
                <rect x="0" y="13" width="110" height="12" fill="#FFCDD2"/>
                <text x="55" y="18" text-anchor="middle" font-size="11" fill="#C62828" font-weight="bold">❌ 止痛药</text>
                <!-- 药瓶 -->
                <g transform="translate(55,60)" class="anim-shake-item">
                  <rect x="-15" y="0" width="30" height="35" rx="4" fill="#F5F5F5" stroke="#9E9E9E" stroke-width="1"/>
                  <rect x="-12" y="-8" width="24" height="12" rx="2" fill="#BDBDBD"/>
                  <circle cx="0" cy="18" r="8" fill="#E91E63"/>
                  <text x="0" y="22" text-anchor="middle" font-size="10" fill="white">💊</text>
                </g>
                <!-- 大叉 -->
                <path d="M15 55 L95 135 M95 55 L15 135" stroke="#D32F2F" stroke-width="8" stroke-linecap="round" class="anim-ban-x1"/>
                <!-- 原因 -->
                <text x="55" y="155" text-anchor="middle" font-size="10" fill="#555">抑制呼吸中枢</text>
                <text x="55" y="170" text-anchor="middle" font-size="9" fill="#999">吗啡/杜冷丁</text>
                <text x="55" y="185" text-anchor="middle" font-size="9" fill="#C62828" font-weight="bold">→ 危险！</text>
              </g>
              <!-- 卡片2：酒精 -->
              <g transform="translate(145,65)">
                <rect width="110" height="190" rx="12" fill="white" stroke="#EF5350" stroke-width="2" class="anim-card-shake2"/>
                <rect x="0" y="0" width="110" height="25" rx="12" fill="#FFCDD2"/>
                <rect x="0" y="13" width="110" height="12" fill="#FFCDD2"/>
                <text x="55" y="18" text-anchor="middle" font-size="11" fill="#C62828" font-weight="bold">❌ 酒 精</text>
                <!-- 酒瓶 -->
                <g transform="translate(55,60)" class="anim-shake-item">
                  <ellipse cx="0" cy="15" rx="14" ry="25" fill="#8D6E63" stroke="#5D4037" stroke-width="1"/>
                  <rect x="-5" y="-8" width="10" height="12" fill="#5D4037"/>
                  <ellipse cx="0" cy="15" rx="10" ry="18" fill="#A1887F"/>
                  <text x="0" y="20" text-anchor="middle" font-size="12">🍺</text>
                </g>
                <path d="M15 55 L95 135 M95 55 L15 135" stroke="#D32F2F" stroke-width="8" stroke-linecap="round" class="anim-ban-x2"/>
                <text x="55" y="155" text-anchor="middle" font-size="10" fill="#555">扩张血管</text>
                <text x="55" y="170" text-anchor="middle" font-size="9" fill="#999">加速毒液吸收</text>
                <text x="55" y="185" text-anchor="middle" font-size="9" fill="#C62828" font-weight="bold">→ 扩散！</text>
              </g>
              <!-- 卡片3：冰敷 -->
              <g transform="translate(265,65)">
                <rect width="110" height="190" rx="12" fill="white" stroke="#EF5350" stroke-width="2" class="anim-card-shake3"/>
                <rect x="0" y="0" width="110" height="25" rx="12" fill="#FFCDD2"/>
                <rect x="0" y="13" width="110" height="12" fill="#FFCDD2"/>
                <text x="55" y="18" text-anchor="middle" font-size="11" fill="#C62828" font-weight="bold">❌ 冰 敷</text>
                <!-- 冰块 -->
                <g transform="translate(55,60)" class="anim-shake-item">
                  <polygon points="-15,30 -12,0 12,0 15,30" fill="#B3E5FC" stroke="#0288D1" stroke-width="1" opacity="0.8"/>
                  <polygon points="-10,28 -8,5 8,5 10,28" fill="#E1F5FE"/>
                  <text x="0" y="20" text-anchor="middle" font-size="12">🧊</text>
                </g>
                <path d="M15 55 L95 135 M95 55 L15 135" stroke="#D32F2F" stroke-width="8" stroke-linecap="round" class="anim-ban-x3"/>
                <text x="55" y="155" text-anchor="middle" font-size="10" fill="#555">血管收缩</text>
                <text x="55" y="170" text-anchor="middle" font-size="9" fill="#999">加重组织缺血</text>
                <text x="55" y="185" text-anchor="middle" font-size="9" fill="#C62828" font-weight="bold">→ 坏死！</text>
              </g>
            </svg>
          </view>

          <!-- 第9关：禁忌清单 - 左右对比+对勾叉 -->
          <view v-if="currentStep === 8" class="scene-wrap">
            <svg viewBox="0 0 400 280" class="scene-svg">
              <rect width="400" height="280" fill="#FCE4EC"/>
              <!-- 标题 -->
              <text x="200" y="30" text-anchor="middle" font-size="16" fill="#880E4F" font-weight="bold">📋 急救清单：记住这些！</text>
              <!-- 左边：千万不要（红） -->
              <g transform="translate(15,50)">
                <rect width="175" height="215" rx="12" fill="#FFEBEE" stroke="#E57373" stroke-width="2"/>
                <rect x="0" y="0" width="175" height="30" rx="12" fill="#C62828"/>
                <rect x="0" y="20" width="175" height="10" fill="#C62828"/>
                <text x="87" y="22" text-anchor="middle" font-size="13" fill="white" font-weight="bold">🚫 千万不要</text>
                <!-- 条目 -->
                <g transform="translate(15,45)">
                  <circle r="8" fill="#EF5350"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✕</text>
                  <text x="18" y="4" font-size="11" fill="#B71C1C">切开伤口放血</text>
                </g>
                <g transform="translate(15,70)">
                  <circle r="8" fill="#EF5350"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✕</text>
                  <text x="18" y="4" font-size="11" fill="#B71C1C">用嘴吸出毒液</text>
                </g>
                <g transform="translate(15,95)">
                  <circle r="8" fill="#EF5350"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✕</text>
                  <text x="18" y="4" font-size="11" fill="#B71C1C">敷草药/牙膏</text>
                </g>
                <g transform="translate(15,120)">
                  <circle r="8" fill="#EF5350"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✕</text>
                  <text x="18" y="4" font-size="11" fill="#B71C1C">抬高伤肢跑步</text>
                </g>
                <g transform="translate(15,145)">
                  <circle r="8" fill="#EF5350"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✕</text>
                  <text x="18" y="4" font-size="11" fill="#B71C1C">喝白酒/咖啡</text>
                </g>
                <g transform="translate(15,175)">
                  <circle r="8" fill="#EF5350"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✕</text>
                  <text x="18" y="4" font-size="11" fill="#B71C1C">自行停药观察</text>
                </g>
                <text x="87" y="210" text-anchor="middle" font-size="9" fill="#999">⚠ 这些都会加速毒液扩散！</text>
              </g>
              <!-- 右边：正确做法（绿） -->
              <g transform="translate(210,50)">
                <rect width="175" height="215" rx="12" fill="#E8F5E9" stroke="#81C784" stroke-width="2"/>
                <rect x="0" y="0" width="175" height="30" rx="12" fill="#2E7D32"/>
                <rect x="0" y="20" width="175" height="10" fill="#2E7D32"/>
                <text x="87" y="22" text-anchor="middle" font-size="13" fill="white" font-weight="bold">✅ 正确做法</text>
                <!-- 条目 -->
                <g transform="translate(15,45)">
                  <circle r="8" fill="#4CAF50"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✓</text>
                  <text x="18" y="4" font-size="11" fill="#1B5E20">冷静慢行远离</text>
                </g>
                <g transform="translate(15,70)">
                  <circle r="8" fill="#4CAF50"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✓</text>
                  <text x="18" y="4" font-size="11" fill="#1B5E20">记住蛇的样子</text>
                </g>
                <g transform="translate(15,95)">
                  <circle r="8" fill="#4CAF50"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✓</text>
                  <text x="18" y="4" font-size="11" fill="#1B5E20">伤肢平放制动</text>
                </g>
                <g transform="translate(15,120)">
                  <circle r="8" fill="#4CAF50"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✓</text>
                  <text x="18" y="4" font-size="11" fill="#1B5E20">大量喝水排尿</text>
                </g>
                <g transform="translate(15,145)">
                  <circle r="8" fill="#4CAF50"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✓</text>
                  <text x="18" y="4" font-size="11" fill="#1B5E20">立刻拨打 120</text>
                </g>
                <g transform="translate(15,175)">
                  <circle r="8" fill="#4CAF50"/>
                  <text x="-3" y="4" text-anchor="middle" font-size="10" fill="white">✓</text>
                  <text x="18" y="4" font-size="11" fill="#1B5E20">尽快打抗蛇毒血清</text>
                </g>
                <text x="87" y="210" text-anchor="middle" font-size="9" fill="#2E7D32">🏆 生命第一，不要逞强！</text>
              </g>
              <!-- 中间心跳线 -->
              <path d="M195 60 L200 70 L205 60 L210 70 L215 60 L218 70" 
                    stroke="#E91E63" stroke-width="2" fill="none" class="anim-heartbeat"/>
            </svg>
          </view>
        </view>        </svg>
          </view>
        </view>  <!-- 第4关：模拟拨120 -->
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

/* ===== 🎬 每关动画舞台 ===== */
.anim-stage { height: 220rpx; display: flex; align-items: center; justify-content: center; margin: 20rpx 0; background: linear-gradient(135deg, #FFF3E0, #FFECB3); border-radius: 16rpx; position: relative; overflow: hidden; }
.level-emoji-wrap { position: relative; width: 100rpx; height: 100rpx; }
.level-emoji-anim { font-size: 64rpx; animation: pulseEmoji 1.5s ease-in-out infinite; display: block; }
.level-emoji-ring { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: 3rpx solid #FF9800; border-radius: 50%; animation: ringPulse 2s ease-out infinite; }
@keyframes pulseEmoji { 0%,100% { transform: scale(1) } 50% { transform: scale(1.2) } }
@keyframes ringPulse { 0% { transform: scale(1); opacity: 0.8 } 100% { transform: scale(1.8); opacity: 0 } }

/* 第1关 脱离 */
.anim-escape { width: 100%; height: 100%; position: relative; }
.anim-person { position: absolute; font-size: 70rpx; left: 60rpx; bottom: 60rpx; }
.anim-snake { position: absolute; font-size: 60rpx; right: 60rpx; bottom: 70rpx; }
.anim-arrows { position: absolute; font-size: 36rpx; animation: arrowFly 0.6s linear infinite; }
.anim-arrow1 { top: 50rpx; animation-delay: 0s }
.anim-arrow2 { top: 100rpx; animation-delay: 0.2s }
.anim-arrow3 { top: 150rpx; animation-delay: 0.4s }
.anim-ground { position: absolute; bottom: 40rpx; left: 0; width: 100%; height: 8rpx; background: #795548; border-radius: 4rpx; }
.anim-run { animation: runBounce 0.4s ease-in-out infinite; }
.anim-snake-warn { animation: snakeWarn 0.8s ease-in-out infinite; }
@keyframes arrowFly { 0% { transform: translateX(-150rpx); opacity: 0 } 50% { opacity: 1 } 100% { transform: translateX(200rpx); opacity: 0 } }
@keyframes runBounce { 0%,100% { transform: translateY(0) } 50% { transform: translateY(-15rpx) } }
@keyframes snakeWarn { 0%,100% { transform: rotate(-8deg) } 50% { transform: rotate(8deg) } }

/* 第2关 辨认蛇种 SVG */
.anim-identify { display: flex; gap: 30rpx; width: 100%; padding: 0 30rpx; }
.anim-bad-snake, .anim-good-snake { flex: 1; display: flex; flex-direction: column; align-items: center; }
.anim-svg { width: 140rpx; height: 80rpx; }
.anim-tooth { animation: toothPulse 0.8s ease-in-out infinite; transform-origin: center; }
.anim-tri-head { animation: badWarn 0.6s ease-in-out infinite; }
.anim-round-head { animation: goodFloat 2s ease-in-out infinite; }
.anim-label { font-size: 20rpx; margin-top: 8rpx; font-weight: bold; }
.anim-label.bad { color: #D32F2F; }
.anim-label.good { color: #2E7D32; }
@keyframes toothPulse { 0%,100% { transform: scaleY(1) } 50% { transform: scaleY(1.3) } }
@keyframes badWarn { 0%,100% { fill: #D32F2F } 50% { fill: #FFCDD2 } }
@keyframes goodFloat { 0%,100% { transform: translateY(0) } 50% { transform: translateY(-3rpx) } }

/* 第3关 深呼吸光圈 */
.anim-calm { position: relative; display: flex; align-items: center; justify-content: center; width: 100%; height: 100%; }
.anim-face { font-size: 80rpx; z-index: 10; animation: calmBreath 3s ease-in-out infinite; }
.anim-breath-ring { position: absolute; width: 120rpx; height: 120rpx; border: 4rpx solid #81C784; border-radius: 50%; }
.ring1 { animation: ringExpand 3s ease-out infinite; }
.ring2 { animation: ringExpand 3s ease-out infinite 1s; }
.ring3 { animation: ringExpand 3s ease-out infinite 2s; }
.anim-breath-text { position: absolute; bottom: 30rpx; font-size: 22rpx; color: #388E3C; }
@keyframes calmBreath { 0%,100% { transform: scale(1) } 50% { transform: scale(1.1) } }
@keyframes ringExpand { 0% { transform: scale(0.4); opacity: 0.8 } 100% { transform: scale(1.2); opacity: 0 } }

/* 第4关 手机震动 */
.anim-phone-call { position: relative; display: flex; align-items: center; justify-content: center; width: 100%; height: 100%; }
.anim-phone { font-size: 100rpx; }
.anim-vibrate { animation: phoneVibrate 0.15s linear infinite; }
.anim-waves { position: absolute; font-size: 40rpx; }
.wave1 { right: 80rpx; top: 60rpx; animation: wavePulse 1s ease-out infinite; }
.wave2 { right: 80rpx; top: 100rpx; animation: wavePulse 1s ease-out infinite 0.3s; }
.wave3 { right: 80rpx; top: 140rpx; animation: wavePulse 1s ease-out infinite 0.6s; }
@keyframes phoneVibrate { 0%,100% { transform: translateX(0) } 25% { transform: translateX(-4rpx) } 75% { transform: translateX(4rpx) } }
@keyframes wavePulse { 0% { transform: translateX(0); opacity: 1 } 100% { transform: translateX(80rpx); opacity: 0 } }

/* 第5关 水滴落下 */
.anim-wash { position: relative; width: 100%; height: 100%; }
.anim-drop { position: absolute; font-size: 40rpx; top: 10rpx; }
.anim-drop1 { left: 60rpx; animation: dropFall 1.2s ease-in infinite; }
.anim-drop2 { left: 130rpx; animation: dropFall 1.2s ease-in infinite 0.4s; }
.anim-drop3 { left: 200rpx; animation: dropFall 1.2s ease-in infinite 0.8s; }
.anim-wound { position: absolute; bottom: 50rpx; left: 50%; transform: translateX(-50%); display: flex; flex-direction: column; align-items: center; }
.anim-wound-mark { font-size: 60rpx; animation: woundPulse 1s ease-in-out infinite; }
.anim-wound-label { font-size: 18rpx; color: #1565C0; margin-top: 5rpx; }
@keyframes dropFall { 0% { top: 10rpx; opacity: 1 } 80% { top: 130rpx; opacity: 1 } 100% { top: 130rpx; opacity: 0 } }
@keyframes woundPulse { 0%,100% { transform: scale(1) } 50% { transform: scale(1.15) } }

/* 第6关 伤肢制动 */
.anim-limb { position: relative; display: flex; flex-direction: column; align-items: center; justify-content: center; width: 100%; height: 100%; }
.anim-limb-wrap { display: flex; align-items: center; gap: 10rpx; animation: limbFloat 2s ease-in-out infinite; }
.anim-arm { font-size: 70rpx; }
.anim-arrow.right { font-size: 50rpx; }
.anim-limb-label { font-size: 22rpx; color: #2E7D32; margin-top: 20rpx; font-weight: bold; }
@keyframes limbFloat { 0%,100% { transform: translateX(0) } 50% { transform: translateX(10rpx) } }

/* 第7关 绷带缠绕 */
.anim-bandage-scene { display: flex; align-items: center; justify-content: center; width: 100%; height: 100%; }
.anim-bandage-target { position: relative; width: 200rpx; height: 100rpx; }
.anim-bandage-arm { font-size: 80rpx; position: absolute; top: 10rpx; left: 40rpx; }
.anim-bandage-wrap { position: absolute; left: 20rpx; height: 14rpx; background: #E57373; border-radius: 3rpx; animation: bandageWrap 1.5s ease-out infinite; }
.wrap1 { top: 30rpx; width: 160rpx; animation-delay: 0s; }
.wrap2 { top: 55rpx; width: 160rpx; animation-delay: 0.5s; }
.wrap3 { top: 80rpx; width: 160rpx; animation-delay: 1s; }
.anim-wound-mark-small { position: absolute; right: 30rpx; top: 45rpx; font-size: 28rpx; animation: woundPulse 1s ease-in-out infinite; }
@keyframes bandageWrap { 0% { transform: translateX(-160rpx); opacity: 0 } 30% { opacity: 1 } 100% { transform: translateX(0); opacity: 1 } }

/* 第8关 止痛禁止 */
.anim-pain { display: flex; gap: 20rpx; width: 100%; padding: 0 30rpx; }
.anim-ban { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 8rpx; }
.anim-ban-item { font-size: 60rpx; }
.anim-shake { animation: shake 0.4s ease-in-out infinite; }
.anim-ban-x { font-size: 40rpx; color: #D32F2F; animation: banPulse 1s ease-in-out infinite; }
.anim-ban-label { font-size: 18rpx; color: #D32F2F; font-weight: bold; }
@keyframes shake { 0%,100% { transform: rotate(0) } 25% { transform: rotate(-8deg) } 75% { transform: rotate(8deg) } }
@keyframes banPulse { 0%,100% { transform: scale(1) } 50% { transform: scale(1.3) } }

/* 第9关 禁忌清单 */
.anim-taboo { display: flex; flex-wrap: wrap; gap: 16rpx; width: 100%; padding: 0 20rpx; }
.anim-taboo-item { width: calc(50% - 8rpx); display: flex; align-items: center; gap: 10rpx; padding: 14rpx; border-radius: 12rpx; animation: tabooFloat 2s ease-in-out infinite; }
.anim-taboo-item.bad { background: #FFEBEE; border: 2rpx solid #EF5350; }
.anim-taboo-item.good { background: #E8F5E9; border: 2rpx solid #66BB6A; animation-delay: 1s; }
.anim-taboo-icon { font-size: 36rpx; }
.anim-taboo-text { font-size: 22rpx; font-weight: bold; }
.anim-taboo-item.bad .anim-taboo-text { color: #C62828; }
.anim-taboo-item.good .anim-taboo-text { color: #2E7D32; }
@keyframes tabooFloat { 0%,100% { transform: translateY(0) } 50% { transform: translateY(-6rpx) } }

/* ===== 🎨 SVG 场景插图专用样式 ===== */
.scene-stage { width: 100%; margin: 16rpx 0; border-radius: 16rpx; overflow: hidden; background: #FFF; box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.08); }
.scene-wrap { width: 100%; }
.scene-svg { width: 100%; height: auto; display: block; }

/* 第1关动画 */
.anim-person-run { animation: personRun 1s ease-in-out infinite; transform-origin: center; }
.anim-snake-warn { animation: snakeWarn 1.2s ease-in-out infinite; transform-origin: 20px 30px; }
.anim-warn-bubble { animation: bubblePop 1.5s ease-in-out infinite; }
.anim-back-arrow { animation: backArrowPulse 0.8s ease-in-out infinite; }
@keyframes personRun { 0%,100% { transform: translateY(0) translateX(0) } 50% { transform: translateY(-4px) translateX(-3px) } }
@keyframes snakeWarn { 0%,100% { transform: rotate(-3deg) translateX(0) } 50% { transform: rotate(3deg) translateX(2px) } }
@keyframes bubblePop { 0%,100% { transform: scale(1) translateY(0) } 50% { transform: scale(1.15) translateY(-3px) } }
@keyframes backArrowPulse { 0%,100% { opacity: 1 } 50% { opacity: 0.5 } }

/* 第2关动画 */
.anim-tri-warn { animation: triWarn 0.8s ease-in-out infinite; }
.anim-tooth1 { animation: toothPulse 0.6s ease-in-out infinite; transform-origin: 125px 50px; }
.anim-tooth2 { animation: toothPulse 0.6s ease-in-out infinite 0.3s; transform-origin: 135px 50px; }
.anim-round-float { animation: roundFloat 2s ease-in-out infinite; }
@keyframes triWarn { 0%,100% { fill: #B71C1C } 50% { fill: #FF5252 } }
@keyframes toothPulse { 0%,100% { transform: scaleY(1) } 50% { transform: scaleY(1.4) } }
@keyframes roundFloat { 0%,100% { transform: translateY(0) } 50% { transform: translateY(-2px) } }

/* 第3关动画 */
.anim-ring1 { animation: ringExpand 3s ease-out infinite; transform-origin: 30px 25px; }
.anim-ring2 { animation: ringExpand 3s ease-out infinite 1s; transform-origin: 30px 25px; }
.anim-ring3 { animation: ringExpand 3s ease-out infinite 2s; transform-origin: 30px 25px; }
.anim-breathe-text { animation: breatheText 3s ease-in-out infinite; }
@keyframes ringExpand { 0% { transform: scale(0.4); opacity: 0.8 } 100% { transform: scale(1.2); opacity: 0 } }
@keyframes breatheText { 0%,100% { transform: scale(1); letter-spacing: 2px } 50% { transform: scale(1.1); letter-spacing: 6px } }

/* 第4关动画 */
.anim-emergency-pulse { animation: emergencyPulse 1s ease-in-out infinite; transform-origin: center; }
.anim-phone-vibrate { animation: phoneVibrate 0.12s linear infinite; transform-origin: center; }
.anim-digits { animation: digitsGlow 1s ease-in-out infinite; }
.anim-signal-wave { animation: signalExpand 2s ease-out infinite; transform-origin: center; }
@keyframes emergencyPulse { 0%,100% { transform: scale(1) } 50% { transform: scale(1.08) } }
@keyframes phoneVibrate { 0%,100% { transform: translateX(0) translateY(0) } 25% { transform: translateX(-2px) translateY(1px) } 75% { transform: translateX(2px) translateY(-1px) } }
@keyframes digitsGlow { 0%,100% { fill: #00E676 } 50% { fill: #69F0AE } }
@keyframes signalExpand { 0% { opacity: 1; transform: scale(0.8) } 100% { opacity: 0; transform: scale(1.3) } }

/* 第5关动画 */
.anim-water-drop1 { animation: dropFall 1.2s ease-in infinite; transform-origin: center; }
.anim-water-drop2 { animation: dropFall 1.2s ease-in infinite 0.4s; transform-origin: center; }
.anim-water-drop3 { animation: dropFall 1.2s ease-in infinite 0.8s; transform-origin: center; }
.anim-wound-pulse { animation: woundPulse 1.2s ease-in-out infinite; transform-origin: center; }
.anim-water-flow { animation: waterFlow 1.5s linear infinite; stroke-dasharray: 200; stroke-dashoffset: 200; }
@keyframes dropFall { 0% { transform: translateY(0); opacity: 1 } 70% { transform: translateY(60px); opacity: 1 } 100% { transform: translateY(60px); opacity: 0 } }
@keyframes woundPulse { 0%,100% { transform: scale(1); fill: #FFCDD2 } 50% { transform: scale(1.1); fill: #FF8A80 } }
@keyframes waterFlow { 0% { stroke-dashoffset: 200 } 100% { stroke-dashoffset: 0 } }

/* 第6关动画 */
.anim-limb-flat { animation: limbFloat 2s ease-in-out infinite; transform-origin: center; }
@keyframes limbFloat { 0%,100% { transform: translateX(0) } 50% { transform: translateX(3px) } }

/* 第7关动画 */
.anim-bandage1 { animation: bandageWrap 1.5s ease-out infinite; transform-origin: center; }
.anim-bandage2 { animation: bandageWrap 1.5s ease-out infinite 0.5s; transform-origin: center; }
.anim-bandage-spiral { animation: spiralMove 2s linear infinite; stroke-dasharray: 80; stroke-dashoffset: 80; }
@keyframes bandageWrap { 0% { transform: scaleX(0); opacity: 0 } 60% { opacity: 1 } 100% { transform: scaleX(1); opacity: 0.85 } }
@keyframes spiralMove { 0% { stroke-dashoffset: 80 } 100% { stroke-dashoffset: 0 } }

/* 第8关动画 */
.anim-ban-x1 { animation: banShake 1.5s ease-in-out infinite; transform-origin: center; }
.anim-ban-x2 { animation: banShake 1.5s ease-in-out infinite 0.3s; transform-origin: center; }
.anim-ban-x3 { animation: banShake 1.5s ease-in-out infinite 0.6s; transform-origin: center; }
@keyframes banShake { 0%,100% { transform: scale(1) rotate(0) } 25% { transform: scale(1.1) rotate(5deg) } 75% { transform: scale(1.1) rotate(-5deg) } }

/* ===== 补充动画：新场景元素 ===== */
/* 太阳 */
.anim-sun { animation: sunGlow 3s ease-in-out infinite; transform-origin: center; }
@keyframes sunGlow { 0%,100% { fill: #FFD54F } 50% { fill: #FFB300 } }

/* 野花摇摆 */
.anim-flower1 { animation: flowerSway 2.5s ease-in-out infinite; transform-origin: bottom center; }
.anim-flower2 { animation: flowerSway 2.5s ease-in-out infinite 1.2s; transform-origin: bottom center; }
@keyframes flowerSway { 0%,100% { transform: rotate(-3deg) } 50% { transform: rotate(3deg) } }

/* 小鸟飞 */
.anim-bird { animation: birdFly 4s ease-in-out infinite; }
.anim-bird2 { animation: birdFly 4s ease-in-out infinite 2s; }
@keyframes birdFly { 0%,100% { transform: translate(180px, 60px) } 50% { transform: translate(200px, 50px) } }

/* 叶子飘 */
.anim-leaf1 { animation: leafFall 6s linear infinite; transform-origin: center; }
.anim-leaf2 { animation: leafFall 8s linear infinite 2s; transform-origin: center; }
.anim-leaf3 { animation: leafFall 7s linear infinite 4s; transform-origin: center; }
@keyframes leafFall { 0% { transform: translate(0,0) rotate(0); opacity: 1 } 100% { transform: translate(300px,200px) rotate(360deg); opacity: 0 } }

/* 蛇吐信 */
.anim-tongue { animation: tongueOut 1.5s ease-in-out infinite; transform-origin: 138px 2px; }
@keyframes tongueOut { 0%,100% { transform: scaleX(0) } 50% { transform: scaleX(1) } }

/* 毒汁滴 */
.anim-venom-drop { animation: venomFall 2s ease-in infinite; transform-origin: center; }
@keyframes venomFall { 0% { transform: translateY(0); opacity: 1 } 100% { transform: translateY(20px); opacity: 0 } }

/* 救护车 */
.anim-ambulance { animation: ambulanceMove 4s ease-in-out infinite; transform-origin: center; }
@keyframes ambulanceMove { 0%,100% { transform: translate(300px,180px) } 50% { transform: translate(280px,180px) } }

/* 警报灯闪烁 */
.anim-siren { animation: sirenFlash 0.4s ease-in-out infinite; transform-origin: center; }
@keyframes sirenFlash { 0%,100% { fill: #FFEB3B } 50% { fill: #FF5252 } }

/* 车灯闪 */
.anim-flash { animation: flashBeam 1s ease-in-out infinite; transform-origin: left center; }
@keyframes flashBeam { 0%,100% { opacity: 0.6 } 50% { opacity: 0.2 } }

/* 水龙头把手 */
.anim-handle { animation: handleTurn 3s ease-in-out infinite; transform-origin: 60px 50px; }
@keyframes handleTurn { 0%,100% { transform: rotate(0) } 50% { transform: rotate(-10deg) } }

/* 水花 */
.anim-splash { animation: splashPulse 1.2s ease-out infinite; transform-origin: center; }
@keyframes splashPulse { 0%,100% { transform: scale(1); opacity: 0.7 } 50% { transform: scale(1.3); opacity: 0.4 } }

/* 绿色勾弹出 */
.anim-check { animation: checkPop 2.5s ease-in-out infinite; transform-origin: center; }
@keyframes checkPop { 0%,100% { transform: scale(1) } 50% { transform: scale(1.15) } }

/* 绷带第3层 */
.anim-bandage3 { animation: bandageWrap 1.5s ease-out infinite 1s; transform-origin: center; }

/* 方向箭头 */
.anim-direction-arrow path { animation: arrowBounce 1.5s ease-in-out infinite; }
@keyframes arrowBounce { 0%,100% { transform: translateX(0) } 50% { transform: translateX(3px) } }

/* 禁止卡片摇晃 */
.anim-card-shake, .anim-card-shake2, .anim-card-shake3 { animation: cardWobble 3s ease-in-out infinite; transform-origin: center; }
.anim-card-shake2 { animation-delay: 0.5s; }
.anim-card-shake3 { animation-delay: 1s; }
@keyframes cardWobble { 0%,100% { transform: rotate(-1deg) } 50% { transform: rotate(1deg) } }

/* 心跳线 */
.anim-heartbeat { animation: heartBeat 1.5s ease-in-out infinite; transform-origin: left center; }
@keyframes heartBeat { 0%,100% { stroke-dashoffset: 0; } 50% { stroke-dashoffset: -20; } }

/* 通话中文字 */
.anim-calling-text { animation: textBlink 1.2s ease-in-out infinite; }
@keyframes textBlink { 0%,100% { opacity: 1 } 50% { opacity: 0.5 } }

/* 绷带方向指示脉冲线 */
.anim-pulse-line { animation: linePulse 1.5s ease-in-out infinite; }
@keyframes linePulse { 0%,100% { opacity: 0.3 } 50% { opacity: 1 } }

/* 气泡文字 */
.anim-arrow-text { animation: textBlink 1.5s ease-in-out infinite; }

/* 腿摆动 */
.anim-leg1 { animation: legSwing 0.5s ease-in-out infinite; transform-origin: top center; }
.anim-leg2 { animation: legSwing 0.5s ease-in-out infinite 0.25s; transform-origin: top center; }
@keyframes legSwing { 0%,100% { transform: rotate(0) } 50% { transform: rotate(-15deg) } }

/* ===== SVG 动画核心修复：让 transform 在 SVG 元素上工作 ===== */
.scene-svg circle, .scene-svg g, .scene-svg path, .scene-svg rect, 
.scene-svg ellipse, .scene-svg polygon, .scene-svg line, .scene-svg text {
  transform-box: fill-box;
}

/* 强制所有动画类都有 transform-box */
.anim-ring1, .anim-ring2, .anim-ring3, .anim-ring4,
.anim-signal-wave circle,
.anim-water-drop1, .anim-water-drop2, .anim-water-drop3,
.anim-flower1, .anim-flower2,
.anim-leaf1, .anim-leaf2, .anim-leaf3,
.anim-person-run, .anim-snake-warn, .anim-warn-bubble,
.anim-tongue, .anim-venom-drop,
.anim-tri-warn, .anim-tooth1, .anim-tooth2, .anim-round-float,
.anim-emergency-pulse, .anim-phone-vibrate,
.anim-ambulance, .anim-siren,
.anim-wound-pulse, .anim-splash, .anim-limb-flat,
.anim-bandage1, .anim-bandage2, .anim-bandage3, .anim-bandage-spiral,
.anim-ban-x1, .anim-ban-x2, .anim-ban-x3,
.anim-card-shake, .anim-card-shake2, .anim-card-shake3,
.anim-handle, .anim-bird, .anim-bird2,
.anim-check, .anim-heartbeat, .anim-sun,
.anim-leg1, .anim-leg2, .anim-flash, .anim-digits,
.anim-back-arrow, .anim-arrow-text, .anim-pulse-line,
.anim-direction-arrow path, .anim-ban-shake-item {
  transform-box: fill-box !important;
}

/* ringExpand 的 transform-origin 改为 center */
@keyframes ringExpand { 
  0% { transform: scale(0.3); opacity: 0.9 } 
  100% { transform: scale(2.0); opacity: 0 } 
}
</style>