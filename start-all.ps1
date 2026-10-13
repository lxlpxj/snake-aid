# start-all.ps1 - 一键启动后端 + 前端代理
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host " 🐍 蛇伤急救 全栈启动" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

# 1. 后端 Java Spring Boot
Write-Host "`n[1/2] 启动后端 Spring Boot..."
$java = "C:\Program Files\Java\jdk-21\bin\java.exe"
$jar = Join-Path $PSScriptRoot "snake-aid-backend\target\snake-aid-backend-0.0.1-SNAPSHOT.jar"
if (-not (Test-Path $jar)) { Write-Host "❌ JAR 不存在: $jar"; exit 1 }
Start-Process -FilePath $java -ArgumentList "-jar",$jar -WorkingDirectory (Join-Path $PSScriptRoot "snake-aid-backend")
Write-Host "   等待后端就绪..."
for ($i = 0; $i -lt 60; $i++) {
    Start-Sleep 2
    $r = curl.exe -s -o NUL -w "%{http_code}" --connect-timeout 1 "http://127.0.0.1:8080/api/app/video/list?page=1&size=1" 2>$null
    if ($r -eq "200" -or $r -eq "404") { Write-Host "   ✅ 后端就绪!" -ForegroundColor Green; break }
}

# 2. 前端 + API 反代 Node.js
Write-Host "`n[2/2] 启动前端+反代 Node 代理 (端口 3000)..."
$proxyLog = Join-Path $PSScriptRoot "proxy.log"
$proxy = Join-Path $PSScriptRoot "snake-proxy.js"
if (-not (Test-Path $proxy)) { Write-Host "❌ snake-proxy.js 不存在"; exit 1 }
Start-Process -FilePath "node" -ArgumentList $proxy -WorkingDirectory $PSScriptRoot
Start-Sleep 2
$r = curl.exe -s -o NUL -w "%{http_code}" --connect-timeout 1 "http://127.0.0.1:3000/"
Write-Host "   前端代理: HTTP $r" -ForegroundColor $(if($r -eq "200"){"Green"}else{"Yellow"})

Write-Host "`n✅ 全部启动成功！" -ForegroundColor Green
Write-Host ""
Write-Host "👉 现在开另一个 PowerShell 窗口，跑："
Write-Host "   cd `"$($PSScriptRoot)`""
Write-Host "   .\tunnel-auto.ps1"
Write-Host ""
Write-Host "   它会自动重连，窗口别关！"
Write-Host ""
Write-Host "=== 本机测试 ==="
Write-Host "前端: http://127.0.0.1:3000/"
Write-Host "API:  http://127.0.0.1:3000/api/app/video/list?page=1&size=1"
