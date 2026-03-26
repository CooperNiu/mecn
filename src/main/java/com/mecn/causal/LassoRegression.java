package com.mecn.causal;

import java.util.HashMap;
import java.util.Map;

/**
 * LASSO 回归因果发现实现（简化版本）
 * 
 * 使用线性回归 + 阈值过滤替代完整的 LASSO 实现
 * 待后续集成正确的 Smile LASSO API
 * 
 * @example
 * {@code
 * CausalMethod lasso = new LassoRegression()
 *     .withLambda(0.1)
 *     .withThreshold(0.05)
 *     .withWeight(1.5);
 * }
 */
public class LassoRegression extends CausalMethod {
    
    private static final double DEFAULT_THRESHOLD = 0.1;     // 默认阈值
    private static final double DEFAULT_MIN_STRENGTH = 0.15; // 最小因果强度
    
    public LassoRegression() {
        super("LASSO");
        this.weight = 1.0;
        this.parameters.put("threshold", DEFAULT_THRESHOLD);
        this.parameters.put("minStrength", DEFAULT_MIN_STRENGTH);
    }
    
    /**
     * 设置正则化参数 lambda
     * 
     * @param lambda 正则化参数
     * @return this instance for chaining
     */
    public LassoRegression withLambda(double lambda) {
        this.parameters.put("lambda", lambda);
        return this;
    }
    
    /**
     * 设置阈值
     * 
     * @param threshold 因果强度阈值
     * @return this instance for chaining
     */
    public LassoRegression withThreshold(double threshold) {
        this.parameters.put("threshold", threshold);
        return this;
    }
    
    /**
     * 设置最小因果强度
     * 
     * @param minStrength 最小因果强度
     * @return this instance for chaining
     */
    public LassoRegression withMinStrength(double minStrength) {
        this.parameters.put("minStrength", minStrength);
        return this;
    }
    
    @Override
    public CausalMatrix compute(double[][] data, Map<String, Object> params) {
        if (!validateData(data)) {
            throw new IllegalArgumentException("Invalid input data");
        }
        
        int T = data.length - 1;  // 时间点数（减去滞后）
        int N = data[0].length;   // 指标数
        
        // 获取参数
        double threshold = params != null && params.containsKey("threshold")
            ? ((Number) params.get("threshold")).doubleValue()
            : DEFAULT_THRESHOLD;
        
        double minStrength = params != null && params.containsKey("minStrength")
            ? ((Number) params.get("minStrength")).doubleValue()
            : DEFAULT_MIN_STRENGTH;
        
        CausalMatrix result = new CausalMatrix(N, getName());
        
        // 构建滞后数据集：X(t-1) -> Y(t)
        // 使用 T-1 个样本，避免越界
        int numSamples = T - 1;
        double[][] X_lag = new double[numSamples][N];  // t 期的所有指标
        double[][] Y_cur = new double[numSamples][N];  // t+1 期的所有指标
        
        for (int t = 0; t < numSamples; t++) {
            System.arraycopy(data[t], 0, X_lag[t], 0, N);
            System.arraycopy(data[t + 1], 0, Y_cur[t], 0, N);
        }
        
        // 对每一个指标 i，用所有其他指标的滞后项进行回归
        for (int i = 0; i < N; i++) {
            // 简化的 OLS 回归估计
            // 实际应用中应该使用 Smile 的正确 LASSO API
            double[] coefficients = estimateCoefficients(X_lag, Y_cur[i], numSamples, N);
            
            // 填充因果矩阵：j -> i 的影响强度
            for (int j = 0; j < N; j++) {
                if (j != i && Math.abs(coefficients[j]) > threshold) {
                    result.setCausalEffect(j, i, coefficients[j]);
                    
                    // 计算 p 值（简化估计）
                    double pValue = estimatePValue(coefficients[j], numSamples);
                    result.pValues[i][j] = pValue;
                }
            }
        }
        
        result.addMetadata("threshold", threshold);
        result.addMetadata("minStrength", minStrength);
        result.addMetadata("numSamples", T);
        result.addMetadata("numVariables", N);
        result.addMetadata("method", "simplified_OLS");
        
        return result;
    }
    
    /**
     * 简化的系数估计（使用 OLS 近似）
     * 
     * 注意：这是临时实现，后续会替换为正确的 Smile LASSO
     */
    private double[] estimateCoefficients(double[][] X, double[] y, int T, int N) {
        double[] coefficients = new double[N];
        
        // 简化的单变量回归（每个特征单独回归）
        // 这是一种近似，真正的 LASSO 需要多变量联合优化
        for (int j = 0; j < N; j++) {
            double sumXY = 0.0;
            double sumXX = 0.0;
            double meanX = 0.0;
            double meanY = 0.0;
            
            // 计算均值
            for (int t = 0; t < T; t++) {
                meanX += X[t][j];
                meanY += y[t];
            }
            meanX /= T;
            meanY /= T;
            
            // 计算协方差和方差
            for (int t = 0; t < T; t++) {
                double dx = X[t][j] - meanX;
                double dy = y[t] - meanY;
                sumXY += dx * dy;
                sumXX += dx * dx;
            }
            
            // OLS 估计
            if (sumXX > 1e-10) {
                coefficients[j] = sumXY / sumXX;
            } else {
                coefficients[j] = 0.0;
            }
        }
        
        return coefficients;
    }
    
    /**
     * 简化 p 值估计
     */
    private double estimatePValue(double coefficient, int sampleSize) {
        // 简化的 t 检验近似
        // 假设标准误约为系数绝对值的 1/3
        double se = Math.abs(coefficient) / 3.0 + 0.01;
        double tStat = Math.abs(coefficient) / se;
        
        // 使用正态分布近似 p 值（大样本）
        // P(Z > |t|) ≈ exp(-0.5 * t^2) / sqrt(2π)
        double pValue = Math.exp(-0.5 * tStat * tStat);
        
        return Math.min(1.0, pValue);
    }
    
    @Override
    public double getConfidence(double[][] data, int source, int target) {
        // 基于系数稳定性和强度计算置信度
        // 简化实现：返回默认值
        return 0.75;
    }
}
