<template>
        <view class="leaderboard">
                <view class="lb-header">
                        <text class="lb-title">🏆 蛇伤急救达人榜</text>
                        <text class="lb-sub">学习越多急救知识，排名越高！</text>
                </view>

                <!-- TOP 3 领奖台 -->
                <view class="podium" v-if="top3.length">
                        <view class="pod-item second" v-if="top3[1]">
                                <view class="pod-avatar">{{ top3[1].levelIcon || '🥚' }}</view>
                                <view class="pod-name">{{ top3[1].nickname }}</view>
                                <view class="pod-pts">{{ top3[1].points }}分</view>
                                <view class="pod-badge">🥈</view>
                        </view>
                        <view class="pod-item first" v-if="top3[0]">
                                <view class="pod-crown">👑</view>
                                <view class="pod-avatar">{{ top3[0].levelIcon || '🥚' }}</view>
                                <view class="pod-name">{{ top3[0].nickname }}</view>
                                <view class="pod-pts">{{ top3[0].points }}分</view>
                                <view class="pod-badge">🥇</view>
                        </view>
                        <view class="pod-item third" v-if="top3[2]">
                                <view class="pod-avatar">{{ top3[2].levelIcon || '🥚' }}</view>
                                <view class="pod-name">{{ top3[2].nickname }}</view>
                                <view class="pod-pts">{{ top3[2].points }}分</view>
                                <view class="pod-badge">🥉</view>
                        </view>
                </view>

                <!-- 4 名及以后 -->
                <view class="lb-list">
                        <view v-for="(item, idx) in restList" :key="item.id" class="lb-row">
                                <text class="lb-rank">{{ idx + 4 }}</text>
                                <text class="lb-icon">{{ item.levelIcon || '🥚' }}</text>
                                <text class="lb-name">{{ item.nickname }}</text>
                                <text class="lb-lv">Lv.{{ item.level }}</text>
                                <text class="lb-pts">{{ item.points }}分</text>
                        </view>
                        <view v-if="!restList.length && !top3.length" class="empty">暂无用户数据</view>
                </view>
        </view>
</template>

<script>
const TOKEN_KEY = 'snake_token'
const API = 'http://127.0.0.1:8080/api'

export default {
        data() { return { list: [] } },
        computed: {
                top3() { return this.list.slice(0, 3) },
                restList() { return this.list.slice(3) }
        },
        onShow() {
                const token = uni.getStorageSync(TOKEN_KEY) || ''
                if (!token) return
                uni.request({
                        url: API + '/app/points/leaderboard?topN=10',
                        header: { 'Authorization': 'Bearer ' + token },
                        success: (res) => {
                                if (res.data?.code === 200) this.list = res.data.data || []
                        }
                })
        }
}
</script>

<style>
.leaderboard { padding: 24rpx; min-height: 100vh; background: linear-gradient(180deg, #FFF8E1, #FFF); }
.lb-header { text-align: center; padding: 20rpx 0 40rpx; }
.lb-title { font-size: 40rpx; font-weight: bold; color: #E65100; display: block; }
.lb-sub { font-size: 24rpx; color: #8D6E00; margin-top: 8rpx; }

.podium { display: flex; justify-content: center; align-items: flex-end; gap: 20rpx; margin-bottom: 40rpx; }
.pod-item { text-align: center; padding: 20rpx 16rpx; border-radius: 20rpx; min-width: 160rpx; }
.pod-item.first { background: linear-gradient(180deg, #FFD700, #FFA000); transform: scale(1.15); padding-top: 40rpx; }
.pod-item.second { background: linear-gradient(180deg, #E0E0E0, #9E9E9E); }
.pod-item.third { background: linear-gradient(180deg, #FFCC80, #FF8A65); }
.pod-crown { font-size: 44rpx; margin-bottom: -10rpx; }
.pod-avatar { font-size: 80rpx; }
.pod-name { font-size: 28rpx; font-weight: bold; color: #333; margin-top: 6rpx; }
.pod-pts { font-size: 24rpx; color: #666; }
.pod-badge { font-size: 32rpx; margin-top: 6rpx; }

.lb-list { background: #FFF; border-radius: 16rpx; overflow: hidden; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.06); }
.lb-row {
        display: flex; align-items: center; gap: 16rpx;
        padding: 24rpx; border-bottom: 1rpx solid #F5F5F5;
}
.lb-row:last-child { border-bottom: none; }
.lb-rank { width: 48rpx; font-size: 28rpx; font-weight: bold; color: #999; text-align: center; }
.lb-icon { font-size: 36rpx; }
.lb-name { flex: 1; font-size: 28rpx; color: #333; }
.lb-lv { font-size: 22rpx; color: #E65100; background: #FFF3E0; padding: 4rpx 12rpx; border-radius: 8rpx; }
.lb-pts { font-size: 28rpx; font-weight: bold; color: #FF6F00; }
.empty { padding: 80rpx; text-align: center; color: #999; font-size: 28rpx; }
</style>


