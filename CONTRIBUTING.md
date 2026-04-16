# 贡献指南 (Contributing to MECN)

感谢您对 MECN (Macro Economic Causal Network) 项目的关注！我们欢迎各种形式的贡献，包括代码改进、文档完善、Bug 修复和功能建议。

## 📋 目录

- [行为守则](#行为守则)
- [如何贡献](#如何贡献)
- [开发环境设置](#开发环境设置)
- [代码规范](#代码规范)
- [提交规范](#提交规范)
- [Pull Request 流程](#pull-request-流程)
- [测试要求](#测试要求)
- [文档规范](#文档规范)

## 行为守则

本项目采用 [Contributor Covenant](https://www.contributor-covenant.org/) 行为守则。参与此项目即表示您同意遵守其条款。请阅读 [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) 了解详情。

## 如何贡献

### 1. 报告 Bug

如果您发现了 Bug，请在 GitHub Issues 中创建一个新的 Issue，并包含：

- **清晰的标题**：简明扼要地描述问题
- **复现步骤**：详细说明如何复现该问题
- **预期行为**：描述您期望的正确行为
- **实际行为**：描述实际发生的情况
- **环境信息**：Java 版本、操作系统、MECN 版本等
- **相关日志**：如果有错误日志或堆栈跟踪，请一并提供

### 2. 提出新功能

在实现新功能之前，请先：

1. 检查现有的 Issues 和 Pull Requests，确保没有重复
2. 创建一个新的 Issue 讨论您的想法
3. 等待维护者的反馈和批准

### 3. 提交代码

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'feat: add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个 Pull Request

## 开发环境设置

### 前置要求

- **JDK 17+**（推荐 JDK 17 或更高版本）
- **Maven 3.6+**
- **Git**

### 快速开始

```bash
# 1. Fork 并克隆仓库
git clone https://github.com/YOUR_USERNAME/mecn.git
cd mecn

# 2. 添加上游远程仓库
git remote add upstream https://github.com/ORIGINAL_OWNER/mecn.git

# 3. 设置 JDK 17
./scripts/setup-jdk17.sh

# 4. 编译项目
mvn clean install

# 5. 运行测试
mvn test
```

## 代码规范

### Java 编码规范

我们遵循 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)，主要要点：

#### 命名规范
- **类名**：使用 PascalCase（大驼峰），如 `CausalEngine`
- **方法名**：使用 camelCase（小驼峰），如 `discoverCausalRelations`
- **常量**：使用 UPPER_SNAKE_CASE，如 `MAX_ITERATIONS`
- **变量**：使用 camelCase，具有描述性，避免单字母（除循环变量外）

#### 代码格式
- 使用 2 个空格缩进
- 每行最多 100 个字符
- 方法之间空一行
- 相关的代码块之间空两行

#### 注释规范
- **类注释**：每个公共类必须有 Javadoc 注释
- **方法注释**：公共方法必须有 Javadoc，包括 @param、@return、@throws
- **行内注释**：解释"为什么"而不是"是什么"
- **TODO 注释**：使用 `// TODO: 描述` 格式

#### 示例

```java
/**
 * 因果发现引擎接口
 * 
 * 定义因果挖掘的核心契约，支持多种因果发现算法的注册和执行
 * 
 * @author MECN Team
 * @since 1.0.0
 */
public interface CausalEngine {
    
    /**
     * 执行因果发现
     * 
     * @param data 时间序列数据矩阵 [T][N]，T 为时间点数，N 为指标数
     * @param config 因果发现配置
     * @return 因果图（包含邻接矩阵和置信度）
     * @throws IllegalArgumentException 如果数据为空或配置无效
     */
    CausalResult discover(double[][] data, CausalConfig config);
}
```

### 异常处理

- 使用项目统一的 `MECNException` 及其子类
- 提供有意义的错误消息
- 记录适当的日志级别（ERROR、WARN、INFO、DEBUG）

```java
if (data == null || data.length == 0) {
    logger.error("输入数据为空");
    throw new MECNException(
        MECNException.ErrorCode.DATA_VALIDATION_ERROR,
        "输入数据不能为空"
    );
}
```

### 日志规范

使用 SLF4J + Logback：

```java
private static final Logger logger = LoggerFactory.getLogger(YourClass.class);

logger.info("开始处理 {} 个指标", indicatorCount);
logger.debug("参数配置: {}", config);
logger.warn("检测到 {} 个缺失值", missingCount);
logger.error("处理失败", exception);
```

## 提交规范

我们遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范。

### 提交消息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

- **feat**: 新功能
- **fix**: Bug 修复
- **docs**: 文档更新
- **style**: 代码格式（不影响代码运行）
- **refactor**: 代码重构
- **test**: 测试相关
- **chore**: 构建过程或辅助工具的变动
- **perf**: 性能优化

### 示例

```bash
# 新功能
git commit -m "feat(causal): 添加 Transfer Entropy 算法支持"

# Bug 修复
git commit -m "fix(network): 修复中心性计算中的空指针异常"

# 文档更新
git commit -m "docs: 更新 API 文档和使用示例"

# 代码重构
git commit -m "refactor(preprocess): 提取预处理器抽象基类"

# 测试
git commit -m "test(causal): 增加 LASSO 算法的边界测试用例"
```

### 详细提交示例

```bash
git commit -m "feat(causal): 添加自动超参数调优功能

- 实现 LASSO λ 参数的交叉验证自动搜索
- 支持网格搜索和贝叶斯优化两种策略
- 添加模型评估指标输出（AIC、BIC、R²）

Closes #123"
```

## Pull Request 流程

### 1. 准备工作

```bash
# 同步上游仓库
git fetch upstream
git checkout main
git merge upstream/main

# 创建特性分支
git checkout -b feature/your-feature-name
```

### 2. 开发

- 小步提交，每次提交都有明确的目的
- 编写测试用例
- 更新相关文档
- 确保代码通过所有测试

### 3. 提交前检查清单

- [ ] 代码符合编码规范
- [ ] 添加了必要的单元测试
- [ ] 所有测试通过 (`mvn test`)
- [ ] 代码覆盖率满足要求（>90%）
- [ ] 更新了相关文档
- [ ] 提交消息符合规范
- [ ] 没有遗留的调试代码或注释

### 4. 提交 PR

1. 推送您的分支到 GitHub
2. 在 GitHub 上创建 Pull Request
3. 填写 PR 模板：
   - **描述**：清晰说明这个 PR 做了什么
   - **相关 Issue**：关联相关的 Issue（如 `Closes #123`）
   - **测试**：说明如何测试这些更改
   - **截图**：如果有 UI 变化，提供截图

### 5. Code Review

- 至少需要 1 个维护者审核通过
- 根据反馈进行修改
- 保持友好的讨论氛围

### 6. 合并

- 维护者会将您的 PR 合并到主分支
- 删除特性分支

## 测试要求

### 单元测试

- 每个公共方法都应有对应的单元测试
- 测试覆盖率要求：
  - 指令覆盖率：≥ 90%
  - 分支覆盖率：≥ 85%
- 使用 JUnit 5 + Mockito + AssertJ

```java
@Test
void testDiscoverWithValidData() {
    // Given
    double[][] data = generateTestData();
    CausalConfig config = new CausalConfig();
    
    // When
    CausalResult result = engine.discover(data, config);
    
    // Then
    assertThat(result).isNotNull();
    assertThat(result.getAdjacencyMatrix()).hasDimensions(N, N);
    assertThat(result.getMethodName()).isEqualTo("ensemble");
}
```

### 集成测试

- 测试模块间的交互
- 测试完整的业务流程
- 标记为 `@IntegrationTest`

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=CausalEngineImplTest

# 生成覆盖率报告
mvn jacoco:report

# 查看报告
open target/site/jacoco/index.html
```

## 文档规范

### README 更新

任何功能性更改都应更新 README.md：

- 新功能的说明
- API 变更的说明
- 配置项的更新
- 使用示例的补充

### JavaDoc

所有公共 API 必须有完整的 JavaDoc：

```java
/**
 * 构建因果网络
 * 
 * <p>根据因果发现结果和网络配置，构建有向加权图。</p>
 * 
 * @param causalResult 因果发现结果
 * @param nodeNames 节点名称列表
 * @return 构建好的网络图
 * @throws IllegalArgumentException 如果输入参数无效
 * @see CausalResult
 * @see NetworkGraph
 */
public NetworkGraph build(CausalResult causalResult, List<String> nodeNames) {
    // ...
}
```

### 示例代码

在文档中提供可运行的示例代码：

```java
// 1. 准备数据
List<EconomicIndicator> indicators = Arrays.asList(
    new EconomicIndicator("GDP", "国内生产总值"),
    new EconomicIndicator("CPI", "消费者价格指数")
);

// 2. 获取数据
DataProvider provider = new SimulatedDataProvider();
List<TimeSeriesData> data = provider.fetch(indicators, startDate, endDate);

// 3. 预处理
Preprocessor preprocessor = new DefaultPreprocessor();
TimeSeriesData processed = preprocessor.process(data.get(0), config);

// 4. 因果发现
CausalEngine engine = CausalEngineBuilder.create()
    .addMethod(new GrangerCausality())
    .addMethod(new LassoRegression())
    .build();

CausalResult result = engine.discover(dataMatrix, config);

// 5. 网络分析
NetworkGraph graph = networkBuilder.build(result, nodeNames);
List<CentralityResult> centralities = centralityAnalyzer.analyze(graph);
```

## 发布流程

### 版本号规范

遵循 [Semantic Versioning](https://semver.org/lang/zh-CN/)：

- **MAJOR.MINOR.PATCH**（主版本号.次版本号.修订号）
- **MAJOR**：不兼容的 API 变更
- **MINOR**：向后兼容的功能新增
- **PATCH**：向后兼容的问题修正

### 发布步骤

1. 更新版本号（pom.xml）
2. 更新 CHANGELOG.md
3. 创建 Git Tag
4. 构建并发布
5. 在 GitHub 创建 Release

## 常见问题

### Q: 我的 PR 多久会被审核？

A: 我们尽量在 3-5 个工作日内审核所有 PR。如果超过一周没有回应，可以礼貌地 @ 维护者。

### Q: 我可以同时提交多个 PR 吗？

A: 可以，但建议每个 PR 专注于一个功能或修复。相关的更改可以放在同一个 PR 中。

### Q: 如何保持我的 Fork 与上游同步？

```bash
git fetch upstream
git checkout main
git merge upstream/main
git push origin main
```

### Q: 测试失败怎么办？

A: 
1. 检查是否是您的更改导致的问题
2. 在本地运行测试确认
3. 查看 CI 日志了解详细信息
4. 如有需要，在 Issue 中寻求帮助

## 联系我们

- **GitHub Issues**: [https://github.com/mecn-project/mecn/issues](https://github.com/mecn-project/mecn/issues)
- **Email**: mecn-team@example.com
- **Discussion**: [https://github.com/mecn-project/mecn/discussions](https://github.com/mecn-project/mecn/discussions)

## 致谢

感谢所有为 MECN 项目做出贡献的开发者！🎉

---

**最后更新**: 2026-04-16
