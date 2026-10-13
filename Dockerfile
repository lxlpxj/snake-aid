# ==================== Stage 1: build backend jar ====================
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /build
COPY snake-aid-backend/maven-settings.xml /root/.m2/settings.xml
COPY snake-aid-backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY snake-aid-backend/src ./src
RUN mv src/main/resources/application.yml.example src/main/resources/application.yml
RUN mvn clean package -DskipTests -B && ls -lh target/*.jar

# ==================== Stage 2: build H5 frontend ====================
FROM node:20-alpine AS frontend-build
WORKDIR /app
COPY snake-aid-app/package.json snake-aid-app/package-lock.json ./
RUN npm ci
COPY snake-aid-app/ ./
RUN npm run build:h5

# ==================== Stage 3: runtime ====================
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache nginx tzdata curl && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone
RUN mkdir -p /app/uploads /app/frontend /var/www/html
WORKDIR /app
COPY --from=backend-build /build/target/snake-aid-backend-0.0.1-SNAPSHOT.jar app.jar
COPY --from=frontend-build /app/dist/build/h5/ /var/www/html/
COPY nginx.conf /etc/nginx/http.d/default.conf
EXPOSE 8080
COPY docker-entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
