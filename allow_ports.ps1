# 蛇伤急救系统 - 防火墙放行端口脚本
# 必须以管理员身份运行！

Write-Host "=========================================="
Write-Host "  蛇伤急救系统 - 防火墙端口放行"
Write-Host "=========================================="
Write-Host ""

$ports = @(
    @{ Port = 5173; Name = "Snake-Vite-Dev"; Desc = "Vite 开发服务器" },
    @{ Port = 8080; Name = "Snake-Backend";   Desc = "Spring Boot 后端 API" },
    @{ Port = 8081; Name = "Snake-Frontend";  Desc = "uni-app 生产前端" },
    @{ Port = 3307; Name = "Snake-MySQL";     Desc = "MySQL 数据库" },
    @{ Port = 6379; Name = "Snake-Redis";     Desc = "Redis 缓存" }
)

foreach ($p in $ports) {
    Write-Host "放行 TCP $($p.Port)  ($($p.Desc)) ..." -NoNewline
    
    # 先删旧的（如果有）
    netsh advfirewall firewall delete rule name=$p.Name 2>$null | Out-Null
    
    # 加新的
    $result = netsh advfirewall firewall add rule `
        name=$p.Name `
        dir=in action=allow protocol=TCP localport=$($p.Port) `
        profile=any enable=yes 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host " ✅" -ForegroundColor Green
    } else {
        Write-Host " ❌ $result" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=========================================="
Write-Host "  完成！验证："
Write-Host "=========================================="

Write-Host ""
Write-Host "--- 当前生效的 Snake 规则 ---"
netsh advfirewall firewall show rule name="Snake-*" 2>&1 | Select-String -Pattern "Rule Name|LocalPort|Enabled|Action"

Write-Host ""
Write-Host "=========================================="
Write-Host "  你的局域网地址："
Write-Host "=========================================="
$ip = (Get-NetIPAddress -AddressFamily IPv4 | 
       Where-Object { $_.InterfaceAlias -match "WLAN|无线" -and $_.IPAddress -notmatch "127|169" } | 
       Select-Object -First 1).IPAddress
Write-Host "  前端地址: http://$($ip):8081" -ForegroundColor Cyan
Write-Host "  后端API: http://$($ip):8080/api" -ForegroundColor Cyan
Write-Host ""
Write-Host "  让同 WiFi 的同学用手机浏览器打开上面的地址即可！" -ForegroundColor Yellow
Write-Host ""

Read-Host "按回车退出"
