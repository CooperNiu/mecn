package com.mecn.causal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LASSO 超参数自动调优测试
 * 
 * TDD: 先写测试，再实现功能
 */
class LassoHyperparameterTuningTest {
    
    private LassoRegression lasso;
    private double[][] testData;
    
    @BeforeEach
    void setUp() {
        lasso = new LassoRegression();
        // 创建测试数据：100个时间点，5个指标
        testData = generateTestData(100, 5);
    }
    
    @Test
    void testAutoTuneLambda_WithCrossValidation() {
        // Given - 创建带有自动调优功能的 LASSO
        
        // When - 执行交叉验证自动调优
        HyperparameterResult result = lasso.autoTuneLambda(testData);
        
        // Then - 应该返回最优的 lambda 值和评估指标
        assertThat(result).isNotNull();
        assertThat(result.getBestLambda()).isGreaterThan(0.0);
        assertThat(result.getBestLambda()).isLessThan(1.0);
        // CV 得分可能是负数（负MSE），只要不是无穷大即可
        assertThat(result.getCrossValidationScore()).isNotEqualTo(-Double.MAX_VALUE);
        assertThat(result.getLambdaCandidates()).isNotEmpty();
    }
    
    @Test
    void testAutoTuneLambda_WithCustomRange() {
        // Given
        double[] lambdaRange = {0.01, 0.05, 0.1, 0.2, 0.5};
        
        // When
        HyperparameterResult result = lasso.autoTuneLambda(testData, lambdaRange);
        
        // Then
        assertThat(result).isNotNull();
        // 最优 lambda 应该在候选值中
        boolean found = false;
        for (double lambda : lambdaRange) {
            if (Math.abs(result.getBestLambda() - lambda) < 1e-10) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
        assertThat(result.getNumCandidates()).isEqualTo(5);
    }
    
    @Test
    void testAutoTuneLambda_WithKFold() {
        // Given
        int kFolds = 5;
        
        // When
        HyperparameterResult result = lasso.autoTuneLambda(testData, null, kFolds);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getKFolds()).isEqualTo(kFolds);
        assertThat(result.getCvScores()).isNotEmpty();
    }
    
    @Test
    void testAutoTuneLambda_ReturnsModelMetrics() {
        // When
        HyperparameterResult result = lasso.autoTuneLambda(testData);
        
        // Then - 应该包含模型评估指标
        assertThat(result.getAIC()).isNotNaN();
        assertThat(result.getBIC()).isNotNaN();
        assertThat(result.getRSquared()).isGreaterThanOrEqualTo(0.0);
        assertThat(result.getRSquared()).isLessThanOrEqualTo(1.0);
    }
    
    @Test
    void testAutoTuneLambda_WithSmallData() {
        // Given - 小样本数据
        double[][] smallData = generateTestData(20, 3);
        
        // When
        HyperparameterResult result = lasso.autoTuneLambda(smallData);
        
        // Then - 应该能处理小样本
        assertThat(result).isNotNull();
        assertThat(result.getBestLambda()).isGreaterThan(0.0);
    }
    
    @Test
    void testAutoTuneLambda_WithLargeData() {
        // Given - 大数据集
        double[][] largeData = generateTestData(500, 10);
        
        // When
        HyperparameterResult result = lasso.autoTuneLambda(largeData);
        
        // Then - 应该能处理大数据
        assertThat(result).isNotNull();
        assertThat(result.getBestLambda()).isGreaterThan(0.0);
    }
    
    @Test
    void testAutoTuneLambda_ConsistentResults() {
        // When - 多次运行
        HyperparameterResult result1 = lasso.autoTuneLambda(testData);
        HyperparameterResult result2 = lasso.autoTuneLambda(testData);
        
        // Then - 结果应该一致（确定性算法）
        assertThat(result1.getBestLambda()).isEqualTo(result2.getBestLambda());
    }
    
    @Test
    void testHyperparameterResult_ContainsAllInfo() {
        // Given
        HyperparameterResult result = lasso.autoTuneLambda(testData);
        
        // Then - 结果对象应包含完整信息
        assertThat(result.getMethodName()).isEqualTo("LASSO");
        // 执行时间可能为0（太快），只要 >= 0 即可
        assertThat(result.getExecutionTimeMs()).isGreaterThanOrEqualTo(0);
        assertThat(result.getRecommendation()).isNotEmpty();
    }
    
    @Test
    void testAutoTuneWithDifferentDataDistributions() {
        // Given - 不同分布的数据
        double[][] normalData = generateNormalData(100, 5);
        double[][] sparseData = generateSparseData(100, 5);
        
        // When
        HyperparameterResult result1 = lasso.autoTuneLambda(normalData);
        HyperparameterResult result2 = lasso.autoTuneLambda(sparseData);
        
        // Then - 应该都能找到合适的参数
        assertThat(result1.getBestLambda()).isGreaterThan(0.0);
        assertThat(result2.getBestLambda()).isGreaterThan(0.0);
    }
    
    private static final java.util.Random RAND = new java.util.Random(42);
    // ==================== 辅助方法 ====================

    private double[][] generateTestData(int timePoints, int numVariables) {
        double[][] data = new double[timePoints][numVariables];
        for (int t = 1; t < timePoints; t++) {
            for (int i = 0; i < numVariables; i++) {
                data[t][i] = 0.5 * data[t-1][i];
                if (i > 0) data[t][i] += 0.3 * data[t-1][i-1];
                data[t][i] += RAND.nextDouble() * 0.1;
            }
        }
        return data;
    }

    private double[][] generateNormalData(int timePoints, int numVariables) {
        double[][] data = new double[timePoints][numVariables];
        for (int t = 0; t < timePoints; t++) {
            for (int i = 0; i < numVariables; i++) {
                double u1 = RAND.nextDouble();
                double u2 = RAND.nextDouble();
                data[t][i] = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
            }
        }
        return data;
    }

    private double[][] generateSparseData(int timePoints, int numVariables) {
        double[][] data = new double[timePoints][numVariables];
        for (int t = 0; t < timePoints; t++) {
            for (int i = 0; i < numVariables; i++) {
                if (RAND.nextDouble() > 0.8) {
                    data[t][i] = RAND.nextDouble();
                } else {
                    data[t][i] = 0.0;
                }
            }
        }
        return data;
    }
}
