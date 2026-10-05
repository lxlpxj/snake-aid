<template>
	<view class="ai-chat">
		<!-- 顶部 -->
		<view class="chat-header">
			<view class="avatar">🤖</view>
			<view class="header-info">
				<text class="header-title">蛇伤急救 AI 助手</text>
				<text class="header-sub">24 小时在线 · 即时回复</text>
			</view>
			<view class="clear-btn" @click="clearHistory">清空</view>
		</view>

		<!-- 消息列表 -->
		<scroll-view class="msg-list" scroll-y :scroll-into-view="scrollToId">
			<view class="msg-bubble welcome" v-if="!messages.length">
				<text>🐍 你好！我是**蛇伤急救 AI 助手** 🐍</text>
				<text>我可以帮你：</text>
				<text>📸 识别蛇种 · 🚨 急救指导 · 🩺 症状判断</text>
				<text>🏥 找血清 · 🛡️ 预防方法</text>
			</view>

			<view v-for="(m, idx) in messages" :key="idx" :id="'msg-' + idx"
				  :class="['msg-row', m.role === 'user' ? 'user' : 'ai']">
				<view class="msg-avatar">{{ m.role === 'user' ? '👤' : '🤖' }}</view>
				<view :class="['msg-bubble', m.role === 'user' ? 'user' : 'ai']">
					<text class="msg-text">{{ m.content }}</text>
					<text class="msg-time">{{ formatTime(m.createTime) }}</text>
				</view>
			</view>

			<view v-if="loading" class="msg-row ai">
				<view class="msg-avatar">🤖</view>
				<view class="msg-bubble ai">
					<text class="loading-dots">思考中<span>...</span></text>
				</view>
			</view>
		</scroll-view>

		<!-- 快捷提问 -->
		<view class="quick-questions" v-if="!messages.length">
			<text class="qq-title">💡 试试这些问题：</text>
			<view class="qq-list">
				<view v-for="q in quickQs" :key="q" class="qq-item" @click="sendQuick(q)">{{ q }}</view>
			</view>
		</view>

		<!-- 输入区 -->
		<view class="input-bar">
			<input class="msg-input" v-model="inputText" placeholder="输入你的问题..."
				   confirm-type="send" @confirm="send" />
			<view class="send-btn" @click="send" :class="{ disabled: loading || !inputText.trim() }">发送</view>
		</view>
	</view>
</template>

<script>
const API = 'http://127.0.0.1:8080/api'

export default {
	data() {
		return {
			token: '',
			messages: [],
			inputText: '',
			loading: false,
			scrollToId: '',
			quickQs: [
				'银环蛇有毒吗？',
				'被蛇咬了怎么办？',
				'五步蛇有什么症状？',
				'附近哪里有血清？',
				'野外如何防蛇？'
			]
		}
	},
	onShow() {
		this.token = uni.getStorageSync('token') || ''
		this.loadHistory()
	},
	methods: {
		sendQuick(q) {
			this.inputText = q
			this.send()
		},
		async send() {
			const text = this.inputText.trim()
			if (!text || this.loading) return
			this.inputText = ''

			// 先本地显示用户消息
			this.messages.push({ role: 'user', content: text, createTime: Date.now() })
			this.scrollToBottom()

			this.loading = true
			try {
				const res = await new Promise((resolve, reject) => {
					uni.request({
						url: API + '/app/ai/chat',
						method: 'POST',
						data: { message: text },
						header: { 'Authorization': 'Bearer ' + this.token, 'Content-Type': 'application/json' },
						success: resolve, fail: reject
					})
				})
				this.loading = false
				if (res.data && res.data.code === 200) {
					this.messages.push({
						role: 'assistant',
						content: res.data.data.reply,
						createTime: Date.now()
					})
				} else {
					this.messages.push({ role: 'assistant', content: '抱歉，我暂时无法回答这个问题 😢', createTime: Date.now() })
				}
				this.scrollToBottom()
			} catch (e) {
				this.loading = false
				this.messages.push({ role: 'assistant', content: '网络错误，请稍后再试', createTime: Date.now() })
			}
		},
		async loadHistory() {
			try {
				const res = await new Promise((resolve, reject) => {
					uni.request({
						url: API + '/app/ai/history',
						header: { 'Authorization': 'Bearer ' + this.token },
						success: resolve, fail: reject
					})
				})
				if (res.data?.code === 200 && res.data.data) {
					// 后端返回的是倒序，反转一下
					this.messages = res.data.data.reverse().map(h => ({
						role: h.role === 'assistant' ? 'ai' : 'user',
						content: h.content,
						createTime: h.createTime
					}))
					this.$nextTick(() => this.scrollToBottom())
				}
			} catch (e) {}
		},
		clearHistory() {
			if (!this.messages.length) return
			uni.showModal({
				title: '确认', content: '清空对话历史？',
				success: async (r) => {
					if (r.confirm) {
						this.messages = []
						try { await new Promise((res, rej) => uni.request({
							url: API + '/app/ai/history', method: 'DELETE',
							header: { 'Authorization': 'Bearer ' + this.token },
							success: res, fail: rej
						})) } catch (e) {}
					}
				}
			})
		},
		scrollToBottom() {
			this.$nextTick(() => {
				if (this.messages.length) {
					this.scrollToId = 'msg-' + (this.messages.length - 1)
				}
			})
		},
		formatTime(t) {
			if (!t) return ''
			const d = new Date(t)
			const pad = n => n < 10 ? '0' + n : n
			return `${pad(d.getHours())}:${pad(d.getMinutes())}`
		}
	}
}
</script>

<style>
.ai-chat { display: flex; flex-direction: column; height: 100vh; background: #F5F7FA; }
.chat-header {
	display: flex; align-items: center; padding: 20rpx 24rpx;
	background: linear-gradient(135deg, #2E7D32, #43A047); color: #FFF;
}
.avatar { width: 72rpx; height: 72rpx; border-radius: 50%; background: rgba(255,255,255,0.2);
	display: flex; align-items: center; justify-content: center; font-size: 40rpx; }
.header-info { flex: 1; margin-left: 16rpx; }
.header-title { font-size: 32rpx; font-weight: bold; display: block; }
.header-sub { font-size: 22rpx; opacity: 0.8; }
.clear-btn { font-size: 24rpx; padding: 8rpx 20rpx; background: rgba(255,255,255,0.2); border-radius: 24rpx; }

.msg-list { flex: 1; padding: 24rpx; }
.msg-row { display: flex; margin-bottom: 24rpx; align-items: flex-start; }
.msg-row.ai { flex-direction: row; }
.msg-row.user { flex-direction: row-reverse; }
.msg-avatar { width: 60rpx; height: 60rpx; border-radius: 50%;
	display: flex; align-items: center; justify-content: center; font-size: 32rpx; flex-shrink: 0; }

.msg-bubble { max-width: 75%; padding: 20rpx 24rpx; border-radius: 20rpx; font-size: 28rpx; line-height: 1.6; white-space: pre-wrap; }
.msg-bubble.ai { background: #FFF; color: #333; border-top-left-radius: 6rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.06); margin-left: 12rpx; }
.msg-bubble.user { background: #4CAF50; color: #FFF; border-top-right-radius: 6rpx; margin-right: 12rpx; }
.msg-bubble.welcome { background: #FFF; color: #555; padding: 32rpx; text-align: center; font-size: 28rpx; }
.msg-bubble.welcome text { display: block; margin: 6rpx 0; }
.msg-time { font-size: 20rpx; opacity: 0.5; display: block; margin-top: 8rpx; }
.loading-dots { color: #999; font-size: 26rpx; }

.quick-questions { padding: 0 24rpx 16rpx; background: #F5F7FA; }
.qq-title { font-size: 24rpx; color: #888; display: block; margin-bottom: 12rpx; }
.qq-list { display: flex; flex-wrap: wrap; gap: 12rpx; }
.qq-item {
	padding: 12rpx 20rpx; background: #FFF; border-radius: 24rpx;
	font-size: 24rpx; color: #43A047; border: 1rpx solid #A5D6A7;
}

.input-bar { display: flex; gap: 16rpx; padding: 20rpx 24rpx 30rpx; background: #FFF; border-top: 1rpx solid #EEE; }
.msg-input { flex: 1; padding: 16rpx 24rpx; background: #F5F5F5; border-radius: 32rpx; font-size: 28rpx; }
.send-btn { padding: 16rpx 32rpx; background: #4CAF50; color: #FFF; border-radius: 32rpx; font-size: 28rpx; font-weight: bold; }
.send-btn.disabled { background: #CCC; }
</style>
