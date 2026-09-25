# ============================================================
# 前端 —— 纯 Docker 多阶段构建
#   Stage 1: Node 18 编译（淘宝 npm 镜像加速）
#   Stage 2: Nginx Alpine 运行（只留 dist 静态文件，镜像 ~75MB）
# ============================================================

# ==================== 阶段 1：编译 ====================
FROM node:18-alpine AS builder

# 淘宝 npm 镜像（关键！否则 npm install 超慢）
RUN npm config set registry https://registry.npmmirror.com

WORKDIR /build

# 先只拷 package.json + package-lock.json 装依赖（利用 Docker 缓存）
COPY package.json package-lock.json* ./
RUN npm install --registry=https://registry.npmmirror.com

# 再拷源码 build
COPY . .
RUN npm run build && ls -la dist/

# ==================== 阶段 2：运行 ====================
FROM nginx:1.25-alpine

# 时区
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

# 从编译阶段拿产物
COPY --from=builder /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=15s --timeout=3s --start-period=10s --retries=3 \
    CMD wget -qO- http://localhost >/dev/null || exit 1
