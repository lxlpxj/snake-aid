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
const TOKEN_KEY = 'snake_token'
const USER_KEY = 'snake_user'

export default {
	data() {
		return {
			token: '',
			user: null
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
	},
	methods: {
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
						this.token = ''
						this.user = null
						uni.showToast({ title: '已退出', icon: 'success' })
					}
				}
			})
		},
		toEmergency() { uni.navigateTo({ url: '/pages/emergency/emergency' }) },
		toSnake() { uni.navigateTo({ url: '/pages/snake/gallery' }) },
		toHospital() { uni.navigateTo({ url: '/pages/hospital/nearby' }) },
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

.menu-group { margin: 24rpx; background: #FFF; border-radius: 16rpx; overflow: hidden; }
.menu-item {
	display: flex; align-items: center; padding: 28rpx 24rpx;
	border-bottom: 1rpx solid #F5F5F5;
}
.menu-item:last-child { border-bottom: none; }
.m-icon { font-size: 36rpx; margin-right: 20rpx; }
.m-text { flex: 1; font-size: 28rpx; color: #333; }
.m-arrow { color: #CCC; font-size: 32rpx; }

.disclaimer {
	margin: 24rpx; padding: 24rpx;
	background: #FFF3E0; border-radius: 16rpx;
}
.disclaimer-title { font-size: 28rpx; font-weight: bold; color: #E65100; margin-bottom: 12rpx; }
.disclaimer-text { font-size: 26rpx; color: #666; line-height: 1.7; }

.version { text-align: center; margin-top: 40rpx; font-size: 24rpx; color: #BBB; }
</style>
