<script>
import { setTheme as _setTheme, getThemeConfig, onThemeChange } from '@/utils/theme.js'
import { speak as _speak, isSpeechOn as _isSpeechOn } from '@/utils/dialect.js'
import { sfxClick, sfxCorrect, sfxWrong, sfxSnake, sfxWin, sfxEmergency, sfxDone, playBGM, stopBGM } from '@/utils/audio.js'

let showing401 = false

function is401(res) {
  if (!res) return false
  if (res.statusCode === 401) return true
  try {
    const body = typeof res.data === 'string' ? JSON.parse(res.data) : res.data
    return body && body.code === 401
  } catch (e) { return false }
}

export default {
  // 全局注册组件
  components: {
    SnakeAnim: () => import('@/components/SnakeAnim.vue'),
    FirstAidAnim: () => import('@/components/FirstAidAnim.vue'),
    GlobalTools: () => import('@/components/GlobalTools.vue')
  },
  
  // 全局 provide 给所有页面用
  provide() {
    return {
      // 主题
      theme: getThemeConfig(),
      setTheme: _setTheme,
      // 音效（H5 only）
      sfxClick, sfxCorrect, sfxWrong, sfxSnake, sfxWin, sfxEmergency, sfxDone,
      playBGM, stopBGM,
      // 语音
      speak: _speak
    }
  },

  onLaunch: function() {
    console.log('🚀 Snake Aid App Launch')

    uni.addInterceptor('request', {
      invoke(args) {
        const token = uni.getStorageSync('token')
        if (token) {
          args.header = args.header || {}
          args.header['Authorization'] = 'Bearer ' + token
        }
        return args
      },
      complete(res) {
        if (is401(res) && !showing401) {
          showing401 = true
          uni.removeStorageSync('token')
          uni.removeStorageSync('user')
          uni.removeStorageSync('appProfile')
          uni.showModal({
            title: '登录已过期',
            content: '登录状态已失效，是否重新登录？',
            confirmText: '去登录',
            cancelText: '取消',
            success: (r) => {
              showing401 = false
              if (r.confirm) {
                uni.navigateTo({ url: '/pages/mine/login' })
              }
            }
          })
        }
      },
      fail(err) { console.error('[request fail]', err) }
    })

    // 监听主题变化，应用到 CSS 变量
    // #ifdef H5
    onThemeChange((cfg) => {
      try {
        document.documentElement.style.setProperty('--font-scale', cfg.fontScale)
        document.documentElement.style.setProperty('--primary-color', cfg.primaryColor)
        document.documentElement.style.setProperty('--bg-color', cfg.bgColor)
      } catch (e) {}
    })
    // #endif
  },
  onShow: function() { console.log('App Show') },
  onHide: function() { console.log('App Hide') }
}
</script>

<style>
/* ===== 全局基础样式 ===== */

/* CSS 变量（响应主题） */
:root {
  --font-scale: 1.0;
  --primary-color: #E53935;
  --bg-color: #FAFAFA;
}

/* 基础字号（合同要求：正文 30rpx 起，大字模式放大） */
page {
  font-size: calc(28rpx * var(--font-scale, 1.0));
  background: var(--bg-color, #FAFAFA);
  color: #212121;
  -webkit-font-smoothing: antialiased;
}

/* 老人模式：更高对比度 */
.elder-mode {
  --font-scale: 1.4;
}

.elder-mode page {
  font-weight: 500;
}

/* 统一卡片 */
.card {
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.08);
}

/* 统一按钮 */
.btn-primary {
  background: var(--primary-color, #E53935);
  color: #fff;
  border-radius: 50rpx;
  padding: 20rpx 48rpx;
  font-size: calc(30rpx * var(--font-scale, 1.0));
  font-weight: bold;
  text-align: center;
}

/* 合同 UI 规范：字号定义
 * mini: 24rpx - 辅助说明
 * small: 26rpx - 次要文字
 * body: 30rpx - 正文（WCAG AA）
 * title: 34rpx - 小标题
 * heading: 40rpx - 标题（WCAG AAA）
 * big: 48rpx - 大标题
 * huge: 60rpx - 巨大标题
 */
.text-mini { font-size: calc(24rpx * var(--font-scale, 1.0)); }
.text-small { font-size: calc(26rpx * var(--font-scale, 1.0)); }
.text-body { font-size: calc(30rpx * var(--font-scale, 1.0)); }
.text-title { font-size: calc(34rpx * var(--font-scale, 1.0)); font-weight: 600; }
.text-heading { font-size: calc(40rpx * var(--font-scale, 1.0)); font-weight: bold; }
.text-big { font-size: calc(48rpx * var(--font-scale, 1.0)); font-weight: bold; }
.text-huge { font-size: calc(60rpx * var(--font-scale, 1.0)); font-weight: bold; }

/* 合同要求：单任务线性导航时的聚焦高亮 */
.focus-ring:focus {
  outline: 4rpx solid #2196F3;
  outline-offset: 4rpx;
  border-radius: 8rpx;
}
</style>
