<template>
	<view class="login-page">
		<!-- 品牌区 -->
		<view class="brand">
			<text class="brand-logo">🐍</text>
			<text class="brand-name">毒蛇自救</text>
			<text class="brand-slogan">科普 · 自救 · 救命</text>
		</view>

		<!-- 登录表单 -->
		<view class="form-card">
			<view class="form-title">手机号登录</view>
			<view class="form-sub">未注册手机号自动创建账号</view>

			<view class="input-row">
				<text class="prefix">+86</text>
				<input v-model="phone" class="input" type="number" maxlength="11" placeholder="请输入手机号" />
			</view>

			<view class="input-row">
				<input v-model="code" class="input" type="number" maxlength="6" placeholder="6 位验证码" />
				<view class="code-btn" :class="{disabled: countdown>0}" @click="sendCode">
					{{ countdown > 0 ? countdown + 's' : '获取验证码' }}
				</view>
			</view>

			<view v-if="codeHint" class="code-hint">测试验证码：{{ codeHint }}</view>

			<view class="submit-btn" :class="{disabled: !canSubmit}" @click="doLogin">
				登录 / 注册
			</view>

			<view class="admin-link" @click="goAdmin">
				🔐 管理员登录入口
			</view>
		</view>

		<!-- 隐私合规（业务规则） -->
		<view class="privacy">
			登录即同意《用户协议》和《隐私政策》
			定位仅用于查找附近有血清的医院，不做其他用途
		</view>
	</view>
</template>

<script>
const API = 'http://localhost:8080/api'

export default {
	data() {
		return {
			phone: '',
			code: '',
			countdown: 0,
			codeHint: ''
		}
	},
	computed: {
		canSubmit() {
			return /^1[3-9]\d{9}$/.test(this.phone) && this.code.length === 6
		}
	},
	methods: {
		sendCode() {
			if (this.countdown > 0) return
			if (!/^1[3-9]\d{9}$/.test(this.phone)) {
				uni.showToast({ title: '请输入正确手机号', icon: 'none' }); return
			}
			uni.request({
				url: API + '/app/auth/send-sms',
				method: 'POST',
				data: { phone: this.phone },
				success: (res) => {
					const d = res.data
					if (d && d.code === 200) {
						this.codeHint = '123456'
						uni.showToast({ title: '验证码已发送（测试：123456）', icon: 'none' })
						this.countdown = 60
						const t = setInterval(() => {
							this.countdown--
							if (this.countdown <= 0) clearInterval(t)
						}, 1000)
					} else {
						uni.showToast({ title: d?.msg || '发送失败', icon: 'none' })
					}
				},
				fail: () => { uni.showToast({ title: '网络错误', icon: 'none' }) }
			})
		},
		doLogin() {
			if (!this.canSubmit) return
			uni.request({
				url: API + '/app/auth/login',
				method: 'POST',
				data: { phone: this.phone, smsCode: this.code },
				success: (res) => {
					const d = res.data
					if (d && d.code === 200) {
						uni.setStorageSync('appUser', d.data)
						uni.showToast({ title: '登录成功', icon: 'success' })
						setTimeout(() => { uni.switchTab({ url: '/pages/mine/mine' }) }, 600)
					} else {
						uni.showToast({ title: d?.msg || '登录失败', icon: 'none' })
					}
				},
				fail: () => { uni.showToast({ title: '网络错误', icon: 'none' }) }
			})
		},
		goAdmin() {
			uni.navigateTo({ url: '/pages/mine/admin-entry' })
		}
	}
}
</script>

<style>
.login-page {
	min-height: 100vh; background: linear-gradient(180deg, #E53935 0%, #FFF 60%);
	padding: 80rpx 40rpx 40rpx;
}

.brand { text-align: center; margin-bottom: 60rpx; }
.brand-logo { font-size: 100rpx; display: block; }
.brand-name { font-size: 44rpx; font-weight: bold; color: #FFF; display: block; margin-top: 8rpx; }
.brand-slogan { font-size: 26rpx; color: rgba(255,255,255,0.8); margin-top: 6rpx; }

.form-card {
	background: #FFF; border-radius: 24rpx; padding: 48rpx 32rpx;
	box-shadow: 0 8rpx 40rpx rgba(0,0,0,0.08);
}
.form-title { font-size: 36rpx; font-weight: bold; color: #222; }
.form-sub { font-size: 24rpx; color: #888; margin-top: 8rpx; margin-bottom: 32rpx; }

.input-row {
	display: flex; align-items: center;
	background: #F5F5F5; border-radius: 12rpx; padding: 0 20rpx;
	margin-bottom: 20rpx; height: 88rpx;
}
.prefix { font-size: 28rpx; color: #222; margin-right: 12rpx; }
.input { flex: 1; font-size: 30rpx; color: #222; height: 88rpx; }
.code-btn {
	font-size: 24rpx; color: #E53935; padding: 10rpx 16rpx;
	background: #FFEBEE; border-radius: 8rpx;
}
.code-btn.disabled { color: #999; background: #EEE; }

.code-hint {
	text-align: center; font-size: 24rpx; color: #E65100;
	background: #FFF8E1; padding: 10rpx; border-radius: 8rpx; margin-bottom: 20rpx;
}

.submit-btn {
	background: #E53935; color: #FFF; text-align: center;
	padding: 24rpx; border-radius: 44rpx; font-size: 32rpx; font-weight: bold;
	margin-top: 20rpx;
}
.submit-btn.disabled { background: #CCC; }

.admin-link {
	text-align: center; font-size: 24rpx; color: #888;
	margin-top: 40rpx; padding-top: 24rpx; border-top: 1rpx solid #EEE;
}

.privacy {
	position: fixed; bottom: 40rpx; left: 40rpx; right: 40rpx;
	text-align: center; font-size: 22rpx; color: #999; line-height: 1.6;
}
</style>
