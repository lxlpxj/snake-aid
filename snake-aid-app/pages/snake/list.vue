<template>
	<view class="snake-list">
		<!-- AI 识别入口 -->
		<view class="ai-banner" @click="goRecognize">
			<view class="ai-banner-left">
				<text class="ai-banner-emoji">📸</text>
				<view class="ai-banner-text">
					<view class="ai-banner-title">AI 蛇种识别</view>
					<view class="ai-banner-sub">上传照片，一键识别</view>
				</view>
			</view>
			<text class="ai-banner-arrow">→</text>
		</view>

		<!-- 搜索栏 -->
		<view class="search-bar">
			<input v-model="keyword" class="search-input" placeholder="搜索蛇种名称..." confirm-type="search" @confirm="onSearch" />
			<view class="filter-toggle" @click="showFeatureFilter = !showFeatureFilter">
				<text>🔍 特征筛选</text>
			</view>
		</view>

		<!-- 特征筛选面板（FR11 头型/花纹/颜色/尾部） -->
		<view class="feature-panel" v-if="showFeatureFilter">
			<view class="feature-row" v-for="f in featureOptions" :key="f.key">
				<text class="feature-label">{{ f.label }}：</text>
				<view class="feature-chips">
					<view class="f-chip" :class="{active: features[f.key]===''}" @click="setFeature(f.key, '')">不限</view>
					<view
						class="f-chip"
						:class="{active: features[f.key] === opt}"
						v-for="opt in f.options"
						:key="opt"
						@click="setFeature(f.key, opt)"
					>{{ opt }}</view>
				</view>
			</view>
			<view class="feature-actions">
				<view class="btn-reset" @click="resetFeatures">重置</view>
				<view class="btn-apply" @click="applyFeatures">应用筛选</view>
			</view>
		</view>

		<!-- 省市区定位（FR9 按地区浏览，内置全国数据） -->
		<view class="region-bar">
			<view class="region-picker" @click="openSelector('province')">
				📍 {{ selectedProvince || '选择省份' }}
				<text v-if="selectedProvince"> ▾</text>
			</view>
			<view v-if="cityNames.length > 0" class="region-picker small" @click="openSelector('city')">
				{{ selectedCity || '城市' }}▾
			</view>
			<view v-if="districtNames.length > 0" class="region-picker small" @click="openSelector('district')">
				{{ selectedDistrict || '区县' }}▾
			</view>
			<view v-if="selectedProvince" class="region-clear" @click="clearRegion">×</view>
		</view>

		<!-- 自定义选择弹窗（始终居中） -->
		<view v-if="selectorOpen" class="selector-mask" @click="selectorOpen = false">
			<view class="selector-modal" @click.stop>
				<view class="selector-title">
					{{ selectorTitle }}
					<text class="selector-close" @click="selectorOpen = false">✕</text>
				</view>
				<scroll-view scroll-y class="selector-list">
					<view
						v-for="(item, idx) in selectorItems"
						:key="idx"
						class="selector-item"
						:class="{ active: selectorSelectedIdx === idx }"
						@click="onSelectorPick(idx)"
					>{{ item }}</view>
				</scroll-view>
			</view>
		</view>

		<!-- 有毒/无毒 切换 -->
		<view class="poison-filter">
			<view class="chip" :class="{active: isPoisonous===1}" @click="filterPoisonous(1)">🐍 有毒</view>
			<view class="chip safe" :class="{active: isPoisonous===0}" @click="filterPoisonous(0)">🌱 无毒（对比）</view>
			<view class="chip" :class="{active: isPoisonous===null}" @click="filterPoisonous(null)">全部</view>
		</view>

		<!-- 列表 -->
		<view class="snake-cards">
			<view v-for="item in list" :key="item.id" class="snake-card" @click="goDetail(item.id)">
				<view class="snake-cover" :class="{'non-poisonous': item.isPoisonous !== 1}">
					<image v-if="item.imageUrl" :src="imageUrl(item.imageUrl)" mode="aspectFill" class="cover-img" />
					<text v-else>{{ item.name?.slice(0,1) || '?' }}</text>
				</view>
				<view class="snake-info">
					<view class="snake-name">{{ item.name }}</view>
					<view class="snake-meta">
						<text v-if="item.isPoisonous===1" class="tag tag-danger">有毒 · {{ item.venomType || '未知毒型' }}</text>
						<text v-else class="tag tag-safe">🌱 无毒蛇</text>
						<text v-if="item.hazardLevel && item.isPoisonous===1" class="tag tag-level">危险 {{ item.hazardLevel }}</text>
					</view>
					<view class="snake-features">
						<text v-if="item.headShape">头:{{ item.headShape }}</text>
						<text v-if="item.pattern">纹:{{ item.pattern }}</text>
						<text v-if="item.color">色:{{ item.color }}</text>
					</view>
					<view v-if="item.distribution" class="snake-dist">📍 {{ item.distribution }}</view>
				</view>
			</view>
		</view>

		<view class="empty" v-if="list.length === 0 && !loading">
			{{ keyword || selectedProvince || showFeatureFilter ? '未找到匹配的蛇种' : '暂无已发布蛇种数据' }}
		</view>

		<!-- FR12 无毒蛇对照提示 -->
		<view class="safe-section" v-if="isPoisonous===1">
			<view class="safe-title">🌱 你知道吗？这些常见蛇都是无毒的</view>
			<view class="safe-list">
				<view class="safe-chip" v-for="s in nonPoisonous" :key="s.id" @click="goDetail(s.id)">
					{{ s.name }}
				</view>
				<view v-if="nonPoisonous.length === 0" class="safe-empty">暂无无毒蛇数据</view>
			</view>
			<view class="safe-tip">
				被蛇咬后先保持冷静！很多蛇其实无毒，可根据"头圆、纹均匀、瞳孔圆"初步判断。
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
			keyword: '',
			isPoisonous: 1,
			china,
			provinceIndex: -1,
			cityIndex: -1,
			districtIndex: -1,
			selectedProvince: '',
			selectedCity: '',
			selectedDistrict: '',
			selectorOpen: false,
			selectorType: '',
			selectorTitle: '',
			selectorItems: [],
			selectorSelectedIdx: -1,
			showFeatureFilter: false,
			features: { headShape: '', pattern: '', color: '', tailShape: '' },
			// 特征选项（从 init.sql 蛇种数据中提取的常见值）
			featureOptions: [
				{ key: 'headShape', label: '头型', options: ['椭圆形','圆形','三角形','菱形','钝圆','扁平'] },
				{ key: 'pattern',   label: '花纹', options: ['无','横纹','纵纹','斑点','斑块','条纹','环形'] },
				{ key: 'color',     label: '颜色', options: ['黑色','棕色','绿色','灰色','黄色','红色','白色','蓝色','紫褐色'] },
				{ key: 'tailShape', label: '尾部', options: ['尖细','钝圆','侧扁','尾尖','短粗','细长'] }
			],
			list: [],
			nonPoisonous: [],
			page: 1,
			loading: false
		}
	},
	computed: {
		provinceNames() { return this.china.map(p => p.name) },
		_currentProvince() {
			return this.selectedProvince ? this.china.find(x => x.name === this.selectedProvince) : null
		},
		_currentCity() {
			if (!this._currentProvince) return null
			const city = this._currentProvince.cities[this.cityIndex]
			return city || null
		},
		cityNames() {
			if (!this._currentProvince) return []
			return this._currentProvince.cities.map(c => c.name)
		},
		districtNames() {
			if (!this._currentCity) return []
			return this._currentCity.districts || []
		}
	},
	onLoad() {
		this.loadData()
	},
	methods: {
		goRecognize() {
			uni.navigateTo({ url: '/pages/snake/recognize' })
		},
		imageUrl(url) {
			if (!url) return ''
			if (url.startsWith('http')) return url
			return API + url
		},
		loadData() {
			if (this.loading) return
			this.loading = true
			const params = { current: this.page, size: 20 }
			if (this.keyword) params.keyword = this.keyword
			if (this.isPoisonous !== null && this.isPoisonous !== undefined) params.isPoisonous = this.isPoisonous
			if (this.selectedProvince) params.province = this.selectedProvince
			if (this.selectedCity) params.city = this.selectedCity
			if (this.selectedDistrict) params.district = this.selectedDistrict

			uni.request({
				url: API + '/app/snake/page',
				method: 'GET',
				data: params,
				success: (res) => {
					this.loading = false
					const d = res.data
					if (d && d.code === 200 && d.data) {
						this.list = d.data.records || []
					} else {
						this.list = []
					}
				},
				fail: () => { this.loading = false; this.list = [] }
			})

			// FR12：无毒蛇对照（只在显示有毒蛇时拉）
			if (this.isPoisonous === 1) {
				uni.request({
					url: API + '/app/snake/page',
					method: 'GET',
					data: { current: 1, size: 6, isPoisonous: 0 },
					success: (res) => {
						const d = res.data
						if (d && d.code === 200 && d.data) {
							this.nonPoisonous = d.data.records || []
						}
					},
					fail: () => {}
				})
			} else {
				this.nonPoisonous = []
			}
		},
		// 特征筛选
		setFeature(key, val) { this.features[key] = val },
		resetFeatures() { this.features = { headShape: '', pattern: '', color: '', tailShape: '' } },
		applyFeatures() {
			this.showFeatureFilter = false
			const params = { ...this.features }
			Object.keys(params).forEach(k => params[k] = params[k] || null)
			uni.request({
				url: API + '/app/snake/features',
				method: 'GET',
				data: params,
				success: (res) => {
					const d = res.data
					if (d && d.code === 200 && d.data) {
						this.list = d.data
					} else {
						this.list = []
					}
				},
				fail: () => { uni.showToast({ title: '筛选失败', icon: 'none' }) }
			})
		},
		// 省市区选择弹窗（始终居中）
		openSelector(type) {
			this.selectorType = type
			if (type === 'province') {
				this.selectorTitle = '选择省份'
				this.selectorItems = this.provinceNames
				this.selectorSelectedIdx = this.provinceIndex
			} else if (type === 'city') {
				this.selectorTitle = '选择城市'
				this.selectorItems = this.cityNames
				this.selectorSelectedIdx = this.cityIndex
			} else if (type === 'district') {
				this.selectorTitle = '选择区县'
				this.selectorItems = this.districtNames
				this.selectorSelectedIdx = this.districtIndex
			}
			this.selectorOpen = true
		},
		onSelectorPick(idx) {
			this.selectorOpen = false
			if (this.selectorType === 'province') {
				this.provinceIndex = idx
				this.selectedProvince = this.provinceNames[idx]
				this.selectedCity = ''; this.cityIndex = -1
				this.selectedDistrict = ''; this.districtIndex = -1
				if (this.cityNames.length === 1) {
					this.cityIndex = 0; this.selectedCity = this.cityNames[0]
				}
			} else if (this.selectorType === 'city') {
				this.cityIndex = idx
				this.selectedCity = this.cityNames[idx]
				this.selectedDistrict = ''; this.districtIndex = -1
			} else if (this.selectorType === 'district') {
				this.districtIndex = idx
				this.selectedDistrict = this.districtNames[idx]
			}
			this.page = 1; this.loadData()
		},
		clearRegion() {
			this.selectedProvince = ''; this.selectedCity = ''; this.selectedDistrict = ''
			this.provinceIndex = -1; this.cityIndex = -1; this.districtIndex = -1
			this.page = 1
			this.loadData()
		},
		onSearch() { this.page = 1; this.loadData() },
		filterPoisonous(v) { this.isPoisonous = v; this.page = 1; this.loadData() },
		goDetail(id) { uni.navigateTo({ url: `/pages/snake/detail?id=${id}` }) }
	}
}
</script>

<style>
.snake-list { padding-bottom: 60rpx; overflow-x: hidden; }

/* AI 识别入口 */
.ai-banner {
	display: flex; align-items: center; justify-content: space-between;
	margin: 20rpx 24rpx 8rpx; padding: 24rpx 28rpx;
	background: linear-gradient(135deg, #6A1B9A, #AB47BC);
	border-radius: 20rpx; box-shadow: 0 6rpx 20rpx rgba(106,27,154,0.3);
}
.ai-banner-left { display: flex; align-items: center; }
.ai-banner-emoji { font-size: 56rpx; margin-right: 20rpx; }
.ai-banner-title { color: white; font-size: 32rpx; font-weight: bold; }
.ai-banner-sub { color: rgba(255,255,255,0.85); font-size: 24rpx; margin-top: 6rpx; }
.ai-banner-arrow { color: white; font-size: 36rpx; }

/* 搜索栏 */
.search-bar {
	display: flex; gap: 16rpx; padding: 20rpx 24rpx 8rpx;
}
.search-input {
	flex: 1; background: #FFF; padding: 16rpx 24rpx; border-radius: 40rpx;
	font-size: 28rpx; border: 1rpx solid #DDD;
}
.filter-toggle {
	padding: 16rpx 20rpx; background: #2E7D32; color: #FFF;
	border-radius: 40rpx; font-size: 24rpx;
}

/* 特征筛选面板 */
.feature-panel {
	margin: 12rpx 24rpx 0; padding: 20rpx;
	background: #F1F8E9; border-radius: 12rpx;
}
.feature-row { margin-bottom: 12rpx; display: flex; align-items: flex-start; }
.feature-label { font-size: 24rpx; color: #555; width: 90rpx; flex-shrink: 0; padding-top: 8rpx; }
.feature-chips { display: flex; flex-wrap: wrap; gap: 10rpx; flex: 1; }
.f-chip {
	padding: 6rpx 18rpx; background: #FFF; border: 1rpx solid #A5D6A7;
	border-radius: 30rpx; font-size: 22rpx; color: #555;
}
.f-chip.active { background: #2E7D32; color: #FFF; border-color: #2E7D32; }
.feature-actions { display: flex; gap: 16rpx; justify-content: flex-end; margin-top: 16rpx; }
.btn-reset { padding: 8rpx 20rpx; color: #888; font-size: 24rpx; }
.btn-apply { padding: 8rpx 28rpx; background: #2E7D32; color: #FFF; border-radius: 30rpx; font-size: 24rpx; }

/* 地区 picker */
.region-bar {
	display: flex; flex-wrap: wrap; gap: 12rpx; row-gap: 8rpx;
	align-items: center; justify-content: center;
	padding: 16rpx 24rpx; overflow: visible; position: relative; z-index: 999;
}
.region-picker {
	background: #FFF; padding: 10rpx 18rpx; border-radius: 30rpx;
	font-size: 26rpx; color: #222; border: 1rpx solid #2E7D32;
	position: relative; overflow: visible;
	display: flex; align-items: center; justify-content: center;
	text-align: center; line-height: 1;
}
.region-picker.small { font-size: 24rpx; color: #555; border-color: #CCC; }
.region-picker.disabled { color: #BBB; border-color: #EEE; }
.region-clear { color: #C62828; font-size: 32rpx; width: 40rpx; text-align: center; }

/* 有毒/无毒 chips */
.poison-filter {
	display: flex; gap: 16rpx; padding: 0 24rpx 12rpx;
}
.chip {
	padding: 10rpx 24rpx; background: #F5F5F5; color: #666;
	border-radius: 40rpx; font-size: 24rpx;
}
.chip.active { background: #C62828; color: #FFF; }
.chip.safe { }
.chip.safe.active { background: #2E7D32; color: #FFF; }

/* 卡片列表 */
.snake-cards { padding: 0 24rpx; }
.snake-card {
	display: flex; gap: 20rpx; background: #FFF;
	border-radius: 16rpx; padding: 20rpx; margin-bottom: 16rpx;
	box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.snake-cover {
	width: 140rpx; height: 140rpx; border-radius: 12rpx;
	background: #C62828; color: #FFF; font-size: 56rpx; font-weight: bold;
	display: flex; align-items: center; justify-content: center; overflow: hidden; flex-shrink: 0;
}
.snake-cover.non-poisonous { background: #2E7D32; }
.cover-img { width: 100%; height: 100%; }
.snake-info { flex: 1; }
.snake-name { font-size: 32rpx; font-weight: bold; color: #222; }
.snake-meta { margin: 8rpx 0; display: flex; flex-wrap: wrap; gap: 6rpx; }
.tag { font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; }
.tag-danger { background: #FFEBEE; color: #C62828; }
.tag-safe { background: #E8F5E9; color: #2E7D32; }
.tag-level { background: #FFF3E0; color: #E65100; }
.snake-features { font-size: 22rpx; color: #666; margin-top: 4rpx; }
.snake-features text { margin-right: 12rpx; }
.snake-dist { font-size: 22rpx; color: #999; margin-top: 4rpx; }

.empty { text-align: center; padding: 80rpx; color: #999; font-size: 26rpx; }

/* FR12 无毒蛇对照 */
.safe-section {
	margin: 24rpx; padding: 20rpx; background: #E8F5E9;
	border-radius: 12rpx; border: 1rpx solid #A5D6A7;
}
.safe-title { font-size: 26rpx; font-weight: bold; color: #2E7D32; margin-bottom: 12rpx; }
.safe-list { display: flex; flex-wrap: wrap; gap: 12rpx; margin-bottom: 12rpx; }
.safe-chip {
	padding: 8rpx 18rpx; background: #FFF; color: #2E7D32;
	border-radius: 30rpx; font-size: 24rpx; border: 1rpx solid #A5D6A7;
}
.safe-empty { font-size: 22rpx; color: #888; padding: 8rpx; }
.safe-tip { font-size: 22rpx; color: #558B2F; line-height: 1.6; background: #FFF; padding: 12rpx; border-radius: 8rpx; }

/* 自定义选择弹窗 — 始终居中于视口 */
.selector-mask {
	position: fixed; top: 0; left: 0; right: 0; bottom: 0;
	background: rgba(0,0,0,0.45);
	display: flex; align-items: center; justify-content: center;
	z-index: 9999;
}
.selector-modal {
	width: 600rpx; max-height: 70vh;
	background: #FFF; border-radius: 20rpx; overflow: hidden;
	display: flex; flex-direction: column;
	box-shadow: 0 8rpx 40rpx rgba(0,0,0,0.25);
}
.selector-title {
	padding: 28rpx 32rpx; font-size: 30rpx; font-weight: bold; color: #2E7D32;
	border-bottom: 1rpx solid #EEE; text-align: center; position: relative;
}
.selector-close {
	position: absolute; right: 24rpx; top: 28rpx; color: #999; font-size: 32rpx;
	padding: 4rpx 12rpx;
}
.selector-list { max-height: 55vh; }
.selector-item {
	padding: 24rpx 32rpx; font-size: 28rpx; color: #333;
	border-bottom: 1rpx solid #F0F0F0; text-align: center;
}
.selector-item:last-child { border-bottom: none; }
.selector-item.active { color: #2E7D32; font-weight: bold; background: #E8F5E9; }
.selector-item:active { background: #F5F5F5; }
</style>
