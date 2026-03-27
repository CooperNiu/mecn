# 贡献指南

感谢您对 MECN 项目的关注！我们欢迎各种形式的贡献，包括代码提交、问题报告、功能建议、文档改进等。

## 📋 目录

- [行为准则](#行为准则)
- [如何参与](#如何参与)
- [开发环境设置](#开发环境设置)
- [提交代码流程](#提交代码流程)
- [代码规范](#代码规范)
- [测试要求](#测试要求)
- [提交信息规范](#提交信息规范)

## 行为准则

请遵循以下原则：
- **尊重他人**：保持友好和专业的交流氛围
- **开放包容**：欢迎不同背景和经验贡献者
- **建设性反馈**：提出问题的同时提供改进建议

## 如何参与

### 1. 报告问题

发现 bug 或有改进建议？请创建 Issue：

- **Bug 报告**：提供详细的复现步骤、环境信息、错误日志
- **功能建议**：说明使用场景、预期行为、可能的实现方案
- **文档问题**：指出不准确或缺失的内容

### 2. 提交代码

#### 简单修复（如拼写错误、文档更新）
直接创建 Pull Request (PR)，在描述中说明修改内容。

#### 功能开发或重大修改
1. 先创建 Issue 讨论需求和设计方案
2. 获得认可后再开始开发
3. 完成开发后提交 PR

### 3. 审查代码

欢迎帮助审查 PR，提出建设性的改进意见。

## 开发环境设置

### 前置要求

- **JDK 版本**: JDK 17 或更高版本
- **Maven**: Maven 3.6+
- **Git**: Git 2.x+

### 快速开始

```bash
# 1. Fork 项目
点击右上角 Fork 按钮

# 2. 克隆到本地
git clone https://github.com/YOUR_USERNAME/mecn.git
cd mecn

# 3. 设置 JDK 17+
./scripts/setup-jdk17.sh

# 4. 编译项目
mvn clean compile

# 5. 运行测试
mvn test

# 6. 创建新分支
git checkout -b feature/your-feature-name
```

## 提交代码流程

### 1. 分支命名

- **新功能**: `feature/功能简述`
- **Bug 修复**: `fix/问题简述`
- **文档更新**: `docs/更新简述`
- **重构**: `refactor/重构简述`
- **性能优化**: `perf/优化简述`

### 2. 开发流程

```bash
# 同步主分支
git fetch upstream
git checkout main
git rebase upstream/main

# 切换到开发分支
git checkout feature/your-feature

# 开发完成后合并主分支最新代码
git fetch upstream
git rebase upstream/main

# 推送到远程
git push origin feature/your-feature
```

### 3. 创建 Pull Request

1. 在 GitHub 上访问您的 fork
2. 点击 "Compare & pull request"
3. 填写 PR 描述（参考下方模板）
4. 等待 CI 检查和代码审查

### PR 模板

```markdown
## 变更类型
<!-- 请选择适用的类型 -->
- [ ] 新功能
- [ ] Bug 修复
- [ ] 文档更新
- [ ] 代码重构
- [ ] 性能优化
- [ ] 其他（请说明）

## 变更描述
<!-- 详细说明此 PR 的变更内容 -->

## 相关 Issue
<!-- 关联相关的 Issue，例如 Fixes #123 -->

## 测试情况
<!-- 说明已进行的测试 -->
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 手动测试完成

## 检查清单
- [ ] 代码遵循项目规范
- [ ] 已添加必要的单元测试
- [ ] 更新了相关文档
- [ ] 无新的编译警告
```

## 代码规范

### Java 编码规范

#### 命名规范
```java
// 类名：大驼峰
public class CausalEngine { }

// 方法名：小驼峰
public void discoverCausal() { }

// 变量名：小驼峰
private int maxLag;

// 常量：全大写，下划线分隔
private static final double DEFAULT_THRESHOLD = 0.05;

// 泛型：单字母大写
public class Result<T> { }
```

#### 注释规范
```java
/**
 * 执行因果发现分析
 * 
 * @param data 时间序列数据 [T][N]
 * @param config 配置参数
 * @return 因果发现结果
 * @throws IllegalArgumentException 当数据格式不正确时
 */
public CausalResult discover(double[][] data, CausalConfig config);
```

#### 代码风格
- 使用 4 个空格缩进
- 行宽不超过 120 字符
- 方法长度不超过 50 行
- 类长度不超过 500 行
- 避免过深的嵌套（不超过 4 层）

### 最佳实践

```java
// ✅ 推荐：清晰的变量命名
List<EconomicIndicator> indicators = new ArrayList<>();

// ❌ 不推荐：模糊的缩写
List<EI> list = new ArrayList<>();

// ✅ 推荐：使用 Stream API 简化集合操作
indicators.stream()
    .filter(i -> i.getCategory() == Category.MACRO)
    .map(EconomicIndicator::getCode)
    .collect(Collectors.toList());

// ❌ 不推荐：冗长的循环
List<String> codes = new ArrayList<>();
for (EconomicIndicator indicator : indicators) {
    if (indicator.getCategory() == Category.MACRO) {
        codes.add(indicator.getCode());
    }
}
```

## 测试要求

### 单元测试

所有新功能必须包含单元测试：

```java
@DisplayName("验证 Granger 因果检验的正确性")
@Test
void testGrangerCausality() {
    // Given: 准备测试数据
    double[][] testData = createTestData();
    
    // When: 执行因果发现
    GrangerCausality granger = new GrangerCausality();
    CausalResult result = granger.discover(testData);
    
    // Then: 验证结果
    assertThat(result.getEdges()).isNotEmpty();
    assertThat(result.getPValue()).isLessThan(0.05);
}
```

### 覆盖率要求

- 新增代码行覆盖率 ≥ 90%
- 分支覆盖率 ≥ 85%
- 关键算法必须 100% 覆盖

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=CausalEngineTest

# 生成覆盖率报告
mvn clean test jacoco:report

# 查看覆盖率报告
open target/site/jacoco/index.html
```

## 提交信息规范

### Commit Message 格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建、依赖等杂项

### Scope 范围

- `causal`: 因果发现引擎
- `network`: 网络分析
- `data`: 数据层
- `api`: REST API
- `web`: Web 前端
- `docs`: 文档
- `build`: 构建配置

### Subject 主题

- 使用祈使句："add" 而不是 "added" 或 "adds"
- 首字母小写
- 不使用句号结尾
- 长度不超过 50 字符

### Body 正文（可选）

- 详细说明变更动机
- 对比变更前后行为
- 说明设计决策的理由

### Footer 页脚（可选）

- 关联 Issue：`Fixes #123`
- 破坏性变更：`BREAKING CHANGE: ...`

### 示例

```
feat(causal): add Granger causality test implementation

- 实现基于 VAR 模型的 Granger 因果检验
- 支持 F 统计量和 p 值计算
- 自动选择最优滞后阶数（AIC 准则）

Fixes #45

BREAKING CHANGE: CausalMethod 接口增加 getLagOrder() 方法
```

## 文档贡献

### 更新 README.md

确保 README.md 包含：
- 清晰的项目介绍
- 快速开始指南
- 核心功能说明
- 使用示例

### 更新技术文档

- API 变更及时更新 `docs/API_USAGE.md`
- 部署流程变更更新 `docs/DEPLOYMENT.md`
- 测试方法变更更新 `docs/TESTING.md`

### 代码注释

- public 方法必须有 Javadoc
- 复杂算法需要详细注释
- 使用中文注释（考虑到团队主要使用中文）

## 发布流程

### 版本号规则

遵循语义化版本（Semantic Versioning）：

- **MAJOR.MINOR.PATCH** (例如：1.2.3)
- MAJOR: 破坏性变更
- MINOR: 向后兼容的新功能
- PATCH: 向后兼容的问题修复

### 发布检查清单

- [ ] 所有测试通过
- [ ] 代码覆盖率达标
- [ ] 更新 CHANGELOG.md
- [ ] 更新版本号（pom.xml）
- [ ] 更新文档
- [ ] 创建 Release Tag

## 联系方式

如有疑问，欢迎通过以下方式联系：

- 创建 GitHub Issue
- 发送邮件至项目维护者
- 参与项目讨论区

---

感谢您的贡献！🎉
