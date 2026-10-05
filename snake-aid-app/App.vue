<script>
        let showing401 = false

        function is401(res) {
                if (!res) return false
                if (res.statusCode === 401) return true
                try {
                        const body = typeof res.data === 'string' ? JSON.parse(res.data) : res.data
                        return body && body.code === 401
                } catch (e) { return false }
        }

        export default {
                onLaunch: function() {
                        console.log('App Launch')

                        uni.addInterceptor('request', {
                                invoke(args) {
                                        const token = uni.getStorageSync('token')
                                        if (token) {
                                                args.header = args.header || {}
                                                args.header['Authorization'] = 'Bearer ' + token
                                        }
                                        return args
                                },
                                complete(res) {
                                        if (is401(res) && !showing401) {
                                                showing401 = true
                                                uni.removeStorageSync('token')
                                                uni.removeStorageSync('user')
                                                uni.showModal({
                                                        title: '登录已过期',
                                                        content: '登录状态已失效，是否重新登录？',
                                                        confirmText: '去登录',
                                                        cancelText: '取消',
                                                        success: (r) => {
                                                                showing401 = false
                                                                if (r.confirm) {
                                                                        uni.navigateTo({ url: '/pages/mine/login' })
                                                                }
                                                        }
                                                })
                                        }
                                },
                                fail(err) {
                                        console.error('[request fail]', err)
                                }
                        })
                },
                onShow: function() {
                        console.log('App Show')
                },
                onHide: function() {
                        console.log('App Hide')
                }
        }
</script>

<style>
</style>