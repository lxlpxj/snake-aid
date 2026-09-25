# ============================================================
# 后端 —— 纯 Docker 多阶段构建
#   Stage 1: Maven 编译（JDK 17 + 阿里云 Maven 镜像加速）
#   Stage 2: JRE Alpine 运行（只留 fat jar，镜像 ~200MB）
# ============================================================

# ==================== 阶段 1：编译 ====================
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# 阿里云 Maven 镜像（关键！否则容器里下载依赖超慢）
COPY maven-settings.xml /root/.m2/settings.xml

# 先只拷 pom.xml 下载依赖（利用 Docker 缓存，pom 不变就不重新下）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 再拷源码编译
COPY src ./src

# ★ Docker 构建时用 application.yml.example 作为默认配置（compose 的 env vars 会覆盖）
RUN mv src/main/resources/application.yml.example src/main/resources/application.yml

RUN mvn clean package -DskipTests -B && ls -lh target/*.jar

# ==================== 阶段 2：运行 ====================
FROM eclipse-temurin:17-jre-alpine

# 时区 + curl（健康检查用）
RUN apk add --no-cache tzdata curl \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

WORKDIR /app
RUN mkdir -p /app/uploads

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=5 \
    CMD curl -sf http://localhost:8080/api/v3/api-docs >/dev/null 2>&1 || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
