# MECN - High-Dimensional Macroeconomic Causal Network Model

![Java CI](https://github.com/CooperNiu/mecn/actions/workflows/ci.yml/badge.svg)
![Tests](https://img.shields.io/badge/tests-201%20passed-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)
![Version](https://img.shields.io/badge/version-v1.5.0--dev-blue)

Macro Economic Causal Network (MECN) - An advanced complex network model for analyzing causal relationships among macroeconomic indicators.

## 📊 Project Overview

MECN treats conventional economic indicators as an interconnected, dynamically evolving complex network. It captures causal relationships between indicators using advanced causal discovery algorithms (LASSO, Granger, PCMCI) and supports ripple effect simulation to analyze the cascading impact of individual indicator fluctuations on the entire economic network.

## 🎯 Core Features

### 1. Causal Discovery Engine
- **LASSO Regression**: L1 regularization to identify sparse causal structures
- **Granger Causality Test**: F-statistic test based on VAR models
- **PCMCI Algorithm**: Advanced causal discovery with PC + MCI stages
- **Ensemble Fusion Strategy**: Voting mechanism, confidence weighting, hybrid strategies

### 2. Network Analysis
- **7 Centrality Metrics**: Degree, In-Degree, Out-Degree, Closeness, Betweenness, PageRank, Eigenvector
- **Community Detection**: Connectivity-based community discovery algorithm
- **Systemic Importance Identification**: Identify nodes with maximum impact on network efficiency

### 3. Data Source Support
- **FRED API**: Federal Reserve Economic Data (50+ indicators)
- **World Bank API**: World Bank Open Data
- **Enhanced Simulated Data**: 40 economic indicators, 150 periods of historical data

### 4. Data Preprocessing
- **Seasonal Adjustment**: X-13ARIMA method
- **Stationarity Test**: ADF unit root test
- **Missing Value Handling**: Multiple interpolation methods

### 5. Ripple Effect Simulation
- **Shock Propagation Algorithm**: Simulate indicator fluctuation transmission in the network
- **Risk Path Finding**: DFS search for all influence paths
- **Decay Factor Control**: Configurable propagation decay mechanism

### 6. Report Generation
- **PDF Analysis Reports**: Executive summary, network statistics, centrality analysis, key findings
- **Visualization Export**: D3.js force-directed graph JSON format

### 7. Command Line Interface (CLI)
- **Complete CLI Interface**: Supports analyze, preprocess, visualize commands
- **Flexible Parameter Configuration**: Short/long parameter formats, equals sign format
- **CSV Data Import**: Support reading time series data from CSV files
- **Real-time Progress Display**: Beautiful console output and progress prompts
- **Comprehensive Documentation**: Complete usage guide and examples

## 🚀 Quick Start

### Prerequisites

- Java 17+ (JDK 17 or higher recommended)
- Maven 3.6+
- Git

### Method 1: Local Development

```bash
# 1. Clone repository
git clone https://github.com/CooperNiu/mecn.git
cd mecn

# 2. Build project
mvn clean package -DskipTests

# 3. Run complete analysis example
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.CompleteAnalysisExample

# 4. Launch Web interface
# Option 1: Use Spring Boot application
mvn spring-boot:run

# Option 2: Or use HTTP server
python3 -m http.server 8080 -d src/main/resources/static
```

> **Note**: The frontend uses simulated data by default. To connect to the backend API, modify the API calls in `index.html`.

### Method 2: Command Line Tool (CLI)

```bash
# Build project
mvn clean package -DskipTests

# Show help
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI --help

# Execute analysis with simulated data
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI

# Execute analysis from CSV file
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI -i examples/sample_data.csv

# Specify algorithm and parameters
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI \
  --algorithm ensemble \
  --max-lag 3 \
  -i examples/sample_data.csv
```

**Detailed Documentation**: [CLI Usage Guide](docs/CLI_USAGE_GUIDE.md)

### Method 3: Docker Deployment (Production Recommended)

**Quick Start:**

```bash
# Build and start (application only)
docker-compose up --build

# Run in background
docker-compose up -d

# View logs
docker-compose logs -f mecn-app

# Stop services
docker-compose down
```

Access after startup: http://localhost:8080

**Using Nginx Reverse Proxy (Optional):**

```bash
# Start application + Nginx
docker-compose --profile with-nginx up -d
```

Access: http://localhost (Nginx listens on port 80)

**Manual Docker Operations:**

```bash
# 1. Build image
docker build -t mecn:latest .

# 2. Run container
docker run -d -p 8080:8080 \
  -e JAVA_OPTS="-Xms512m -Xmx1024m" \
  --name mecn \
  mecn:latest

# 3. View logs
docker logs -f mecn

# 4. Enter container
docker exec -it mecn sh
```

**Configuration File Mounting:**

```bash
# Copy configuration example
cp config/application-prod.yml.example config/application-prod.yml

# Edit configuration
vim config/application-prod.yml

# Mount configuration when running
docker run -d -p 8080:8080 \
  -v $(pwd)/config/application-prod.yml:/app/config/application-prod.yml \
  mecn:latest
```

## 📁 Project Structure

```
mecn/
├── src/main/java/com/mecn/
│   ├── api/                    # RESTful API controllers
│   ├── causal/                 # Causal discovery engine
│   │   ├── CausalEngine.java           # Engine interface
│   │   ├── CausalEngineImpl.java       # Engine implementation
│   │   ├── LassoRegression.java        # LASSO algorithm
│   │   ├── GrangerCausality.java       # Granger causality
│   │   └── EnsembleFusionStrategy.java # Ensemble strategy
│   ├── cli/                    # Command line interface
│   │   ├── CommandLineParser.java      # Parameter parser
│   │   └── MECNCLI.java                # CLI entry point
│   ├── config/                 # Configuration management
│   ├── data/                   # Data providers
│   │   ├── provider/           # Data source implementations
│   │   └── generator/          # Data generators
│   ├── diagnosis/              # Causal diagnosis
│   ├── io/                     # Data I/O
│   │   └── CsvDataReader.java          # CSV reader
│   ├── model/                  # Data models
│   ├── network/                # Network analysis
│   │   ├── NetworkBuilder.java         # Network construction
│   │   ├── CentralityAnalyzer.java     # Centrality analysis
│   │   ├── CommunityDetector.java      # Community detection
│   │   └── RippleSimulator.java        # Ripple simulation
│   ├── preprocess/             # Data preprocessing
│   ├── report/                 # Report generation
│   ├── service/                # Business logic layer
│   │   └── AnalysisService.java        # Analysis service
│   └── visualization/          # Visualization export
│       └── NetworkVisualizer.java      # Network visualizer
├── src/test/java/              # Test code
├── docs/                       # Documentation
├── examples/                   # Example files
├── config/                     # Configuration files
├── docker/                     # Docker configuration
├── scripts/                    # Utility scripts
├── pom.xml                     # Maven configuration
├── Dockerfile                  # Docker build file
└── docker-compose.yml          # Docker Compose configuration
```

## 🧪 Testing

The project follows TDD (Test-Driven Development) practices with comprehensive test coverage:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CausalEngineImplTest

# Generate coverage report
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

**Test Statistics**:
- Total Tests: 201
- Pass Rate: 100% ✅
- Coverage: JaCoCo integrated

## 📖 Documentation

- **[Project Progress Report](docs/PROJECT_PROGRESS_REPORT.md)** - Current development status
- **[CLI Usage Guide](docs/CLI_USAGE_GUIDE.md)** - Complete CLI documentation
- **[API Usage Guide](docs/API_USAGE.md)** - RESTful API documentation
- **[Deployment Guide](docs/DEPLOYMENT.md)** - Deployment instructions
- **[CI/CD Guide](docs/CICD_GUIDE.md)** - Continuous integration/deployment
- **[Testing Guide](docs/TESTING.md)** - Testing practices and guidelines
- **[Module Structure](docs/MODULE_STRUCTURE.md)** - Module architecture details
- **[TDD Practice Record](docs/TDD_PRACTICE_RECORD.md)** - TDD development records

## 🛠️ Technology Stack

- **Core Language**: Java 17
- **Build Tool**: Maven 3.6+
- **Statistical Computing**: Smile 3.1.1
- **Graph Theory**: JGraphT 1.5.2
- **Web Framework**: Spring Boot 3.2.0
- **PDF Generation**: iText 7.2.5
- **JSON Processing**: Jackson 2.15.3
- **Testing**: JUnit 5.10.1, AssertJ 3.24.2
- **Code Coverage**: JaCoCo 0.8.11
- **Containerization**: Docker, Docker Compose

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Smile Library**: For powerful statistical computing capabilities
- **JGraphT**: For comprehensive graph theory algorithms
- **Spring Boot**: For excellent web framework support
- **FRED & World Bank**: For providing open economic data APIs

## 📧 Contact

- **Author**: Cooper Niu
- **GitHub**: [@CooperNiu](https://github.com/CooperNiu)
- **Project Link**: [https://github.com/CooperNiu/mecn](https://github.com/CooperNiu/mecn)

---

**Last Updated**: 2026-04-17  
**Version**: v1.5.0-dev  
**Status**: Phase 1 & 2 Completed, Visualization Implemented
