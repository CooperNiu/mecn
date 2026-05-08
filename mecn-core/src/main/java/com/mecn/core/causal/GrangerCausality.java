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
    
    /**
     * 自动调优 Lag 参数（使用信息准则）
     * 
     * @param data 时间序列数据
     * @return 超参数调优结果
     */
    public HyperparameterResult autoTuneLag(double[][] data) {
        // 默认的 lag 候选值
        int[] defaultLags = {1, 2, 3, 4, 5, 6, 7, 8};
        return autoTuneLag(data, defaultLags);
    }
    
    /**
     * 自动调优 Lag 参数（自定义候选值）
     * 
     * @param data 时间序列数据
     * @param lagCandidates 候选 lag 值数组
     * @return 超参数调优结果
     */
    public HyperparameterResult autoTuneLag(double[][] data, int[] lagCandidates) {
        long startTime = System.currentTimeMillis();
        
        if (lagCandidates == null || lagCandidates.length == 0) {
            lagCandidates = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        }
        
        HyperparameterResult result = new HyperparameterResult(getName());
        result.setTuningMethod("information_criteria");
        
        int T = data.length;
        int N = data[0].length;
        
        double[] aicScores = new double[lagCandidates.length];
        double[] bicScores = new double[lagCandidates.length];
        double[] rSquaredScores = new double[lagCandidates.length];
        
        // 对每个候选 lag 计算信息准则
        for (int i = 0; i < lagCandidates.length; i++) {
            int lag = lagCandidates[i];
            
            if (lag >= T - 1) {
                // lag 太大，跳过
                aicScores[i] = Double.MAX_VALUE;
                bicScores[i] = Double.MAX_VALUE;
                rSquaredScores[i] = 0.0;
                continue;
            }
            
            // 计算该 lag 下的模型指标
            double[] metrics = calculateModelMetrics(data, lag);
            aicScores[i] = metrics[0];
            bicScores[i] = metrics[1];
            rSquaredScores[i] = metrics[2];
        }
        
        // 使用 AIC 和 BIC 的加权平均选择最优 lag
        double[] combinedScores = new double[lagCandidates.length];
        for (int i = 0; i < lagCandidates.length; i++) {
            // 归一化 AIC 和 BIC
            double minAIC = java.util.Arrays.stream(aicScores).min().orElse(0);
            double maxAIC = java.util.Arrays.stream(aicScores).max().orElse(1);
            double minBIC = java.util.Arrays.stream(bicScores).min().orElse(0);
            double maxBIC = java.util.Arrays.stream(bicScores).max().orElse(1);
            
            double normAIC = maxAIC != minAIC ? (aicScores[i] - minAIC) / (maxAIC - minAIC) : 0;
            double normBIC = maxBIC != minBIC ? (bicScores[i] - minBIC) / (maxBIC - minBIC) : 0;
            
            // AIC 和 BIC 越小越好，所以取反
            combinedScores[i] = -(0.5 * normAIC + 0.5 * normBIC);
        }
        
        result.setLambdaCandidates(java.util.Arrays.stream(lagCandidates)
            .asDoubleStream()
            .toArray());
        result.setCvScores(combinedScores);
        
        // 找到最优的 lag
        int bestIndex = 0;
        double bestScore = combinedScores[0];
        for (int i = 1; i < combinedScores.length; i++) {
            if (combinedScores[i] > bestScore) {
                bestScore = combinedScores[i];
                bestIndex = i;
            }
        }
        
        int bestLag = lagCandidates[bestIndex];
        result.setBestLambda(bestLag);
        result.setCrossValidationScore(bestScore);
        
        // 设置模型评估指标
        double[] bestMetrics = calculateModelMetrics(data, bestLag);
        result.setAIC(bestMetrics[0]);
        result.setBIC(bestMetrics[1]);
        result.setRSquared(bestMetrics[2]);
        
        long endTime = System.currentTimeMillis();
        result.setExecutionTimeMs(endTime - startTime);
        
        // 生成建议
        String recommendation = generateLagRecommendation(bestLag, bestMetrics, T, N);
        result.setRecommendation(recommendation);
        
        return result;
    }
    
    /**
     * 获取最优显著性水平
     * 
     * @param data 时间序列数据
     * @return 最优显著性水平
     */
    public double getOptimalSignificanceLevel(double[][] data) {
        int T = data.length;
        int N = data[0].length;
        
        // 根据样本量调整显著性水平
        // 大样本使用更严格的显著性水平
        if (T > 200) {
            return 0.01;  // 更严格
        } else if (T > 100) {
            return 0.05;  // 标准
        } else {
            return 0.10;  // 较宽松
        }
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 计算模型评估指标 (AIC, BIC, R²)
     */
    private double[] calculateModelMetrics(double[][] data, int lag) {
        int T = data.length - lag;
        int N = data[0].length;
        
        if (T <= 0) {
            return new double[]{Double.MAX_VALUE, Double.MAX_VALUE, 0.0};
        }
        
        // 计算残差平方和
        double rss = 0.0;
        double tss = 0.0;
        
        // 计算均值
        double[] means = new double[N];
        for (int t = lag; t < data.length; t++) {
            for (int i = 0; i < N; i++) {
                means[i] += data[t][i];
            }
        }
        for (int i = 0; i < N; i++) {
            means[i] /= T;
        }
        
        // 简化的 VAR 模型拟合
        for (int t = lag; t < data.length; t++) {
            for (int i = 0; i < N; i++) {
                // 简化预测：使用前一个时间点
                double predicted = data[t-1][i];
                double actual = data[t][i];
                
                rss += Math.pow(actual - predicted, 2);
                tss += Math.pow(actual - means[i], 2);
            }
        }
        
        // R²
        double rSquared = tss > 0 ? 1.0 - (rss / tss) : 0.0;
        rSquared = Math.max(0.0, Math.min(1.0, rSquared));
        
        // 参数数量：每个变量的 lag 个自回归系数 + 截距
        int k = N * (N * lag + 1);
        
        // AIC = n*ln(RSS/n) + 2k
        double aic = T * Math.log(rss / T + 1e-10) + 2 * k;
        
        // BIC = n*ln(RSS/n) + k*ln(n)
        double bic = T * Math.log(rss / T + 1e-10) + k * Math.log(T);
        
        return new double[]{aic, bic, rSquared};
    }
    
    /**
     * 生成 lag 选择建议
     */
    private String generateLagRecommendation(int bestLag, double[] metrics, int T, int N) {
        StringBuilder sb = new StringBuilder();
        
        if (bestLag <= 2) {
            sb.append("最优 Lag 较小，表明数据的短期依赖性较强。");
        } else if (bestLag <= 5) {
            sb.append("最优 Lag 适中，平衡了模型复杂度和拟合效果。");
        } else {
            sb.append("最优 Lag 较大，可能存在长期依赖关系。建议检查数据平稳性。");
        }
        
        sb.append(String.format("\nR²=%.3f 表明模型解释了 %.1f%% 的方差。", 
            metrics[2], metrics[2] * 100));
        
        if (T < 50) {
            sb.append("\n注意：样本量较小，建议选择较小的 Lag 以避免过拟合。");
        }
        
        return sb.toString();
    }
}
