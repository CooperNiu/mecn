# MkDocs Documentation Site Guide

This guide introduces how to build and deploy the MECN project documentation website using MkDocs.

## 📋 Table of Contents

- [Quick Start](#quick-start)
- [Local Preview](#local-preview)
- [Build Site](#build-site)
- [Deploy to GitHub Pages](#deploy-to-github-pages)
- [Custom Theme](#custom-theme)
- [Add New Pages](#add-new-pages)

## 🚀 Quick Start

### Prerequisites

- **Python**: Python 3.7+
- **pip**: Python package manager

### One-Click Build

```bash
# Build documentation site
./build-docs.sh
```

This script will automatically:
1. Create Python virtual environment
2. Install MkDocs and plugin dependencies
3. Build static website to `site/` directory

### First Time Use

If using for the first time, it's recommended to manually execute these steps:

```bash
# 1. Create virtual environment
python3 -m venv venv

# 2. Activate virtual environment
source venv/bin/activate

# 3. Install dependencies
pip install -r docs/requirements-mkdocs.txt

# 4. Verify installation
mkdocs --version
```

## 🌐 Local Preview

### Start Development Server

```bash
# Use shortcut script (recommended)
./serve-docs.sh

# Or start manually
source venv/bin/activate
mkdocs serve --dev-addr=127.0.0.1:8000
```

### Access Website

Open browser and visit: **http://localhost:8000**

The development server supports **live reload**, automatically refreshing after document modifications.

### Stop Server

Press `Ctrl+C` in terminal to stop the server.

## 🏗️ Build Site

### Production Build

```bash
# Build optimized static site
./build-docs.sh

# Or build manually
mkdocs build --clean --config-file mkdocs.yml
```

Build artifacts are located in `site/` directory, containing all static files (HTML, CSS, JS).

### Local Build Verification

```bash
# Enter site directory
cd site

# Start simple HTTP server
python3 -m http.server 8080

# Visit http://localhost:8080
```

## 📤 Deploy to GitHub Pages

### Configure GitHub Actions

Create `.github/workflows/deploy-docs.yml` in project root:

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

### Version Management with mike

[mike](https://github.com/jimporter/mike) is a version management tool for MkDocs.

```bash
# Install mike
pip install mike

# Deploy new version
mike deploy --push main

# Set default version
mike set-default --push main

# List all versions
mike list
```

Visit: `https://your-username.github.io/mecn/`

## 🎨 Custom Theme

### Modify Color Scheme

Edit `mkdocs.yml`:

```yaml
theme:
  palette:
    - scheme: default
      primary: indigo
      accent: indigo
```

Available colors: `red`, `pink`, `purple`, `deep-purple`, `indigo`, `blue`, `light-blue`, `cyan`, `teal`, `green`, `light-green`, `lime`, `yellow`, `amber`, `orange`, `deep-orange`, `grey`, `blue-grey`, `white`, `black`

### Add Custom CSS

Create `docs/stylesheets/extra.css`:

```css
/* Custom fonts */
:root {
  --md-text-font: "Roboto";
  --md-code-font: "Roboto Mono";
}

/* Custom colors */
[data-md-color-scheme="default"] {
  --md-primary-fg-color: #3f51b5;
  --md-accent-fg-color: #3f51b5;
}

/* Custom admonition style */
.admonition {
  border-radius: 4px;
}
```

Reference in `mkdocs.yml`:

```yaml
extra_css:
  - stylesheets/extra.css
```

## 📄 Add New Pages

### 1. Create Markdown File

Create file in appropriate directory, for example:

```bash
# API documentation
touch docs/api/new-feature.md

# Deployment guide
touch deployment/new-guide.md
```

### 2. Add to Navigation

Edit `mkdocs.yml`, add to `nav` section:

```yaml
nav:
  - User Guide:
    - New Feature: api/new-feature.md
  - Deployment & Operations:
    - New Guide: deployment/new-guide.md
```

### 3. Use MkDocs Syntax

```markdown
# Page Title

This document introduces the usage of new features.

## Section Title

### Content

This is body content.

!!! note "Note"
    This is a note box.

!!! warning "Warning"
    This is a warning box.

```python
# Code example
def hello():
    print("Hello, World!")
```

| Column 1 | Column 2 |
|----------|----------|
| Value 1  | Value 2  |
```

## 🔧 Common Commands

### Basic Commands

```bash
# View version
mkdocs --version

# Start development server
mkdocs serve

# Build site
mkdocs build

# Clean build cache
mkdocs build --clean

# Use custom configuration
mkdocs serve --config-file mkdocs.yml
```

### Advanced Commands

```bash
# Strict mode (warnings become errors)
mkdocs build --strict

# Verbose output
mkdocs build -v

# Quiet mode
mkdocs build -q
```

## 📊 Plugin Description

### Enabled Plugins

1. **search**: Full-text search (Chinese and English)
2. **git-revision-date-localized**: Display document last update time
3. **minify**: Compress HTML/CSS/JS
4. **awesome-pages**: Simplify page navigation configuration
5. **mkdocstrings**: Auto-generate API documentation
6. **redirects**: Page redirection

### Optional Plugins

```bash
# Chart support
pip install mkdocs-mermaid2-plugin

# Mathematical formulas
pip install mkdocs-katex-plugin

# Blog functionality
pip install mkdocs-blog-plugin
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Virtual Environment Problems

```bash
# Delete virtual environment and recreate
rm -rf venv
python3 -m venv venv
source venv/bin/activate
pip install -r docs/requirements-mkdocs.txt
```

#### 2. Port Already in Use

```bash
# Use different port
mkdocs serve --dev-addr=127.0.0.1:8080
```

#### 3. Build Failure

```bash
# View detailed errors
mkdocs build -v

# Clean cache
rm -rf site/
mkdocs build --clean
```

### Checklist

- [ ] Python version correct (3.7+)
- [ ] Virtual environment activated
- [ ] Dependencies fully installed
- [ ] mkdocs.yml configuration correct
- [ ] Document files exist and format is correct

## 📚 References

- [MkDocs Official Documentation](https://www.mkdocs.org/)
- [Material for MkDocs Theme](https://squidfunk.github.io/mkdocs-material/)
- [MkDocs Plugin Catalog](https://github.com/mkdocs/catalog)
- [Markdown Syntax Guide](https://www.markdownguide.org/)

---

**Last Updated**: 2026-03-27  
**Maintainer**: MECN Team
