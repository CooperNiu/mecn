# MECN 项目模块结构说明

## 项目概述
MECN (Macro Economic Causal Network) - 高维宏观经济因果网络联动模型

## 模块划分

### 1. 主入口模块 (com.mecn)
**职责**: 应用启动和核心工具类
- `MECNApplication.java` - Spring Boot 应用启动类
- `MECNTools.java` - 核心工具类集合
- `MacroEconomicNetwork.java` - 宏观经济网络主类

### 2. API 层模块 (com.mecn.api)
**职责**: RESTful API 接口，提供 Web 服务
- `DocsController.java` - API 文档控制器
- `NetworkController.java` - 网络分析 API 控制器

### 3. 数据模块 (com.mecn.data)
**职责**: 数据获取、生成和管理

#### 3.1 数据提供者 (data.provider)
- `DataProvider.java` - 数据提供者接口（统一契约）
- `FredDataProvider.java` - FRED 数据源实现
- `WorldBankDataProvider.java` - 世界银行数据源实现
- `SimulatedDataProvider.java` - 模拟数据源实现

#### 3.2 数据生成器 (data.generator)
- `EnhancedDataGenerator.java` - 增强型数据生成器

### 4. 预处理模块 (com.mecn.preprocess)
**职责**: 时间序列数据预处理
- `Preprocessor.java` - 预处理器主类
- `ADFTest.java` - ADF 平稳性检验
- `SeasonalAdjustment.java` - 季节性调整

### 5. 因果算法模块 (com.mecn.causal)
**职责**: 因果发现算法实现

#### 5.1 核心接口与引擎
- `CausalEngine.java` - 因果发现引擎接口
- `CausalEngineImpl.java` - 因果引擎实现
- `CausalEngineBuilder.java` - 引擎构建器
- `CausalConfig.java` - 因果发现配置
- `CausalMethod.java` - 因果方法枚举

#### 5.2 算法实现
- `GrangerCausality.java` - Granger 因果检验
- `LassoRegression.java` - LASSO 回归
- `PCMCI.java` - PCMCI 算法

#### 5.3 结果融合
- `FusionStrategy.java` - 融合策略接口
- `EnsembleFusionStrategy.java` - 集成融合策略实现

#### 5.4 结果模型
- `CausalResult.java` - 因果发现结果

#### 5.5 示例
- `CausalDiscoveryExample.java` - 因果发现示例

### 6. 网络分析模块 (com.mecn.network)
**职责**: 网络构建、分析和模拟

#### 6.1 网络构建
- `NetworkBuilder.java` - 网络构建器

#### 6.2 中心性分析
- `CentralityAnalyzer.java` - 中心性分析器

#### 6.3 社区检测
- `CommunityDetector.java` - 社区检测器

#### 6.4 风险传播模拟
- `RippleSimulator.java` - 涟漪效应模拟器
- `RippleResult.java` - 涟漪模拟结果
- `RiskPath.java` - 风险路径

#### 6.5 系统重要性
- `SystemicImportance.java` - 系统重要性评估

### 7. 诊断模块 (com.mecn.diagnosis)
**职责**: 因果诊断和分析报告
- `CausalDiagnoser.java` - 因果诊断器
- `DiagnosticReport.java` - 诊断报告
- `CausalDiagnosisExample.java` - 诊断示例

### 8. 模型模块 (com.mecn.model)
**职责**: 核心数据模型定义
- `TimeSeriesData.java` - 时间序列数据模型
- `EconomicIndicator.java` - 经济指标模型
- `CausalEdge.java` - 因果边模型
- `NetworkGraph.java` - 网络图模型
- `CentralityResult.java` - 中心性结果模型

### 9. 报告模块 (com.mecn.report)
**职责**: 报告生成
- `PdfReportGenerator.java` - PDF 报告生成器

## 模块依赖关系

```
API 层 (api)
    ↓
主入口 (mecn)
    ↓
┌─────────────────────────────────────┐
│  业务流程层                           │
│  ├── 诊断模块 (diagnosis)            │
│  └── 报告模块 (report)               │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│  核心分析层                           │
│  ├── 因果算法 (causal)              │
│  └── 网络分析 (network)             │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│  数据处理层                           │
│  ├── 预处理 (preprocess)            │
│  └── 数据模块 (data)                │
└─────────────────────────────────────┘
    ↓
模型层 (model) - 被所有层依赖
```

## 设计原则

1. **接口驱动**: 关键组件定义了清晰的接口（如 `CausalEngine`, `DataProvider`）
2. **模块化**: 各模块职责明确，低耦合
3. **可扩展**: 通过接口和策略模式支持算法扩展
4. **分层架构**: 清晰的分层结构便于维护和测试

## 当前状态评估

### ✅ 已完成
- 基本的模块划分已建立
- 核心接口已定义（CausalEngine, DataProvider, FusionStrategy）
- 多种因果算法已实现
- 网络分析功能完整
- 单元测试框架已配置

### 🔧 待优化
- 部分模块间耦合度可进一步降低
- 缺少统一的异常处理机制
- 日志记录需要规范化
- 配置管理可以更集中化
- 缺少模块间的依赖关系文档

## 重构建议

### 短期优化（阶段一）
1. 提取公共接口和抽象基类
2. 统一数据输入/输出接口
3. 添加模块级注释和文档
4. 规范异常处理

### 中期优化（阶段二）
1. 引入依赖注入框架优化
2. 实现配置管理中心
3. 添加统一的日志框架
4. 优化模块间通信

### 长期优化（阶段三+）
1. 考虑微服务化拆分
2. 引入消息队列解耦
3. 实现插件化架构
4. 添加性能监控

---

*最后更新: 2026-04-16*
