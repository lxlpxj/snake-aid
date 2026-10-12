<template>
	<view class="quiz-page">
		<!-- 顶部状态栏 -->
		<view class="top-bar">
			<view class="progress">{{ current + 1 }} / {{ questions.length }}</view>
			<view class="timer" :class="{ warn: remaining < 60 }">
				⏱ {{ formatTime(remaining) }}
			</view>
			<view class="submit-btn" @click="confirmSubmit">交卷</view>
		</view>

		<!-- 进度条 -->
		<view class="progress-bar">
			<view class="progress-fill" :style="{ width: progressPct + '%' }"></view>
		</view>

		<!-- 题目卡片 -->
		<view class="q-card" v-if="questions.length > 0">
			<view class="q-header">
				<text class="q-index">第 {{ current + 1 }} 题</text>
				<text class="q-type">{{ typeMap[questions[current].questionType] }}</text>
				<text class="q-score">{{ questions[current].score }} 分</text>
			</view>
			<view class="q-text">{{ questions[current].question }}</view>

			<!-- 选项 -->
			<view class="opt-list">
				<view class="opt-item" v-for="(opt, idx) in options" :key="idx"
					:class="{ selected: isSelected(idx), disabled: opt === '' }"
					@click="opt !== '' && selectOption(idx)">
					<view class="opt-letter">{{ letters[idx] }}</view>
					<view class="opt-text">{{ opt }}</view>
					<view class="opt-check" v-if="isSelected(idx)">✓</view>
				</view>
			</view>

			<!-- 多选题提示 -->
			<view class="multi-hint" v-if="questions[current].questionType === 2">
				💡 多选题：可选择多个选项
			</view>
		</view>

		<!-- 底部导航 -->
		<view class="bottom-bar">
			<view class="nav-btn" :class="{ disabled: current === 0 }" @click="prev">上一题</view>
			<view class="jump-grid">
				<view class="jump-item" v-for="(q, i) in questions" :key="i"
					:class="{ current: i === current, answered: answers[i] }"
					@click="current = i">{{ i + 1 }}</view>
			</view>
			<view class="nav-btn" :class="{ disabled: current === questions.length - 1 }" @click="next">
				{{ current === questions.length - 1 ? '检查' : '下一题' }}
			</view>
		</view>
	</view>
</template>

<script>
	const API = '/api'
	export default {
		data() {
			return {
				category: '',
				count: 10,
				questions: [],
				answers: {}, // { questionIndex: userAnswerString }
				current: 0,
				totalSec: 0,
				remaining: 0,
				timer: null,
				letters: ['A', 'B', 'C', 'D'],
				typeMap: { 1: '单选题', 2: '多选题', 3: '判断题' },
			}
		},
		computed: {
			q() { return this.questions[this.current] || {} },
			options() {
				const q = this.q
				if (!q) return []
				return [q.optionA || '', q.optionB || '', q.optionC || '', q.optionD || '']
			},
			progressPct() {
				if (!this.questions.length) return 0
				return ((this.current + 1) / this.questions.length) * 100
			},
		},
		onLoad(options) {
			this.category = options.category || ''
			this.count = parseInt(options.count) || 10
			this.loadQuestions()
		},
		onUnload() {
			this.stopTimer()
		},
		methods: {
			loadQuestions() {
				uni.showLoading({ title: '抽题中...' })
				const token = uni.getStorageSync('token')
				uni.request({
					url: API + '/app/quiz/pick',
					data: { count: this.count, category: this.category || undefined },
					header: token ? { Authorization: 'Bearer ' + token } : {},
					success: (res) => {
						uni.hideLoading()
						if (res.data && res.data.code === 200 && res.data.data.length > 0) {
							this.questions = res.data.data
							this.totalSec = this.questions.length * 90 // 每题 90 秒
							this.remaining = this.totalSec
							this.startTimer()
						} else {
							uni.showToast({ title: '题库暂无题目', icon: 'none' })
							setTimeout(() => uni.navigateBack(), 1500)
						}
					},
					fail: () => { uni.hideLoading(); uni.showToast({ title: '网络错误', icon: 'none' }) }
				})
			},
			startTimer() {
				this.timer = setInterval(() => {
					this.remaining--
					if (this.remaining <= 0) {
						this.stopTimer()
						this.autoSubmit()
					}
				}, 1000)
			},
			stopTimer() { if (this.timer) { clearInterval(this.timer); this.timer = null } },
			formatTime(s) {
				const m = Math.floor(s / 60), sec = s % 60
				return `${m.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
			},
			isSelected(idx) {
				const userAns = this.answers[this.current] || ''
				const letter = this.letters[idx]
				if (this.q.questionType === 2) {
					return userAns.includes(letter)
				}
				return userAns === letter
			},
			selectOption(idx) {
				const letter = this.letters[idx]
				if (this.q.questionType === 2) {
					// 多选
					let ans = this.answers[this.current] || ''
					if (ans.includes(letter)) {
						ans = ans.replace(letter, '')
					} else {
						ans = ans + letter
					}
					this.answers[this.current] = ans.split('').sort().join('')
				} else {
					this.answers[this.current] = letter
				}
				this.$forceUpdate()
			},
			prev() { if (this.current > 0) this.current-- },
			next() {
				if (this.current < this.questions.length - 1) this.current++
				else this.current = this.questions.length - 1
			},
			confirmSubmit() {
				const unanswered = this.questions.filter((_, i) => !this.answers[i]).length
				let msg = '确定交卷吗？'
				if (unanswered > 0) msg = `还有 ${unanswered} 题未作答，确定交卷吗？`
				uni.showModal({
					title: '交卷确认',
					content: msg,
					success: (res) => { if (res.confirm) this.submit() }
				})
			},
			autoSubmit() {
				uni.showModal({
					title: '时间到！',
					content: '已自动交卷',
					showCancel: false,
					success: () => this.submit()
				})
			},
			submit() {
				this.stopTimer()
				uni.showLoading({ title: '评分中...' })
				const token = uni.getStorageSync('token')
				const details = this.questions.map((q, i) => ({
					questionId: q.id,
					userAnswer: this.answers[i] || '',
				}))
				uni.request({
					url: API + '/app/quiz/submit',
					method: 'POST',
					data: { details, durationSec: this.totalSec - this.remaining },
					header: { 'Content-Type': 'application/json', Authorization: 'Bearer ' + token },
					success: (res) => {
						uni.hideLoading()
						if (res.data && res.data.code === 200) {
							const quizId = res.data.data.quizId || res.data.data.id
							uni.redirectTo({ url: `/pages/quiz/result?id=${quizId}` })
						} else {
							uni.showToast({ title: res.data?.message || '提交失败', icon: 'none' })
						}
					},
					fail: () => { uni.hideLoading(); uni.showToast({ title: '网络错误', icon: 'none' }) }
				})
			}
		}
	}
</script>

<style scoped>
	.quiz-page { min-height: 100vh; background: #f5f7fa; padding-bottom: 180rpx; }
	.top-bar {
		display: flex; justify-content: space-between; align-items: center;
		padding: 24rpx 28rpx; background: #fff;
	}
	.progress { font-size: 28rpx; color: #666; }
	.timer { font-size: 32rpx; font-weight: bold; color: #1565C0; }
	.timer.warn { color: #E53935; }
	.submit-btn {
		background: #E53935; color: #fff; padding: 12rpx 28rpx;
		border-radius: 30rpx; font-size: 26rpx;
	}
	.progress-bar { height: 6rpx; background: #eee; }
	.progress-fill { height: 100%; background: linear-gradient(90deg, #1565C0, #42A5F5); transition: width 0.3s; }

	.q-card { margin: 24rpx; background: #fff; border-radius: 20rpx; padding: 32rpx; box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.05); }
	.q-header { display: flex; align-items: center; gap: 16rpx; margin-bottom: 20rpx; }
	.q-index { font-size: 24rpx; color: #999; }
	.q-type { background: #E3F2FD; color: #1565C0; font-size: 22rpx; padding: 4rpx 14rpx; border-radius: 6rpx; }
	.q-score { background: #FFF3E0; color: #E65100; font-size: 22rpx; padding: 4rpx 14rpx; border-radius: 6rpx; }
	.q-text { font-size: 32rpx; color: #222; line-height: 1.6; margin-bottom: 32rpx; }

	.opt-list { display: flex; flex-direction: column; gap: 20rpx; }
	.opt-item {
		display: flex; align-items: center; background: #f8f9fb;
		border-radius: 14rpx; padding: 24rpx; border: 3rpx solid transparent;
		transition: all 0.15s;
	}
	.opt-item.selected { border-color: #1565C0; background: #E3F2FD; }
	.opt-item.disabled { opacity: 0.4; }
	.opt-letter {
		width: 52rpx; height: 52rpx; line-height: 52rpx; text-align: center;
		background: #fff; color: #666; border-radius: 50%; font-size: 26rpx; font-weight: bold;
		margin-right: 20rpx; flex-shrink: 0;
	}
	.opt-item.selected .opt-letter { background: #1565C0; color: #fff; }
	.opt-text { flex: 1; font-size: 28rpx; color: #333; line-height: 1.5; }
	.opt-check { color: #1565C0; font-size: 36rpx; margin-left: 10rpx; }
	.multi-hint { font-size: 22rpx; color: #999; margin-top: 20rpx; padding-left: 8rpx; }

	.bottom-bar {
		position: fixed; bottom: 0; left: 0; right: 0; background: #fff;
		padding: 20rpx 28rpx; border-top: 2rpx solid #eee;
	}
	.bottom-bar .nav-btn {
		position: absolute; top: 50%; transform: translateY(-50%);
		background: #1565C0; color: #fff; padding: 14rpx 28rpx;
		border-radius: 30rpx; font-size: 26rpx;
	}
	.bottom-bar .nav-btn.disabled { background: #ccc; }
	.bottom-bar .nav-btn:first-of-type { left: 28rpx; }
	.bottom-bar .nav-btn:last-of-type { right: 28rpx; }
	.jump-grid {
		display: flex; flex-wrap: wrap; gap: 10rpx 12rpx; justify-content: center;
		padding: 4rpx 80rpx;
	}
	.jump-item {
		width: 56rpx; height: 56rpx; line-height: 56rpx; text-align: center;
		background: #f0f0f0; border-radius: 10rpx; font-size: 24rpx; color: #666;
	}
	.jump-item.current { background: #1565C0; color: #fff; font-weight: bold; }
	.jump-item.answered { background: #E8F5E9; color: #2E7D32; }
</style>


