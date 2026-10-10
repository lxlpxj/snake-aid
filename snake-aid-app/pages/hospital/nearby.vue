<template>
	<view class="hospital-list">
		<!-- 💡 用我的位置 -->
		<view class="locate-bar" @click="useMyLocation">
			<text class="locate-icon">📍</text>
			<text class="locate-text">{{ locating ? '定位中...' : '使用我的位置（按距离排序）' }}</text>
			<text class="locate-arrow">›</text>
		</view>

		<!-- 省市区筛选 -->
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
			<view v-if="selectedProvince || myLat" class="clear-btn" @click="clearFilter">清除</view>
		</view>

		<!-- 提示条 -->
		<view class="tip-bar" v-if="selectedProvince || myLat">
			<text v-if="myLat">🔵 已按位置排序</text>
			<text v-if="selectedProvince">{{ selectedProvince }}{{ selectedCity ? ' · ' + selectedCity : '' }}</text>
		</view>

		<!-- 医院列表 -->
		<view class="hospital-list-wrap">
			<view v-for="item in list" :key="item.id" class="hospital-card">
				<view class="hospital-top">
					<view class="name">{{ item.name }}</view>
					<view v-if="item.distance != null" class="distance">{{ fmtDist(item.distance) }}</view>
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
			{{ selectedProvince ? '该地区暂无医院数据' : '请先选择省份或使用位置' }}
		</view>
	</view>
</template>

<script>
import { china } from '@/common/china.js'
const API = '/api'

export default {
	data() {
		return {
			china,
			provinceIndex: -1,
			cityIndex: -1,
			selectedProvince: '',
			selectedCity: '',
			myLat: null,        // 用户当前纬度
			myLng: null,        // 用户当前经度
			locating: false,
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
		fmtDist(m) {
			if (m == null) return ''
			if (m < 1) return Math.round(m * 1000) + 'm'
			return m.toFixed(1) + 'km'
		},
		useMyLocation() {
			this.locating = true
			uni.getLocation({
				type: 'gcj02',
				isHighAccuracy: true,
				timeout: 8000,
				success: (r) => {
					this.myLat = r.latitude
					this.myLng = r.longitude
					this.loadHospitals()
				},
				fail: (e) => {
					this.locating = false
					uni.showModal({
						title: '定位失败',
						content: e.errMsg + '\n\n请在手机设置中允许定位权限，或在 H5 中使用 HTTPS 或 localhost',
						showCancel: false
					})
				},
				complete: () => { this.locating = false }
			})
		},
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
			this.myLat = null
			this.myLng = null
			this.loadHospitals()
		},
		loadHospitals() {
			if (this.loading) return
			this.loading = true

			const params = {}
			if (this.selectedProvince) params.province = this.selectedProvince
			if (this.selectedCity) params.city = this.selectedCity
			if (this.myLat && this.myLng) {
				params.lat = this.myLat
				params.lng = this.myLng
			}

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

.locate-bar {
	background: linear-gradient(135deg, #667eea, #764ba2);
	padding: 24rpx; margin: 24rpx; border-radius: 16rpx;
	display: flex; align-items: center; gap: 16rpx;
	box-shadow: 0 4rpx 16rpx rgba(102,126,234,0.3);
}
.locate-icon { font-size: 40rpx; }
.locate-text { flex: 1; color: #fff; font-size: 30rpx; font-weight: 500; }
.locate-arrow { color: rgba(255,255,255,0.7); font-size: 36rpx; }

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
	background: #BBDEFB;
}

.hospital-list-wrap { padding: 0 24rpx; }
.hospital-card {
	background: #FFF; border-radius: 16rpx; padding: 24rpx;
	margin-bottom: 16rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.hospital-top { display: flex; justify-content: space-between; align-items: flex-start; }
.name { font-size: 32rpx; font-weight: bold; color: #222; flex: 1; }
.distance {
	font-size: 26rpx; color: #764ba2; background: #ede9fe;
	padding: 4rpx 16rpx; border-radius: 20rpx; font-weight: 500;
}
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
