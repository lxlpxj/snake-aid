# ============================================================
# 蛇伤急救系统 —— Render 全栈部署
# 单容器: Spring Boot 后端 + Nginx 前端静态托管
# ============================================================
FROM eclipse-temurin:21-jre-alpine

# 更新包列表 + 装 Nginx + 时区 + 工具
# 分开装更清晰，先 update 避免网络问题
RUN apk update && apk add --no-cache nginx tzdata curl

# 设置上海时区
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 准备目录
RUN mkdir -p /app/uploads /var/www/html

# ---------- 后端 ----------
WORKDIR /app
COPY snake-aid-backend/target/snake-aid-backend-0.0.1-SNAPSHOT.jar app.jar

# ---------- 前端 (必须是 build 产物，不是源码) ----------
COPY snake-aid-app/dist/build/h5/ /var/www/html/

# ---------- Nginx 配置 ----------
COPY nginx.conf /etc/nginx/http.d/default.conf

# Render 会注入 PORT 环境变量
EXPOSE 10000 8080

# ---------- 启动脚本 ----------
COPY docker-entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
