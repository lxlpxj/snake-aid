<template>
	<view class="consult-page">
		<!-- ========== 创建新咨询 ========== -->
		<view class="create-card" v-if="!activeTicket">
			<view class="create-title">👩‍⚕️ 咨询医疗顾问</view>
			<view class="create-sub">由专业医生为你在线解答，一般 1-2 小时内回复</view>

			<view class="form-group">
				<text class="form-label">咨询分类</text>
				<picker :range="categories" range-key="label" @change="onCatChange">
					<view class="picker-box">{{ categories[catIdx].label }} ›</view>
				</picker>
			</view>

			<view class="form-group">
				<text class="form-label">问题标题</text>
				<input class="title-input" v-model="title" placeholder="一句话描述你的问题" maxlength="30" />
			</view>

			<view class="form-group">
				<text class="form-label">详细描述</text>
				<textarea class="desc-input" v-model="content" placeholder="详细描述症状、时间、地点等信息..."
						  maxlength="500" />
			</view>

			<view class="submit-btn" @click="submitTicket">提交咨询</view>
		</view>

		<!-- ========== 工单详情（对话）========== -->
		<view v-else class="ticket-chat">
			<view class="ticket-header">
				<view class="back-btn" @click="backToList">‹ 返回</view>
				<view class="t-header-info">
					<text class="t-title">{{ activeTicket.title }}</text>
					<text class="t-status" :class="activeTicket.status">{{ statusLabel(activeTicket.status) }}</text>
				</view>
				<view class="t-advisor" v-if="activeTicket.advisorName">👩‍⚕️ {{ activeTicket.advisorName }}</view>
				<view class="del-ticket-btn" @click="onDeleteTicket">🗑️</view>
			</view>

			<scroll-view class="chat-msg" scroll-y :scroll-into-view="'m-' + (msgs.length - 1)">
				<view v-for="(m, idx) in msgs" :key="m.id" :id="'m-' + idx"
					  :class="['chat-row', m.senderType === 'USER' ? 'me' : 'doc']">
					<view class="chat-avatar">{{ m.senderType === 'USER' ? '👤' : '👩‍⚕️' }}</view>
					<view :class="['chat-bubble', m.senderType === 'USER' ? 'me' : 'doc']">
						<text>{{ m.content }}</text>
						<view class="chat-meta">
							<text class="chat-time">{{ formatTime(m.createTime) }}</text>
							<text v-if="m.senderType === 'USER'" class="chat-del" @click="onDeleteMsg(m, idx)">删除</text>
						</view>
					</view>
				</view>
			</scroll-view>

			<view class="reply-bar">
				<input class="reply-input" v-model="replyText" placeholder="输入消息..." />
				<view class="reply-send" @click="sendReply">发送</view>
			</view>
		</view>

		<!-- ========== 我的工单列表 ========== -->
		<view class="ticket-list" v-if="!activeTicket && tickets.length">
			<view class="list-title">📋 我的咨询记录</view>
			<view v-for="t in tickets" :key="t.id" class="ticket-card">
				<view class="tc-main" @click="openTicket(t)">
					<view class="tc-header">
						<text class="tc-title">{{ t.title }}</text>
						<text class="tc-status" :class="t.status">{{ statusLabel(t.status) }}</text>
					</view>
					<text class="tc-cat">{{ t.category }}</text>
					<text class="tc-preview">{{ t.lastMessage }}</text>
					<view class="tc-footer">
						<text class="tc-advisor" v-if="t.advisorName">👩‍⚕️ {{ t.advisorName }}</text>
						<text class="tc-time">{{ formatTime(t.lastTime) }}</text>
					</view>
				</view>
				<view class="tc-del" @click.stop="onDeleteTicketFromList(t)">删除</view>
			</view>
		</view>

		<view v-if="!activeTicket && !tickets.length" class="empty">
			<view class="empty-icon">👩‍⚕️</view>
			<view class="empty-text">还没有咨询记录</view>
			<view class="empty-sub">在上方提交你的问题，医疗顾问会尽快回复</view>
		</view>
	</view>
</template>

<script>
const API = 'http://127.0.0.1:8080/api'

export default {
	data() {
		return {
			token: '',
			categories: [
				{ label: '蛇伤急救', value: '蛇伤急救' },
				{ label: '蛇种识别', value: '蛇种识别' },
				{ label: '症状咨询', value: '症状咨询' },
				{ label: '医院推荐', value: '医院推荐' },
				{ label: '其他问题', value: '其他' }
			],
			catIdx: 0,
			title: '',
			content: '',
			tickets: [],
			activeTicket: null,
			msgs: [],
			replyText: ''
		}
	},
	onShow() {
		this.token = uni.getStorageSync('token') || ''
		if (!this.token) {
			uni.showModal({
				title: '登录提示',
				content: '使用咨询功能需要先登录，是否前往登录？',
				confirmText: '去登录',
				cancelText: '取消',
				success: (r) => {
					if (r.confirm) uni.navigateTo({ url: '/pages/mine/login' })
					else uni.navigateBack({ delta: 1 })
				}
			})
			return
		}
		this.loadTickets()
	},
	methods: {
		requireLogin() {
			if (!this.token) {
				uni.showModal({
					title: '登录提示',
					content: '登录后才能发送消息，是否前往登录？',
					confirmText: '去登录', cancelText: '取消',
					success: (r) => { if (r.confirm) uni.navigateTo({ url: '/pages/mine/login' }) }
				})
				return false
			}
			return true
		},
		async submitTicket() {
			if (!this.requireLogin()) return
			if (!this.title.trim()) return uni.showToast({ title: '请输入标题', icon: 'none' })
			if (!this.content.trim()) return uni.showToast({ title: '请描述问题', icon: 'none' })

			try {
				const res = await new Promise((resolve, reject) => {
					uni.request({
						url: API + '/app/consult/ticket',
						method: 'POST',
						data: { title: this.title.trim(), category: this.categories[this.catIdx].value, message: this.content.trim() },
						header: { 'Authorization': 'Bearer ' + this.token, 'Content-Type': 'application/json' },
						success: resolve, fail: reject
					})
				})
				if (res.data?.code === 200) {
					uni.showToast({ title: '提交成功！', icon: 'success' })
					this.title = ''; this.content = ''
					this.loadTickets()
					// 自动打开新工单
					setTimeout(() => this.openTicket(res.data.data), 600)
				} else if (res.data?.code === 401) {
					uni.showModal({
						title: '登录已过期',
						content: '登录状态已失效，是否重新登录？',
						confirmText: '去登录', cancelText: '取消',
						success: (r) => { if (r.confirm) uni.navigateTo({ url: '/pages/mine/login' }) }
					})
					return
				} else {
					uni.showToast({ title: res.data?.message || '提交失败', icon: 'none' })
				}
			} catch (e) {
				uni.showToast({ title: '网络错误', icon: 'none' })
			}
		},
		async loadTickets() {
			try {
				const res = await new Promise((resolve, reject) => {
					uni.request({
						url: API + '/app/consult/tickets',
						header: { 'Authorization': 'Bearer ' + this.token },
						success: resolve, fail: reject
					})
				})
				if (res.data?.code === 200) this.tickets = res.data.data || []
			else if (res.data?.code === 401) return
			} catch (e) {}
		},
		async openTicket(t) {
			this.activeTicket = t
			try {
				const res = await new Promise((resolve, reject) => {
					uni.request({
						url: API + '/app/consult/ticket/' + t.id,
						header: { 'Authorization': 'Bearer ' + this.token },
						success: resolve, fail: reject
					})
				})
				if (res.data?.code === 200) {
					this.msgs = res.data.data?.messages || []
					this.activeTicket = res.data.data
					this.$nextTick(() => {
						if (this.msgs.length) this.scrollToBottom()
					})
				}
			} catch (e) {}
		},
		async sendReply() {
			const text = this.replyText.trim()
			if (!text) return
			try {
				const res = await new Promise((resolve, reject) => {
					uni.request({
						url: API + '/app/consult/ticket/' + this.activeTicket.id + '/reply',
						method: 'POST',
						data: { content: text },
						header: { 'Authorization': 'Bearer ' + this.token, 'Content-Type': 'application/json' },
						success: resolve, fail: reject
					})
				})
				if (res.data?.code === 200) {
					this.msgs.push(res.data.data)
					this.replyText = ''
					this.$nextTick(() => this.scrollToBottom())
				}
			} catch (e) {}
		},
		backToList() {
			this.activeTicket = null
			this.msgs = []
			this.loadTickets()
		},
		onCatChange(e) { this.catIdx = parseInt(e.detail.value) },
		statusLabel(s) {
			return { PENDING: '待处理', IN_PROGRESS: '处理中', RESOLVED: '已解决', CLOSED: '已关闭' }[s] || s
		},
		scrollToBottom() {
			if (this.msgs.length) {
				uni.pageScrollTo({ scrollTop: 99999, duration: 200 })
			}
		},
		formatTime(t) {
			if (!t) return ''
			const d = new Date(t)
			const pad = n => n < 10 ? '0' + n : n
			return `${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
		},
		onDeleteMsg(m, idx) {
			uni.showModal({
				title: '删除消息',
				content: '确定删除这条消息？',
				success: async (r) => {
					if (!r.confirm) return
					try {
						const res = await new Promise((resolve, reject) => {
							uni.request({
								url: API + '/app/consult/message/' + m.id,
								method: 'DELETE',
								header: { 'Authorization': 'Bearer ' + this.token },
								success: resolve, fail: reject
							})
						})
						if (res.data?.code === 200) {
							this.msgs.splice(idx, 1)
							uni.showToast({ title: '已删除', icon: 'success' })
						} else {
							uni.showToast({ title: res.data?.message || '删除失败', icon: 'none' })
						}
					} catch (e) {
						uni.showToast({ title: '网络错误', icon: 'none' })
					}
				}
			})
		},
		onDeleteTicket() {
			uni.showModal({
				title: '删除工单',
				content: '确定删除整个工单及所有消息？',
				success: async (r) => {
					if (!r.confirm) return
					try {
						const res = await new Promise((resolve, reject) => {
							uni.request({
								url: API + '/app/consult/ticket/' + this.activeTicket.id,
								method: 'DELETE',
								header: { 'Authorization': 'Bearer ' + this.token },
								success: resolve, fail: reject
							})
						})
						if (res.data?.code === 200) {
							uni.showToast({ title: '已删除', icon: 'success' })
							this.backToList()
						} else {
							uni.showToast({ title: res.data?.message || '删除失败', icon: 'none' })
						}
					} catch (e) {
						uni.showToast({ title: '网络错误', icon: 'none' })
					}
				}
			})
		},
		onDeleteTicketFromList(t) {
			uni.showModal({
				title: '删除工单',
				content: `确定删除"${t.title}"？`,
				success: async (r) => {
					if (!r.confirm) return
					try {
						const res = await new Promise((resolve, reject) => {
							uni.request({
								url: API + '/app/consult/ticket/' + t.id,
								method: 'DELETE',
								header: { 'Authorization': 'Bearer ' + this.token },
								success: resolve, fail: reject
							})
						})
						if (res.data?.code === 200) {
							uni.showToast({ title: '已删除', icon: 'success' })
							this.loadTickets()
						} else {
							uni.showToast({ title: res.data?.message || '删除失败', icon: 'none' })
						}
					} catch (e) {
						uni.showToast({ title: '网络错误', icon: 'none' })
					}
				}
			})
		}
	}
}
</script>

<style>
.consult-page { min-height: 100vh; background: #F5F7FA; padding-bottom: 40rpx; }

.create-card { margin: 24rpx; background: #FFF; border-radius: 20rpx; padding: 32rpx; box-shadow: 0 2rpx 16rpx rgba(0,0,0,0.06); }
.create-title { font-size: 36rpx; font-weight: bold; color: #333; }
.create-sub { font-size: 24rpx; color: #888; margin-top: 8rpx; margin-bottom: 32rpx; }
.form-group { margin-bottom: 24rpx; }
.form-label { font-size: 26rpx; color: #555; display: block; margin-bottom: 12rpx; }
.picker-box, .title-input {
	padding: 20rpx 24rpx; background: #F8F9FA; border-radius: 12rpx; font-size: 28rpx; color: #333;
}
.picker-box { display: flex; justify-content: space-between; align-items: center; }
.desc-input {
	width: 100%; height: 200rpx; padding: 20rpx 24rpx; background: #F8F9FA; border-radius: 12rpx;
	font-size: 28rpx; box-sizing: border-box;
}
.submit-btn {
	margin-top: 24rpx; padding: 28rpx; background: linear-gradient(90deg, #4CAF50, #2E7D32);
	color: #FFF; text-align: center; border-radius: 32rpx; font-size: 32rpx; font-weight: bold;
}

/* 工单列表 */
.ticket-list { margin-top: 16rpx; }
.list-title { padding: 24rpx 32rpx 12rpx; font-size: 26rpx; color: #888; }
.ticket-card {
	margin: 16rpx 24rpx; background: #FFF; border-radius: 16rpx; padding: 24rpx;
	box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04); display: flex; align-items: flex-start; gap: 16rpx;
}
.tc-main { flex: 1; }
.tc-header { display: flex; justify-content: space-between; align-items: center; }
.tc-title { font-size: 30rpx; font-weight: bold; color: #333; flex: 1; }
.tc-cat { font-size: 22rpx; color: #43A047; background: #E8F5E9; padding: 4rpx 12rpx; border-radius: 8rpx; margin-right: 12rpx; }
.tc-preview { font-size: 26rpx; color: #666; margin: 12rpx 0; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.tc-footer { display: flex; justify-content: space-between; }
.tc-advisor { font-size: 22rpx; color: #888; }
.tc-time { font-size: 22rpx; color: #BBB; }
.tc-del { font-size: 24rpx; color: #E53935; padding: 8rpx 16rpx; border: 1rpx solid #FFCDD2; border-radius: 8rpx; flex-shrink: 0; }

/* 状态色 */
.tc-status, .t-status { font-size: 22rpx; padding: 6rpx 16rpx; border-radius: 16rpx; }
.tc-status.PENDING, .t-status.PENDING { background: #FFF3E0; color: #E65100; }
.tc-status.IN_PROGRESS, .t-status.IN_PROGRESS { background: #E3F2FD; color: #1565C0; }
.tc-status.RESOLVED, .t-status.RESOLVED { background: #E8F5E9; color: #2E7D32; }

/* 对话 */
.ticket-chat { display: flex; flex-direction: column; height: 100vh; background: #F5F7FA; }
.ticket-header {
	display: flex; align-items: center; padding: 20rpx 24rpx;
	background: linear-gradient(135deg, #1976D2, #42A5F5); color: #FFF;
}
.back-btn { font-size: 32rpx; padding: 0 16rpx; }
.t-header-info { flex: 1; }
.t-title { font-size: 30rpx; font-weight: bold; display: block; }
.t-status { display: inline-block; margin-top: 4rpx; }
.t-advisor { font-size: 24rpx; }
.del-ticket-btn { font-size: 36rpx; padding: 0 16rpx; }

.chat-msg { flex: 1; padding: 24rpx; }
.chat-row { display: flex; margin-bottom: 24rpx; align-items: flex-start; }
.chat-row.me { flex-direction: row-reverse; }
.chat-avatar { width: 56rpx; height: 56rpx; border-radius: 50%; background: #FFF;
	display: flex; align-items: center; justify-content: center; font-size: 30rpx; flex-shrink: 0; }
.chat-bubble { max-width: 75%; padding: 20rpx 24rpx; border-radius: 20rpx; font-size: 28rpx; line-height: 1.6; }
.chat-bubble.doc { background: #FFF; margin-left: 12rpx; border-top-left-radius: 6rpx; }
.chat-bubble.me { background: #42A5F5; color: #FFF; margin-right: 12rpx; border-top-right-radius: 6rpx; }
.chat-meta { display: flex; justify-content: space-between; align-items: center; margin-top: 8rpx; gap: 16rpx; }
.chat-time { font-size: 20rpx; opacity: 0.6; }
.chat-del { font-size: 22rpx; opacity: 0.7; text-decoration: underline; }

.reply-bar { display: flex; gap: 16rpx; padding: 20rpx 24rpx 30rpx; background: #FFF; border-top: 1rpx solid #EEE; }
.reply-input { flex: 1; padding: 16rpx 24rpx; background: #F5F5F5; border-radius: 32rpx; font-size: 28rpx; }
.reply-send { padding: 16rpx 32rpx; background: #1976D2; color: #FFF; border-radius: 32rpx; font-size: 28rpx; font-weight: bold; }

.empty { padding: 120rpx 0; text-align: center; }
.empty-icon { font-size: 120rpx; }
.empty-text { font-size: 30rpx; color: #666; margin-top: 20rpx; }
.empty-sub { font-size: 24rpx; color: #999; margin-top: 8rpx; }
</style>


