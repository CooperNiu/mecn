package com.mecn.preprocess;

import java.util.Arrays;

/**
 * X-13ARIMA 季节性调整
 * 
 * 实现基于 X-13ARIMA 方法的季节性调整算法
 * 
 * X-13ARIMA 是美国人口普查局开发的季节性调整标准方法，结合了 X-12-ARIMA 和 TRAMO/SEATS 的优点。
 * 
 * @example
 * {@code
 * SeasonalAdjustment adjustment = new SeasonalAdjustment();
 * double[] originalData = {...}; // 原始时间序列数据
 * double[] adjustedData = adjustment.adjust(originalData);
 * 
 * // 获取季节因子
 * double[] seasonalFactors = adjustment.getSeasonalFactors();
 * }
 */
public class SeasonalAdjustment {
    
    private static final int DEFAULT_MOVING_AVERAGE_ORDER = 13;  // X-13 默认移动平均阶数
    
    private double[] seasonalFactors;
    private double[] trendCycle;
    private double[] irregularComponent;
    private double[] seasonallyAdjusted;
    
    /**
     * 对时间序列进行季节性调整
     * 
     * @param data 原始时间序列数据
     * @return 季节性调整后的数据
     */
    public double[] adjust(double[] data) {
        if (data == null || data.length < 36) {
            throw new IllegalArgumentException("数据长度至少需要 36 个观测值（3 年）");
        }
        
        int n = data.length;
        
        // 1. 估计趋势循环成分（使用 Henderson 移动平均）
        trendCycle = estimateTrendCycle(data);
        
        // 2. 计算去趋势序列
        double[] detrended = new double[n];
        for (int i = 0; i < n; i++) {
            detrended[i] = data[i] - trendCycle[i];
        }
        
        // 3. 估计季节因子
        seasonalFactors = estimateSeasonalFactors(detrended);
        
        // 4. 计算季节性调整序列
        seasonallyAdjusted = new double[n];
        for (int i = 0; i < n; i++) {
            seasonallyAdjusted[i] = data[i] - seasonalFactors[i];
        }
        
        // 5. 计算不规则成分
        irregularComponent = new double[n];
        for (int i = 0; i < n; i++) {
            irregularComponent[i] = seasonallyAdjusted[i] - trendCycle[i];
        }
        
        return seasonallyAdjusted;
    }
    
    /**
     * 估计趋势循环成分
     */
    private double[] estimateTrendCycle(double[] data) {
        int n = data.length;
        double[] trend = new double[n];
        
        // 使用 Henderson 移动平均（简化为对称加权移动平均）
        int halfWindow = DEFAULT_MOVING_AVERAGE_ORDER / 2;
        
        // 对于两端使用非对称权重
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            double weightSum = 0.0;
            
            int start = Math.max(0, i - halfWindow);
            int end = Math.min(n - 1, i + halfWindow);
            
            for (int j = start; j <= end; j++) {
                // Henderson 权重（简化版本）
                double weight = hendersonWeight(Math.abs(j - i), halfWindow);
                sum += weight * data[j];
                weightSum += weight;
            }
            
            trend[i] = sum / weightSum;
        }
        
        return trend;
    }
    
    /**
     * Henderson 权重函数
     */
    private double hendersonWeight(int lag, int halfWindow) {
        // 简化的 Henderson 权重计算
        // 实际 X-13ARIMA 使用更复杂的公式
        if (lag > halfWindow) {
            return 0.0;
        }
        
        // 使用二次权重作为近似
        double normalizedLag = (double) lag / halfWindow;
        return 1.0 - normalizedLag * normalizedLag;
    }
    
    /**
     * 估计季节因子
     */
    private double[] estimateSeasonalFactors(double[] detrended) {
        int n = detrended.length;
        int period = 12;  // 假设月度数据
        
        double[] factors = new double[n];
        
        // 计算每个时期的平均季节效应
        double[] periodAverages = new double[period];
        int[] periodCounts = new int[period];
        
        for (int i = 0; i < n; i++) {
            int periodIndex = i % period;
            periodAverages[periodIndex] += detrended[i];
            periodCounts[periodIndex]++;
        }
        
        for (int i = 0; i < period; i++) {
            if (periodCounts[i] > 0) {
                periodAverages[i] /= periodCounts[i];
            }
        }
        
        // 标准化季节因子（使其总和为 0）
        double overallAverage = 0.0;
        for (double avg : periodAverages) {
            overallAverage += avg;
        }
        overallAverage /= period;
        
        for (int i = 0; i < period; i++) {
            periodAverages[i] -= overallAverage;
        }
        
        // 将季节因子扩展到整个序列
        for (int i = 0; i < n; i++) {
            factors[i] = periodAverages[i % period];
        }
        
        return factors;
    }
    
    /**
     * 获取季节因子
     */
    public double[] getSeasonalFactors() {
        return seasonalFactors;
    }
    
    /**
     * 获取趋势循环成分
     */
    public double[] getTrendCycle() {
        return trendCycle;
    }
    
    /**
     * 获取不规则成分
     */
    public double[] getIrregularComponent() {
        return irregularComponent;
    }
    
    /**
     * 获取季节性调整后的数据
     */
    public double[] getSeasonallyAdjusted() {
        return seasonallyAdjusted;
    }
    
    /**
     * 计算季节性强度指标
     * 
     * @return 季节性强度（0-1，越大表示季节性越强）
     */
    public double calculateSeasonalityStrength() {
        if (seasonalFactors == null || irregularComponent == null) {
            return 0.0;
        }
        
        double seasonalVariance = variance(seasonalFactors);
        double irregularVariance = variance(irregularComponent);
        
        if (seasonalVariance + irregularVariance < 1e-10) {
            return 0.0;
        }
        
        return seasonalVariance / (seasonalVariance + irregularVariance);
    }
    
    /**
     * 计算方差
     */
    private double variance(double[] data) {
        double mean = Arrays.stream(data).average().orElse(0.0);
        double sumSquaredDiff = 0.0;
        
        for (double value : data) {
            double diff = value - mean;
            sumSquaredDiff += diff * diff;
        }
        
        return sumSquaredDiff / data.length;
    }
}
