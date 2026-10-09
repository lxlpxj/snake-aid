<template>
	<view class="home">
		<!-- 顶部紧急大按钮：2 步内到达自救 -->
		<view class="hero">
			<view class="hero-bg"></view>
			<view class="hero-content">
				<view class="hero-subtitle">被蛇咬了？立即自救</view>
				<view class="emergency-btn" @click="goEmergency">
					<view class="btn-ring"></view>
					<text class="btn-icon">🚨</text>
					<text class="btn-text">紧急模式</text>
					<text class="btn-hint">分秒必争 · 立即进入</text>
				</view>
				<view class="hero-sub-btns">
					<view class="mini-btn" @click="call120">
						<text class="mini-icon">🚑</text>
						<text class="mini-text">一键 120</text>
					</view>
					<view class="mini-btn" @click="goSteps">
						<text class="mini-icon">📖</text>
						<text class="mini-text">自救步骤</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 免责声明（FR8，全程固定） -->
		<view class="disclaimer">
			⚠ 自救不等于治疗，必须尽快就医！本应用指引仅供参考，不能替代专业医疗。
		</view>

		<!-- 四大功能入口 -->
		<view class="entry-grid">
			<view class="entry-card" @click="goSnakeList">
				<text class="entry-icon">🐍</text>
				<text class="entry-title">蛇种图库</text>
				<text class="entry-sub">识别 · 特征比对</text>
			</view>
			<view class="entry-card highlight-orange" @click="goLearn">
				<text class="entry-icon">🎯</text>
				<text class="entry-title">四步学习</text>
				<text class="entry-sub">玩-认-练-测 认知闭环</text>
			</view>
			<view class="entry-card" @click="goHospital">
				<text class="entry-icon">🏥</text>
				<text class="entry-title">就近医院</text>
				<text class="entry-sub">有血清优先</text>
			</view>
			<view class="entry-card" @click="goGuide">
				<text class="entry-icon">📚</text>
				<text class="entry-title">自救指南</text>
				<text class="entry-sub">6 步标准流程</text>
			</view>
			<view class="entry-card" @click="goFeatures">
				<text class="entry-icon">🔍</text>
				<text class="entry-title">特征检索</text>
				<text class="entry-sub">头型/花纹/颜色</text>
			</view>
			</view>
			<view class="entry-card highlight" @click="goGame">
				<text class="entry-icon">🎮</text>
				<text class="entry-title">急救闯关</text>
				<text class="entry-sub">9步游戏化 · 练急救</text>
			</view>
			<view class="entry-card" @click="goQuiz">
				<text class="entry-icon">📝</text>
				<text class="entry-title">考试自测</text>
				<text class="entry-sub">随机抽题 · 巩固知识</text>
			</view>
			<view class="entry-card highlight-video" @click="goVideo">
				<text class="entry-icon">🎬</text>
				<text class="entry-title">科普视频</text>
				<text class="entry-sub">急救教学 · 专家讲解</text>
			</view>

		<!-- 本地高发毒蛇（FR9：按地区浏览） -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">📍 本地高发毒蛇</text>
				<picker mode="selector" :range="provinces" @change="onProvincePick">
					<view class="section-more">
						{{ selectedProvince || '选择地区' }} ▾
					</view>
				</picker>
			</view>
			<scroll-view class="snake-scroll" scroll-x v-if="localSnakes.length > 0">
				<view class="snake-chip" v-for="s in localSnakes" :key="s.id" @click="goSnakeDetail(s.id)">
					<image v-if="s.imageUrl" :src="imgBase + s.imageUrl" mode="aspectFill" class="chip-img"></image>
					<view v-else class="chip-img placeholder">🐍</view>
					<view class="chip-info">
						<text class="chip-name">{{ s.name }}</text>
						<text class="chip-tag" :class="'level-' + (s.hazardLevel||'')">
							{{ venomLabel(s.isPoisonous, s.hazardLevel) }}
						</text>
					</view>
				</view>
			</scroll-view>
			<view v-else class="empty-hint">{{ selectedProvince ? '该地区暂无录入蛇种数据' : '请选择省份查看本地高发毒蛇' }}</view>
		</view>

		<!-- 每日科普 -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">📖 科普小知识</text>
				<text class="section-more" @click="goArticles">更多 ›</text>
			</view>
			<view class="tip-list">
				<view class="tip-item" @click="goGuide">
					<text class="tip-num">01</text>
					<view class="tip-body">
						<text class="tip-title">被蛇咬伤后的第一反应</text>
						<text class="tip-desc">脱离现场 → 保持冷静 → 制动伤肢 → 尽快就医</text>
					</view>
				</view>
				<view class="tip-item" @click="goSnakeList">
					<text class="tip-num warn">02</text>
					<view class="tip-body">
						<text class="tip-title">如何区分毒蛇和无毒蛇？</text>
						<text class="tip-desc">看头型 · 看花纹 · 看瞳孔 · 对比常见无毒蛇</text>
					</view>
				</view>
				<view class="tip-item" @click="goHospital">
					<text class="tip-num safe">03</text>
					<view class="tip-body">
						<text class="tip-title">就近找有血清的医院</text>
						<text class="tip-desc">按地区筛选，优先展示有抗蛇毒血清库存的医院</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { china } from '@/common/china.js'
const API = 'http://127.0.0.1:8080/api'

export default {
	data() {
		return {
			selectedProvince: '',
			provinces: china.map(p => p.name),
			localSnakes: [],
			imgBase: API
		}
	},
	onShow() {
		// 每次回到首页刷新本地高发毒蛇
		if (this.selectedProvince) this.loadLocalSnakes()
	},
	methods: {
		goEmergency() {
			uni.navigateTo({ url: '/pages/emergency/emergency' })
		},
		goSteps() {
			uni.navigateTo({ url: '/pages/guide/steps' })
		},
		goSnakeList() {
			uni.navigateTo({ url: '/pages/snake/gallery' })
		},
		goHospital() {
			uni.navigateTo({ url: '/pages/hospital/nearby' })
		},
		goGuide() {
			uni.navigateTo({ url: '/pages/guide/guide' })
		},
		goFeatures() {
			// 特征筛选入口：跳独立的 gallery 页（有返回键）
			uni.navigateTo({ url: '/pages/snake/gallery?feature=true' })
		},
		goArticles() {
			uni.navigateTo({ url: '/pages/snake/gallery' })
		},
		goQuiz() {
			uni.navigateTo({ url: '/pages/quiz/index' })
		},
		goGame() {
			uni.navigateTo({ url: '/pages/guide/game' })
		},
		goLearn() {
			uni.navigateTo({ url: '/pages/snake/learn' })
		},
		goVideo() {
			uni.navigateTo({ url: '/pages/video/list' })
		},
		goSnakeDetail(id) {
			uni.navigateTo({ url: '/pages/snake/detail?id=' + id })
		},
		call120() {
			uni.makePhoneCall({ phoneNumber: '120' })
		},
		onProvincePick(e) {
			this.selectedProvince = this.provinces[e.detail.value]
			this.loadLocalSnakes()
		},
		loadLocalSnakes() {
			uni.request({
				url: API + '/app/snake/page',
				method: 'GET',
				data: { province: this.selectedProvince, pageSize: 10, pageNum: 1 },
				success: (res) => {
					const d = res.data
					if (d && d.code === 200 && d.data) {
						this.localSnakes = (d.data.records || []).filter(s => s.isPoisonous === 1)
					} else {
						this.localSnakes = []
					}
				},
				fail: () => { this.localSnakes = [] }
			})
		},
		venomLabel(isPoisonous, level) {
			if (isPoisonous !== 1) return '无毒'
			const map = { 1: '轻微毒', 2: '中等毒', 3: '剧毒', 4: '极危' }
			return map[level] || '有毒'
		}
	}
}
</script>

<style>
.home { padding-bottom: 40rpx; background: #FAFAFA; }

/* Hero 紧急按钮区 */
.hero {
	position: relative; padding: 40rpx 32rpx 48rpx;
	background: linear-gradient(135deg, #E53935 0%, #C62828 100%);
	border-radius: 0 0 40rpx 40rpx; overflow: hidden;
}
.hero-bg {
	position: absolute; top: -100rpx; right: -100rpx;
	width: 400rpx; height: 400rpx; background: rgba(255,255,255,0.08);
	border-radius: 50%;
}
.hero-content { position: relative; z-index: 2; text-align: center; }
.hero-subtitle { color: rgba(255,255,255,0.85); font-size: 26rpx; margin-bottom: 28rpx; }

.emergency-btn {
	position: relative; width: 260rpx; height: 260rpx; margin: 0 auto 24rpx;
	background: #FFEBEE; border-radius: 50%;
	display: flex; flex-direction: column; justify-content: center; align-items: center;
	box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.25);
}
.btn-ring {
	position: absolute; inset: -12rpx; border-radius: 50%;
	border: 4rpx solid rgba(255,255,255,0.5);
	animation: pulse 2s ease-in-out infinite;
}
@keyframes pulse {
	0% { transform: scale(1); opacity: 1; }
	100% { transform: scale(1.15); opacity: 0; }
}
.btn-icon { font-size: 64rpx; }
.btn-text { font-size: 34rpx; font-weight: bold; color: #C62828; margin-top: 8rpx; }
.btn-hint { font-size: 22rpx; color: #E53935; margin-top: 4rpx; }

.hero-sub-btns { display: flex; justify-content: center; gap: 48rpx; }
.mini-btn {
	display: flex; flex-direction: column; align-items: center;
	color: #FFF; font-size: 22rpx;
}
.mini-icon { font-size: 40rpx; margin-bottom: 6rpx; }

/* 免责声明 */
.disclaimer {
	margin: 20rpx 24rpx; padding: 16rpx 20rpx;
	background: #FFF8E1; border-left: 6rpx solid #FF9800;
	font-size: 24rpx; color: #E65100; line-height: 1.6; border-radius: 0 8rpx 8rpx 0;
}

/* 功能入口 */
.entry-grid {
	display: grid; grid-template-columns: 1fr 1fr; gap: 20rpx;
	padding: 0 24rpx;
}
.entry-card {
	background: #FFF; border-radius: 16rpx; padding: 28rpx 24rpx;
	box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
}
.entry-icon { font-size: 48rpx; }
.entry-title { display: block; font-size: 30rpx; font-weight: bold; color: #222; margin-top: 12rpx; }
.entry-sub { display: block; font-size: 22rpx; color: #888; margin-top: 4rpx; }
.entry-card.highlight {
	background: linear-gradient(135deg, #FFEBEE 0%, #FFCDD2 100%);
	border: 2rpx solid #E53935;
	position: relative;
}
.entry-card.highlight::before {
	content: '新';
	position: absolute; top: 12rpx; right: 16rpx;
	background: #E53935; color: #fff;
	font-size: 18rpx; padding: 2rpx 10rpx; border-radius: 20rpx;
}
.entry-card.highlight .entry-title { color: #C62828; }
.entry-card.highlight-orange {
	background: linear-gradient(135deg, #FFF3E0 0%, #FFE0B2 100%);
	border: 2rpx solid #E65100;
	position: relative;
}
.entry-card.highlight-orange::before {
	content: '学';
	position: absolute; top: 12rpx; right: 16rpx;
	background: #E65100; color: #fff;
	font-size: 18rpx; padding: 2rpx 10rpx; border-radius: 20rpx;
}
.entry-card.highlight-orange .entry-title { color: #E65100; }
.entry-card.highlight-video {
	background: linear-gradient(135deg, #F3E5F5 0%, #E1BEE7 100%);
	border: 2rpx solid #7B1FA2;
	position: relative;
}
.entry-card.highlight-video::before {
	content: 'NEW';
	position: absolute; top: 12rpx; right: 16rpx;
	background: #7B1FA2; color: #fff;
	font-size: 18rpx; padding: 2rpx 12rpx; border-radius: 20rpx;
}
.entry-card.highlight-video .entry-title { color: #6A1B9A; }

/* 区块 */
.section { margin-top: 36rpx; padding: 0 24rpx; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.section-title { font-size: 30rpx; font-weight: bold; color: #222; }
.section-more { font-size: 24rpx; color: #1565C0; }

.snake-scroll { white-space: nowrap; }
.snake-chip {
	display: inline-flex; gap: 16rpx; background: #FFF; border-radius: 12rpx;
	padding: 16rpx; margin-right: 16rpx; box-shadow: 0 2rpx 6rpx rgba(0,0,0,0.05);
	width: 260rpx; vertical-align: top;
}
.chip-img { width: 80rpx; height: 80rpx; border-radius: 8rpx; background: #F5F5F5; flex-shrink: 0; }
.chip-img.placeholder { text-align: center; line-height: 80rpx; font-size: 40rpx; }
.chip-info { display: flex; flex-direction: column; justify-content: center; }
.chip-name { font-size: 26rpx; color: #222; font-weight: 500; }
.chip-tag { font-size: 20rpx; padding: 2rpx 10rpx; border-radius: 4rpx; margin-top: 4rpx; }
.chip-tag.level-1 { background: #E8F5E9; color: #2E7D32; }
.chip-tag.level-2 { background: #FFF3E0; color: #E65100; }
.chip-tag.level-3 { background: #FFEBEE; color: #C62828; }
.chip-tag.level-4 { background: #B71C1C; color: #FFF; }
.empty-hint { text-align: center; padding: 30rpx; color: #AAA; font-size: 24rpx; background: #FFF; border-radius: 12rpx; }

.tip-list { background: #FFF; border-radius: 16rpx; padding: 8rpx 24rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05); }
.tip-item { display: flex; gap: 16rpx; padding: 24rpx 0; border-bottom: 1rpx solid #F0F0F0; }
.tip-item:last-child { border-bottom: none; }
.tip-num { font-size: 36rpx; font-weight: bold; color: #E53935; width: 64rpx; flex-shrink: 0; }
.tip-num.warn { color: #FF9800; }
.tip-num.safe { color: #2E7D32; }
.tip-body { flex: 1; }
.tip-title { display: block; font-size: 28rpx; color: #222; font-weight: 500; }
.tip-desc { display: block; font-size: 22rpx; color: #888; margin-top: 6rpx; }
</style>


