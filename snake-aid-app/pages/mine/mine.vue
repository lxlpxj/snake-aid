<template>
        <view class="mine">
                <!-- 用户信息卡片 -->
                <view class="user-card" @click="goLogin">
                        <view class="avatar">🐍</view>
                        <view class="user-info">
                                <view class="nickname">{{ nickname }}</view>
                                <view class="phone">{{ phone }}</view>
                        </view>
                        <text v-if="!isLogin" class="login-hint">点击登录 ›</text>
                        <text v-else class="login-hint" @click.stop="doLogout">退出</text>
                </view>

                <!-- ======== 积分/等级卡片（登录后显示） ======== -->
                <view v-if="isLogin" class="points-card">
                        <view class="pc-header">
                                <view class="pc-title">
                                        <text class="pc-icon">{{ profile.levelIcon || '🥚' }}</text>
                                        <text class="pc-level">Lv.{{ profile.level || 1 }}</text>
                                        <text class="pc-lv-title">{{ profile.levelTitle || '萌新学徒' }}</text>
                                </view>
                                <view class="pc-points" @click="toPointsLogs">
                                        <text class="pc-pts-num">{{ profile.points || 0 }}</text>
                                        <text class="pc-pts-label">积分 ›</text>
                                </view>
                        </view>
                        <view class="pc-progress" v-if="profile.nextLevel">
                                <view class="pc-bar">
                                        <view class="pc-bar-inner" :style="{ width: (profile.progress || 0) + '%' }"></view>
                                </view>
                                <text class="pc-bar-label">距 Lv.{{ profile.nextLevel }} {{ profile.nextTitle }} 还差 {{ profile.pointsToNext }} 分</text>
                        </view>
                        <view class="pc-actions">
                                <view class="pc-btn" @click="toPointsLogs">📋 积分明细</view>
                                <view class="pc-btn" @click="toLeaderboard">🏆 排行榜</view>
                        </view>
                </view>

                <!-- 功能菜单 -->
                <view class="menu-group">
                        <view class="menu-item" @click="toEmergency">
                                <text class="m-icon">🚨</text>
                                <text class="m-text">紧急自救</text>
                                <text class="m-arrow">›</text>
                        </view>
                        <view class="menu-item" @click="toSnake">
                                <text class="m-icon">🐍</text>
                                <text class="m-text">蛇类识别</text>
                                <text class="m-arrow">›</text>
                        </view>
                        <view class="menu-item" @click="toHospital">
                                <text class="m-icon">🏥</text>
                                <text class="m-text">就近医院</text>
                                <text class="m-arrow">›</text>
                        </view>
                </view>

                <!-- 临床工具 -->
                <view class="menu-group">
                        <view class="menu-item" @click="toAssessment">
                                <text class="m-icon">⚖️</text>
                                <text class="m-text">严重程度评估</text>
                                <text class="m-arrow">›</text>
                        </view>
                        <view class="menu-item" @click="toProtocol">
                                <text class="m-icon">🆘</text>
                                <text class="m-text">血清规范 / 并发症流程</text>
                                <text class="m-arrow">›</text>
                        </view>
                        <view class="menu-item" @click="toRecord">
                                <text class="m-icon">📋</text>
                                <text class="m-text">我的蛇伤病历</text>
                                <text class="m-arrow">›</text>
                        </view>
                </view>

                <!-- 咨询中心 -->
                <view class="menu-group">
                        <view class="menu-item" @click="toAiChat">
                                <text class="m-icon">🤖</text>
                                <text class="m-text">AI 蛇伤助手</text>
                                <text class="m-arrow">›</text>
                        </view>
                        <view class="menu-item" @click="toConsult">
                                <text class="m-icon">👩‍⚕️</text>
                                <text class="m-text">咨询医疗顾问</text>
                                <text class="m-arrow">›</text>
                        </view>
                </view>

                <!-- 管理后台入口 -->
                <view class="menu-group">
                        <view class="menu-item" @click="toAdmin">
                                <text class="m-icon">🔐</text>
                                <text class="m-text">管理后台入口</text>
                                <text class="m-arrow">›</text>
                        </view>
                        <view class="menu-item" @click="toApiDoc">
                                <text class="m-icon">📖</text>
                                <text class="m-text">接口文档（开发）</text>
                                <text class="m-arrow">›</text>
                        </view>
                </view>

                <!-- 免责声明 -->
                <view class="disclaimer">
                        <view class="disclaimer-title">📋 免责声明</view>
                        <view class="disclaimer-text">
                                本 APP 提供的自救指引仅为应急参考，不能替代专业医疗诊断和治疗。
                                被毒蛇咬伤后，请务必保持冷静、制动伤肢、尽快前往有抗蛇毒血清的医疗机构就诊。
                        </view>
                </view>

                <view class="version">版本 v1.0.0</view>
        </view>
</template>

<script>
const TOKEN_KEY = 'token'
const USER_KEY = 'appUser'
const API = '/api'

export default {
        data() {
                return {
                        token: '',
                        user: null,
                        profile: {}  // 积分/等级
                }
        },
        computed: {
                isLogin() { return !!this.token },
                nickname() {
                        return this.user && this.user.nickname ? this.user.nickname : '未登录用户'
                },
                phone() {
                        return this.user && this.user.phone ? this.user.phone : '点击上方登录'
                }
        },
        onShow() {
                this.token = uni.getStorageSync(TOKEN_KEY) || ''
                this.user = uni.getStorageSync(USER_KEY) || null
                if (this.isLogin) {
                        // 1️⃣ 先读本地缓存（立刻显示，不白屏）
                        const cached = uni.getStorageSync('appProfile')
                        if (cached && cached.points !== undefined) {
                                this.profile = cached
                        }
                        // 2️⃣ 再异步拉后端最新（拉到了就覆盖）
                        this.loadProfile()
                }
        },
        methods: {
                async loadProfile() {
                        try {
                                const res = await new Promise((resolve, reject) => {
                                        uni.request({
                                                url: API + '/app/points/profile',
                                                method: 'GET',
                                                header: { 'Authorization': 'Bearer ' + this.token },
                                                timeout: 5000,
                                                success: resolve,
                                                fail: reject
                                        })
                                })
                                if (res.data && res.data.code === 200 && res.data.data) {
                                        this.profile = res.data.data
                                        // ✅ 缓存到本地（关键！刷新不丢）
                                        uni.setStorageSync('appProfile', res.data.data)
                                }
                        } catch (e) {
                                // 请求失败就用本地缓存兜底，不显示 0
                                console.warn('loadProfile failed, using cache', e)
                        }
                },
                goLogin() {
                        if (this.isLogin) return
                        uni.navigateTo({ url: '/pages/mine/login' })
                },
                doLogout() {
                        uni.showModal({
                                title: '提示',
                                content: '确定退出登录？',
                                success: (res) => {
                                        if (res.confirm) {
                                                        uni.removeStorageSync(TOKEN_KEY)
                                                        uni.removeStorageSync(USER_KEY)
                                                        uni.removeStorageSync('appProfile')
                                                        this.token = ''
                                                        this.user = null
                                                        this.profile = {}
                                                        uni.showToast({ title: '已退出', icon: 'success' })
                                                }
                                }
                        })
                },
                toPointsLogs() {
                        uni.navigateTo({ url: '/pages/mine/points-logs' })
                },
                toLeaderboard() {
                        uni.navigateTo({ url: '/pages/mine/leaderboard' })
                },
                toAiChat() { uni.navigateTo({ url: '/pages/mine/ai-chat' }) },
                toConsult() { uni.navigateTo({ url: '/pages/mine/consult' }) },
                toEmergency() { uni.navigateTo({ url: '/pages/emergency/emergency' }) },
                toSnake() { uni.navigateTo({ url: '/pages/snake/gallery' }) },
                toHospital() { uni.navigateTo({ url: '/pages/hospital/nearby' }) },
                toAssessment() { uni.navigateTo({ url: '/pages/assessment/severity' }) },
                toProtocol() { uni.navigateTo({ url: '/pages/protocol/index' }) },
                toRecord() { uni.navigateTo({ url: '/pages/record/index' }) },
                toAdmin() {
                        // #ifdef H5
                        window.location.href = 'http://localhost:5173/#/pages/mine/admin-entry'
                        // #endif
                        // #ifdef APP-PLUS
                        uni.navigateTo({ url: '/pages/mine/admin-entry' })
                        // #endif
                },
                toApiDoc() {
                        // #ifdef H5
                        window.open('/api/doc.html', '_blank')
                        // #endif
                        // #ifdef APP-PLUS
                        uni.setClipboardData({
                                data: '/api/doc.html',
                                success: () => uni.showToast({ title: '地址已复制', icon: 'none' })
                        })
                        // #endif
                }
        }
}
</script>

<style>
.mine { padding-bottom: 60rpx; }
.user-card {
        background: linear-gradient(135deg, #455A64, #263238);
        padding: 48rpx 32rpx; display: flex; gap: 24rpx; align-items: center;
        color: #FFF;
}
.avatar {
        width: 120rpx; height: 120rpx; border-radius: 50%;
        background: rgba(255,255,255,0.2);
        display: flex; align-items: center; justify-content: center; font-size: 60rpx;
}
.user-info { flex: 1; }
.nickname { font-size: 36rpx; font-weight: bold; }
.phone { font-size: 26rpx; opacity: 0.7; margin-top: 6rpx; }
.login-hint { font-size: 26rpx; opacity: 0.8; }

/* ======== 积分卡片 ======== */
.points-card {
        margin: 24rpx; background: linear-gradient(135deg, #FFF8E1, #FFECB3);
        border-radius: 20rpx; padding: 28rpx; box-shadow: 0 4rpx 16rpx rgba(255,193,7,0.15);
}
.pc-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.pc-title { display: flex; align-items: center; gap: 12rpx; }
.pc-icon { font-size: 44rpx; }
.pc-level { font-size: 32rpx; font-weight: bold; color: #E65100; }
.pc-lv-title { font-size: 26rpx; color: #BF360C; }
.pc-points { text-align: right; }
.pc-pts-num { font-size: 40rpx; font-weight: bold; color: #FF6F00; }
.pc-pts-label { font-size: 22rpx; color: #BF360C; margin-left: 4rpx; }
.pc-progress { margin-top: 8rpx; }
.pc-bar { height: 16rpx; background: #FFE082; border-radius: 8rpx; overflow: hidden; }
.pc-bar-inner { height: 100%; background: linear-gradient(90deg, #FF9800, #FF6F00); border-radius: 8rpx; transition: width 0.4s; }
.pc-bar-label { font-size: 22rpx; color: #8D6E00; margin-top: 8rpx; display: block; }
.pc-actions { display: flex; gap: 16rpx; margin-top: 20rpx; }
.pc-btn {
        flex: 1; text-align: center; padding: 16rpx 0;
        background: rgba(255,255,255,0.7); border-radius: 12rpx;
        font-size: 26rpx; color: #E65100; font-weight: 500;
}
.pc-btn:active { background: rgba(255,255,255,0.95); }

.menu-group { margin: 24rpx; background: #FFF; border-radius: 16rpx; overflow: hidden; }
.menu-item {
        display: flex; align-items: center; padding: 28rpx 24rpx;
        border-bottom: 1rpx solid #F5F5F5;
        font-size: 30rpx;
}
.menu-item:last-child { border-bottom: none; }
.m-icon { font-size: 36rpx; margin-right: 20rpx; }
.m-text { flex: 1; color: #333; }
.m-arrow { color: #CCC; font-size: 30rpx; }

.disclaimer { margin: 24rpx; padding: 24rpx; background: #F5F5F5; border-radius: 12rpx; }
.disclaimer-title { font-size: 26rpx; color: #666; margin-bottom: 12rpx; font-weight: bold; }
.disclaimer-text { font-size: 24rpx; color: #999; line-height: 1.6; }
.version { text-align: center; color: #CCC; font-size: 22rpx; margin-top: 40rpx; }
</style>


