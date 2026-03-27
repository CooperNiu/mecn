#!/bin/bash

# MECN 文档站点构建脚本

set -e

echo "🚀 开始构建 MkDocs 文档站点..."

# 检查 Python 是否安装
if ! command -v python3 &> /dev/null; then
    echo "❌ 错误：未找到 Python 3"
    exit 1
fi

echo "✅ Python 版本：$(python3 --version)"

# 进入项目目录
cd "$(dirname "$0")"

# 创建虚拟环境（如果不存在）
if [ ! -d "venv" ]; then
    echo "📦 创建 Python 虚拟环境..."
    python3 -m venv venv
fi

# 激活虚拟环境
echo "🔧 激活虚拟环境..."
source venv/bin/activate

# 安装依赖
echo "📥 安装 MkDocs 依赖..."
pip install -r docs/requirements-mkdocs.txt --quiet

# 构建文档
echo "🏗️  构建文档站点..."
mkdocs build --clean --config-file mkdocs.yml

echo ""
echo "✅ 文档站点构建完成！"
echo ""
echo "📂 输出目录：site/"
echo ""
echo "🌐 本地预览，运行："
echo "   mkdocs serve"
echo ""
echo "📤 部署到 GitHub Pages，运行："
echo "   mike deploy --push main"
echo ""

# 显示构建统计
echo "📊 构建统计:"
echo "   文件数：$(find site -type f | wc -l | tr -d ' ')"
echo "   总大小：$(du -sh site | cut -f1)"
echo ""
