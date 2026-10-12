#!/bin/sh
# ============================================================
# Docker 启动脚本 —— 同时启动 Spring Boot + Nginx
# ============================================================

set -e

echo "=== 蛇伤急救系统 启动 ==="
echo "PORT: ${PORT:-8080}"
echo "MySQL: ${MYSQL_URL:-localhost:3306/snake}"
echo "Redis: ${REDIS_URL:-localhost:6379}"

# ---------- 1. 替换 Nginx 端口 ----------
if [ -n "$PORT" ]; then
    sed -i "s/\${PORT:-8080}/$PORT/g" /etc/nginx/http.d/default.conf
fi

# ---------- 2. 启动 Spring Boot (后台) ----------
echo "启动 Spring Boot..."

# 解析 Railway MYSQL_URL → 拆成 Spring Boot 能识别的格式
# Railway 格式: mysql://user:password@host:port/database
if [ -n "$MYSQL_URL" ]; then
    DB_USER=$(echo "$MYSQL_URL" | sed -n 's|mysql://\([^:]*\):.*@.*|\1|p')
    DB_PASS=$(echo "$MYSQL_URL" | sed -n 's|mysql://[^:]*:\([^@]*\)@.*|\1|p')
    DB_HOST=$(echo "$MYSQL_URL" | sed -n 's|mysql://.*@\([^:/]*\).*|\1|p')
    DB_PORT=$(echo "$MYSQL_URL" | sed -n 's|mysql://.*@[^:]*:\([^/]*\)/.*|\1|p')
    DB_NAME=$(echo "$MYSQL_URL" | sed -n 's|mysql://.*@[^/]*/\([^?]*\).*|\1|p')
    export DB_URL="jdbc:mysql://${DB_HOST}:${DB_PORT:-3306}/${DB_NAME}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
    export DB_USERNAME="$DB_USER"
    export DB_PASSWORD="$DB_PASS"
    echo "  → MySQL 连接就绪: ${DB_HOST}:${DB_PORT}/${DB_NAME}"
fi

# 解析 Railway REDIS_URL
# Railway 格式: redis://user:password@host:port
if [ -n "$REDIS_URL" ]; then
    RD_HOST=$(echo "$REDIS_URL" | sed -n 's|redis://.*@\([^:/]*\).*|\1|p')
    RD_PORT=$(echo "$REDIS_URL" | sed -n 's|redis://.*@[^:]*:\([^/]*\).*|\1|p')
    export REDIS_HOST="$RD_HOST"
    export REDIS_PORT="${RD_PORT:-6379}"
    echo "  → Redis 连接就绪: ${RD_HOST}:${RD_PORT}"
fi

# Spring Boot 必须监听 8080 (Nginx 反代目标)
# 不管 Railway PORT 是多少，Spring Boot 固定 8080
java -jar /app/app.jar --server.port=8080 &
SPRING_PID=$!

echo "  Spring Boot PID: $SPRING_PID (启动中约 15-30 秒)"

# ---------- 3. 等 Spring Boot 就绪 ----------
echo "等待 Spring Boot 就绪..."
for i in $(seq 1 60); do
    if curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8080/api/app/auth/sendCode?phone=13800138000 2>/dev/null | grep -qE "200|400"; then
        echo "✅ Spring Boot 就绪! (第 ${i} 秒)"
        break
    fi
    sleep 1
done

# ---------- 4. 启动 Nginx (前台) ----------
echo "启动 Nginx..."
nginx -g "daemon off;" &
NGINX_PID=$!

echo "✅ 系统就绪! (Nginx PID: $NGINX_PID, Spring Boot PID: $SPRING_PID)"
echo "对外端口: ${PORT:-8080}"

# ---------- 5. 保持容器运行 ----------
# 监听两个进程，任一挂掉就退出
trap "echo '=== 关闭中 ==='; kill $SPRING_PID $NGINX_PID 2>/dev/null; exit 0" SIGTERM SIGINT

wait
