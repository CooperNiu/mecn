# MECN API 使用指南

## 快速开始

### 1. 最简单的使用方式

```java
import com.mecn.MECNTools;
import com.mecn.causal.CausalResult;

// 准备数据 [T 时间点数][N 指标数]
double[][] data = loadData();

// 一行代码完成因果发现
CausalResult result = MECNTools.discoverCausalStructure(data, 0.05);
```

### 2. 使用 Builder 模式（推荐）

```java
import com.mecn.causal.CausalEngineBuilder;
import com.mecn.causal.LassoRegression;
import com.mecn.causal.FusionStrategy;

CausalResult result = new CausalEngineBuilder()
    .data(data)                              // 设置数据
    .method(new LassoRegression()            // 添加 LASSO 方法
        .withLambda(0.1)                     // 正则化参数
        .withThreshold(0.05))                // 阈值
    .significanceLevel(0.05)                 // 显著性水平
    .maxLag(12)                             // 最大滞后阶数
    .fusionStrategy(FusionStrategy.HYBRID)   // 融合策略
    .minVotes(2)                            // 最小投票数
    .discover();                            // 执行发现
```

### 3. 多方法集成

```java
import java.util.Arrays;
import com.mecn.causal.CausalMethod;

List<CausalMethod> methods = Arrays.asList(
    new LassoRegression().withLambda(0.1),
    // new GrangerCausality().withLag(2),  // 待实现
    // new PCMCI().withTauMax(5)           // 待实现
);

CausalResult result = new CausalEngineBuilder()
    .data(data)
    .methods(methods)                      // 添加多个方法
    .significanceLevel(0.05)
    .fusionStrategy(FusionStrategy.VOTING.minVotes(2))
    .discover();
```

### 4. 完整的端到端分析

```java
import com.mecn.MECNTools;
import com.mecn.model.EconomicIndicator;
import java.time.LocalDate;

// 定义指标
List<EconomicIndicator> indicators = Arrays.asList(
    EconomicIndicator.of("GDP", "国内生产总值"),
    EconomicIndicator.of("CPI", "消费者物价指数"),
    EconomicIndicator.of("M2", "广义货币供应量")
);

// 执行完整分析
MECNTools.AnalysisResult analysis = MECNTools.analyze()
    .indicators(indicators)
    .timeRange(LocalDate.of(2010, 1), LocalDate.of(2022, 12))
    .significanceLevel(0.05)
    .maxLag(12)
    .edgeThreshold(0.1)
    .execute();

// 获取结果
NetworkGraph network = analysis.getNetwork();
CausalResult causalResult = analysis.getCausalResult();
```

### 5. 冲击传播模拟

```java
import com.mecn.network.RippleSimulator;
import com.mecn.network.RippleResult;

RippleSimulator simulator = new RippleSimulator();

// 模拟 GDP 受到 -10% 的冲击
RippleResult ripple = simulator.simulate(network, "GDP", -0.1);

// 查看结果
System.out.println("总影响：" + ripple.getTotalImpact());
System.out.println("每个节点响应：" + ripple.getNodeResponses("CPI"));
```

### 6. 使用 MECNTools 快捷方法

```java
// 构建网络
NetworkGraph network = MECNTools.buildNetwork(causalResult, indicatorCodes, 0.1);

// 模拟冲击
RippleResult shockEffect = MECNTools.simulateShock(network, "GDP", -0.1);

// 自定义参数
RippleResult customShock = MECNTools.simulateShock(
    network, "M2", 0.05,  // 节点和幅度
    0.85,                 // 衰减因子
    30                    // 时间步数
);
```

## API 对比

### 传统方式（繁琐）

```java
// 旧的使用方式
CausalEngineImpl engine = new CausalEngineImpl(true);
engine.registerMethod(new LassoRegression());

CausalConfig config = new CausalConfig();
config.setMaxLag(12);
config.setSignificanceLevel(0.05);
config.setParallel(true);

CausalResult result = engine.discover(data, config);

NetworkBuilder builder = new NetworkBuilder(0.1);
NetworkGraph graph = builder.build(result, indicatorCodes);

RippleSimulator simulator = new RippleSimulator(0.9, 20);
RippleResult ripple = simulator.simulate(graph, "GDP", -0.1);
```

### 新的 Builder 方式（简洁）

```java
// 新的流式 API
CausalResult result = new CausalEngineBuilder()
    .data(data)
    .method(new LassoRegression().withLambda(0.1))
    .significanceLevel(0.05)
    .maxLag(12)
    .discover();

NetworkGraph graph = MECNTools.buildNetwork(result, indicatorCodes, 0.1);
RippleResult ripple = MECNTools.simulateShock(graph, "GDP", -0.1);
```

## 配置选项

### CausalEngineBuilder 参数

| 方法 | 说明 | 默认值 |
|------|------|--------|
| `data(double[][])` | 时间序列数据 [T][N] | **必填** |
| `method(CausalMethod)` | 添加因果方法 | **至少一个** |
| `methods(List<CausalMethod>)` | 添加多个因果方法 | 可选 |
| `maxLag(int)` | 最大滞后阶数 | 12 |
| `significanceLevel(double)` | 显著性水平 | 0.05 |
| `parallel(boolean)` | 是否并行计算 | true |
| `fusionStrategy(FusionStrategy)` | 融合策略 | HYBRID |
| `minVotes(int)` | 最小投票数 | 2 |

### FusionStrategy 融合策略

- **VOTING**: 只保留获得足够多方法支持的因果关系
- **WEIGHTED_AVERAGE**: 根据方法权重对因果强度进行加权平均
- **HYBRID**: 先投票筛选，再加权平均（推荐）

### LassoRegression 参数

```java
new LassoRegression()
    .withLambda(0.1)         // 正则化参数，控制稀疏性
    .withThreshold(0.05)     // 因果强度阈值
    .withMinStrength(0.15)   // 最小因果强度
    .withWeight(1.5);        // 方法权重（用于融合）
```

## 最佳实践

### 1. 选择合适的显著性水平

```java
// 探索性分析：较宽松
.significanceLevel(0.1)

// 标准分析：常用
.significanceLevel(0.05)

// 严格验证：较保守
.significanceLevel(0.01)
```

### 2. 多方法集成提高可靠性

```java
// 单一方法可能不稳定
List<CausalMethod> methods = Arrays.asList(
    new LassoRegression().withLambda(0.1),
    // 未来添加更多方法
    // new GrangerCausality().withLag(2),
    // new PCMCI().withTauMax(5)
);

// 使用混合策略，要求至少 2 个方法支持
.fusionStrategy(FusionStrategy.HYBRID.minVotes(2))
```

### 3. 调整边阈值平衡网络密度

```java
// 稠密网络：保留更多连接
MECNTools.buildNetwork(result, codes, 0.05);

// 稀疏网络：只保留强连接
MECNTools.buildNetwork(result, codes, 0.2);

// 标准密度
MECNTools.buildNetwork(result, codes, 0.1);
```

### 4. 冲击模拟的参数选择

```java
// 短期冲击（快速衰减）
new RippleSimulator(0.7, 10)

// 中期冲击（标准）
new RippleSimulator(0.9, 20)

// 长期冲击（缓慢衰减）
new RippleSimulator(0.95, 50)
```

## 错误处理

```java
try {
    CausalResult result = new CausalEngineBuilder()
        .data(data)
        .method(new LassoRegression())
        .discover();
} catch (IllegalArgumentException e) {
    // 数据格式错误或参数不合法
    System.err.println("Invalid input: " + e.getMessage());
} catch (RuntimeException e) {
    // 其他运行时错误
    System.err.println("Discovery failed: " + e.getMessage());
}
```

## 性能优化

### 并行计算

```java
// 大数据集建议开启并行
.parallel(true)  // 默认开启

// 小数据集可以关闭
.parallel(false)
```

### 内存优化

```java
// 对于超大数据集，减少滞后阶数
.maxLag(6)  // 而不是默认的 12

// 或使用更严格的阈值减少边的数量
.edgeThreshold(0.2)  // 过滤弱连接
```

## 迁移指南

如果您之前使用旧版本 API，迁移步骤：

1. 将 `CausalEngineImpl` 替换为 `CausalEngineBuilder`
2. 将链式调用方法改为 Builder 风格
3. 使用 `MECNTools` 简化常见操作

旧代码：
```java
CausalEngine engine = new CausalEngineImpl();
engine.registerMethod(new LassoRegression());
CausalResult result = engine.discover(data, config);
```

新代码：
```java
CausalResult result = new CausalEngineBuilder()
    .data(data)
    .method(new LassoRegression())
    .discover();
```

## 参考设计

本 API 设计参考了以下优秀项目：

- **Tigramite** (Python): 简洁的因果发现 API
- **scikit-learn**: Estimator 模式和 fit/predict 范式
- **Builder Pattern**: 流式构建复杂对象

核心设计理念：
1. **流畅的链式调用**：减少样板代码
2. **合理的默认值**：开箱即用
3. **渐进式复杂度**：简单场景简单用，复杂场景可定制
