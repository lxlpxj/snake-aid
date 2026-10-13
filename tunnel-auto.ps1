# tunnel-auto.ps1 - 自动重连公网穿透脚本
# 断了自动重连，打印最新URL
param($port = 3000)

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host " 🔄 蛇伤急救 穿透自动重连 (localhost.run)" -ForegroundColor Cyan
Write-Host "  目标端口: $port" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "⚠️ 这个窗口别关！关了公网链接就断。" -ForegroundColor Yellow
Write-Host ""

$attempt = 0
while ($true) {
    $attempt++
    Write-Host "[$(Get-Date -Format HH:mm:ss)] 尝试连接 (第 $attempt 次)..." -ForegroundColor DarkGray
    
    # localhost.run 比 serveo 稳，命令短
    # -R 80:localhost:3000 = 远端 80 → 本地 3000
    # nokey@localhost.run = 免密钥登录
    ssh -o StrictHostKeyChecking=no `
        -o ServerAliveInterval=30 `
        -o ServerAliveCountMax=3 `
        -o ExitOnForwardFailure=yes `
        -o ConnectTimeout=10 `
        -R 80:localhost:$port `
        nokey@localhost.run
    
    Write-Host "[$(Get-Date -Format HH:mm:ss)] 连接断开，5 秒后重连..." -ForegroundColor Red
    Start-Sleep 5
}
