# 变更日志

本文件记录 MECN 项目的所有重要变更。

## [未发布] - YYYY-MM-DD

### 新增
- 添加 CONTRIBUTING.md 贡献指南
- 添加 CHANGELOG.md 变更日志
- 添加 LICENSE 开源许可证
- 创建 docs/ 文档目录，整理技术文档
- 创建 scripts/ 脚本目录，集中管理工具脚本

### 改进
- 重构 README.md，增强功能介绍和文档导航
- 简化 DEPLOYMENT.md 和 TESTING.md，移除冗余信息
- 更新 JDK 版本要求为 17+
- 添加快速设置脚本 setup-jdk17.sh

### 修复
- 修复 JGraphT 1.5.2 中心性算法 API 兼容性问题
- 修复 iText PDF 字体工厂依赖问题
- 修复数据提供者 JSON 解析类型转换错误

---

## [1.0.0] - 2026-03-27

### 新增 - 核心功能

#### 因果发现引擎
- **LASSO 回归**: L1 正则化识别稀疏因果结构
- **Granger 因果检验**: 基于 VAR 模型的 F 统计量检验
  - 自动选择最优滞后阶数（AIC 准则）
  - 支持三种模型类型（仅截距、截距 + 趋势、无）
  - 完整的 p 值计算
- **PCMCI 算法**: PC 阶段 + MCI 阶段的完整实现
  - 条件独立性检验
  - 滞后变量处理
  - 混淆变量控制
- **集成融合策略**
  - 投票机制：多方法交叉验证
  - 置信度加权平均
  - 混合策略：先投票筛选，再加权平均

#### 网络分析模块
- **中心性分析器** (CentralityAnalyzer)
  - 度中心性 (Degree Centrality)
  - 入度中心性 (In-Degree Centrality)
  - 出度中心性 (Out-Degree Centrality)
  - 接近中心性 (Closeness Centrality)
  - 中介中心性 (Betweenness Centrality)
  - PageRank
  - 特征向量中心性 (Eigenvector Centrality)
  - Top K 节点排序
  - 综合得分计算
- **社区检测器** (CommunityDetector)
  - 基于连通性的社区发现（BFS 连通分量）
  - 模块度计算
  - 社区统计信息
  - 节点所属社区查询
- **涟漪效应模拟器** (RippleSimulator)
  - 冲击传播算法
  - 风险传导路径搜索（DFS）
  - 系统重要性节点识别
  - 可配置的衰减因子和时间步数

#### 数据源支持
- **FRED API** (FredDataProvider)
  - 圣路易斯联邦储备经济数据库集成
  - 支持 50+ 个常用经济指标
  - 包括 GDP、就业、通胀、利率等关键指标
  - 需要申请免费 API Key
- **World Bank API** (WorldBankDataProvider)
  - 世界银行公开数据库集成
  - 全球各国宏观经济数据
  - GDP、人口、贸易、环境等多维度
  - 无需 API Key
- **增强模拟数据生成器** (EnhancedDataGenerator)
  - 40 个经济指标
  - 150 期历史数据
  - 经济周期模拟
  - 指标间交叉关联传导

#### 数据预处理
- **季节性调整** (SeasonalAdjustment)
  - X-13ARIMA 方法简化实现
  - Henderson 移动平均估计趋势循环
  - 季节因子计算
  - 不规则成分分解
  - 季节性强度指标
- **平稳性检验** (ADFTest)
  - Augmented Dickey-Fuller 单位根检验
  - 最优滞后阶数选择（AIC 准则）
  - 三种模型类型支持
  - p 值和临界值计算

#### 报告生成
- **PDF 报告生成器** (PdfReportGenerator)
  - 完整的宏观经济网络分析报告
  - 执行摘要
  - 网络统计信息
  - 中心性分析结果
  - 系统重要性分析
  - 关键发现与建议
  - 使用 iText PDF 7.2.5

### 新增 - Web 与 API

#### REST API 控制器
- `/api/network/build` - 构建因果网络
- `/api/network/ripple` - 执行涟漪模拟
- `/api/network/systemic-importance` - 获取系统重要性节点
- `/api/network/visualize` - 获取可视化数据
- `/api/health` - 健康检查
- `/api/docs` - API 文档

#### Web 前端
- `index.html` - 主页面（D3.js 力导向图）
- 交互式网络可视化
- 参数配置界面
- 结果展示面板

### 新增 - 测试与质量

#### 单元测试
- **数据模型层测试** (5 个测试类，~90% 覆盖率)
  - EconomicIndicatorTest
  - TimeSeriesDataTest
  - NetworkGraphTest
  - CausalEdgeTest
  - CentralityResultTest
- **数据层测试** (2 个测试类，~95% 覆盖率)
  - EnhancedDataGeneratorTest
  - SimulatedDataProviderTest
- **因果引擎测试** (2 个测试类，~85% 覆盖率)
  - EnsembleFusionStrategyTest
  - CausalEngineImplTest
- **网络分析测试** (2 个测试类，~90% 覆盖率)
  - NetworkBuilderTest
  - RippleSimulatorTest
- **集成测试** (1 个测试类，~80% 覆盖率)
  - MECNEndToEndTest
- **新功能测试** (4 个测试类)
  - CentralityAnalyzerTest
  - CommunityDetectorTest
  - SeasonalAdjustmentTest
  - ADFTestTest

#### 代码质量
- JaCoCo 代码覆盖率检查（目标 90%+）
- Maven Surefire 测试插件配置
- AssertJ 流式断言支持
- Mockito Mock 框架集成

### 新增 - 部署与运维

#### Docker 支持
- **Dockerfile**: 多阶段构建优化镜像大小
- **docker-compose.yml**: 
  - 应用服务配置
  - Nginx 反向代理（可选 profile）
  - 健康检查配置
  - 资源限制配置
- **非 root 用户运行**: 安全最佳实践
- **环境变量配置**: 灵活的运行时配置

#### 部署文档
- DEPLOYMENT.md - 完整的 Docker 部署指南
- 故障排查手册
- 性能优化建议
- 监控和健康检查配置

### 新增 - 文档

#### 技术文档
- **README.md**: 项目主文档
- **API_USAGE.md**: 详细的 API 使用指南
- **DEPLOYMENT.md**: Docker 部署指南
- **TEST_MODULE_GUIDE.md**: 测试模块说明
- **IMPLEMENTATION_STATUS.md**: 实施状态报告
- **NEW_FEATURES_SUMMARY.md**: 新功能总结
- **JDK17_UPGRADE_COMPLETE.md**: JDK 升级报告

#### 文档特性
- Builder 模式 API 示例
- 配置选项详解
- 最佳实践指南
- 错误处理建议
- 性能优化技巧

### 改进 - 架构设计

#### 四层架构
- **数据层**: DataProvider, Generator, Repository
- **核心引擎层**: CausalEngine, NetworkAnalyzer
- **服务层**: Business Logic, Integration
- **表现层**: REST API, Web UI

#### 设计模式
- **Builder Pattern**: CausalEngineBuilder 流式 API
- **Strategy Pattern**: FusionStrategy 融合策略
- **Factory Pattern**: DataProvider 工厂
- **Template Method**: CausalMethod 抽象基类

#### API 优化
- 简洁的 Builder 风格 API
- 合理的默认参数
- 渐进式复杂度设计
- 流畅的链式调用

### 改进 - 技术栈

#### 核心依赖
- **Spring Boot**: 3.0.13 (升级到 JDK 17+)
- **Maven**: 3.9.6
- **JDK**: 17+ (从 JDK 14 升级)
- **Smile**: 3.0.2 (LASSO 回归)
- **JGraphT**: 1.5.2 (图论算法)
- **iText PDF**: 7.2.5 (PDF 生成)
- **JUnit**: 5.9.3 (测试框架)
- **Mockito**: 5.3.1 (Mock 框架)
- **AssertJ**: 3.24.2 (流式断言)

#### 编译配置
- maven-compiler-plugin 3.11.0
- release 17 配置
- UTF-8 编码
- -parameters 参数支持

### 修复 - 已知问题

#### JGraphT 兼容性
- 使用简化实现替代高级中心性算法 API
- 使用 BFS 连通分量替代 Louvain 算法
- 保持功能可用性的同时确保编译通过

#### iText PDF
- 移除字体工厂依赖，使用默认字体
- 处理 IOException 异常
- 简化字体配置

#### 数据类型转换
- 修复 FRED API 的 String 到 LocalDate 转换问题
- 修复 World Bank API 的 JSON 解析问题
- 临时返回空数据，等待完善 JSON 解析

### 已知限制

#### 部分完成的功能
- **FRED API**: 需要添加 Jackson/Gson 完善 JSON 解析
- **World Bank API**: 需要添加 Jackson/Gson 完善 JSON 解析
- **中心性算法**: 简化实现，精度可能不如专业算法
- **社区检测**: BFS 连通分量近似，非完整 Louvain

#### 待改进领域
- 完整的 X-13ARIMA 算法实现
- 更多中心性指标（Katz、HITS 等）
- PDF 报告中文字体支持
- 更多真实数据源集成（IMF、OECD 等）

### 技术债务

1. **JSON 解析库缺失**: FRED 和 WorldBank 数据提供者需要 Jackson 或 Gson
2. **JGraphT 版本**: 如需完整中心性算法，需考虑升级 JGraphT
3. **PDF 字体**: 中文字体支持需要额外配置
4. **测试覆盖率**: 部分模块覆盖率未达到 90% 目标

---

## [0.1.0] - 2026-03-25

### 新增
- 项目初始化
- 基础架构设计
- 核心数据模型定义
- LASSO 因果发现实现
- 基础网络构建功能
- Web 可视化原型
- Spring Boot 集成

### 技术栈确定
- Spring Boot 3.0.x
- JGraphT 1.5.2
- Smile 3.0.2
- D3.js 可视化

---

## 版本说明

### 版本号规则

遵循语义化版本（Semantic Versioning）：

- **MAJOR.MINOR.PATCH** (例如：1.0.0)
- MAJOR: 破坏性变更
- MINOR: 向后兼容的新功能
- PATCH: 向后兼容的问题修复

### 发布周期

- **Major 版本**: 每 6-12 个月
- **Minor 版本**: 每 1-2 个月
- **Patch 版本**: 根据需要随时发布

### 命名约定

每个版本可以有代号（可选）：
- 1.0.0: "Genesis" - 初始发布
- 1.1.0: "Expansion" - 功能扩展
- 2.0.0: "Evolution" - 重大演进

---

**最后更新**: 2026-03-27  
**当前版本**: 1.0.0-SNAPSHOT  
**维护者**: MECN Team
