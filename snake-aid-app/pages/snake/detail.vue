<template>
	<view class="snake-detail">
		<!-- 头图区 -->
		<view class="banner">
			<view class="banner-photo">
				<image v-if="detail.imageUrl" :src="imageUrl(detail.imageUrl)" mode="aspectFill" class="banner-img" />
				<text v-else>{{ detail.name?.slice(0,1) || '?' }}</text>
			</view>
			<view class="banner-info">
				<view class="banner-name">{{ detail.name }}</view>
				<view class="banner-latin">{{ detail.scientificName }}</view>
				<view class="banner-tag">
					<text v-if="detail.isPoisonous===1" class="tag-danger">有毒 · {{ detail.venomType }}</text>
					<text v-else class="tag-safe">无毒</text>
				</view>
			</view>
		</view>

		<!-- 识别特征 -->
		<view class="section">
			<view class="section-title">🔍 识别特征</view>
			<view class="feature-grid">
				<view class="feature-item"><text class="label">头型</text><text class="val">{{ detail.headShape }}</text></view>
				<view class="feature-item"><text class="label">花纹</text><text class="val">{{ detail.pattern }}</text></view>
				<view class="feature-item"><text class="label">颜色</text><text class="val">{{ detail.color }}</text></view>
				<view class="feature-item"><text class="label">尾部</text><text class="val">{{ detail.tailShape }}</text></view>
			</view>
		</view>

		<!-- 外观描述 -->
		<view class="section">
			<view class="section-title">📝 外观描述</view>
			<view class="content-text">{{ detail.appearanceDesc || '暂无描述' }}</view>
		</view>

		<!-- 分布地区 -->
		<view class="section">
			<view class="section-title">🗺 分布地区</view>
			<view class="content-text">{{ detail.distribution || '暂无数据' }}</view>
		</view>

		<!-- 被咬后症状 -->
		<view class="section danger">
			<view class="section-title">⚠ 被咬后特异性症状</view>
			<view class="content-text">{{ detail.symptoms || '暂无数据' }}</view>
		</view>

		<!-- 特异性处理要点 -->
		<view class="section">
			<view class="section-title">💡 处理要点</view>
			<view class="content-text">{{ detail.firstAidSpecifics || '暂无数据' }}</view>
		</view>

		<!-- 底部固定按钮 -->
		<view class="footer-bar">
			<view class="btn-primary" @click="goGuide">查看自救步骤</view>
			<view class="btn-secondary" @click="goHospital">就近找医院</view>
		</view>
	</view>
</template>

<script>
const API = 'http://localhost:8080/api'

export default {
	data() {
		return { detail: {} }
	},
	onLoad(opt) {
		this.loadDetail(opt.id)
	},
	methods: {
		imageUrl(url) {
			if (!url) return ''
			if (url.startsWith('http')) return url
			return API + url
		},
		loadDetail(id) {
			uni.request({
				url: API + '/app/snake/' + id,
				method: 'GET',
				success: (res) => {
					const d = res.data
					if (d && d.code === 200 && d.data) {
						this.detail = d.data
					} else {
						uni.showToast({ title: '蛇种不存在', icon: 'none' })
					}
				},
				fail: (err) => {
					console.error('加载蛇种详情失败', err)
					uni.showToast({ title: '加载失败', icon: 'none' })
				}
			})
		},
		goGuide() { uni.navigateTo({ url: '/pages/guide/steps' }) },
		goHospital() { uni.navigateTo({ url: '/pages/hospital/nearby' }) }
	}
}
</script>

<style>
.snake-detail { padding-bottom: 160rpx; }
.banner {
	background: #2E7D32; color: #FFF; padding: 40rpx 32rpx;
	display: flex; gap: 24rpx; align-items: center;
}
.banner-photo {
	width: 160rpx; height: 160rpx; border-radius: 16rpx;
	background: #FFF; color: #2E7D32; font-size: 72rpx; font-weight: bold;
	display: flex; align-items: center; justify-content: center;
	overflow: hidden;
}
.banner-img { width: 100%; height: 100%; }
.banner-name { font-size: 40rpx; font-weight: bold; }
.banner-latin { font-size: 24rpx; opacity: 0.8; font-style: italic; margin-top: 4rpx; }
.banner-tag { margin-top: 12rpx; }
.tag-danger { background: #FFEBEE; color: #C62828; padding: 4rpx 16rpx; border-radius: 6rpx; font-size: 22rpx; }
.tag-safe { background: #E8F5E9; color: #2E7D32; padding: 4rpx 16rpx; border-radius: 6rpx; font-size: 22rpx; }

.section {
	margin: 24rpx; background: #FFF; border-radius: 16rpx;
	padding: 24rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.section.danger { background: #FFEBEE; border-left: 6rpx solid #E53935; }
.section-title { font-size: 30rpx; font-weight: bold; color: #333; margin-bottom: 16rpx; }
.feature-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16rpx; }
.feature-item { background: #F5F5F5; padding: 16rpx; border-radius: 8rpx; }
.feature-item .label { font-size: 24rpx; color: #888; display: block; }
.feature-item .val { font-size: 28rpx; color: #333; font-weight: 500; }
.content-text { font-size: 28rpx; color: #555; line-height: 1.7; }

.footer-bar {
	position: fixed; bottom: 0; left: 0; right: 0;
	display: flex; gap: 16rpx; padding: 20rpx 24rpx;
	background: #FFF; border-top: 1rpx solid #EEE;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
.btn-primary {
	flex: 2; background: #E53935; color: #FFF;
	text-align: center; padding: 20rpx; border-radius: 40rpx;
	font-size: 30rpx; font-weight: bold;
}
.btn-secondary {
	flex: 1; background: #FFF; color: #E53935;
	text-align: center; padding: 20rpx; border-radius: 40rpx;
	font-size: 28rpx; border: 2rpx solid #E53935;
}
</style>
