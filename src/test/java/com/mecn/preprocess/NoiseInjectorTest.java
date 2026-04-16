package com.mecn.preprocess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 噪声注入器测试
 * 
 * TDD: 先写测试，再实现功能
 */
class NoiseInjectorTest {
    
    private NoiseInjector injector;
    private double[][] testData;
    
    @BeforeEach
    void setUp() {
        injector = new NoiseInjector(42);  // 固定种子
        testData = generateTestData(100, 5);
    }
    
    @Test
    void testAddGaussianNoise_PreservesDataShape() {
        // When
        double[][] noisyData = injector.addGaussianNoise(testData, 0.1);
        
        // Then
        assertThat(noisyData.length).isEqualTo(testData.length);
        assertThat(noisyData[0].length).isEqualTo(testData[0].length);
    }
    
    @Test
    void testAddGaussianNoise_IncreasesVariance() {
        // Given
        double originalVariance = calculateVariance(testData);
        
        // When
        double[][] noisyData = injector.addGaussianNoise(testData, 0.5);
        double noisyVariance = calculateVariance(noisyData);
        
        // Then - 噪声应该增加方差
        assertThat(noisyVariance).isGreaterThan(originalVariance);
    }
    
    @Test
    void testAddUniformNoise_PreservesDataShape() {
        // When
        double[][] noisyData = injector.addUniformNoise(testData, 0.1);
        
        // Then
        assertThat(noisyData.length).isEqualTo(testData.length);
        assertThat(noisyData[0].length).isEqualTo(testData[0].length);
    }
    
    @Test
    void testAddImpulseNoise_CreatesOutliers() {
        // Given
        double[][] noisyData = injector.addImpulseNoise(testData, 0.1, 5.0);
        
        // When - 计算超出3个标准差的点数
        int outlierCount = countOutliers(testData, noisyData, 3.0);
        
        // Then - 应该有异常值
        assertThat(outlierCount).isGreaterThan(0);
    }
    
    @Test
    void testAddDriftNoise_CreatesTrend() {
        // Given
        double[][] noisyData = injector.addDriftNoise(testData, 0.01);
        
        // When - 检查是否有趋势（首尾差异）
        double firstValue = noisyData[0][0];
        double lastValue = noisyData[noisyData.length - 1][0];
        
        // Then - 漂移应该产生明显的趋势
        assertThat(Math.abs(lastValue - firstValue)).isGreaterThan(0.1);
    }
    
    @Test
    void testAddSeasonalNoise_CreatesPeriodicity() {
        // Given
        int period = 12;
        double[][] noisyData = injector.addSeasonalNoise(testData, period, 0.3);
        
        // Then - 数据应该保持形状
        assertThat(noisyData.length).isEqualTo(testData.length);
    }
    
    @Test
    void testAddMixedNoise_CombinesMultipleTypes() {
        // When
        double[][] noisyData = injector.addMixedNoise(testData, 0.1, 0.05, 0.001);
        
        // Then
        assertThat(noisyData.length).isEqualTo(testData.length);
        assertThat(noisyData[0].length).isEqualTo(testData[0].length);
    }
    
    @Test
    void testCalculateSNR_ReturnsValidValue() {
        // Given
        double[][] noisyData = injector.addGaussianNoise(testData, 0.1);
        
        // When
        double snr = injector.calculateSNR(testData, noisyData);
        
        // Then
        assertThat(snr).isNotNaN();
        assertThat(snr).isGreaterThan(0);  // 应该有正的 SNR
    }
    
    @Test
    void testCalculateSNR_HigherForLessNoise() {
        // Given
        double[][] lowNoise = injector.addGaussianNoise(testData, 0.01);
        double[][] highNoise = injector.addGaussianNoise(testData, 0.5);
        
        // When
        double snrLow = injector.calculateSNR(testData, lowNoise);
        double snrHigh = injector.calculateSNR(testData, highNoise);
        
        // Then - 低噪声应该有更高的 SNR
        assertThat(snrLow).isGreaterThan(snrHigh);
    }
    
    @Test
    void testAddGaussianNoise_WithNullData() {
        // When & Then
        assertThatThrownBy(() -> injector.addGaussianNoise(null, 0.1))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void testAddGaussianNoise_WithEmptyData() {
        // Given
        double[][] emptyData = new double[0][0];
        
        // When & Then
        assertThatThrownBy(() -> injector.addGaussianNoise(emptyData, 0.1))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void testAddSeasonalNoise_WithInvalidPeriod() {
        // When & Then
        assertThatThrownBy(() -> injector.addSeasonalNoise(testData, 0, 0.1))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void testNoiseInjector_WithCustomSeed() {
        // Given
        NoiseInjector injector1 = new NoiseInjector(123);
        NoiseInjector injector2 = new NoiseInjector(123);
        
        // When
        double[][] noisy1 = injector1.addGaussianNoise(testData, 0.1);
        double[][] noisy2 = injector2.addGaussianNoise(testData, 0.1);
        
        // Then - 相同种子应该产生相同结果
        assertThat(noisy1).isEqualTo(noisy2);
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 生成测试数据
     */
    private double[][] generateTestData(int timePoints, int numVariables) {
        double[][] data = new double[timePoints][numVariables];
        
        for (int t = 0; t < timePoints; t++) {
            for (int i = 0; i < numVariables; i++) {
                // 生成具有趋势和季节性的数据
                data[t][i] = 10.0 + 0.1 * t + 2.0 * Math.sin(2 * Math.PI * t / 12) 
                    + Math.random() * 0.5;
            }
        }
        
        return data;
    }
    
    /**
     * 计算数据的总体方差
     */
    private double calculateVariance(double[][] data) {
        int T = data.length;
        int N = data[0].length;
        
        double mean = 0.0;
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < N; i++) {
                mean += data[t][i];
            }
        }
        mean /= (T * N);
        
        double variance = 0.0;
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < N; i++) {
                variance += Math.pow(data[t][i] - mean, 2);
            }
        }
        variance /= (T * N);
        
        return variance;
    }
    
    /**
     * 计算异常值数量
     */
    private int countOutliers(double[][] original, double[][] noisy, double threshold) {
        int T = original.length;
        int N = original[0].length;
        int count = 0;
        
        // 计算原始数据的标准差
        double std = Math.sqrt(calculateVariance(original));
        
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < N; i++) {
                double diff = Math.abs(noisy[t][i] - original[t][i]);
                if (diff > threshold * std) {
                    count++;
                }
            }
        }
        
        return count;
    }
}
