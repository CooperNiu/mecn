# MECN Docker 部署指南

本目录包含 MECN 项目的 Docker 部署相关文件。

## 📁 文件说明

```
docker/
├── nginx/
│   ├── nginx.conf              # Nginx 反向代理配置
│   └── ssl/                    # SSL 证书目录（需自行创建）
├── config/
│   └── application-prod.yml.example  # 生产环境配置示例
└── logs/                       # 日志目录
```

## 🚀 快速开始

### 1. 使用 Docker Compose（推荐）

```bash
# 构建并启动
docker-compose up --build

# 后台运行
docker-compose up -d

# 查看状态
docker-compose ps

# 查看日志
docker-compose logs -f mecn-app

# 停止服务
docker-compose down
```

### 2. 使用 Nginx 反向代理

```bash
# 启动应用 + Nginx
docker-compose --profile with-nginx up -d

# 访问 http://localhost
```

### 3. 纯 Docker 部署

```bash
# 构建镜像
docker build -t mecn:latest .

# 运行容器
docker run -d -p 8080:8080 \
  -e JAVA_OPTS="-Xms512m -Xmx1024m" \
  --name mecn \
  mecn:latest
```

## ⚙️ 配置说明

### 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `JAVA_OPTS` | JVM 参数 | `-Xms512m -Xmx1024m` |
| `SPRING_PROFILES_ACTIVE` | Spring Profile | `prod` |
| `SERVER_PORT` | 服务端口 | `8080` |
| `MECN_LEGACY_MODE` | 兼容模式 | `false` |
| `MECN_EDGE_THRESHOLD` | 边阈值 | `0.08` |

### 挂载配置文件

```bash
# 复制配置示例
cp config/application-prod.yml.example config/application-prod.yml

# 编辑配置后运行
docker run -d -p 8080:8080 \
  -v $(pwd)/config/application-prod.yml:/app/config/application-prod.yml:ro \
  mecn:latest
```

## 🔍 常用命令

```bash
# 查看容器资源使用
docker stats mecn

# 进入容器
docker exec -it mecn sh

# 重启容器
docker restart mecn

# 删除容器和镜像
docker rm -f mecn
docker rmi mecn:latest
```

## 📊 健康检查

```bash
# 检查服务状态
curl http://localhost:8080/api/health

# 查看健康检查日志
docker inspect --format='{{json .State.Health}}' mecn | jq
```

## 🔒 安全建议

1. **非 root 用户运行**：镜像已配置非 root 用户（mecn:mecn）
2. **只读文件系统**：配置文件使用 `:ro` 只读挂载
3. **资源限制**：在 docker-compose 中已配置 CPU/内存限制
4. **HTTPS**：生产环境建议启用 Nginx SSL

## 🐛 故障排查

### 容器无法启动

```bash
# 查看日志
docker logs mecn

# 检查端口占用
netstat -tlnp | grep 8080
```

### 健康检查失败

```bash
# 手动执行健康检查
docker exec mecn wget --spider http://localhost:8080/api/health

# 检查应用状态
curl http://localhost:8080/api/health
```

### 内存不足

```bash
# 调整 JVM 参数
docker run -e JAVA_OPTS="-Xms256m -Xmx512m" ...
```

## 📈 性能优化

### JVM 调优

```yaml
environment:
  - JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

### Docker 资源限制

```yaml
deploy:
  resources:
    limits:
      cpus: '2.0'
      memory: 2G
```

## 📝 下一步

- [ ] 配置 CI/CD 自动部署
- [ ] 添加 Prometheus 监控
- [ ] 配置日志收集（ELK）
- [ ] 实现蓝绿部署

---

**最后更新**: 2026-03-26  
**版本**: 1.0.0
