package com.mecn.causal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Granger 因果检验超参数自动调优测试
 * 
 * TDD: 先写测试，再实现功能
 */
class GrangerHyperparameterTuningTest {
    
    private GrangerCausality granger;
    private double[][] testData;
    
    @BeforeEach
    void setUp() {
        granger = new GrangerCausality();
        // 创建具有时序依赖的测试数据
        testData = generateTimeSeriesData(150, 5);
    }
    
    @Test
    void testAutoTuneLag_WithInformationCriteria() {
        // When - 使用信息准则自动选择最优 lag
        HyperparameterResult result = granger.autoTuneLag(testData);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBestLambda()).isGreaterThan(0);  // 这里用 bestLambda 存储最优 lag
        assertThat(result.getBestLambda()).isLessThanOrEqualTo(10);  // lag 不应太大
        assertThat(result.getAIC()).isNotNaN();
        assertThat(result.getBIC()).isNotNaN();
    }
    
    @Test
    void testAutoTuneLag_WithCustomRange() {
        // Given
        int[] lagRange = {1, 2, 3, 4, 5};
        
        // When
        HyperparameterResult result = granger.autoTuneLag(testData, lagRange);
        
        // Then
        assertThat(result).isNotNull();
        double bestLag = result.getBestLambda();
        boolean found = false;
        for (int lag : lagRange) {
            if (Math.abs(bestLag - lag) < 1e-10) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }
    
    @Test
    void testAutoTuneLag_ReturnsModelMetrics() {
        // When
        HyperparameterResult result = granger.autoTuneLag(testData);
        
        // Then - 应该包含模型评估指标
        assertThat(result.getAIC()).isNotNaN();
        assertThat(result.getBIC()).isNotNaN();
        assertThat(result.getRSquared()).isGreaterThanOrEqualTo(0.0);
        assertThat(result.getRSquared()).isLessThanOrEqualTo(1.0);
        assertThat(result.getRecommendation()).isNotEmpty();
    }
    
    @Test
    void testAutoTuneLag_WithSmallData() {
        // Given
        double[][] smallData = generateTimeSeriesData(30, 3);
        
        // When
        HyperparameterResult result = granger.autoTuneLag(smallData);
        
        // Then - 小样本应该选择较小的 lag
        assertThat(result).isNotNull();
        assertThat(result.getBestLambda()).isLessThanOrEqualTo(3);
    }
    
    @Test
    void testAutoTuneLag_WithLargeData() {
        // Given
        double[][] largeData = generateTimeSeriesData(500, 8);
        
        // When
        HyperparameterResult result = granger.autoTuneLag(largeData);
        
        // Then - 大样本可以尝试更大的 lag
        assertThat(result).isNotNull();
        assertThat(result.getBestLambda()).isGreaterThan(0);
    }
    
    @Test
    void testAutoTuneLag_ComparesMultipleCriteria() {
        // When
        HyperparameterResult result = granger.autoTuneLag(testData);
        
        // Then - 应该比较多个信息准则
        assertThat(result.getCvScores()).isNotEmpty();  // 存储不同准则的得分
        assertThat(result.getMethodName()).isEqualTo("Granger");
    }
    
    @Test
    void testGetOptimalSignificanceLevel() {
        // When
        double optimalSig = granger.getOptimalSignificanceLevel(testData);
        
        // Then
        assertThat(optimalSig).isGreaterThan(0.0);
        assertThat(optimalSig).isLessThan(0.2);
    }
    
    @Test
    void testAutoTuneLag_GeneratesReport() {
        // When
        HyperparameterResult result = granger.autoTuneLag(testData);
        
        // Then
        String report = result.generateReport();
        assertThat(report).contains("Granger");
        assertThat(report).contains("最优");
        assertThat(report).contains("AIC");
        assertThat(report).contains("BIC");
    }
    
    @Test
    void testAutoTuneLag_DifferentDataPatterns() {
        // Given - 不同模式的数据
        double[][] ar1Data = generateAR1Data(100, 4);
        double[][] ar2Data = generateAR2Data(100, 4);
        
        // When
        HyperparameterResult result1 = granger.autoTuneLag(ar1Data);
        HyperparameterResult result2 = granger.autoTuneLag(ar2Data);
        
        // Then - AR(2) 数据应该倾向于选择 lag=2
        assertThat(result1.getBestLambda()).isGreaterThan(0);
        assertThat(result2.getBestLambda()).isGreaterThan(0);
    }
    
    @Test
    void testHyperparameterResult_ForGranger() {
        // When
        HyperparameterResult result = granger.autoTuneLag(testData);
        
        // Then
        assertThat(result.getMethodName()).isEqualTo("Granger");
        assertThat(result.getTuningMethod()).isEqualTo("information_criteria");
        assertThat(result.getExecutionTimeMs()).isGreaterThanOrEqualTo(0);
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 生成时序数据
     */
    private double[][] generateTimeSeriesData(int timePoints, int numVariables) {
        double[][] data = new double[timePoints][numVariables];
        
        // 初始化
        for (int i = 0; i < numVariables; i++) {
            data[0][i] = Math.random();
        }
        
        // 生成 AR(1) 过程
        for (int t = 1; t < timePoints; t++) {
            for (int i = 0; i < numVariables; i++) {
                data[t][i] = 0.6 * data[t-1][i] + Math.random() * 0.2;
                
                // 添加变量间的依赖
                if (i > 0) {
                    data[t][i] += 0.2 * data[t-1][i-1];
                }
            }
        }
        
        return data;
    }
    
    /**
     * 生成 AR(1) 数据
     */
    private double[][] generateAR1Data(int timePoints, int numVariables) {
        double[][] data = new double[timePoints][numVariables];
        
        for (int i = 0; i < numVariables; i++) {
            data[0][i] = Math.random();
        }
        
        for (int t = 1; t < timePoints; t++) {
            for (int i = 0; i < numVariables; i++) {
                // AR(1): y_t = 0.7 * y_{t-1} + noise
                data[t][i] = 0.7 * data[t-1][i] + Math.random() * 0.1;
            }
        }
        
        return data;
    }
    
    /**
     * 生成 AR(2) 数据
     */
    private double[][] generateAR2Data(int timePoints, int numVariables) {
        double[][] data = new double[timePoints][numVariables];
        
        for (int i = 0; i < numVariables; i++) {
            data[0][i] = Math.random();
            if (timePoints > 1) {
                data[1][i] = 0.5 * data[0][i] + Math.random() * 0.1;
            }
        }
        
        for (int t = 2; t < timePoints; t++) {
            for (int i = 0; i < numVariables; i++) {
                // AR(2): y_t = 0.5 * y_{t-1} + 0.3 * y_{t-2} + noise
                data[t][i] = 0.5 * data[t-1][i] + 0.3 * data[t-2][i] + Math.random() * 0.1;
            }
        }
        
        return data;
    }
}
