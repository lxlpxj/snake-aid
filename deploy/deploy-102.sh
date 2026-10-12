#!/usr/bin/env bash
# =============================================================
# 蛇伤急救项目 - 服务器一键部署脚本（离线版）
# 目标机器: 192.168.1.102 (Linux，无外网，仅局域网)
# 用法:     sudo bash deploy-102.sh
#
# 离线依赖:
#   /home/ubuntu/software/docker/*.deb   Docker 安装包组（约定目录，优先）
#   offline/docker-deb/*.deb             或项目内回退目录
#   offline/images.tar                   预构建镜像(backend/frontend/mysql/redis)
#
# 功能: 装Docker -> 开机自启 -> 载入镜像 -> 启动容器
#       -> 容器自启(restart策略) -> 防火墙 -> 验证
# 幂等: 可重复执行
# =============================================================
set -euo pipefail

APP_DIR=/opt/snakebite-first-aid
OPEN_PORTS=(8080 8081)

log() { echo -e "\n>>> $*"; }

# ---------- 1. 安装 Docker ----------
log "步骤1/6: 检查 Docker"
if command -v docker >/dev/null 2>&1; then
    echo "Docker 已安装: $(docker --version)"
else
    DOCKER_DEB_DIR=""
    for d in /home/ubuntu/software/docker "$APP_DIR/offline/docker-deb"; do
        if ls "$d"/*.deb >/dev/null 2>&1; then DOCKER_DEB_DIR="$d"; break; fi
    done
    if [ -n "$DOCKER_DEB_DIR" ]; then
        log "离线安装 Docker（.deb 包组，来自 $DOCKER_DEB_DIR）"
        dpkg -i "$DOCKER_DEB_DIR"/*.deb || apt-get -f install -y || true
        dpkg -i "$DOCKER_DEB_DIR"/*.deb
    else
        log "未找到离线包（/home/ubuntu/software/docker/），尝试在线安装"
        curl -fsSL https://get.docker.com | sh
    fi
fi

# ---------- 2. Docker 开机自启 ----------
log "步骤2/6: 配置 Docker 开机自启"
systemctl enable docker
systemctl start docker
echo "docker 服务状态: $(systemctl is-enabled docker)"

# ---------- 3. compose 插件检查 ----------
log "步骤3/6: 检查 docker compose 插件"
if ! docker compose version >/dev/null 2>&1; then
    echo "错误: 缺少 docker compose 插件（离线包组应含 docker-compose-plugin）"
    exit 1
fi
docker compose version

# ---------- 4. 项目代码就位 ----------
log "步骤4/6: 项目代码就位 ($APP_DIR)"
if [ -f "$APP_DIR/docker-compose.yml" ]; then
    echo "项目文件已就位，跳过获取"
else
    git clone https://gitee.com/work_xxj/snakebite-first-aid.git "$APP_DIR" \
        || { echo "克隆失败：离线机器请先用 curl 下载源码包解压到 $APP_DIR"; exit 1; }
fi
cd "$APP_DIR"

if ! grep -Eq "restart: *(always|unless-stopped)" docker-compose.yml; then
    echo "警告: docker-compose.yml 缺少 restart 策略，容器将不会开机自启"
fi

# ---------- 5. 载入离线镜像 ----------
log "步骤5/6: 载入离线镜像（如存在）"
if [ -f "$APP_DIR/offline/images.tar" ]; then
    docker load -i "$APP_DIR/offline/images.tar"
else
    echo "未找到 offline/images.tar（联网机器会自动 build）"
fi

# ---------- 6. 端口冲突预检（避免影响机器上已有服务） ----------
log "预检: 检查端口是否被占用"
PORT_CONFLICT=0
for p in 8080 8081 3307 6379; do
    if ss -tlnp 2>/dev/null | grep -q ":$p "; then
        echo "⚠ 端口 $p 已被占用，占用者:"
        ss -tlnp 2>/dev/null | grep ":$p " | sed 's/^/    /'
        PORT_CONFLICT=1
    fi
done
if [ "$PORT_CONFLICT" = "1" ]; then
    echo "存在端口冲突。请先确认这些服务能否让位，或调整 docker-compose.yml 端口后重跑本脚本。"
    exit 1
fi
echo "端口 8080/8081/3307/6379 全部空闲 ✅"

# ---------- 7. 启动容器（不带 --build，直接用已载入镜像） ----------
log "步骤6/6: 启动容器"
docker compose up -d

# ---------- 防火墙 ----------
log "防火墙放行 ${OPEN_PORTS[*]}"
if command -v ufw >/dev/null 2>&1 && ufw status 2>/dev/null | grep -q "Status: active"; then
    for p in "${OPEN_PORTS[@]}"; do ufw allow "$p/tcp"; done
elif command -v firewall-cmd >/dev/null 2>&1 && firewall-cmd --state >/dev/null 2>&1; then
    firewall-cmd --permanent --add-port=8080/tcp
    firewall-cmd --permanent --add-port=8081/tcp
    firewall-cmd --reload
else
    echo "防火墙未启用，跳过（如日后启用，请放行: ${OPEN_PORTS[*]}）"
fi
echo "注意: 3307(MySQL)/6379(Redis) 不要对外放行"

# ---------- 验证 ----------
log "验证: 容器状态"
sleep 5
docker compose ps

IP=$(hostname -I 2>/dev/null | awk '{print $1}' || echo "本机IP")
cat <<EOF

=============================================================
部署完成！访问地址:
  管理后台:  http://${IP}:8081
  后端 API:  http://${IP}:8080/api/swagger-ui.html

开机自启验证（重启服务器后执行）:
  docker ps     # 应看到 4 个容器全部 running
=============================================================
EOF
