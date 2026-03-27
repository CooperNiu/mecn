# MECN 项目文档索引

本目录包含 MECN 项目的所有重要文档。

## 📖 文档列表

### 核心文档
- **[API 使用指南](./API_USAGE.md)** - 详细的 API 使用说明，包括 Builder 模式、配置选项、最佳实践
- **[部署指南](./DEPLOYMENT.md)** - Docker 部署完整指南，包括 docker-compose、Nginx 配置、故障排查
- **[测试指南](./TESTING.md)** - 测试模块说明，包含测试类列表、覆盖率报告、运行方式

### 根目录文档
- **[README.md](../README.md)** - 项目主文档，快速开始、功能介绍
- **[pom.xml](../pom.xml)** - Maven 项目配置

### 脚本工具
- **[scripts/setup-jdk17.sh](../scripts/setup-jdk17.sh)** - JDK 17+ 环境设置脚本

## 🗂️ 项目结构概览

```
mecn/
├── docs/                      # 文档目录
│   ├── README.md             # 本文档
│   ├── API_USAGE.md          # API 使用指南
│   ├── DEPLOYMENT.md         # 部署指南
│   └── TESTING.md            # 测试指南
├── scripts/                   # 脚本工具
│   └── setup-jdk17.sh        # JDK 环境设置
├── src/                       # 源代码
├── docker/                    # Docker 配置
├── config/                    # 配置文件
├── README.md                  # 项目主文档
├── pom.xml                    # Maven 配置
├── Dockerfile                 # Docker 构建
└── docker-compose.yml         # Docker Compose 配置
```

## 📊 功能模块文档

### 因果发现引擎
- **LASSO 回归**: `src/main/java/com/mecn/causal/LassoRegression.java`
- **Granger 因果检验**: `src/main/java/com/mecn/causal/GrangerCausality.java`
- **PCMCI 算法**: `src/main/java/com/mecn/causal/PCMCI.java`
- **集成融合策略**: `src/main/java/com/mecn/causal/EnsembleFusionStrategy.java`

### 网络分析
- **中心性分析器**: `src/main/java/com/mecn/network/CentralityAnalyzer.java`
- **社区检测器**: `src/main/java/com/mecn/network/CommunityDetector.java`
- **涟漪模拟器**: `src/main/java/com/mecn/network/RippleSimulator.java`

### 数据源
- **FRED API**: `src/main/java/com/mecn/data/provider/FredDataProvider.java`
- **World Bank API**: `src/main/java/com/mecn/data/provider/WorldBankDataProvider.java`
- **模拟数据**: `src/main/java/com/mecn/data/generator/EnhancedDataGenerator.java`

### 预处理
- **季节性调整**: `src/main/java/com/mecn/preprocess/SeasonalAdjustment.java`
- **ADF 检验**: `src/main/java/com/mecn/preprocess/ADFTest.java`

### 报告生成
- **PDF 报告**: `src/main/java/com/mecn/report/PdfReportGenerator.java`

## 🔧 快速链接

### 开发相关
- [API 使用示例](./API_USAGE.md#快速开始)
- [运行测试](./TESTING.md#运行测试)
- [代码覆盖率](./TESTING.md#测试统计)

### 部署相关
- [Docker 快速启动](./DEPLOYMENT.md#快速开始)
- [环境变量配置](./DEPLOYMENT.md#配置说明)
- [故障排查](./DEPLOYMENT.md#故障排查)

---

**最后更新**: 2026-03-27  
**文档版本**: 1.0.0
