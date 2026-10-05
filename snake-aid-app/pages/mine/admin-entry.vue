<template>
	<view class="admin-entry">
		<view class="header">
			<view class="logo">🔐</view>
			<view class="title">管理后台</view>
			<view class="subtitle">仅限授权管理人员使用</view>
		</view>

		<view class="roles">
			<view class="role-card" v-for="r in roles" :key="r.code" @click="goLogin(r)">
				<view class="role-icon">{{ r.icon }}</view>
				<view class="role-name">{{ r.name }}</view>
				<view class="role-desc">{{ r.desc }}</view>
			</view>
		</view>

		<view class="login-box">
			<view class="section-title">账号密码登录</view>
			<view class="form-item">
				<input class="input" v-model="username" placeholder="账号" />
			</view>
			<view class="form-item">
				<input class="input" v-model="password" type="password" placeholder="密码" />
			</view>
			<button class="submit-btn" @click="doLogin">登录后台</button>
			<view class="tip">默认账号见后端 init.sql，密码 BCrypt 加密</view>
		</view>
	</view>
</template>

<script>
const API = 'http://192.168.2.15:8080/api'
const ADMIN_TOKEN = 'snake_admin_token'
const ADMIN_INFO = 'snake_admin_info'

export default {
	data() {
		return {
			username: 'admin',
			password: 'admin123',
			roles: [
				{ code: 3, icon: '🛠', name: '系统管理员', desc: '用户/权限/系统维护' },
				{ code: 1, icon: '📝', name: '内容管理员', desc: '蛇种/医院/文章维护' },
				{ code: 2, icon: '⚕️', name: '医疗顾问', desc: '审核内容/急救指南' }
			]
		}
	},
	methods: {
		goLogin(role) {
			uni.showToast({
				title: `请使用 ${role.name} 账号密码登录`,
				icon: 'none'
			})
		},
		doLogin() {
			if (!this.username || !this.password) {
				uni.showToast({ title: '请输入账号密码', icon: 'none' })
				return
			}
			uni.showLoading({ title: '登录中...' })
			uni.request({
				url: `${API}/admin/auth/login`,
				method: 'POST',
				data: { username: this.username, password: this.password },
				success: (res) => {
					uni.hideLoading()
					if (res.data && res.data.code === 200) {
						const data = res.data.data
						uni.setStorageSync(ADMIN_TOKEN, data.token)
						uni.setStorageSync(ADMIN_INFO, data)
						uni.showToast({ title: `登录成功：${this.roleName(data.roleType)}`, icon: 'success' })
						setTimeout(() => {
							// #ifdef H5
							window.open('/api/doc.html', '_blank')
							// #endif
							// #ifndef H5
							uni.setClipboardData({
								data: '/api/doc.html',
								success: () => uni.showToast({ title: '文档地址已复制', icon: 'none' })
							})
							// #endif
						}, 800)
					} else {
						uni.showToast({
							title: (res.data && res.data.msg) || '登录失败',
							icon: 'none'
						})
					}
				},
				fail: () => {
					uni.hideLoading()
					uni.showToast({ title: '后端未连接', icon: 'none' })
				}
			})
		},
		roleName(code) {
			const m = { 1: '内容管理员', 2: '医疗顾问', 3: '系统管理员' }
			return m[code] || '未知角色'
		}
	}
}
</script>

<style>
.admin-entry { padding: 48rpx; min-height: 100vh; background: #F5F5F5; }
.header { text-align: center; margin-bottom: 48rpx; }
.logo { font-size: 100rpx; }
.title { font-size: 36rpx; font-weight: bold; margin-top: 12rpx; }
.subtitle { font-size: 24rpx; color: #888; margin-top: 6rpx; }

.roles { display: flex; flex-direction: column; gap: 16rpx; margin-bottom: 40rpx; }
.role-card {
	background: #FFF; border-radius: 16rpx; padding: 28rpx 32rpx;
	display: flex; align-items: center; gap: 20rpx;
}
.role-icon { font-size: 48rpx; }
.role-name { font-size: 30rpx; font-weight: bold; color: #263238; }
.role-desc { font-size: 24rpx; color: #888; margin-top: 4rpx; }

.login-box {
	background: #FFF; border-radius: 16rpx; padding: 32rpx;
}
.section-title { font-size: 28rpx; font-weight: bold; margin-bottom: 24rpx; color: #455A64; }
.form-item { margin-bottom: 24rpx; }
.input { border: 1rpx solid #DDD; border-radius: 12rpx; padding: 20rpx; font-size: 28rpx; }
.submit-btn { background: #455A64; color: #FFF; border-radius: 12rpx; font-size: 30rpx; margin-top: 20rpx; }
.tip { text-align: center; font-size: 22rpx; color: #AAA; margin-top: 16rpx; }
</style>
