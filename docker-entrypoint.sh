#!/bin/sh
# ============================================================
# Docker 启动脚本 —— 同时启动 Spring Boot + Nginx
# 兼容 Render / Railway / 本地 三种环境变量风格
# ============================================================

echo "========================================="
echo " 🐍 蛇伤急救系统 启动中..."
echo "========================================="

# Render 注入 PORT 环境变量 (默认 10000)
# Spring Boot 固定监听 8080 (Nginx 反代目标)
# Nginx 监听 PORT (对外暴露端口)
RENDER_PORT="${PORT:-10000}"

echo "对外端口: $RENDER_PORT"
echo "Spring Boot 内部端口: 8080"

# ---------- 1. 替换 Nginx 模板中的端口 ----------
# nginx.conf 里的 ${PORT:-8080} 换成实际端口
sed -i "s|\${PORT:-8080}|$RENDER_PORT|g" /etc/nginx/http.d/default.conf

# ---------- 2. 解析数据库连接 (兼容 Render 和 Railway) ----------
# 优先级: 单独变量 > URL 变量 > 默认值
if [ -n "$MYSQL_HOST" ] && [ -n "$MYSQL_USER" ]; then
    # Render 风格: MYSQL_HOST, MYSQL_PORT, MYSQL_USER, MYSQL_PASSWORD, MYSQL_DB
    DB_URL="jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT:-3306}/${MYSQL_DB:-snake_aid}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
    DB_USERNAME="$MYSQL_USER"
    DB_PASSWORD="$MYSQL_PASSWORD"
    echo "MySQL (Render): ${MYSQL_HOST}:${MYSQL_PORT:-3306}/${MYSQL_DB}"
elif [ -n "$MYSQL_URL" ]; then
    # Railway 风格: mysql://user:pass@host:port/db
    DB_USER=$(echo "$MYSQL_URL" | sed -n 's|mysql://\([^:]*\):.*@.*|\1|p')
    DB_PASS=$(echo "$MYSQL_URL" | sed -n 's|mysql://[^:]*:\([^@]*\)@.*|\1|p')
    DB_HOST=$(echo "$MYSQL_URL" | sed -n 's|mysql://.*@\([^:/]*\).*|\1|p')
    DB_PORT=$(echo "$MYSQL_URL" | sed -n 's|mysql://.*@[^:]*:\([^/]*\)/.*|\1|p')
    DB_NAME=$(echo "$MYSQL_URL" | sed -n 's|mysql://.*@[^/]*/\([^?]*\).*|\1|p')
    DB_URL="jdbc:mysql://${DB_HOST}:${DB_PORT:-3306}/${DB_NAME}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
    DB_USERNAME="$DB_USER"
    DB_PASSWORD="$DB_PASS"
    echo "MySQL (Railway): ${DB_HOST}:${DB_PORT}/${DB_NAME}"
fi

if [ -n "$DB_URL" ]; then
    export DB_URL DB_USERNAME DB_PASSWORD
fi

# ---------- 3. 解析 Redis ----------
if [ -n "$REDIS_HOST" ]; then
    export REDIS_HOST REDIS_PORT="${REDIS_PORT:-6379}" REDIS_PASSWORD="${REDIS_PASSWORD:-}"
    echo "Redis (Render): ${REDIS_HOST}:${REDIS_PORT}"
elif [ -n "$REDIS_URL" ]; then
    RD_HOST=$(echo "$REDIS_URL" | sed -n 's|redis://.*@\([^:/]*\).*|\1|p')
    RD_PORT=$(echo "$REDIS_URL" | sed -n 's|redis://.*@[^:]*:\([^/]*\).*|\1|p')
    RD_PASS=$(echo "$REDIS_URL" | sed -n 's|redis://[^:]*:\([^@]*\)@.*|\1|p')
    export REDIS_HOST="$RD_HOST" REDIS_PORT="${RD_PORT:-6379}" REDIS_PASSWORD="$RD_PASS"
    echo "Redis (Railway): ${RD_HOST}:${RD_PORT}"
fi

# ---------- 4. 启动 Spring Boot (后台, 固定 8080) ----------
echo ""
echo "启动 Spring Boot..."
java -jar /app/app.jar --server.port=8080 &
SPRING_PID=$!
echo "Spring Boot PID: $SPRING_PID (启动中约 15-30 秒)"

# ---------- 5. 等 Spring Boot 就绪 ----------
echo "等待 Spring Boot 就绪..."
SPRING_READY=0
for i in $(seq 1 60); do
    sleep 1
    # 用 curl 探测 8080 端口有响应
    if curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8080/api/app/video/list?page=1\&size=1 2>/dev/null | grep -qE "[0-9]+"; then
        echo "✅ Spring Boot 就绪! (第 ${i} 秒)"
        SPRING_READY=1
        break
    fi
    # 检查进程还活着
    if ! kill -0 $SPRING_PID 2>/dev/null; then
        echo "❌ Spring Boot 进程挂了! 退出码: $(wait $SPRING_PID 2>/dev/null || echo unknown)"
        exit 1
    fi
    if [ $((i % 5)) -eq 0 ]; then
        echo "  等待中... ${i}s"
    fi
done

if [ "$SPRING_READY" -eq 0 ]; then
    echo "⚠️  Spring Boot 60 秒内未就绪，但继续启动..."
fi

# ---------- 6. 启动 Nginx (前台) ----------
echo ""
echo "启动 Nginx (端口 $RENDER_PORT)..."
nginx -g "daemon off;" &
NGINX_PID=$!

echo ""
echo "========================================="
echo " ✅ 系统就绪!"
echo "    Nginx PID: $NGINX_PID"
echo "    Spring Boot PID: $SPRING_PID"
echo "    对外端口: $RENDER_PORT"
echo "========================================="

# ---------- 7. 保持容器运行 ----------
trap "echo '=== 关闭中 ==='; kill $SPRING_PID $NGINX_PID 2>/dev/null; wait; exit 0" SIGTERM SIGINT

wait
