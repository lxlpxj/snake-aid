# ============================================================
# 蛇伤急救系统 —— Railway 全栈部署
# 单容器: Spring Boot 后端 + Nginx 前端静态托管
# ============================================================
FROM eclipse-temurin:21-jre-alpine

# 安装 Nginx
RUN apk add --no-cache nginx tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 准备目录
RUN mkdir -p /app/uploads /app/frontend /var/www/html

# ---------- 后端 ----------
WORKDIR /app
COPY snake-aid-backend/target/snake-aid-backend-0.0.1-SNAPSHOT.jar app.jar

# ---------- 前端 ----------
COPY snake-aid-app/dist/build/h5/ /var/www/html/

# ---------- Nginx 配置 ----------
COPY nginx.conf /etc/nginx/http.d/default.conf

# Railway 注入 PORT 环境变量，Nginx 监听它
# 默认 8080（本地测试），Railway 会覆盖
EXPOSE 8080

# ---------- 启动脚本 ----------
COPY docker-entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
