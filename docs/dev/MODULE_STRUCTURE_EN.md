# MECN Project Module Structure

## Project Overview
MECN (Macro Economic Causal Network) - High-dimensional Macroeconomic Causal Network Linkage Model

## Module Division

### 1. Main Entry Module (com.mecn)
**Responsibility**: Application startup and core utility classes
- `MECNApplication.java` - Spring Boot application startup class
- `MECNTools.java` - Core utility class collection
- `MacroEconomicNetwork.java` - Main macroeconomic network class

### 2. API Layer Module (com.mecn.api)
**Responsibility**: RESTful API interfaces, providing web services
- `DocsController.java` - API documentation controller
- `NetworkController.java` - Network analysis API controller

### 3. Data Module (com.mecn.data)
**Responsibility**: Data acquisition, generation, and management

#### 3.1 Data Providers (data.provider)
- `DataProvider.java` - Data provider interface (unified contract)
- `FredDataProvider.java` - FRED data source implementation
- `WorldBankDataProvider.java` - World Bank data source implementation
- `SimulatedDataProvider.java` - Simulated data source implementation

#### 3.2 Data Generators (data.generator)
- `EnhancedDataGenerator.java` - Enhanced data generator

### 4. Preprocessing Module (com.mecn.preprocess)
**Responsibility**: Time series data preprocessing
- `Preprocessor.java` - Main preprocessor class
- `ADFTest.java` - ADF stationarity test
- `SeasonalAdjustment.java` - Seasonal adjustment

### 5. Causal Algorithm Module (com.mecn.causal)
**Responsibility**: Causal discovery algorithm implementation

#### 5.1 Core Interfaces and Engine
- `CausalEngine.java` - Causal discovery engine interface
- `CausalEngineImpl.java` - Causal engine implementation
- `CausalEngineBuilder.java` - Engine builder
- `CausalConfig.java` - Causal discovery configuration
- `CausalMethod.java` - Causal method enumeration

#### 5.2 Algorithm Implementations
- `GrangerCausality.java` - Granger causality test
- `LassoRegression.java` - LASSO regression
- `PCMCI.java` - PCMCI algorithm

#### 5.3 Result Fusion
- `FusionStrategy.java` - Fusion strategy interface
- `EnsembleFusionStrategy.java` - Ensemble fusion strategy implementation

#### 5.4 Result Models
- `CausalResult.java` - Causal discovery result

#### 5.5 Examples
- `CausalDiscoveryExample.java` - Causal discovery example

### 6. Network Analysis Module (com.mecn.network)
**Responsibility**: Network construction, analysis, and simulation

#### 6.1 Network Construction
- `NetworkBuilder.java` - Network builder

#### 6.2 Centrality Analysis
- `CentralityAnalyzer.java` - Centrality analyzer

#### 6.3 Community Detection
- `CommunityDetector.java` - Community detector

#### 6.4 Risk Propagation Simulation
- `RippleSimulator.java` - Ripple effect simulator
- `RippleResult.java` - Ripple simulation result
- `RiskPath.java` - Risk path

#### 6.5 Systemic Importance
- `SystemicImportance.java` - Systemic importance assessment

### 7. Diagnosis Module (com.mecn.diagnosis)
**Responsibility**: Causal diagnosis and analysis reports
- `CausalDiagnoser.java` - Causal diagnoser
- `DiagnosticReport.java` - Diagnostic report
- `CausalDiagnosisExample.java` - Diagnosis example

### 8. Model Module (com.mecn.model)
**Responsibility**: Core data model definitions
- `TimeSeriesData.java` - Time series data model
- `EconomicIndicator.java` - Economic indicator model
- `CausalEdge.java` - Causal edge model
- `NetworkGraph.java` - Network graph model
- `CentralityResult.java` - Centrality result model

### 9. Report Module (com.mecn.report)
**Responsibility**: Report generation
- `PdfReportGenerator.java` - PDF report generator

## Module Dependencies

```
API Layer (api)
    ↓
Main Entry (mecn)
    ↓
┌─────────────────────────────────────┐
│  Business Process Layer              │
│  ├── Diagnosis Module (diagnosis)   │
│  └── Report Module (report)         │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│  Core Analysis Layer                 │
│  ├── Causal Algorithms (causal)     │
│  └── Network Analysis (network)     │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│  Data Processing Layer               │
│  ├── Preprocessing (preprocess)     │
│  └── Data Module (data)             │
└─────────────────────────────────────┘
    ↓
Model Layer (model) - Depended by all layers
```

## Design Principles

1. **Interface-Driven**: Key components define clear interfaces (e.g., `CausalEngine`, `DataProvider`)
2. **Modularization**: Clear module responsibilities, low coupling
3. **Extensibility**: Support algorithm extension through interfaces and strategy patterns
4. **Layered Architecture**: Clear layered structure for easy maintenance and testing

## Current Status Assessment

### ✅ Completed
- Basic module division established
- Core interfaces defined (CausalEngine, DataProvider, FusionStrategy)
- Multiple causal algorithms implemented
- Complete network analysis functionality
- Unit test framework configured

### 🔧 To Be Optimized
- Some inter-module coupling can be further reduced
- Lack of unified exception handling mechanism
- Logging needs standardization
- Configuration management can be more centralized
- Missing inter-module dependency documentation

## Refactoring Suggestions

### Short-term Optimization (Phase 1)
1. Extract common interfaces and abstract base classes
2. Unify data input/output interfaces
3. Add module-level comments and documentation
4. Standardize exception handling

### Medium-term Optimization (Phase 2)
1. Optimize with dependency injection framework
2. Implement configuration management center
3. Add unified logging framework
4. Optimize inter-module communication

### Long-term Optimization (Phase 3+)
1. Consider microservice decomposition
2. Introduce message queue for decoupling
3. Implement plugin architecture
4. Add performance monitoring

---

*Last Updated: 2026-04-16*
