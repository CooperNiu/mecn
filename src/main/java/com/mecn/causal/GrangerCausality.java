package com.mecn.causal;

import java.util.Map;

/**
 * Granger 因果检验实现
 * 
 * 基于向量自回归 (VAR) 模型的 Granger 因果检验
 * 
 * 原理：如果 X 的过去值能够帮助预测 Y 的当前值（在控制 Y 的过去值后），
 * 则称 X Granger-cause Y。
 * 
 * @example
 * {@code
 * GrangerCausality granger = new GrangerCausality()
 *     .withLag(2)              // 滞后 2 阶
 *     .withSignificanceLevel(0.05);
 *     
 * CausalMatrix result = granger.compute(data, null);
 * }
 */
public class GrangerCausality extends CausalMethod {
    
    private static final int DEFAULT_LAG = 2;                 // 默认滞后阶数
    private static final double DEFAULT_SIGNIFICANCE = 0.05;  // 默认显著性水平
    
    private int lag = DEFAULT_LAG;
    private double significanceLevel = DEFAULT_SIGNIFICANCE;
    
    public GrangerCausality() {
        super("Granger");
        this.weight = 1.2;
    }
    
    /**
     * 设置滞后阶数
     * 
     * @param lag 滞后阶数
     * @return this instance for chaining
     */
    public GrangerCausality withLag(int lag) {
        this.lag = lag;
        return this;
    }
    
    /**
     * 设置显著性水平
     * 
     * @param significanceLevel 显著性水平
     * @return this instance for chaining
     */
    public GrangerCausality withSignificanceLevel(double significanceLevel) {
        this.significanceLevel = significanceLevel;
        return this;
    }
    
    @Override
    public CausalMatrix compute(double[][] data, Map<String, Object> params) {
        if (!validateData(data)) {
            throw new IllegalArgumentException("Invalid input data");
        }
        
        // 从参数中获取配置
        if (params != null) {
            if (params.containsKey("lag")) {
                this.lag = ((Number) params.get("lag")).intValue();
            }
            if (params.containsKey("significanceLevel")) {
                this.significanceLevel = ((Number) params.get("significanceLevel")).doubleValue();
            }
        }
        
        int T = data.length - lag;  // 有效时间点数
        int N = data[0].length;     // 变量数
        
        CausalMatrix result = new CausalMatrix(N, getName());
        
        // 对每一对变量进行 Granger 因果检验
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i != j) {
                    // 检验 j 是否 Granger-cause i
                    double[] causalStrength = grangerTest(data, T, j, i);
                    
                    if (causalStrength[1] < significanceLevel) {
                        // 拒绝原假设，存在 Granger 因果关系
                        result.setCausalEffect(j, i, causalStrength[0]);
                        result.pValues[i][j] = causalStrength[1];
                    }
                }
            }
        }
        
        // 添加元数据
        result.addMetadata("lag", lag);
        result.addMetadata("significanceLevel", significanceLevel);
        result.addMetadata("numSamples", T);
        result.addMetadata("numVariables", N);
        result.addMetadata("method", "Granger_Causality");
        result.addMetadata("testStatistic", "F_test");
        
        return result;
    }
    
    /**
     * Granger 因果检验
     * 
     * @param data 数据矩阵
     * @param T 有效时间点数
     * @param source 源变量
     * @param target 目标变量
     * @return [因果强度 (F 统计量), p 值]
     */
    private double[] grangerTest(double[][] data, int T, int source, int target) {
        int n = data.length - lag;
        
        // 构建受限模型（仅包含目标的过去值）
        double[][] X_restricted = buildDesignMatrix(data, target, lag, n);
        double[] y = extractSeries(data, target, lag, n);
        
        // 计算受限模型的残差平方和
        double rssRestricted = calculateRSS(X_restricted, y);
        
        // 构建非受限模型（包含目标和源的过去值）
        double[][] X_unrestricted = buildUnrestrictedDesignMatrix(data, target, source, lag, n);
        
        // 计算非受限模型的残差平方和
        double rssUnrestricted = calculateRSS(X_unrestricted, y);
        
        // 计算 F 统计量
        int df1 = lag;  // 分子自由度（新增的参数个数）
        int df2 = n - 2 * lag - 1;  // 分母自由度
        
        if (df2 <= 0) {
            return new double[]{0.0, 1.0};
        }
        
        double fStat = ((rssRestricted - rssUnrestricted) / df1) / (rssUnrestricted / df2);
        
        // 计算 p 值（使用 F 分布）
        double pValue = fDistributionCDF(fStat, df1, df2);
        
        // 因果强度：使用 F 统计量的归一化值
        double strength = fStat / (fStat + df2);
        
        return new double[]{strength, pValue};
    }
    
    /**
     * 构建设计矩阵（用于 VAR 模型）
     */
    private double[][] buildDesignMatrix(double[][] data, int targetVar, int lag, int n) {
        int rows = n;
        int cols = lag + 1;  // lag 个滞后项 + 截距项
        
        double[][] X = new double[rows][cols];
        
        for (int t = 0; t < n; t++) {
            X[t][0] = 1.0;  // 截距项
            for (int l = 1; l <= lag; l++) {
                X[t][l] = data[t + lag - l][targetVar];
            }
        }
        
        return X;
    }
    
    /**
     * 构建非受限模型的设计矩阵（包含额外变量的滞后项）
     */
    private double[][] buildUnrestrictedDesignMatrix(double[][] data, int targetVar, int sourceVar, int lag, int n) {
        int rows = n;
        int cols = 2 * lag + 1;  // 目标 lag + 源 lag + 截距
        
        double[][] X = new double[rows][cols];
        
        for (int t = 0; t < n; t++) {
            X[t][0] = 1.0;  // 截距项
            
            // 目标的滞后项
            for (int l = 1; l <= lag; l++) {
                X[t][l] = data[t + lag - l][targetVar];
            }
            
            // 源的滞后项
            for (int l = 1; l <= lag; l++) {
                X[t][lag + l] = data[t + lag - l][sourceVar];
            }
        }
        
        return X;
    }
    
    /**
     * 提取时间序列
     */
    private double[] extractSeries(double[][] data, int varIndex, int lag, int n) {
        double[] y = new double[n];
        for (int t = 0; t < n; t++) {
            y[t] = data[t + lag][varIndex];
        }
        return y;
    }
    
    /**
     * 计算残差平方和 (RSS)
     */
    private double calculateRSS(double[][] X, double[] y) {
        // 使用 OLS 估计参数
        double[] beta = olsEstimate(X, y);
        
        // 计算预测值和残差
        double rss = 0.0;
        int n = y.length;
        int k = beta.length;
        
        for (int i = 0; i < n; i++) {
            double yHat = 0.0;
            for (int j = 0; j < k; j++) {
                yHat += X[i][j] * beta[j];
            }
            double residual = y[i] - yHat;
            rss += residual * residual;
        }
        
        return rss;
    }
    
    /**
     * OLS 参数估计（简化实现）
     */
    private double[] olsEstimate(double[][] X, double[] y) {
        int n = X.length;
        int k = X[0].length;
        
        // 简化：仅考虑截距和一阶滞后
        // 完整实现需要使用矩阵运算（X'X)^(-1) X'y
        
        double[] beta = new double[k];
        
        // 对于简化情况，直接使用样本均值
        if (k == 1) {
            // 仅截距
            double sumY = 0.0;
            for (double v : y) {
                sumY += v;
            }
            beta[0] = sumY / n;
        } else {
            // 包含滞后项：使用简化的递推公式
            beta[0] = mean(y);
            
            for (int j = 1; j < k; j++) {
                double sumXY = 0.0;
                double sumXX = 0.0;
                double meanX = 0.0;
                double meanY = mean(y);
                
                for (int i = 0; i < n; i++) {
                    meanX += X[i][j];
                }
                meanX /= n;
                
                for (int i = 0; i < n; i++) {
                    double dx = X[i][j] - meanX;
                    double dy = y[i] - meanY;
                    sumXY += dx * dy;
                    sumXX += dx * dx;
                }
                
                if (sumXX > 1e-10) {
                    beta[j] = sumXY / sumXX;
                } else {
                    beta[j] = 0.0;
                }
            }
        }
        
        return beta;
    }
    
    /**
     * 计算均值
     */
    private double mean(double[] data) {
        double sum = 0.0;
        for (double v : data) {
            sum += v;
        }
        return sum / data.length;
    }
    
    /**
     * F 分布累积分布函数近似
     * 
     * 使用 Beta 分布与 F 分布的关系
     */
    private double fDistributionCDF(double f, int df1, int df2) {
        // 转换为 Beta 分布
        double x = df2 / (df2 + df1 * f);
        
        // 计算不完全 Beta 函数比值
        return incompleteBetaRatio(df2 / 2.0, df1 / 2.0, x);
    }
    
    /**
     * 不完全 Beta 函数比值近似
     */
    private double incompleteBetaRatio(double a, double b, double x) {
        if (x <= 0) return 0.0;
        if (x >= 1) return 1.0;
        
        // 使用连分式展开
        double bt = Math.exp(logGamma(a + b) - logGamma(a) - logGamma(b) +
                            a * Math.log(x) + b * Math.log(1 - x));
        
        if (x < (a + 1) / (a + b + 2)) {
            return bt * betaCF(a, b, x) / a;
        } else {
            return 1.0 - bt * betaCF(b, a, 1 - x) / b;
        }
    }
    
    /**
     * Beta 函数的连分式展开
     */
    private double betaCF(double a, double b, double x) {
        int maxIter = 100;
        double eps = 3e-7;
        
        double qab = a + b;
        double qap = a + 1.0;
        double qam = a - 1.0;
        double c = 1.0;
        double d = 1.0 - qab * x / qap;
        
        if (Math.abs(d) < 1e-30) d = 1e-30;
        d = 1.0 / d;
        double h = d;
        
        for (int m = 1; m <= maxIter; m++) {
            int m2 = 2 * m;
            double aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            
            d = 1.0 + aa * d;
            if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1.0 + aa / c;
            if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1.0 / d;
            h *= d * c;
            
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            
            d = 1.0 + aa * d;
            if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1.0 + aa / c;
            if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1.0 / d;
            double del = d * c;
            h *= del;
            
            if (Math.abs(del - 1.0) < eps) break;
        }
        
        return h;
    }
    
    /**
     * Log Gamma 函数近似（Lanczos 逼近）
     */
    private double logGamma(double x) {
        double[] g = {
            76.18009172947146,
            -86.50532032941677,
            24.01409824083091,
            -1.231739572450155,
            0.1208650973866179e-2,
            -0.5395239384953e-5
        };
        
        double tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        
        double ser = 1.000000000190015;
        for (int j = 0; j < 6; j++) {
            ser += g[j] / ++x;
        }
        
        return -tmp + Math.log(2.5066282746310005 * ser);
    }
    
    @Override
    public double getConfidence(double[][] data, int source, int target) {
        // 基于 F 统计量的稳定性
        return 1.0 - significanceLevel;
    }
}
