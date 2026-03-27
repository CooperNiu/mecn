# MECN - 高维宏观经济因果网络联动模型

Macro Economic Causal Network (MECN) - 一个用于分析宏观经济指标间因果关系的复杂网络模型。

## 📊 项目简介

MECN 将常规经济指标视为一个相互连接、动态演化的复杂网络，通过先进的因果发现算法（LASSO、Granger、PCMCI）捕捉指标间的因果关系，并支持涟漪效应模拟，分析单个指标波动对整个经济网络的连锁反应。

## 🎯 核心功能特性

### 1. 因果发现引擎
- **LASSO 回归**：L1 正则化识别稀疏因果结构
- **Granger 因果检验**：基于 VAR 模型的 F 统计量检验
- **PCMCI 算法**：PC 阶段 + MCI 阶段的先进因果发现
- **集成融合策略**：投票机制、置信度加权、混合策略

### 2. 网络分析
- **5 种中心性指标**：度中心性、入度、出度、接近中心性、中介中心性、PageRank、特征向量中心性
- **社区检测**：基于连通性的社区发现算法
- **系统重要性识别**：识别对网络整体效率影响最大的节点

### 3. 数据源支持
- **FRED API**：圣路易斯联邦储备经济数据库（50+ 指标）
- **World Bank API**：世界银行公开数据库
- **增强模拟数据**：40 个经济指标，150 期历史数据

### 4. 数据预处理
- **季节性调整**：X-13ARIMA 方法
- **平稳性检验**：ADF 单位根检验
- **缺失值处理**：多种插值方法

### 5. 涟漪效应模拟
- **冲击传播算法**：模拟指标波动在网络中的传导
- **风险路径查找**：DFS 搜索所有影响路径
- **衰减因子控制**：可配置的传播衰减机制

### 6. 报告生成
- **PDF 分析报告**：包含执行摘要、网络统计、中心性分析、关键发现
- **可视化导出**：D3.js 力导向图 JSON 格式

## 🚀 快速开始

### 环境要求

- Java 17+ (推荐 JDK 17 或更高版本)
- Maven 3.6+

### 快速设置

```bash
# 使用脚本设置 JDK 17+
./scripts/setup-jdk17.sh
```

### 编译项目

```bash
cd mecn
mvn clean install
```

### 运行方式

#### 方式 1: Spring Boot 后端 + Web 前端（推荐）

**第一步：启动 Spring Boot 后端**

```bash
# 使用 Maven 启动
mvn spring-boot:run

# 或直接运行 jar
java -jar target/mecn-1.jar
```

后端服务将在 `http://localhost:8080` 启动，提供以下 API：

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/network/build` | POST | 构建因果网络 |
| `/api/network/ripple` | POST | 执行涟漪模拟 |
| `/api/network/systemic-importance` | GET | 获取系统重要性节点 |
| `/api/health` | GET | 健康检查 |
| `/api/docs` | GET | API 文档 |

**第二步：打开前端页面**

- **直接打开**: `open src/main/resources/static/index.html`
- **或使用 HTTP 服务器**: `python3 -m http.server 8080 -d src/main/resources/static`

> **注意**: 前端默认使用模拟数据，如需连接后端 API，需修改 `index.html` 中的 API 调用。

#### 方式 2: Docker 部署（生产环境推荐）

**快速启动：**

```bash
# 构建并启动（仅应用）
docker-compose up --build

# 后台运行
docker-compose up -d

# 查看日志
docker-compose logs -f mecn-app

# 停止服务
docker-compose down
```

启动后访问：http://localhost:8080

**使用 Nginx 反向代理（可选）：**

```bash
# 启动应用 + Nginx
docker-compose --profile with-nginx up -d
```

访问：http://localhost（Nginx 监听 80 端口）

**手动 Docker 操作：**

```bash
# 1. 构建镜像
docker build -t mecn:latest .

# 2. 运行容器
docker run -d -p 8080:8080 \
  -e JAVA_OPTS="-Xms512m -Xmx1024m" \
  --name mecn \
  mecn:latest

# 3. 查看日志
docker logs -f mecn

# 4. 进入容器
docker exec -it mecn sh
```

**配置文件挂载：**

```bash
# 复制配置示例
cp config/application-prod.yml.example config/application-prod.yml

# 编辑配置后挂载运行
docker run -d -p 8080:8080 \
  -v $(pwd)/config/application-prod.yml:/app/config/application-prod.yml:ro \
  -v $(pwd)/logs:/app/logs \
  --name mecn \
  mecn:latest
```

**环境变量配置：**

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `JAVA_OPTS` | JVM 参数 | `-Xms512m -Xmx1024m` |
| `SPRING_PROFILES_ACTIVE` | Spring 激活 profile | `prod` |
| `SERVER_PORT` | 服务端口 | `8080` |
| `MECN_LEGACY_MODE` | 兼容模式 | `false` |
| `MECN_EDGE_THRESHOLD` | 边阈值 | `0.08` |
| `MECN_DECAY_FACTOR` | 衰减因子 | `0.9` |

### 配置说明

配置文件位于 `src/main/resources/application.yml`：

```yaml
mecn:
  legacy-mode: false          # true=仅 LASSO, false=多算法集成
  default-data-source: simulated  # simulated/fred/worldbank
  causal-methods:
    - lasso
    - granger
    - pcmci
  edge-threshold: 0.08        # 网络边过滤阈值
  ripple-time-steps: 20       # 涟漪模拟时间步数
  decay-factor: 0.9           # 冲击传播衰减因子
```

## 📁 项目结构

```
mecn/
├── src/main/java/com/mecn/
│   ├── model/                    # 数据模型
│   │   ├── EconomicIndicator     # 经济指标定义
│   │   ├── TimeSeriesData        # 时间序列数据
│   │   ├── CausalEdge            # 因果边
│   │   ├── NetworkGraph          # 网络图封装
│   │   └── CentralityResult      # 中心性结果
│   ├── data/                     # 数据层
│   │   ├── provider/             # 数据提供者接口
│   │   ├── generator/            # 数据生成器
│   │   └── repository/           # 数据仓储
│   ├── preprocess/               # 数据预处理
│   ├── causal/                   # 因果发现引擎
│   │   ├── CausalEngine          # 因果引擎接口
│   │   ├── LassoRegression       # LASSO 实现
│   │   ├── GrangerCausality      # Granger 检验 (TODO)
│   │   ├── PCMCIAlgorithm        # PCMCI 算法 (TODO)
│   │   └── EnsembleFusionStrategy # 集成融合策略
│   ├── network/                  # 网络分析模块
│   │   ├── NetworkBuilder        # 网络构建器
│   │   ├── CentralityAnalyzer    # 中心性分析 (TODO)
│   │   ├── CommunityDetector     # 社区检测 (TODO)
│   │   └── RippleSimulator       # 涟漪效应模拟器
│   ├── visualize/                # 可视化服务
│   ├── report/                   # 报告生成 (TODO)
│   ├── api/                      # REST API 控制器
│   └── config/                   # 配置类
└── src/main/resources/
    ├── application.yml           # 应用配置
    └── static/
        ├── index.html            # Web 主页面
        └── js/                   # D3.js 脚本
```

## 🔬 技术架构

### 因果发现算法

1. **LASSO 回归**：通过 L1 正则化进行变量选择，识别稀疏因果结构
2. **Granger 因果检验**：基于 VAR 模型的 F 检验，判断时间序列因果关系
3. **PCMCI 算法**：处理混淆变量的先进因果发现方法

### 集成融合策略

- **投票机制**：至少 N 个方法认为存在因果关系才保留
- **置信度加权**：综合各方法的 p-value 和系数大小
- **混合策略**：先投票筛选，再加权平均

### 涟漪效应算法

```
δ(t+1, vⱼ) = Σᵢ wᵢⱼ × δ(t, vᵢ) × decay_factor
```

其中：
- wᵢⱼ: 从节点 i 到 j 的边权重
- decay_factor: 衰减因子 (默认 0.9)
- 迭代直到收敛或达到最大时间步

## 📊 REST API 接口

### 构建网络

```bash
POST /api/network/build
Content-Type: application/json

{
  "dataSource": "simulated",
  "numPeriods": 150,
  "edgeThreshold": 0.08
}
```

### 获取可视化数据

```bash
GET /api/network/visualize
```

### 执行涟漪模拟

```bash
POST /api/network/ripple
Content-Type: application/json

{
  "shockNode": "CMD_0",
  "magnitude": 1.0,
  "timeSteps": 20
}
```

## 🧪 测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=LassoRegressionTest
```

## 📚 文档

- **[API 使用指南](docs/API_USAGE.md)** - 详细的 API 使用说明
- **[部署指南](docs/DEPLOYMENT.md)** - Docker 部署和配置
- **[测试指南](docs/TESTING.md)** - 测试模块说明和覆盖率报告

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

## 📧 联系方式

如有问题或建议，请创建 Issue。

---

**最后更新**: 2026-03-25
**版本**: 1.0.0-SNAPSHOT
