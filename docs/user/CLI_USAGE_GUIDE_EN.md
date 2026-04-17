# MECN CLI Usage Guide

## 📖 Overview

MECN (Macro Economic Causal Network) command-line tool provides convenient macroeconomic causal network analysis functionality. With simple commands, you can execute the complete analysis workflow from data loading to causal discovery.

## 🚀 Quick Start

### Basic Usage

```bash
# Show help information
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI --help

# Show version information
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI --version

# Execute analysis with simulated data (default)
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI

# Execute analysis from CSV file
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI -i examples/sample_data.csv
```

### Running with Maven

```bash
# Compile project
mvn clean package -DskipTests

# Run CLI tool
mvn exec:java -Dexec.mainClass="com.mecn.MECNCLI" -Dexec.args="--help"
```

## 📋 Command Reference

### Main Commands

| Command | Description | Example |
|---------|-------------|---------|
| `analyze` | Execute causal network analysis (default) | `mecn analyze data.csv` |
| `preprocess` | Data preprocessing | `mecn preprocess data.csv` |
| `visualize` | Visualize analysis results | `mecn visualize results.json` |
| `help`, `-h` | Show help information | `mecn --help` |
| `version`, `-v` | Show version information | `mecn --version` |

## ⚙️ Parameter Options

### Input/Output Parameters

| Parameter | Short | Description | Default |
|-----------|-------|-------------|---------|
| `--input FILE` | `-i` | Input CSV file path | Use simulated data |
| `--output FILE` | `-o` | Output file path | Auto-generated |
| `--format FMT` | `-f` | Data format (csv, json) | csv |

### Algorithm Parameters

| Parameter | Short | Description | Default |
|-----------|-------|-------------|---------|
| `--algorithm ALG` | `-a` | Causal discovery algorithm | ensemble |
| `--lambda VAL` | `-l` | LASSO regularization parameter | 0.01 |
| `--max-lag N` | `-m` | Maximum lag order | 5 |
| `--alpha VAL` | | Significance level | 0.05 |

**Supported Algorithms**:
- `lasso`: LASSO Regression
- `granger`: Granger Causality Test
- `pcmci`: PCMCI Algorithm
- `ensemble`: Ensemble Method (LASSO + Granger, default)

### Data Processing Parameters

| Parameter | Description | Default |
|-----------|-------------|---------|
| `--auto-tune` | Enable hyperparameter auto-tuning | true |
| `--seasonal` | Enable seasonal adjustment | true |
| `--stationarity` | Enable stationarity test | true |

### Network Analysis Parameters

| Parameter | Description | Default |
|-----------|-------------|---------|
| `--centrality M` | Centrality analysis method | eigenvector |
| `--communities` | Enable community detection | true |

**Supported Centrality Methods**:
- `degree`: Degree Centrality
- `betweenness`: Betweenness Centrality
- `eigenvector`: Eigenvector Centrality
- `closeness`: Closeness Centrality

### Output Parameters

| Parameter | Short | Description | Default |
|-----------|-------|-------------|---------|
| `--report-format FMT` | `-r` | Report format (pdf, json, txt) | pdf |
| `--no-report` | | Don't generate report | false |
| `--verbose`, `-V` | | Verbose output mode | false |

## 💡 Usage Examples

### Example 1: Analysis with Default Configuration

```bash
# Use simulated data, all parameters use defaults
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI
```

### Example 2: Analysis from CSV File

```bash
# Read data from CSV file
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI -i examples/sample_data.csv
```

### Example 3: Specify Algorithm and Parameters

```bash
# Use LASSO algorithm, custom lambda and max-lag
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI \
  --algorithm lasso \
  --lambda 0.05 \
  --max-lag 3 \
  -i examples/sample_data.csv
```

### Example 4: Using Equals Sign Format

```bash
# Equals sign format is clearer
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI \
  "--algorithm=ensemble" \
  "--max-lag=2" \
  "--auto-tune=true"
```

### Example 5: Complete Configuration Example

```bash
# Complete analysis configuration
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

### Example 6: Disable Auto-Tuning

```bash
# Use fixed parameters, skip auto-tuning
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI \
  --algorithm lasso \
  --lambda 0.01 \
  --auto-tune=false
```

## 📊 CSV File Format

### Format Requirements

CSV files should follow this format:

```csv
date,GDP,UNRATE,CPI,PCE,FEDFUNDS
2010-01-01,100.5,9.8,217.5,100.0,0.11
2010-02-01,101.2,9.7,217.8,100.3,0.12
...
```

**Requirements**:
- First row is header (column names)
- First column must be date in `YYYY-MM-DD` format
- Subsequent columns are numeric values for economic indicators
- Missing values can be left blank (automatically filtered)
- Comma-separated

### Example File

The project provides a sample CSV file: `examples/sample_data.csv`

Contains 5 economic indicators:
- **GDP**: Gross Domestic Product
- **UNRATE**: Unemployment Rate
- **CPI**: Consumer Price Index
- **PCE**: Personal Consumption Expenditures
- **FEDFUNDS**: Federal Funds Rate

## 🔍 Output Description

### Console Output

The analysis process displays progress in 6 steps:

```
╔═══════════════════════════════════════════╗
║     MECN Macro Economic Causal Network    ║
╚═══════════════════════════════════════════╝

[Step 1/6] Loading data...
✓ Successfully loaded 5 indicators

[Step 2/6] Data preprocessing...
✓ Preprocessing complete, data dimensions: 50 x 5

[Step 3/6] Hyperparameter auto-tuning...
=== Hyperparameter Tuning Report ===
Optimal Lambda: 0.0010, R²: 0.9744

[Step 4/6] Executing causal discovery...
✓ Discovered X causal relationships

[Step 5/6] Building causal network...
✓ Network construction complete (X nodes, X edges)

[Step 6/6] Centrality analysis...
Top 5 Systemically Important Nodes:
  GDP      Composite Score: 0.8234  (Degree Centrality: 0.7500)
  ...

═══════════════════════════════════════════
           Analysis Result Summary
═══════════════════════════════════════════
Number of Indicators:  5
Data Points:           50
Causal Relationships:  X
Network Nodes:         5
Network Edges:         X
═══════════════════════════════════════════

✓ Analysis Complete!
```

### Verbose Mode

Use `--verbose` or `-V` parameter to display more debugging information and stack traces.

## ⚠️ Common Issues

### Q1: Class Not Found Error

**Problem**: `ClassNotFoundException` or `NoClassDefFoundError`

**Solution**: Ensure classpath includes all dependencies:
```bash
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
  com.mecn.MECNCLI
```

### Q2: File Not Found

**Problem**: `File not found: xxx.csv`

**Solution**: 
- Check if file path is correct
- Use absolute path or path relative to current directory
- Confirm file actually exists

### Q3: CSV Format Error

**Problem**: Warnings or errors when parsing CSV

**Solution**:
- Ensure date format is `YYYY-MM-DD`
- Check if column count is consistent
- Remove special characters or non-numeric content

### Q4: Zero Causal Relationships

**Problem**: Analysis result shows 0 causal relationships

**Possible Causes**:
- Too little data (recommend at least 30 time points)
- No significant causal relationships between indicators
- Improper parameter settings (try adjusting max-lag or significance level)

**Suggestions**:
```bash
# Increase lag order
--max-lag 5

# Adjust significance level
--alpha 0.1

# Use different algorithm
--algorithm granger
```

### Q5: Out of Memory

**Problem**: `OutOfMemoryError`

**Solution**: Increase JVM heap memory
```bash
java -Xmx4g -cp target/classes:... com.mecn.MECNCLI ...
```

## 🎯 Best Practices

### 1. Data Preparation

- Ensure data quality (not too many missing values)
- Time series length recommended ≥ 50
- Number of indicators recommended 5-20

### 2. Parameter Selection

- **Small datasets** (< 100 time points): Use smaller max-lag (2-3)
- **Large datasets** (> 200 time points): Can use larger max-lag (5-8)
- **Exploratory analysis**: Use ensemble algorithm
- **Precise analysis**: Try lasso and granger separately, compare results

### 3. Performance Optimization

- Disable unnecessary features (e.g., disable seasonal adjustment if not needed)
- For large datasets, consider sampling or using subsets
- Use `--verbose` to locate performance bottlenecks

### 4. Result Interpretation

- Focus on Top 5 important nodes
- Explain causal relationships combined with economic theory
- Run multiple times to verify result stability

## 📚 Related Documentation

- [README.md](../README.md) - Project overview
- [PROJECT_PROGRESS_REPORT.md](../pm/PROJECT_PROGRESS_REPORT.md) - Project progress report
- [API_USAGE.md](API_USAGE.md) - API usage guide

## 🤝 Contributing

Issues and Pull Requests are welcome!

## 📄 License

MIT License

---

**Last Updated**: 2026-04-17  
**Version**: v1.4.0-dev
