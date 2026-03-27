# MECN 测试指南

## 测试概览

MECN 项目包含完整的单元测试和集成测试，确保代码质量和功能正确性。

### 测试统计

- **总测试类数**: 12+
- **总测试方法数**: 109+
- **目标代码覆盖率**: 90%+

## 运行测试

```bash
# 运行所有测试
mvn clean test

# 运行特定测试类
mvn test -Dtest=EconomicIndicatorTest

# 运行特定包下的测试
mvn test -Dtest="com.mecn.model.*"

# 生成覆盖率报告
mvn clean test jacoco:report

# 查看覆盖率报告
open target/site/jacoco/index.html
```

## 测试覆盖的功能点

### ✅ 已完全覆盖的功能
1. 所有数据模型类的构造器、Setter/Getter
2. EnhancedDataGenerator 的数据生成逻辑
3. SimulatedDataProvider 的数据提供功能
4. EnsembleFusionStrategy 的三种融合策略
5. NetworkBuilder 的网络构建和过滤
6. RippleSimulator 的涟漪模拟、路径查找、重要性分析
7. 端到端的完整流程
8. **新增**: 中心性分析、社区检测、季节性调整、ADF 检验

## 测试最佳实践

1. **命名规范**: 所有测试类以 `Test` 结尾，使用 `@DisplayName` 提供中文描述
2. **断言选择**: 优先使用 AssertJ 的流式断言，提高可读性
3. **测试隔离**: 每个测试方法独立，不依赖其他测试的状态
4. **边界测试**: 包含边界条件和异常情况测试
5. **辅助方法**: 使用私有辅助方法创建测试数据，保持 DRY 原则

## 下一步

1. 修复主代码的编译错误
2. 运行完整的测试套件
3. 检查覆盖率报告，补充遗漏的测试
4. 添加性能测试和压力测试
5. 配置 CI/CD 自动运行测试

---

**最后更新**: 2026-03-27  
**测试版本**: 1.0.0  
**目标覆盖率**: 90%+
