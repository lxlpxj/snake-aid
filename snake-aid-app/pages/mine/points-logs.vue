<template>
        <view class="points-logs">
                <view v-if="loading" class="empty">加载中...</view>
                <view v-else-if="!logs.length" class="empty">
                        <view class="empty-icon">📭</view>
                        <view class="empty-text">暂无积分记录</view>
                        <view class="empty-sub">多学习蛇种、考试拿高分就能获得积分哦！</view>
                </view>
                <view v-else>
                        <view v-for="item in logs" :key="item.id" class="log-item">
                                <view class="log-left">
                                        <text class="log-icon">{{ eventIcon(item.eventType) }}</text>
                                        <view class="log-info">
                                                <view class="log-desc">{{ item.description || eventLabel(item.eventType) }}</view>
                                                <view class="log-time">{{ formatTime(item.createTime) }}</view>
                                        </view>
                                </view>
                                <text class="log-pts">+{{ item.points }}</text>
                        </view>
                </view>
        </view>
</template>

<script>
const TOKEN_KEY = 'snake_token'
const API = 'http://127.0.0.1:8080/api'

export default {
        data() { return { token: '', logs: [], loading: true } },
        onShow() {
                this.token = uni.getStorageSync(TOKEN_KEY) || ''
                this.load()
        },
        methods: {
                async load() {
                        try {
                                const res = await new Promise((resolve, reject) => {
                                        uni.request({
                                                url: API + '/app/points/logs?current=1&size=50',
                                                header: { 'Authorization': 'Bearer ' + this.token },
                                                success: resolve, fail: reject
                                        })
                                })
                                if (res.data?.code === 200) {
                                        this.logs = res.data.data?.records || []
                                }
                        } catch (e) {}
                        this.loading = false
                },
                eventIcon(e) {
                        return { snake_view: '🐍', guide_read: '📖', article_read: '📚',
                                 quiz_submit: '📝', quiz_excellent: '🎉', hospital_view: '🏥',
                                 register_bonus: '🎁', quiz_complete_bonus: '🏆' }[e] || '⭐'
                },
                eventLabel(e) {
                        return { snake_view: '学习蛇种', guide_read: '阅读指南', article_read: '阅读文章',
                                 quiz_submit: '完成考试', quiz_excellent: '考试优秀', hospital_view: '查看医院',
                                 register_bonus: '注册奖励' }[e] || e
                },
                formatTime(t) {
                        if (!t) return ''
                        const d = new Date(t)
                        const pad = n => n < 10 ? '0' + n : n
                        return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
                }
        }
}
</script>

<style>
.points-logs { padding: 24rpx; min-height: 100vh; }
.empty { padding: 200rpx 0; text-align: center; color: #999; }
.empty-icon { font-size: 100rpx; margin-bottom: 20rpx; }
.empty-text { font-size: 32rpx; color: #666; }
.empty-sub { font-size: 24rpx; color: #999; margin-top: 12rpx; }
.log-item {
        display: flex; justify-content: space-between; align-items: center;
        background: #FFF; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx;
        box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.log-left { display: flex; align-items: center; gap: 20rpx; flex: 1; }
.log-icon { font-size: 44rpx; }
.log-info { flex: 1; }
.log-desc { font-size: 28rpx; color: #333; }
.log-time { font-size: 22rpx; color: #999; margin-top: 6rpx; }
.log-pts { font-size: 32rpx; font-weight: bold; color: #FF6F00; }
</style>


