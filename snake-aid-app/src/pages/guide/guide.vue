<template>
	<view class="guide-page">
		<!-- 顶部免责声明条（固定显示，不可关闭） -->
		<view class="disclaimer-bar">
			⚠ 本应用自救指引不能替代专业医疗治疗，无法保证百分之百正确。被蛇咬伤后请务必尽快拨打 120 或前往有抗蛇毒血清的医院！
		</view>

		<view class="intro-card">
			<view class="intro-title">选择自救指南类型</view>
			<view class="intro-desc">如果已经识别出蛇种，建议选择对应的特异性指南</view>
		</view>

		<view class="guide-list">
			<view class="guide-card primary" @click="goSteps('')">
				<view class="g-icon">🆘</view>
				<view class="g-info">
					<view class="g-name">通用自救步骤（推荐）</view>
					<view class="g-desc">适用于所有毒蛇咬伤，6 步标准流程</view>
				</view>
			</view>

			<view class="guide-card" @click="goSteps('nerve')">
				<view class="g-icon">🧠</view>
				<view class="g-info">
					<view class="g-name">神经毒类咬伤</view>
					<view class="g-desc">如银环蛇、金环蛇咬伤</view>
				</view>
			</view>

			<view class="guide-card" @click="goSteps('blood')">
				<view class="g-icon">🩸</view>
				<view class="g-info">
					<view class="g-name">血液毒类咬伤</view>
					<view class="g-desc">如蝮蛇、竹叶青咬伤</view>
				</view>
			</view>

			<view class="guide-card" @click="goSteps('mixed')">
				<view class="g-icon">⚗️</view>
				<view class="g-info">
					<view class="g-name">混合毒类咬伤</view>
					<view class="g-desc">如眼镜蛇、眼镜王蛇咬伤</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	methods: {
		goSteps(type) {
			uni.navigateTo({ url: `/pages/guide/steps?type=${type}` })
		}
	}
}
</script>

<style>
.guide-page { padding: 24rpx; }
/* 免责声明条 —— 固定显示，不可关闭 */
.disclaimer-bar {
	background: #FFF3CD; color: #856404;
	padding: 16rpx 24rpx; font-size: 26rpx;
	text-align: center; line-height: 1.5;
	margin-bottom: 16rpx; border-radius: 8rpx;
	font-weight: 500;
}
.intro-card {
	background: #FFF; border-radius: 16rpx; padding: 28rpx;
	margin-bottom: 24rpx; text-align: center;
}
.intro-title { font-size: 34rpx; font-weight: bold; color: #333; }
.intro-desc { font-size: 26rpx; color: #888; margin-top: 8rpx; }

.guide-list { display: flex; flex-direction: column; gap: 16rpx; }
.guide-card {
	display: flex; gap: 20rpx; background: #FFF;
	border-radius: 16rpx; padding: 24rpx; align-items: center;
	box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.guide-card.primary { border: 2rpx solid #E53935; background: #FFF5F5; }
.g-icon { font-size: 56rpx; }
.g-name { font-size: 30rpx; font-weight: bold; color: #222; }
.g-desc { font-size: 26rpx; color: #888; margin-top: 6rpx; }
</style>
