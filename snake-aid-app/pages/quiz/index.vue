<template>
	<view class="quiz-home">
		<!-- 顶部标题 -->
		<view class="banner">
			<view class="banner-title">📝 急救知识自测</view>
			<view class="banner-sub">检验你的毒蛇急救常识 · 每次随机抽 10 题</view>
		</view>

		<!-- 选择类别 -->
		<view class="section">
			<view class="section-title">选择考试类别</view>
			<view class="cat-grid">
				<view class="cat-card" :class="{ active: category === 'FIRST_AID' }" @click="category = 'FIRST_AID'">
					<text class="cat-emoji">🆘</text>
					<text class="cat-name">急救知识</text>
					<text class="cat-desc">伤口处理 / 自救步骤</text>
				</view>
				<view class="cat-card" :class="{ active: category === 'SNAKE' }" @click="category = 'SNAKE'">
					<text class="cat-emoji">🐍</text>
					<text class="cat-name">蛇种识别</text>
					<text class="cat-desc">特征辨别 / 毒性判断</text>
				</view>
				<view class="cat-card" :class="{ active: category === 'HOSPITAL' }" @click="category = 'HOSPITAL'">
					<text class="cat-emoji">🏥</text>
					<text class="cat-name">医院常识</text>
					<text class="cat-desc">血清储备 / 就医指引</text>
				</view>
				<view class="cat-card" :class="{ active: category === '' }" @click="category = ''">
					<text class="cat-emoji">🎲</text>
					<text class="cat-name">全部随机</text>
					<text class="cat-desc">三个类别混合抽题</text>
				</view>
			</view>
		</view>

		<!-- 题数选择 -->
		<view class="section">
			<view class="section-title">选择题数</view>
			<view class="count-row">
				<view class="count-chip" :class="{ active: count === 5 }" @click="count = 5">5 题</view>
				<view class="count-chip" :class="{ active: count === 10 }" @click="count = 10">10 题</view>
				<view class="count-chip" :class="{ active: count === 15 }" @click="count = 15">15 题</view>
				<view class="count-chip" :class="{ active: count === 20 }" @click="count = 20">20 题</view>
			</view>
		</view>

		<!-- 开始按钮 -->
		<view class="start-btn" @click="startQuiz">🚀 开始答题</view>

		<!-- 考试历史 -->
		<view class="section" v-if="history.length > 0">
			<view class="section-title">最近考试</view>
			<view class="hist-list">
				<view class="hist-item" v-for="h in history.slice(0, 5)" :key="h.id" @click="goResult(h.id)">
					<view class="hist-left">
						<text class="hist-score" :class="gradeClass(h.score / h.totalScore)">{{ h.score }}/{{ h.totalScore }}</text>
						<text class="hist-correct">对 {{ h.correctCount }} 题</text>
					</view>
					<view class="hist-right">
						<text class="hist-date">{{ formatDate(h.createTime) }}</text>
						<text class="hist-grade">{{ grade(h.score / h.totalScore) }}</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	const API = '/api'
	export default {
		data() {
			return {
				category: 'FIRST_AID',
				count: 10,
				history: [],
			}
		},
		onShow() {
			this.loadHistory()
		},
		methods: {
			startQuiz() {
				uni.navigateTo({
					url: `/pages/quiz/quiz?category=${this.category}&count=${this.count}`
				})
			},
			goResult(id) {
				uni.navigateTo({ url: `/pages/quiz/result?id=${id}` })
			},
			loadHistory() {
				const token = uni.getStorageSync('token')
				if (!token) return
				uni.request({
					url: API + '/app/quiz/history?current=1&size=20',
					header: { Authorization: 'Bearer ' + token },
					success: (res) => {
						if (res.data && res.data.code === 200) {
							this.history = res.data.data.records || []
						}
					}
				})
			},
			formatDate(t) {
				if (!t) return ''
				return t.replace('T', ' ').substring(0, 16)
			},
			grade(ratio) {
				if (ratio >= 0.9) return '优秀 ⭐'
				if (ratio >= 0.75) return '良好 👍'
				if (ratio >= 0.6) return '及格 ✓'
				return '加油 💪'
			},
			gradeClass(ratio) {
				if (ratio >= 0.75) return 'good'
				if (ratio >= 0.6) return 'pass'
				return 'fail'
			}
		}
	}
</script>

<style scoped>
	.quiz-home { padding: 24rpx 28rpx 60rpx; min-height: 100vh; background: #f5f7fa; }
	.banner {
		background: linear-gradient(135deg, #1565C0, #42A5F5);
		border-radius: 20rpx; padding: 40rpx 32rpx; color: #fff; margin-bottom: 32rpx;
		box-shadow: 0 8rpx 24rpx rgba(21, 101, 192, 0.3);
	}
	.banner-title { font-size: 40rpx; font-weight: bold; }
	.banner-sub { font-size: 24rpx; opacity: 0.9; margin-top: 10rpx; }
	.section { margin-bottom: 32rpx; }
	.section-title { font-size: 28rpx; color: #333; font-weight: bold; margin-bottom: 20rpx; padding-left: 8rpx; }
	.cat-grid { display: flex; flex-wrap: wrap; gap: 20rpx; }
	.cat-card {
		flex: 1; min-width: 160rpx; background: #fff; border-radius: 16rpx;
		padding: 28rpx 20rpx; text-align: center; border: 3rpx solid transparent;
		transition: all 0.2s;
	}
	.cat-card.active { border-color: #1565C0; background: #E3F2FD; }
	.cat-emoji { font-size: 48rpx; display: block; margin-bottom: 10rpx; }
	.cat-name { font-size: 28rpx; font-weight: bold; color: #333; display: block; }
	.cat-desc { font-size: 22rpx; color: #999; display: block; margin-top: 6rpx; }
	.count-row { display: flex; gap: 16rpx; }
	.count-chip {
		flex: 1; background: #fff; border-radius: 12rpx; padding: 24rpx 0; text-align: center;
		font-size: 26rpx; color: #666; border: 2rpx solid #eee;
	}
	.count-chip.active { border-color: #1565C0; color: #1565C0; background: #E3F2FD; font-weight: bold; }
	.start-btn {
		background: linear-gradient(135deg, #E53935, #FF7043); color: #fff; text-align: center;
		padding: 32rpx 0; border-radius: 50rpx; font-size: 32rpx; font-weight: bold;
		margin: 20rpx 0 40rpx; box-shadow: 0 8rpx 24rpx rgba(229, 57, 53, 0.35);
	}
	.hist-list { background: #fff; border-radius: 16rpx; overflow: hidden; }
	.hist-item {
		display: flex; justify-content: space-between; align-items: center;
		padding: 28rpx; border-bottom: 2rpx solid #f0f0f0;
	}
	.hist-item:last-child { border-bottom: none; }
	.hist-left { display: flex; flex-direction: column; }
	.hist-score { font-size: 36rpx; font-weight: bold; color: #333; }
	.hist-score.good { color: #2E7D32; }
	.hist-score.pass { color: #F9A825; }
	.hist-score.fail { color: #E53935; }
	.hist-correct { font-size: 24rpx; color: #999; margin-top: 4rpx; }
	.hist-right { display: flex; flex-direction: column; align-items: flex-end; }
	.hist-date { font-size: 22rpx; color: #bbb; }
	.hist-grade { font-size: 24rpx; color: #666; margin-top: 6rpx; }
</style>


