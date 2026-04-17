# CI/CD 配置指南

本文档说明 MECN 项目的持续集成和持续部署配置。

## 📋 目录

- [概述](#概述)
- [工作流程](#工作流程)
- [本地测试](#本地测试)
- [故障排查](#故障排查)

---

## 概述

MECN 项目使用 **GitHub Actions** 实现自动化 CI/CD，包含三个主要工作流程：

1. **CI (ci.yml)** - 每次推送和 PR 时自动运行
2. **Release (release.yml)** - 发布新版本时自动部署
3. **Docs (docs.yml)** - 文档更新时自动部署

---

## 工作流程

### 1. 持续集成 (ci.yml)

**触发条件**:
- 推送到 `main` 或 `develop` 分支
- 创建 Pull Request 到 `main` 分支

**执行任务**:

#### Build and Test
```yaml
✅  checkout 代码
✅  设置 JDK 17
✅  Maven 依赖缓存
✅  编译项目
✅  运行单元测试（133个测试）
✅  生成 JaCoCo 覆盖率报告
✅  上传测试结果
✅  上传覆盖率报告
```

#### Code Quality
```yaml
✅  Checkstyle 代码风格检查
✅  PMD 静态代码分析
```

#### Docker Build
```yaml
✅  构建 Docker 镜像
✅  测试 Docker 容器
```

**查看结果**:
- 访问 GitHub Actions 标签页
- 下载测试报告和覆盖率报告

---

### 2. 版本发布 (release.yml)

**触发条件**:
- 创建新的 GitHub Release

**执行任务**:
```yaml
✅  编译打包（跳过测试）
✅  发布到 GitHub Packages
✅  上传 JAR 文件
✅  自动生成发布说明
```

**使用方法**:
1. 在 GitHub 上创建新 Release
2. 设置版本号（如 v1.0.0）
3. 工作流程自动触发

---

### 3. 文档部署 (docs.yml)

**触发条件**:
- 推送到 `main` 分支且修改了文档
- 手动触发（workflow_dispatch）

**执行任务**:
```yaml
✅  生成 Javadoc
✅  安装 MkDocs 依赖
✅  构建 MkDocs 站点
✅  部署到 GitHub Pages
```

**访问文档**:
- Javadoc: `https://cooperniu.github.io/mecn/apidocs/`
- MkDocs: `https://cooperniu.github.io/mecn/`

---

## 本地测试

### 运行完整测试套件

```bash
# 运行所有测试
mvn clean test

# 生成覆盖率报告
mvn test jacoco:report

# 查看覆盖率报告
open target/site/jacoco/index.html
```

### 代码质量检查

```bash
# Checkstyle 检查
mvn checkstyle:check

# PMD 静态分析
mvn pmd:check

# 生成 Javadoc
mvn javadoc:javadoc
```

### Docker 构建测试

```bash
# 构建 Docker 镜像
docker build -t mecn:test .

# 运行容器测试
docker run --rm mecn:test java -version
```

---

## 配置说明

### Maven 配置

在 `pom.xml` 中添加以下插件以支持 CI/CD：

```xml
<!-- JaCoCo 覆盖率 -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
</plugin>

<!-- Checkstyle 代码风格 -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.0</version>
</plugin>

<!-- PMD 静态分析 -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.21.0</version>
</plugin>
```

### GitHub Secrets

需要在 GitHub 仓库设置中配置以下密钥：

| 密钥名 | 用途 | 必需 |
|--------|------|------|
| `GITHUB_TOKEN` | 自动提供，用于发布包 | ✅ |

---

## 故障排查

### 常见问题

#### 1. 测试失败

**症状**: CI 显示测试失败

**解决**:
```bash
# 本地复现
mvn test

# 查看详细错误
cat target/surefire-reports/*.txt
```

#### 2. 依赖下载失败

**症状**: Maven 构建卡在依赖下载

**解决**:
```bash
# 清理本地仓库
rm -rf ~/.m2/repository/com/mecn

# 重新构建
mvn clean install -U
```

#### 3. 覆盖率不达标

**症状**: JaCoCo 检查失败

**解决**:
```bash
# 查看覆盖率报告
open target/site/jacoco/index.html

# 添加缺失的测试
# 确保核心逻辑有充分的测试覆盖
```

#### 4. Docker 构建失败

**症状**: Docker 镜像构建失败

**解决**:
```bash
# 检查 Dockerfile
cat Dockerfile

# 本地构建测试
docker build -t mecn:debug .
docker logs <container_id>
```

---

## 最佳实践

### 1. 提交前检查

```bash
# 运行快速检查
mvn clean compile test

# 确保所有测试通过
# 确保没有编译警告
```

### 2. 编写测试

- 每个新功能必须有对应的单元测试
- 保持测试覆盖率 > 80%
- 使用有意义的测试名称

### 3. 代码审查

- PR 必须通过所有 CI 检查
- 至少一个维护者审查
- 遵循代码风格规范

### 4. 版本管理

- 使用语义化版本（SemVer）
- 每次发布更新 CHANGELOG.md
- 打标签后再创建 Release

---

## 监控和优化

### 构建时间优化

当前平均构建时间：**~2分钟**

优化建议：
- ✅ 使用 Maven 依赖缓存
- ✅ 并行执行独立任务
- ✅ 增量编译

### 覆盖率目标

| 指标 | 目标 | 当前 |
|------|------|------|
| 行覆盖率 | > 80% | ~75% |
| 分支覆盖率 | > 70% | ~65% |
| 方法覆盖率 | > 85% | ~80% |

---

## 相关链接

- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [Maven 官方文档](https://maven.apache.org/guides/)
- [JaCoCo 使用指南](https://www.jacoco.org/jacoco/trunk/doc/)
- [GitHub Packages](https://docs.github.com/en/packages)

---

**最后更新**: 2026-04-16  
**维护者**: MECN Development Team
