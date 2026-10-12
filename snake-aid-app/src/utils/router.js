// uni-app 安全路由工具
export function safeSwitchTab(url) {
  const pages = getCurrentPages()
  if (pages.length > 0) {
    const cur = pages[pages.length - 1]
    if ('/' + cur.route === url) return
  }
  uni.switchTab({ url, fail: () => {} })
}
