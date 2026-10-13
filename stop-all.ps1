Write-Host "停止所有进程..."
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Get-Process -Name node -ErrorAction SilentlyContinue | Where-Object { $_.Path -like "*snake*" } | Stop-Process -Force -ErrorAction SilentlyContinue
Get-Process -Name ssh -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*serveo*" } | Stop-Process -Force -ErrorAction SilentlyContinue
Write-Host "✅ 所有进程已停止"
