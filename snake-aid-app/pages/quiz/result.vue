<template>
	<view class="result-page">
		<!-- 成绩头部 -->
		<view class="result-hero" :class="scoreClass">
			<view class="score-circle">
				<view class="score-num">{{ quiz.score || 0 }}</view>
				<view class="score-total">/ {{ quiz.totalScore || 100 }}</view>
			</view>
			<view class="score-label">{{ grade }}</view>
			<view class="score-stats">
				<text class="stat-item">✅ 对 {{ quiz.correctCount || 0 }} 题</text>
				<text class="stat-item">❌ 错 {{ wrongCount }} 题</text>
				<text class="stat-item">⏱ {{ formatDur(quiz.durationSec) }}</text>
			</view>
		</view>

		<!-- 筛选切换 -->
		<view class="filter-bar">
			<view class="filter-chip" :class="{ active: filter === 'all' }" @click="filter = 'all'">
				全部 {{ details.length }} 题
			</view>
			<view class="filter-chip" :class="{ active: filter === 'wrong' }" @click="filter = 'wrong'">
				错题 {{ wrongCount }} 题
			</view>
		</view>

		<!-- 错题为空提示 -->
		<view v-if="filter === 'wrong' && wrongCount === 0" class="empty-tip">
			🎉 恭喜！全部答对，没有错题～
		</view>

		<!-- 每题详情 -->
		<view class="detail-section">
			<view
				class="q-detail"
				v-for="(d, i) in filteredDetails"
				:key="d.questionId"
				:class="{ wrong: !d.isCorrect }"
			>
				<view class="qd-head">
					<text class="qd-index">第 {{ origIndex(i) }} 题</text>
					<view class="qd-tag" :class="d.isCorrect ? 'tag-good' : 'tag-bad'">
						{{ d.isCorrect ? '✓ 答对' : '✗ 答错' }}
						<text class="qd-score-extra">{{ d.isCorrect ? ` +${d.score}分` : ' 0分' }}</text>
					</view>
				</view>

				<view class="qd-q">
					<text class="qd-q-type">{{ typeLabel(d.questionType) }}</text>
					{{ d.question }}
				</view>

				<!-- 选项列表 -->
				<view class="qd-options" v-if="d.questionType !== 3">
					<view
						class="opt-item"
						v-for="opt in buildOptions(d)"
						:key="opt.key"
						:class="opt.cssClass"
					>
						<text class="opt-key">{{ opt.key }}</text>
						<text class="opt-text">{{ opt.text }}</text>
						<text class="opt-mark" v-if="opt.isRight">✓</text>
						<text class="opt-mark wrong" v-else-if="opt.isWrong">✗</text>
					</view>
				</view>

				<!-- 判断题类型特殊处理（yes/no 两选项） -->
				<view class="qd-options" v-else-if="d.questionType === 3">
					<view class="opt-item" :class="boolOptClass(d, '对')">
						<text class="opt-key">√</text>
						<text class="opt-text">正确</text>
					</view>
					<view class="opt-item" :class="boolOptClass(d, '错')">
						<text class="opt-key">×</text>
						<text class="opt-text">错误</text>
					</view>
				</view>

				<!-- 用户答案 / 正确答案 -->
				<view class="qd-ans-row">
					<view class="ans-tag" :class="d.isCorrect ? 'right-tag' : 'wrong-tag'">
						你的答案：{{ displayAnswer(d.userAnswer) }}
					</view>
					<view class="ans-tag right-tag">
						正确答案：{{ displayAnswer(d.correctAnswer) }}
					</view>
				</view>

				<!-- 解析 -->
				<view class="qd-analysis" v-if="d.analysis">
					<view class="analysis-head" @click="toggleAnalysis(d.questionId)">
						<text class="analysis-title">💡 答案解析</text>
						<text class="analysis-toggle">{{ expanded[d.questionId] !== false ? '收起 ▲' : '展开 ▼' }}</text>
					</view>
					<view class="analysis-body" v-if="expanded[d.questionId] !== false">
						{{ d.analysis }}
					</view>
				</view>
			</view>
		</view>

		<!-- 底部按钮 -->
		<view class="footer-btns">
			<view class="fb fb-home" @click="goHome">返回首页</view>
			<view class="fb fb-retry" @click="retry">再考一次</view>
		</view>
	</view>
</template>

<script>
	const API = 'http://127.0.0.1:8080/api'
	export default {
		data() {
			return {
				quizId: null,
				quiz: {},
				details: [],
				filter: 'all', // 'all' | 'wrong'
				expanded: {}, // { questionId: true/false } 控制每题解析展开状态
			}
		},
		computed: {
			wrongCount() {
				return this.details.filter(d => !d.isCorrect).length
			},
			filteredDetails() {
				if (this.filter === 'wrong') {
					return this.details.filter(d => !d.isCorrect)
				}
				return this.details
			},
			ratio() {
				if (!this.quiz.totalScore) return 0
				return this.quiz.score / this.quiz.totalScore
			},
			grade() {
				if (this.ratio >= 0.9) return '🎉 优秀！急救高手'
				if (this.ratio >= 0.75) return '👍 良好，继续保持'
				if (this.ratio >= 0.6) return '✓ 及格，还需加强'
				return '💪 加油，多多练习'
			},
			scoreClass() {
				if (this.ratio >= 0.75) return 'good'
				if (this.ratio >= 0.6) return 'pass'
				return 'fail'
			},
		},
		onLoad(options) {
			this.quizId = options.id
			this.loadDetail()
		},
		methods: {
			loadDetail() {
				uni.showLoading({ title: '加载中...' })
				const token = uni.getStorageSync('token')
				uni.request({
					url: API + `/app/quiz/detail/${this.quizId}`,
					header: token ? { Authorization: 'Bearer ' + token } : {},
					success: (res) => {
						uni.hideLoading()
						if (res.data && res.data.code === 200) {
							const payload = res.data.data || {}
							this.quiz = payload.quiz || {}
							this.details = payload.details || []
							// 默认全部展开解析
							this.details.forEach(d => { this.expanded[d.questionId] = true })
						}
					},
					fail: () => {
						uni.hideLoading()
						uni.showToast({ title: '网络错误', icon: 'none' })
					}
				})
			},
			// 给筛选后列表映射回"原始题号"
			origIndex(filteredIdx) {
				const d = this.filteredDetails[filteredIdx]
				const idx = this.details.findIndex(x => x.questionId === d.questionId)
				return idx >= 0 ? idx + 1 : filteredIdx + 1
			},
			typeLabel(t) {
				if (t === 1) return '【单选】'
				if (t === 2) return '【多选】'
				if (t === 3) return '【判断】'
				return ''
			},
			// 构建选项列表（ABCD，仅单选/多选题型）
			buildOptions(d) {
				if (d.questionType === 3) return null // 判断题单独处理
				const keys = ['A', 'B', 'C', 'D']
				const texts = [d.optionA, d.optionB, d.optionC, d.optionD]
				const correctAns = (d.correctAnswer || '').toUpperCase()
				const userAns = (d.userAnswer || '').toUpperCase()

				return keys.map((k, i) => {
					const text = texts[i]
					if (!text) return null
					const isRight = correctAns.includes(k)
					const isWrong = userAns.includes(k) && !isRight
					let cssClass = ''
					if (isRight) cssClass = 'opt-correct'
					else if (isWrong) cssClass = 'opt-wrong'
					return { key: k, text, isRight, isWrong, cssClass }
				}).filter(Boolean)
			},
			// 判断题两选项样式
			boolOptClass(d, label) {
				const correctAns = d.correctAnswer
				const userAns = d.userAnswer
				const isRight = correctAns === label
				const isWrong = userAns === label && !isRight
				let cls = 'bool-opt'
				if (isRight) cls += ' opt-correct'
				else if (isWrong) cls += ' opt-wrong'
				return cls
			},
			// 显示答案（多选 "AB" → "A、B"）
			displayAnswer(ans) {
				if (!ans) return '未作答'
				ans = ans.toString().trim().toUpperCase()
				// 判断题 "对"/"错"
				if (ans === '对' || ans === '正确') return '✓ 正确'
				if (ans === '错' || ans === '错误') return '✗ 错误'
				// 多选 AB → A、B
				return ans.split('').map(c => c.trim()).filter(Boolean).join('、')
			},
			toggleAnalysis(qid) {
				this.$set(this.expanded, qid, this.expanded[qid] === false ? true : false)
			},
			formatDur(sec) {
				if (!sec) return '0秒'
				const m = Math.floor(sec / 60), s = sec % 60
				return m > 0 ? `${m}分${s}秒` : `${s}秒`
			},
			goHome() {
				uni.reLaunch({ url: '/pages/index/index' })
			},
			retry() {
				uni.redirectTo({ url: '/pages/quiz/index' })
			},
		}
	}
</script>

<style scoped>
	.result-page { min-height: 100vh; background: #f5f7fa; padding-bottom: 160rpx; }

	/* 头部 */
	.result-hero {
		padding: 60rpx 40rpx 50rpx; text-align: center; color: #fff;
	}
	.result-hero.good { background: linear-gradient(135deg, #2E7D32, #66BB6A); }
	.result-hero.pass { background: linear-gradient(135deg, #F9A825, #FFCA28); }
	.result-hero.fail { background: linear-gradient(135deg, #E53935, #EF5350); }

	.score-circle {
		display: inline-flex; flex-direction: column; align-items: center;
		width: 240rpx; height: 240rpx; border-radius: 50%;
		background: rgba(255,255,255,0.18); border: 4rpx solid rgba(255,255,255,0.4);
		padding-top: 40rpx;
	}
	.score-num { font-size: 80rpx; font-weight: bold; line-height: 1; }
	.score-total { font-size: 26rpx; opacity: 0.9; margin-top: 4rpx; }
	.score-label { font-size: 34rpx; font-weight: bold; margin: 30rpx 0 16rpx; }
	.score-stats { display: flex; justify-content: center; gap: 30rpx; font-size: 24rpx; opacity: 0.9; }
	.stat-item { background: rgba(255,255,255,0.2); padding: 8rpx 20rpx; border-radius: 20rpx; }

	/* 筛选 */
	.filter-bar {
		display: flex; justify-content: center; gap: 20rpx;
		margin: 30rpx 24rpx 10rpx;
	}
	.filter-chip {
		padding: 14rpx 36rpx; border-radius: 40rpx; font-size: 28rpx;
		background: #fff; color: #666; border: 2rpx solid #ddd;
	}
	.filter-chip.active {
		background: #1565C0; color: #fff; border-color: #1565C0; font-weight: bold;
	}

	.empty-tip {
		text-align: center; padding: 60rpx 0; color: #2E7D32; font-size: 30rpx;
	}

	/* 题目卡片 */
	.detail-section { margin: 20rpx 24rpx; }
	.q-detail {
		background: #fff; border-radius: 16rpx; padding: 28rpx; margin-bottom: 24rpx;
		border-left: 8rpx solid #2E7D32;
	}
	.q-detail.wrong { border-left-color: #E53935; }

	.qd-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14rpx; }
	.qd-index { font-size: 26rpx; color: #999; }
	.qd-tag { font-size: 26rpx; padding: 6rpx 18rpx; border-radius: 20rpx; font-weight: bold; }
	.tag-good { background: #E8F5E9; color: #2E7D32; }
	.tag-bad { background: #FFEBEE; color: #E53935; }
	.qd-score-extra { font-weight: normal; font-size: 24rpx; }

	.qd-q {
		font-size: 30rpx; color: #222; line-height: 1.6; margin-bottom: 20rpx;
	}
	.qd-q-type {
		display: inline-block; font-size: 22rpx; color: #1565C0;
		background: #E3F2FD; padding: 2rpx 12rpx; border-radius: 8rpx;
		margin-right: 10rpx;
	}

	/* 选项 */
	.qd-options { margin-bottom: 16rpx; }
	.opt-item {
		display: flex; align-items: flex-start; gap: 14rpx;
		padding: 16rpx 20rpx; border-radius: 12rpx; margin-bottom: 12rpx;
		background: #F8F9FA; border: 2rpx solid #eee;
	}
	.opt-key {
		width: 40rpx; height: 40rpx; line-height: 40rpx; text-align: center;
		border-radius: 50%; background: #ddd; color: #555;
		font-size: 26rpx; font-weight: bold; flex-shrink: 0;
	}
	.opt-text { flex: 1; font-size: 28rpx; color: #333; line-height: 1.5; padding-top: 4rpx; }
	.opt-mark { font-size: 32rpx; color: #2E7D32; font-weight: bold; }
	.opt-mark.wrong { color: #E53935; }

	/* 正确选项高亮 */
	.opt-correct { background: #E8F5E9; border-color: #2E7D32; }
	.opt-correct .opt-key { background: #2E7D32; color: #fff; }
	.opt-correct .opt-text { color: #1B5E20; font-weight: bold; }

	/* 用户选错的选项高亮 */
	.opt-wrong { background: #FFEBEE; border-color: #E53935; }
	.opt-wrong .opt-key { background: #E53935; color: #fff; }
	.opt-wrong .opt-text { color: #C62828; }

	.bool-opt {
		display: inline-flex; align-items: center; gap: 10rpx;
		padding: 12rpx 30rpx; border-radius: 40rpx; margin-right: 20rpx; margin-bottom: 10rpx;
		background: #F8F9FA; border: 2rpx solid #eee; font-size: 28rpx;
	}

	/* 答案标签 */
	.qd-ans-row { display: flex; flex-wrap: wrap; gap: 14rpx; margin-bottom: 14rpx; }
	.ans-tag { font-size: 24rpx; padding: 8rpx 16rpx; border-radius: 8rpx; }
	.wrong-tag { background: #FFEBEE; color: #E53935; }
	.right-tag { background: #E8F5E9; color: #2E7D32; }

	/* 解析 */
	.qd-analysis {
		background: #FFFDE7; border-radius: 10rpx; padding: 14rpx 20rpx;
		border-left: 4rpx solid #F9A825;
	}
	.analysis-head {
		display: flex; justify-content: space-between; align-items: center;
	}
	.analysis-title { font-size: 24rpx; color: #E65100; font-weight: bold; }
	.analysis-toggle { font-size: 22rpx; color: #F9A825; }
	.analysis-body {
		margin-top: 10rpx; font-size: 26rpx; color: #5D4037; line-height: 1.7;
	}

	/* 底部按钮 */
	.footer-btns {
		position: fixed; bottom: 0; left: 0; right: 0; display: flex;
		padding: 20rpx 28rpx; background: #fff; gap: 20rpx; border-top: 2rpx solid #eee;
		padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	}
	.fb { flex: 1; text-align: center; padding: 26rpx 0; border-radius: 50rpx; font-size: 30rpx; font-weight: bold; }
	.fb-home { background: #f0f0f0; color: #555; }
	.fb-retry { background: linear-gradient(135deg, #1565C0, #42A5F5); color: #fff; }
</style>
