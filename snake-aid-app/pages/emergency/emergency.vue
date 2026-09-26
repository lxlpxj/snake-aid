<template>
	<view class="emergency-page">
		<!-- 顶部免责声明条（固定显示，不可关闭） -->
		<view class="disclaimer-bar">
			⚠ 本应用自救指引不能替代专业医疗治疗，无法保证百分之百正确。被蛇咬伤后请务必尽快拨打 120 或前往有抗蛇毒血清的医院！
		</view>

		<!-- 主按钮区 -->
		<view class="hero">
			<view class="hero-title">毒蛇咬伤紧急自救</view>
			<view class="hero-sub">分秒必争 · 先自救再就医</view>
			<view class="main-btn" @click="goSteps">
				<text class="main-btn-text">立即开始自救</text>
			</view>
		</view>

		<!-- 三大核心功能 -->
		<view class="action-grid">
			<view class="action-card" @click="goSnakeIdentify">
				<text class="action-icon">🔍</text>
				<text class="action-text">识别蛇种</text>
			</view>
			<view class="action-card" @click="goHospital">
				<text class="action-icon">🏥</text>
				<text class="action-text">就近找医院</text>
			</view>
			<view class="action-card danger" @click="call120">
				<text class="action-icon">🚑</text>
				<text class="action-text">拨打 120</text>
			</view>
		</view>

		<!-- 禁止动作警示 -->
		<view class="warn-card">
			<view class="warn-title">⛔ 绝对禁止</view>
			<view class="warn-list">
				<text>❌ 用刀切开伤口</text>
				<text>❌ 用嘴吸毒</text>
				<text>❌ 冰敷伤口</text>
				<text>❌ 奔跑/剧烈运动</text>
				<text>❌ 绑扎过紧阻断血液循环</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {}
	},
	methods: {
		// TODO: 调用 /app/emergency/trigger 上报紧急事件
		goSteps() {
			uni.navigateTo({ url: '/pages/guide/steps' })
		},
		goSnakeIdentify() {
			uni.navigateTo({ url: '/pages/snake/gallery' })
		},
		goHospital() {
			uni.navigateTo({ url: '/pages/hospital/nearby' })
		},
		call120() {
			uni.makePhoneCall({ phoneNumber: '120' })
		}
	}
}
</script>

<style>
.emergency-page { padding-bottom: 40rpx; }
.disclaimer-bar {
	background: #FFF3CD; color: #856404;
	padding: 16rpx 24rpx; font-size: 26rpx;
	text-align: center;
}
.hero {
	background: linear-gradient(135deg, #E53935, #B71C1C);
	padding: 80rpx 40rpx 60rpx;
	text-align: center;
}
.hero-title { font-size: 48rpx; color: #FFF; font-weight: bold; }
.hero-sub { font-size: 28rpx; color: #FFCDD2; margin: 16rpx 0 40rpx; }
.main-btn {
	background: #FFF; color: #E53935;
	display: inline-block; padding: 24rpx 80rpx;
	border-radius: 60rpx; font-weight: bold;
	font-size: 34rpx;
}
.main-btn:active { opacity: 0.8; }

.action-grid {
	display: flex; gap: 16rpx; padding: 32rpx;
}
.action-card {
	flex: 1; background: #FFF; border-radius: 16rpx;
	padding: 32rpx 16rpx; text-align: center;
	box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.05);
}
.action-card.danger { background: #FFEBEE; }
.action-icon { font-size: 48rpx; display: block; margin-bottom: 12rpx; }
.action-text { font-size: 26rpx; color: #333; }

.warn-card {
	margin: 0 32rpx; background: #FFEBEE;
	border-left: 8rpx solid #E53935; border-radius: 8rpx;
	padding: 24rpx;
}
.warn-title { font-size: 30rpx; font-weight: bold; color: #C62828; margin-bottom: 16rpx; }
.warn-list text { display: block; font-size: 26rpx; color: #555; line-height: 48rpx; }
</style>
