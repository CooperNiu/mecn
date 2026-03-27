# MkDocs 文档站点指南

本指南介绍如何使用 MkDocs 构建和部署 MECN 项目的文档网站。

## 📋 目录

- [快速开始](#快速开始)
- [本地预览](#本地预览)
- [构建站点](#构建站点)
- [部署到 GitHub Pages](#部署到-github-pages)
- [自定义主题](#自定义主题)
- [添加新页面](#添加新页面)

## 🚀 快速开始

### 前置要求

- **Python**: Python 3.7+
- **pip**: Python 包管理工具

### 一键构建

```bash
# 构建文档站点
./build-docs.sh
```

该脚本会自动：
1. 创建 Python 虚拟环境
2. 安装 MkDocs 及插件依赖
3. 构建静态网站到 `site/` 目录

### 首次使用

如果是首次使用，建议手动执行以下步骤：

```bash
# 1. 创建虚拟环境
python3 -m venv venv

# 2. 激活虚拟环境
source venv/bin/activate

# 3. 安装依赖
pip install -r docs/requirements-mkdocs.txt

# 4. 验证安装
mkdocs --version
```

## 🌐 本地预览

### 启动开发服务器

```bash
# 使用快捷脚本（推荐）
./serve-docs.sh

# 或手动启动
source venv/bin/activate
mkdocs serve --dev-addr=127.0.0.1:8000
```

### 访问网站

打开浏览器访问：**http://localhost:8000**

开发服务器支持**实时重载**，修改文档后会自动刷新。

### 停止服务器

在终端按 `Ctrl+C` 停止服务器。

## 🏗️ 构建站点

### 生产环境构建

```bash
# 构建优化的静态站点
./build-docs.sh

# 或手动构建
mkdocs build --clean --config-file mkdocs.yml
```

构建产物位于 `site/` 目录，包含所有静态文件（HTML、CSS、JS）。

### 本地验证构建

```bash
# 进入 site 目录
cd site

# 启动简单的 HTTP 服务器
python3 -m http.server 8080

# 访问 http://localhost:8080
```

## 📤 部署到 GitHub Pages

### 配置 GitHub Actions

在项目根目录创建 `.github/workflows/deploy-docs.yml`：

```yaml
name: Deploy Docs to GitHub Pages

on:
  push:
    branches:
      - main
    paths:
      - 'docs/**'
      - 'mkdocs.yml'
      - '.github/workflows/deploy-docs.yml'

permissions:
  contents: write

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.x'
      
      - name: Install dependencies
        run: |
          pip install -r docs/requirements-mkdocs.txt
      
      - name: Build site
        run: mkdocs build --clean
      
      - name: Deploy to GitHub Pages
        uses: peaceiris/actions-gh-pages@v3
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./site
```

### 使用 mike 管理版本

[mike](https://github.com/jimporter/mike) 是 MkDocs 的版本管理工具。

```bash
# 安装 mike
pip install mike

# 部署新版本
mike deploy --push main

# 设置默认版本
mike set-default --push main

# 查看所有版本
mike list
```

访问：`https://your-username.github.io/mecn/`

## 🎨 自定义主题

### 修改配色方案

编辑 `mkdocs.yml`：

```yaml
theme:
  palette:
    - scheme: default
      primary: indigo
      accent: indigo
```

可用颜色：`red`, `pink`, `purple`, `deep-purple`, `indigo`, `blue`, `light-blue`, `cyan`, `teal`, `green`, `light-green`, `lime`, `yellow`, `amber`, `orange`, `deep-orange`, `grey`, `blue-grey`, `white`, `black`

### 添加自定义 CSS

创建 `docs/stylesheets/extra.css`：

```css
/* 自定义字体 */
:root {
  --md-text-font: "Roboto";
  --md-code-font: "Roboto Mono";
}

/* 自定义颜色 */
[data-md-color-scheme="default"] {
  --md-primary-fg-color: #3f51b5;
  --md-accent-fg-color: #3f51b5;
}

/* 自定义警告框样式 */
.admonition {
  border-radius: 4px;
}
```

在 `mkdocs.yml` 中引用：

```yaml
extra_css:
  - stylesheets/extra.css
```

## 📄 添加新页面

### 1. 创建 Markdown 文件

在相应目录下创建文件，例如：

```bash
# API 文档
touch docs/api/new-feature.md

# 部署指南
touch deployment/new-guide.md
```

### 2. 添加到导航

编辑 `mkdocs.yml`，在 `nav` 部分添加：

```yaml
nav:
  - 使用指南:
    - 新功能: api/new-feature.md
  - 部署运维:
    - 新指南: deployment/new-guide.md
```

### 3. 使用 MkDocs 语法

```markdown
# 页面标题

本文档介绍新功能的使用方法。

## 小节标题

### 内容

这是正文内容。

!!! note "提示"
    这是一个提示框。

!!! warning "警告"
    这是一个警告框。

```python
# 代码示例
def hello():
    print("Hello, World!")
```

| 列 1 | 列 2 |
|------|------|
| 值 1 | 值 2 |
```

## 🔧 常用命令

### 基础命令

```bash
# 查看版本
mkdocs --version

# 启动开发服务器
mkdocs serve

# 构建站点
mkdocs build

# 清理构建缓存
mkdocs build --clean

# 使用自定义配置
mkdocs serve --config-file mkdocs.yml
```

### 高级命令

```bash
# 严格模式（警告即错误）
mkdocs build --strict

# 详细输出
mkdocs build -v

# 静默模式
mkdocs build -q
```

## 📊 插件说明

### 已启用的插件

1. **search**: 全文搜索（中英文）
2. **git-revision-date-localized**: 显示文档最后更新时间
3. **minify**: 压缩 HTML/CSS/JS
4. **awesome-pages**: 简化页面导航配置
5. **mkdocstrings**: 自动生成 API 文档
6. **redirects**: 页面重定向

### 可选插件

```bash
# 图表支持
pip install mkdocs-mermaid2-plugin

# 数学公式
pip install mkdocs-katex-plugin

# 博客功能
pip install mkdocs-blog-plugin
```

## 🐛 故障排查

### 常见问题

#### 1. 虚拟环境问题

```bash
# 删除虚拟环境重新创建
rm -rf venv
python3 -m venv venv
source venv/bin/activate
pip install -r docs/requirements-mkdocs.txt
```

#### 2. 端口被占用

```bash
# 使用其他端口
mkdocs serve --dev-addr=127.0.0.1:8080
```

#### 3. 构建失败

```bash
# 查看详细错误
mkdocs build -v

# 清理缓存
rm -rf site/
mkdocs build --clean
```

### 检查清单

- [ ] Python 版本正确（3.7+）
- [ ] 虚拟环境已激活
- [ ] 依赖已完整安装
- [ ] mkdocs.yml 配置正确
- [ ] 文档文件存在且格式正确

## 📚 参考资料

- [MkDocs 官方文档](https://www.mkdocs.org/)
- [Material for MkDocs 主题](https://squidfunk.github.io/mkdocs-material/)
- [MkDocs 插件列表](https://github.com/mkdocs/catalog)
- [Markdown 语法指南](https://www.markdownguide.org/)

---

**最后更新**: 2026-03-27  
**维护者**: MECN Team
