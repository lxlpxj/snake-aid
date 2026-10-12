<template>
	<view class="hospital-list">
		<!-- 省市区筛选（内置全国数据，不依赖数据库） -->
		<view class="locator">
			<picker mode="selector" :range="provinceNames" :value="provinceIndex" @change="onProvinceChange">
				<view class="picker-item">
					{{ selectedProvince || '选择省份' }}
					<text class="caret">▾</text>
				</view>
			</picker>
			<picker mode="selector" :range="cityNames" :value="cityIndex" :disabled="!selectedProvince" @change="onCityChange">
				<view class="picker-item" :class="{ placeholder: !selectedProvince }">
					{{ selectedCity || (selectedProvince ? '选择城市' : '—') }}
					<text class="caret">▾</text>
				</view>
			</picker>
			<view v-if="selectedProvince" class="clear-btn" @click="clearFilter">清除</view>
		</view>

		<!-- 提示条 -->
		<view class="tip-bar" v-if="selectedProvince">
			已筛选：{{ selectedProvince }}{{ selectedCity ? ' · ' + selectedCity : '' }}
			<text v-if="selectedCity" class="link" @click="selectedCity=''; cityIndex=-1; loadHospitals()">只看全省</text>
		</view>

		<!-- 医院列表 -->
		<view class="hospital-list-wrap">
			<view v-for="item in list" :key="item.id" class="hospital-card">
				<view class="hospital-top">
					<view class="name">{{ item.name }}</view>
				</view>
				<view class="meta">
					<text class="level">{{ item.level || '未评级' }}</text>
					<text class="region">{{ item.province }} · {{ item.city }}</text>
					<text v-if="item.hasSerum===1" class="badge-serum">✅ 有血清库存</text>
					<text v-else class="badge-noserum">⚠ 暂无血清</text>
				</view>
				<view class="addr">📍 {{ item.address }}</view>
				<view class="actions">
					<view class="act-btn" @click="call(item.phone)">📞 电话</view>
					<view class="act-btn primary" @click="navigate(item)">🧭 导航</view>
				</view>
			</view>
		</view>

		<view class="empty" v-if="list.length === 0 && !loading">
			{{ selectedProvince ? '该地区暂无医院数据，请联系管理员补充' : '请先选择省份' }}
		</view>
	</view>
</template>

<script>
import { API_BASE } from '@/utils/api'
import { china } from '@/common/china.js'
const API = API_BASE

export default {
	data() {
		return {
			china,                    // 完整全国省市区数据
			provinceIndex: -1,
			cityIndex: -1,
			selectedProvince: '',
			selectedCity: '',
			list: [],
			loading: false
		}
	},
	computed: {
		provinceNames() { return this.china.map(p => p.name) },
		cityNames() {
			if (!this.selectedProvince) return []
			const p = this.china.find(x => x.name === this.selectedProvince)
			return p ? p.cities.map(c => c.name) : []
		}
	},
	onLoad() {
		this.loadHospitals()
	},
	methods: {
		onProvinceChange(e) {
			this.provinceIndex = Number(e.detail.value)
			this.selectedProvince = this.provinceNames[this.provinceIndex]
			this.selectedCity = ''
			this.cityIndex = -1
			this.loadHospitals()
		},
		onCityChange(e) {
			this.cityIndex = Number(e.detail.value)
			this.selectedCity = this.cityNames[this.cityIndex]
			this.loadHospitals()
		},
		clearFilter() {
			this.selectedProvince = ''
			this.selectedCity = ''
			this.provinceIndex = -1
			this.cityIndex = -1
			this.loadHospitals()
		},
		loadHospitals() {
			if (this.loading) return
			this.loading = true

			const params = {}
			if (this.selectedProvince) params.province = this.selectedProvince
			if (this.selectedCity) params.city = this.selectedCity

			uni.request({
				url: API + '/app/hospital/nearby',
				method: 'GET',
				data: params,
				success: (res) => {
					this.loading = false
					const d = res.data
					if (d && d.code === 200 && d.data) {
						this.list = d.data
					} else {
						this.list = []
					}
				},
				fail: (err) => {
					this.loading = false
					console.error('加载医院失败', err)
					uni.showToast({ title: '加载失败', icon: 'none' })
				}
			})
		},
		call(phone) {
			if (!phone) { uni.showToast({ title: '无电话', icon: 'none' }); return }
			uni.makePhoneCall({ phoneNumber: phone })
		},
		navigate(item) {
			if (!item.latitude || !item.longitude) {
				uni.showToast({ title: '该医院无坐标信息', icon: 'none' })
				return
			}
			uni.openLocation({
				latitude: Number(item.latitude),
				longitude: Number(item.longitude),
				name: item.name,
				address: item.address || ''
			})
		}
	}
}
</script>

<style>
.hospital-list { padding-bottom: 40rpx; }
.locator {
	background: #E3F2FD; padding: 16rpx 24rpx;
	display: flex; gap: 16rpx; align-items: center;
}
.picker-item {
	flex: 1; background: #FFF; padding: 14rpx 20rpx;
	border-radius: 8rpx; font-size: 28rpx; color: #333;
	display: flex; justify-content: space-between; align-items: center;
	min-height: 40rpx;
}
.picker-item.placeholder { color: #BBB; }
.caret { color: #999; font-size: 24rpx; }
.clear-btn { font-size: 26rpx; color: #1565C0; padding: 0 8rpx; }

.tip-bar {
	padding: 12rpx 24rpx; font-size: 24rpx; color: #1565C0;
	background: #BBDEFB; display: flex; justify-content: space-between;
}
.link { text-decoration: underline; margin-left: 12rpx; }

.hospital-list-wrap { padding: 24rpx; }
.hospital-card {
	background: #FFF; border-radius: 16rpx; padding: 24rpx;
	margin-bottom: 16rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.hospital-top { display: flex; justify-content: space-between; align-items: flex-start; }
.name { font-size: 32rpx; font-weight: bold; color: #222; flex: 1; }
.meta { margin: 12rpx 0; display: flex; gap: 10rpx; align-items: center; flex-wrap: wrap; }
.level { font-size: 24rpx; color: #888; background: #F5F5F5; padding: 4rpx 12rpx; border-radius: 6rpx; }
.region { font-size: 24rpx; color: #555; }
.badge-serum { font-size: 22rpx; background: #E8F5E9; color: #2E7D32; padding: 4rpx 12rpx; border-radius: 6rpx; }
.badge-noserum { font-size: 22rpx; background: #FFF3E0; color: #E65100; padding: 4rpx 12rpx; border-radius: 6rpx; }
.addr { font-size: 26rpx; color: #666; margin-bottom: 16rpx; }
.actions { display: flex; gap: 16rpx; }
.act-btn {
	flex: 1; text-align: center; padding: 14rpx;
	background: #F5F5F5; border-radius: 40rpx; font-size: 26rpx; color: #333;
}
.act-btn.primary { background: #1565C0; color: #FFF; }
.empty { text-align: center; padding: 80rpx; color: #999; font-size: 26rpx; }
</style>


