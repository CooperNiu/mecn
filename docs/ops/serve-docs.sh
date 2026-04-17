#!/bin/bash

# MECN 文档站点本地预览脚本

set -e

echo "🚀 启动 MkDocs 本地预览服务器..."

# 检查 Python 是否安装
if ! command -v python3 &> /dev/null; then
    echo "❌ 错误：未找到 Python 3"
    exit 1
fi

# 进入项目目录
cd "$(dirname "$0")"

# 检查虚拟环境
if [ ! -d "venv" ]; then
    echo "⚠️  虚拟环境不存在，请先运行 ./build-docs.sh"
    exit 1
fi

# 激活虚拟环境
source venv/bin/activate

# 检查 MkDocs 是否安装
if ! command -v mkdocs &> /dev/null; then
    echo "⚠️  MkDocs 未安装，请先运行 ./build-docs.sh"
    exit 1
fi

echo "✅ 启动预览服务器..."
echo ""
echo "📖 访问地址：http://localhost:8000"
echo "💡 按 Ctrl+C 停止服务器"
echo ""

# 启动服务器
mkdocs serve --dev-addr=127.0.0.1:8000
