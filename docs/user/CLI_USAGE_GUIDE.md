# MECN CLI 使用指南

## 📖 概述

MECN (Macro Economic Causal Network) 命令行工具提供了便捷的宏观经济因果网络分析功能。通过简单的命令，您可以执行从数据加载到因果发现的完整分析流程。

## 🚀 快速开始

### 基本用法

```bash
# 显示帮助信息
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI --help

# 显示版本信息
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI --version

# 使用模拟数据执行分析（默认）
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI

# 从CSV文件执行分析
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI -i examples/sample_data.csv
```

### 使用Maven运行

```bash
# 编译项目
mvn clean package -DskipTests

# 运行CLI工具
mvn exec:java -Dexec.mainClass="com.mecn.MECNCLI" -Dexec.args="--help"
```

## 📋 命令说明

### 主要命令

| 命令 | 说明 | 示例 |
|------|------|------|
| `analyze` | 执行因果网络分析（默认） | `mecn analyze data.csv` |
| `preprocess` | 数据预处理 | `mecn preprocess data.csv` |
| `visualize` | 可视化分析结果 | `mecn visualize results.json` |
| `help`, `-h` | 显示帮助信息 | `mecn --help` |
| `version`, `-v` | 显示版本信息 | `mecn --version` |

## ⚙️ 参数选项

### 输入/输出参数

| 参数 | 短格式 | 说明 | 默认值 |
|------|--------|------|--------|
| `--input FILE` | `-i` | 输入CSV文件路径 | 使用模拟数据 |
| `--output FILE` | `-o` | 输出文件路径 | 自动生成 |
| `--format FMT` | `-f` | 数据格式 (csv, json) | csv |

### 算法参数

| 参数 | 短格式 | 说明 | 默认值 |
|------|--------|------|--------|
| `--algorithm ALG` | `-a` | 因果发现算法 | ensemble |
| `--lambda VAL` | `-l` | LASSO正则化参数 | 0.01 |
| `--max-lag N` | `-m` | 最大滞后阶数 | 5 |
| `--alpha VAL` | | 显著性水平 | 0.05 |

**支持的算法**:
- `lasso`: LASSO回归
- `granger`: Granger因果检验
- `pcmci`: PCMCI算法
- `ensemble`: 集成方法（LASSO + Granger，默认）

### 数据处理参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `--auto-tune` | 启用超参数自动调优 | true |
| `--seasonal` | 启用季节调整 | true |
| `--stationarity` | 启用平稳性检验 | true |

### 网络分析参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `--centrality M` | 中心性分析方法 | eigenvector |
| `--communities` | 启用社区检测 | true |

**支持的中心性方法**:
- `degree`: 度中心性
- `betweenness`: 中介中心性
- `eigenvector`: 特征向量中心性
- `closeness`: 接近中心性

### 输出参数

| 参数 | 短格式 | 说明 | 默认值 |
|------|--------|------|--------|
| `--report-format FMT` | `-r` | 报告格式 (pdf, json, txt) | pdf |
| `--no-report` | | 不生成报告 | false |
| `--verbose`, `-V` | | 详细输出模式 | false |

## 💡 使用示例

### 示例1：使用默认配置分析

```bash
# 使用模拟数据，所有参数采用默认值
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI
```

### 示例2：从CSV文件分析

```bash
# 从CSV文件读取数据
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI -i examples/sample_data.csv
```

### 示例3：指定算法和参数

```bash
# 使用LASSO算法，自定义lambda和max-lag
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI \
  --algorithm lasso \
  --lambda 0.05 \
  --max-lag 3 \
  -i examples/sample_data.csv
```

### 示例4：使用等号格式参数

```bash
# 等号格式更清晰
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI \
  "--algorithm=ensemble" \
  "--max-lag=2" \
  "--auto-tune=true"
```

### 示例5：完整配置示例

```bash
# 完整的分析配置
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI \
  -i examples/sample_data.csv \
  -o results/ \
  --algorithm ensemble \
  --auto-tune \
  --max-lag 3 \
  --centrality eigenvector \
  --report-format pdf \
  --verbose
```

### 示例6：禁用自动调优

```bash
# 使用固定参数，跳过自动调优
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI \
  --algorithm lasso \
  --lambda 0.01 \
  --auto-tune=false
```

## 📊 CSV文件格式

### 格式要求

CSV文件应符合以下格式：

```csv
date,GDP,UNRATE,CPI,PCE,FEDFUNDS
2010-01-01,100.5,9.8,217.5,100.0,0.11
2010-02-01,101.2,9.7,217.8,100.3,0.12
...
```

**要求**：
- 第一行是表头（列名）
- 第一列必须是日期，格式为 `YYYY-MM-DD`
- 后续列为各个经济指标的数值
- 缺失值可以留空（会自动过滤）
- 使用逗号分隔

### 示例文件

项目提供了示例CSV文件：`examples/sample_data.csv`

包含5个经济指标：
- **GDP**: 国内生产总值
- **UNRATE**: 失业率
- **CPI**: 消费者价格指数
- **PCE**: 个人消费支出
- **FEDFUNDS**: 联邦基金利率

## 🔍 输出说明

### 控制台输出

分析过程会显示6个步骤的进度：

```
╔═══════════════════════════════════════════╗
║     MECN 宏观经济因果网络分析系统         ║
╚═══════════════════════════════════════════╝

【步骤1/6】加载数据...
✓ 成功加载 5 个指标

【步骤2/6】数据预处理...
✓ 预处理完成，数据维度: 50 x 5

【步骤3/6】超参数自动调优...
=== 超参数调优报告 ===
最优 Lambda: 0.0010, R²: 0.9744

【步骤4/6】执行因果发现...
✓ 发现 X 条因果关系

【步骤5/6】构建因果网络...
✓ 网络构建完成 (X节点, X边)

【步骤6/6】中心性分析...
Top 5 系统重要性节点:
  GDP      综合得分: 0.8234  (度中心性: 0.7500)
  ...

═══════════════════════════════════════════
           分析结果摘要
═══════════════════════════════════════════
指标数量:       5
数据点数:       50
因果关系数:     X
网络节点数:     5
网络边数:       X
═══════════════════════════════════════════

✓ 分析完成！
```

### 详细模式

使用 `--verbose` 或 `-V` 参数可以显示更多调试信息和堆栈跟踪。

## ⚠️ 常见问题

### Q1: 找不到类错误

**问题**: `ClassNotFoundException` 或 `NoClassDefFoundError`

**解决**: 确保classpath包含了所有依赖：
```bash
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI
```

### Q2: 文件不存在

**问题**: `文件不存在: xxx.csv`

**解决**: 
- 检查文件路径是否正确
- 使用绝对路径或相对于当前目录的路径
- 确认文件确实存在

### Q3: CSV格式错误

**问题**: 解析CSV时出现警告或错误

**解决**:
- 确保日期格式为 `YYYY-MM-DD`
- 检查列数是否一致
- 移除特殊字符或非数字内容

### Q4: 因果关系数为0

**问题**: 分析结果显示0条因果关系

**可能原因**:
- 数据量太少（建议至少30个时间点）
- 指标间确实没有显著因果关系
- 参数设置不当（尝试调整max-lag或significance level）

**建议**:
```bash
# 增加滞后阶数
--max-lag 5

# 调整显著性水平
--alpha 0.1

# 使用不同的算法
--algorithm granger
```

### Q5: 内存不足

**问题**: `OutOfMemoryError`

**解决**: 增加JVM堆内存
```bash
java -Xmx4g -cp target/classes:... com.mecn.MECNCLI ...
```

## 🎯 最佳实践

### 1. 数据准备

- 确保数据质量（无过多缺失值）
- 时间序列长度建议 ≥ 50
- 指标数量建议 5-20 个

### 2. 参数选择

- **小数据集** (< 100时间点): 使用较小的max-lag (2-3)
- **大数据集** (> 200时间点): 可以使用较大的max-lag (5-8)
- **探索性分析**: 使用ensemble算法
- **精确分析**: 分别尝试lasso和granger，对比结果

### 3. 性能优化

- 关闭不必要的功能（如不需要季节调整可禁用）
- 对于大型数据集，考虑采样或使用子集
- 使用 `--verbose` 定位性能瓶颈

### 4. 结果解释

- 关注Top 5重要节点
- 结合经济学理论解释因果关系
- 多次运行验证结果稳定性

## 📚 相关文档

- [README.md](../README.md) - 项目总体介绍
- [PROJECT_PROGRESS_REPORT.md](PROJECT_PROGRESS_REPORT.md) - 项目进展报告
- [API_USAGE.md](API_USAGE.md) - API使用指南

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📄 许可证

MIT License

---

**最后更新**: 2026-04-17  
**版本**: v1.4.0-dev
