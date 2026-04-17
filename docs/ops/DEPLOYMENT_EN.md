# MECN Docker Deployment Guide

## 🚀 Quick Start

### 1. Using Docker Compose (Recommended)

```bash
# Build and start
docker-compose up --build

# Run in background
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f mecn-app

# Stop services
docker-compose down
```

### 2. Using Nginx Reverse Proxy

```bash
# Start app + Nginx
docker-compose --profile with-nginx up -d

# Access http://localhost
```

### 3. Pure Docker Deployment

```bash
# Build image
docker build -t mecn:latest .

# Run container
docker run -d -p 8080:8080 \
  -e JAVA_OPTS="-Xms512m -Xmx1024m" \
  --name mecn \
  mecn:latest
```

## ⚙️ Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JAVA_OPTS` | JVM parameters | `-Xms512m -Xmx1024m` |
| `SPRING_PROFILES_ACTIVE` | Spring Profile | `prod` |
| `SERVER_PORT` | Service port | `8080` |
| `MECN_LEGACY_MODE` | Legacy mode | `false` |
| `MECN_EDGE_THRESHOLD` | Edge threshold | `0.08` |

### Mounting Configuration Files

```bash
# Copy configuration example
cp config/application-prod.yml.example config/application-prod.yml

# Edit config and run
docker run -d -p 8080:8080 \
  -v $(pwd)/config/application-prod.yml:/app/config/application-prod.yml:ro \
  mecn:latest
```

## 🔍 Common Commands

```bash
# View container resource usage
docker stats mecn

# Enter container
docker exec -it mecn sh

# Restart container
docker restart mecn

# Remove container and image
docker rm -f mecn
docker rmi mecn:latest
```

## 📊 Health Check

```bash
# Check service status
curl http://localhost:8080/api/health

# View health check logs
docker inspect --format='{{json .State.Health}}' mecn | jq
```

## 🔒 Security Recommendations

1. **Non-root user**: Image configured with non-root user (mecn:mecn)
2. **Read-only filesystem**: Config files mounted with `:ro` read-only flag
3. **Resource limits**: CPU/memory limits configured in docker-compose
4. **HTTPS**: Enable Nginx SSL in production environment

## 🐛 Troubleshooting

### Container Won't Start

```bash
# View logs
docker logs mecn

# Check port usage
netstat -tlnp | grep 8080
```

### Health Check Failed

```bash
# Manually execute health check
docker exec mecn wget --spider http://localhost:8080/api/health

# Check application status
curl http://localhost:8080/api/health
```

### Out of Memory

```bash
# Adjust JVM parameters
docker run -e JAVA_OPTS="-Xms256m -Xmx512m" ...
```

## 📈 Performance Optimization

### JVM Tuning

```yaml
environment:
  - JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

### Docker Resource Limits

```yaml
deploy:
  resources:
    limits:
      cpus: '2.0'
      memory: 2G
```

## 📝 Next Steps

- [ ] Configure CI/CD automatic deployment
- [ ] Add Prometheus monitoring
- [ ] Configure log collection (ELK)
- [ ] Implement blue-green deployment

---

**Last Updated**: 2026-03-26  
**Version**: 1.0.0
