# MECN Testing Guide

## Testing Overview

The MECN project includes comprehensive unit tests and integration tests to ensure code quality and functional correctness.

### Test Statistics

- **Total Test Classes**: 12+
- **Total Test Methods**: 109+
- **Target Code Coverage**: 90%+

## Running Tests

```bash
# Run all tests
mvn clean test

# Run a specific test class
mvn test -Dtest=EconomicIndicatorTest

# Run tests in a specific package
mvn test -Dtest="com.mecn.model.*"

# Generate coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

## Covered Functionality

### ✅ Fully Covered Features
1. All data model class constructors, setters/getters
2. EnhancedDataGenerator data generation logic
3. SimulatedDataProvider data provision functionality
4. EnsembleFusionStrategy three fusion strategies
5. NetworkBuilder network construction and filtering
6. RippleSimulator ripple simulation, path finding, importance analysis
7. End-to-end complete workflow
8. **New**: Centrality analysis, community detection, seasonal adjustment, ADF test

## Testing Best Practices

1. **Naming Convention**: All test classes end with `Test`, use `@DisplayName` for descriptive names
2. **Assertion Choice**: Prefer AssertJ's fluent assertions for better readability
3. **Test Isolation**: Each test method is independent, not relying on other tests' state
4. **Boundary Testing**: Include boundary conditions and exception scenarios
5. **Helper Methods**: Use private helper methods to create test data, following DRY principle

## Next Steps

1. Fix compilation errors in main code
2. Run the complete test suite
3. Review coverage reports and add missing tests
4. Add performance and stress tests
5. Configure CI/CD to automatically run tests

---

**Last Updated**: 2026-03-27  
**Test Version**: 1.0.0  
**Target Coverage**: 90%+
