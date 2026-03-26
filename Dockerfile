# MECN Docker 镜像构建文件
# Macro Economic Causal Network - 高维宏观经济因果网络联动模型

# ========== 构建阶段 ==========
FROM maven:3.9.6-eclipse-temurin-14 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并下载依赖（利用 Docker 缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并编译
COPY src ./src
RUN mvn clean package -DskipTests -B

# ========== 运行阶段 ==========
FROM eclipse-temurin:14-jre-alpine

# 设置工作目录
WORKDIR /app

# 创建非 root 用户运行应用（安全最佳实践）
RUN addgroup -g 1001 mecn && \
    adduser -u 1001 -G mecn -s /bin/sh -D mecn

# 从构建阶段复制 jar 包
COPY --from=builder /app/target/mecn-1.jar app.jar

# 修改文件所有者
RUN chown -R mecn:mecn /app

# 切换到非 root 用户
USER mecn

# 暴露端口
EXPOSE 8080

# JVM 参数优化
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
