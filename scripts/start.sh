#!/bin/bash

# MECN 启动脚本
# 同时启动前后端服务

echo "🚀 启动 MECN 项目..."
echo ""

# 颜色定义
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 启动后端
echo -e "${BLUE}📦 启动后端服务 (Spring Boot)...${NC}"
cd /Users/cooperniu/Documents/code/mecn/mecn-app

# 检查是否已经运行
if lsof -ti:8080 > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  端口 8080 已被占用，跳过后端启动${NC}"
else
    # 后台启动 Spring Boot
    mvn spring-boot:run -q &
    BACKEND_PID=$!
    echo -e "${GREEN}✅ 后端服务启动中 (PID: $BACKEND_PID)${NC}"
    echo -e "${BLUE}   地址: http://localhost:8080${NC}"
fi

echo ""

# 启动前端
echo -e "${BLUE}🎨 启动前端服务 (Vite Dev Server)...${NC}"
cd /Users/cooperniu/Documents/code/mecn/mecn-app/frontend

# 检查是否已经运行
if lsof -ti:3000 > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  端口 3000 已被占用，跳过前端启动${NC}"
else
    # 启动 Vite 开发服务器
    npm run dev &
    FRONTEND_PID=$!
    echo -e "${GREEN}✅ 前端服务启动中 (PID: $FRONTEND_PID)${NC}"
    echo -e "${BLUE}   地址: http://localhost:3000${NC}"
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  MECN 项目启动完成！${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${BLUE}📌 服务地址:${NC}"
echo -e "   前端: ${GREEN}http://localhost:3000${NC}"
echo -e "   后端: ${GREEN}http://localhost:8080${NC}"
echo -e "   API 文档: ${GREEN}http://localhost:8080/api/docs${NC}"
echo ""
echo -e "${YELLOW}💡 提示:${NC}"
echo -e "   - 按 ${YELLOW}Ctrl+C${NC} 停止所有服务"
echo -e "   - 或运行 ${YELLOW}./scripts/stop.sh${NC} 停止服务"
echo ""

# 等待用户中断
wait
