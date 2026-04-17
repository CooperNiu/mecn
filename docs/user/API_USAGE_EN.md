# MECN API Usage Guide

## Quick Start

### 1. Simplest Usage

```java
import com.mecn.MECNTools;
import com.mecn.causal.CausalResult;

// Prepare data [T time points][N indicators]
double[][] data = loadData();

// One-line causal discovery
CausalResult result = MECNTools.discoverCausalStructure(data, 0.05);
```

### 2. Using Builder Pattern (Recommended)

```java
import com.mecn.causal.CausalEngineBuilder;
import com.mecn.causal.LassoRegression;
import com.mecn.causal.FusionStrategy;

CausalResult result = new CausalEngineBuilder()
    .data(data)                              // Set data
    .method(new LassoRegression()            // Add LASSO method
        .withLambda(0.1)                     // Regularization parameter
        .withThreshold(0.05))                // Threshold
    .significanceLevel(0.05)                 // Significance level
    .maxLag(12)                             // Maximum lag order
    .fusionStrategy(FusionStrategy.HYBRID)   // Fusion strategy
    .minVotes(2)                            // Minimum votes
    .discover();                            // Execute discovery
```

### 3. Multi-Method Integration

```java
import java.util.Arrays;
import com.mecn.causal.CausalMethod;

List<CausalMethod> methods = Arrays.asList(
    new LassoRegression().withLambda(0.1),
    // new GrangerCausality().withLag(2),  // To be implemented
    // new PCMCI().withTauMax(5)           // To be implemented
);

CausalResult result = new CausalEngineBuilder()
    .data(data)
    .methods(methods)                      // Add multiple methods
    .significanceLevel(0.05)
    .fusionStrategy(FusionStrategy.VOTING.minVotes(2))
    .discover();
```

### 4. Complete End-to-End Analysis

```java
import com.mecn.MECNTools;
import com.mecn.model.EconomicIndicator;
import java.time.LocalDate;

// Define indicators
List<EconomicIndicator> indicators = Arrays.asList(
    EconomicIndicator.of("GDP", "Gross Domestic Product"),
    EconomicIndicator.of("CPI", "Consumer Price Index"),
    EconomicIndicator.of("M2", "Broad Money Supply")
);

// Execute complete analysis
MECNTools.AnalysisResult analysis = MECNTools.analyze()
    .indicators(indicators)
    .timeRange(LocalDate.of(2010, 1), LocalDate.of(2022, 12))
    .significanceLevel(0.05)
    .maxLag(12)
    .edgeThreshold(0.1)
    .execute();

// Get results
NetworkGraph network = analysis.getNetwork();
CausalResult causalResult = analysis.getCausalResult();
```

### 5. Shock Propagation Simulation

```java
import com.mecn.network.RippleSimulator;
import com.mecn.network.RippleResult;

RippleSimulator simulator = new RippleSimulator();

// Simulate -10% shock to GDP
RippleResult ripple = simulator.simulate(network, "GDP", -0.1);

// View results
System.out.println("Total impact: " + ripple.getTotalImpact());
System.out.println("Node responses: " + ripple.getNodeResponses("CPI"));
```

### 6. Using MECNTools Convenience Methods

```java
// Build network
NetworkGraph network = MECNTools.buildNetwork(causalResult, indicatorCodes, 0.1);

// Simulate shock
RippleResult shockEffect = MECNTools.simulateShock(network, "GDP", -0.1);

// Custom parameters
RippleResult customShock = MECNTools.simulateShock(
    network, "M2", 0.05,  // Node and magnitude
    0.85,                 // Decay factor
    30                    // Time steps
);
```

## API Comparison

### Traditional Approach (Verbose)

```java
// Old usage style
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

### New Builder Approach (Concise)

```java
// New fluent API
CausalResult result = new CausalEngineBuilder()
    .data(data)
    .method(new LassoRegression().withLambda(0.1))
    .significanceLevel(0.05)
    .maxLag(12)
    .discover();

NetworkGraph graph = MECNTools.buildNetwork(result, indicatorCodes, 0.1);
RippleResult ripple = MECNTools.simulateShock(graph, "GDP", -0.1);
```

## Configuration Options

### CausalEngineBuilder Parameters

| Method | Description | Default |
|--------|-------------|---------|
| `data(double[][])` | Time series data [T][N] | **Required** |
| `method(CausalMethod)` | Add causal method | **At least one** |
| `methods(List<CausalMethod>)` | Add multiple causal methods | Optional |
| `maxLag(int)` | Maximum lag order | 12 |
| `significanceLevel(double)` | Significance level | 0.05 |
| `parallel(boolean)` | Enable parallel computation | true |
| `fusionStrategy(FusionStrategy)` | Fusion strategy | HYBRID |
| `minVotes(int)` | Minimum votes | 2 |

### FusionStrategy Options

- **VOTING**: Only retain causal relationships supported by enough methods
- **WEIGHTED_AVERAGE**: Weighted average of causal strengths based on method weights
- **HYBRID**: First vote filtering, then weighted average (recommended)

### LassoRegression Parameters

```java
new LassoRegression()
    .withLambda(0.1)         // Regularization parameter, controls sparsity
    .withThreshold(0.05)     // Causal strength threshold
    .withMinStrength(0.15)   // Minimum causal strength
    .withWeight(1.5);        // Method weight (for fusion)
```

## Best Practices

### 1. Choose Appropriate Significance Level

```java
// Exploratory analysis: more lenient
.significanceLevel(0.1)

// Standard analysis: commonly used
.significanceLevel(0.05)

// Strict validation: more conservative
.significanceLevel(0.01)
```

### 2. Multi-Method Integration for Reliability

```java
// Single method may be unstable
List<CausalMethod> methods = Arrays.asList(
    new LassoRegression().withLambda(0.1),
    // Add more methods in the future
    // new GrangerCausality().withLag(2),
    // new PCMCI().withTauMax(5)
);

// Use hybrid strategy, require at least 2 methods support
.fusionStrategy(FusionStrategy.HYBRID.minVotes(2))
```

### 3. Adjust Edge Threshold to Balance Network Density

```java
// Dense network: retain more connections
MECNTools.buildNetwork(result, codes, 0.05);

// Sparse network: only retain strong connections
MECNTools.buildNetwork(result, codes, 0.2);

// Standard density
MECNTools.buildNetwork(result, codes, 0.1);
```

### 4. Shock Simulation Parameter Selection

```java
// Short-term shock (fast decay)
new RippleSimulator(0.7, 10)

// Medium-term shock (standard)
new RippleSimulator(0.9, 20)

// Long-term shock (slow decay)
new RippleSimulator(0.95, 50)
```

## Error Handling

```java
try {
    CausalResult result = new CausalEngineBuilder()
        .data(data)
        .method(new LassoRegression())
        .discover();
} catch (IllegalArgumentException e) {
    // Data format error or invalid parameters
    System.err.println("Invalid input: " + e.getMessage());
} catch (RuntimeException e) {
    // Other runtime errors
    System.err.println("Discovery failed: " + e.getMessage());
}
```

## Performance Optimization

### Parallel Computation

```java
// Recommended for large datasets
.parallel(true)  // Enabled by default

// Can disable for small datasets
.parallel(false)
```

### Memory Optimization

```java
// For very large datasets, reduce lag order
.maxLag(6)  // Instead of default 12

// Or use stricter threshold to reduce edge count
.edgeThreshold(0.2)  // Filter weak connections
```

## Migration Guide

If you were using the old version API, migration steps:

1. Replace `CausalEngineImpl` with `CausalEngineBuilder`
2. Change chained calls to Builder style
3. Use `MECNTools` to simplify common operations

Old code:
```java
CausalEngine engine = new CausalEngineImpl();
engine.registerMethod(new LassoRegression());
CausalResult result = engine.discover(data, config);
```

New code:
```java
CausalResult result = new CausalEngineBuilder()
    .data(data)
    .method(new LassoRegression())
    .discover();
```

## Design References

This API design draws inspiration from excellent projects:

- **Tigramite** (Python): Clean causal discovery API
- **scikit-learn**: Estimator pattern and fit/predict paradigm
- **Builder Pattern**: Fluent construction of complex objects

Core design principles:
1. **Fluent chain calls**: Reduce boilerplate code
2. **Reasonable defaults**: Works out of the box
3. **Progressive complexity**: Simple for simple cases, customizable for complex scenarios
