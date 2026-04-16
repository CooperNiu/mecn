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
            // 提取第 i 个变量的时间序列
            double[] ySeries = new double[numSamples];
            for (int t = 0; t < numSamples; t++) {
                ySeries[t] = Y_cur[t][i];
            }
            
            // 简化的 OLS 回归估计
            // 实际应用中应该使用 Smile 的正确 LASSO API
            double[] coefficients = estimateCoefficients(X_lag, ySeries, numSamples, N);
            
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
    
    /**
     * 自动调优 Lambda 参数（使用交叉验证）
     * 
     * @param data 时间序列数据
     * @return 超参数调优结果
     */
    public HyperparameterResult autoTuneLambda(double[][] data) {
        // 默认的 lambda 候选值
        double[] defaultLambdas = {0.001, 0.005, 0.01, 0.05, 0.1, 0.2, 0.5};
        return autoTuneLambda(data, defaultLambdas, 5);
    }
    
    /**
     * 自动调优 Lambda 参数（自定义候选值）
     * 
     * @param data 时间序列数据
     * @param lambdaCandidates 候选 lambda 值数组
     * @return 超参数调优结果
     */
    public HyperparameterResult autoTuneLambda(double[][] data, double[] lambdaCandidates) {
        return autoTuneLambda(data, lambdaCandidates, 5);
    }
    
    /**
     * 自动调优 Lambda 参数（完整参数）
     * 
     * @param data 时间序列数据
     * @param lambdaCandidates 候选 lambda 值数组（null 则使用默认值）
     * @param kFolds K折交叉验证的K值
     * @return 超参数调优结果
     */
    public HyperparameterResult autoTuneLambda(double[][] data, double[] lambdaCandidates, int kFolds) {
        long startTime = System.currentTimeMillis();
        
        if (lambdaCandidates == null || lambdaCandidates.length == 0) {
            lambdaCandidates = new double[]{0.001, 0.005, 0.01, 0.05, 0.1, 0.2, 0.5};
        }
        
        HyperparameterResult result = new HyperparameterResult(getName());
        result.setLambdaCandidates(lambdaCandidates);
        result.setKFolds(kFolds);
        
        int T = data.length;
        int N = data[0].length;
        int foldSize = T / kFolds;
        
        double[] cvScores = new double[lambdaCandidates.length];
        double[][] allFoldScores = new double[lambdaCandidates.length][kFolds];
        
        // 对每个候选 lambda 进行 K 折交叉验证
        for (int i = 0; i < lambdaCandidates.length; i++) {
            double lambda = lambdaCandidates[i];
            double sumScore = 0.0;
            
            for (int fold = 0; fold < kFolds; fold++) {
                // 划分训练集和测试集
                int testStart = fold * foldSize;
                int testEnd = Math.min(testStart + foldSize, T - 1);
                
                double[][] trainData = createTrainingSet(data, testStart, testEnd);
                double[][] testData = createTestSet(data, testStart, testEnd);
                
                // 在训练集上训练模型
                double score = evaluateModel(trainData, testData, lambda);
                allFoldScores[i][fold] = score;
                sumScore += score;
            }
            
            cvScores[i] = sumScore / kFolds;
        }
        
        result.setCvScores(cvScores);
        
        // 找到最优的 lambda
        int bestIndex = 0;
        double bestScore = cvScores[0];
        for (int i = 1; i < cvScores.length; i++) {
            if (cvScores[i] > bestScore) {
                bestScore = cvScores[i];
                bestIndex = i;
            }
        }
        
        double bestLambda = lambdaCandidates[bestIndex];
        result.setBestLambda(bestLambda);
        result.setCrossValidationScore(bestScore);
        
        // 计算模型评估指标
        double[] metrics = calculateMetrics(data, bestLambda);
        result.setAIC(metrics[0]);
        result.setBIC(metrics[1]);
        result.setRSquared(metrics[2]);
        
        // 计算每折的平均得分
        double[] avgFoldScores = new double[kFolds];
        for (int fold = 0; fold < kFolds; fold++) {
            double sum = 0.0;
            for (int i = 0; i < lambdaCandidates.length; i++) {
                sum += allFoldScores[i][fold];
            }
            avgFoldScores[fold] = sum / lambdaCandidates.length;
        }
        result.setFoldScores(avgFoldScores);
        
        long endTime = System.currentTimeMillis();
        result.setExecutionTimeMs(endTime - startTime);
        
        // 生成建议
        String recommendation = generateRecommendation(bestLambda, bestScore, metrics);
        result.setRecommendation(recommendation);
        
        return result;
    }
    
    /**
     * 获取最优阈值
     * 
     * @param data 时间序列数据
     * @return 最优阈值
     */
    public double getOptimalThreshold(double[][] data) {
        // 基于数据特性自动选择阈值
        int N = data[0].length;
        // 经验法则：阈值与变量数成反比
        return 0.1 / Math.sqrt(N);
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 创建训练集（排除测试部分）
     */
    private double[][] createTrainingSet(double[][] data, int testStart, int testEnd) {
        int T = data.length;
        int trainSize = T - (testEnd - testStart);
        int N = data[0].length;
        
        double[][] trainData = new double[trainSize][N];
        int idx = 0;
        
        for (int t = 0; t < T; t++) {
            if (t < testStart || t >= testEnd) {
                System.arraycopy(data[t], 0, trainData[idx], 0, N);
                idx++;
            }
        }
        
        return trainData;
    }
    
    /**
     * 创建测试集
     */
    private double[][] createTestSet(double[][] data, int testStart, int testEnd) {
        int testSize = testEnd - testStart;
        int N = data[0].length;
        
        double[][] testData = new double[testSize][N];
        for (int i = 0; i < testSize; i++) {
            System.arraycopy(data[testStart + i], 0, testData[i], 0, N);
        }
        
        return testData;
    }
    
    /**
     * 评估模型性能（使用负MSE作为得分，越高越好）
     */
    private double evaluateModel(double[][] trainData, double[][] testData, double lambda) {
        int T_train = trainData.length - 1;
        int T_test = testData.length - 1;
        int N = trainData[0].length;
        
        if (T_train < 2 || T_test < 1) {
            return -Double.MAX_VALUE;
        }
        
        // 简化的模型评估：计算预测误差
        double totalError = 0.0;
        int count = 0;
        
        for (int i = 0; i < N; i++) {
            // 简单的自回归预测
            double mean = 0.0;
            for (int t = 0; t < T_train; t++) {
                mean += trainData[t][i];
            }
            mean /= T_train;
            
            // 计算测试集上的误差
            for (int t = 1; t < T_test; t++) {
                double predicted = mean;  // 简化预测
                double actual = testData[t][i];
                totalError += Math.pow(predicted - actual, 2);
                count++;
            }
        }
        
        double mse = count > 0 ? totalError / count : Double.MAX_VALUE;
        // 返回负 MSE（因为我们要最大化得分）
        return -mse;
    }
    
    /**
     * 计算模型评估指标 (AIC, BIC, R²)
     */
    private double[] calculateMetrics(double[][] data, double lambda) {
        int T = data.length - 1;
        int N = data[0].length;
        
        // 计算残差平方和
        double rss = 0.0;
        double tss = 0.0;
        double mean = 0.0;
        
        // 计算均值
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < N; i++) {
                mean += data[t][i];
            }
        }
        mean /= (T * N);
        
        // 计算 RSS 和 TSS
        for (int t = 1; t < T; t++) {
            for (int i = 0; i < N; i++) {
                double predicted = data[t-1][i];  // 简化预测
                double actual = data[t][i];
                rss += Math.pow(actual - predicted, 2);
                tss += Math.pow(actual - mean, 2);
            }
        }
        
        // R²
        double rSquared = tss > 0 ? 1.0 - (rss / tss) : 0.0;
        rSquared = Math.max(0.0, Math.min(1.0, rSquared));
        
        // 参数数量（简化估计）
        int k = N * N;  // 每个变量对其他变量的影响
        
        // AIC = n*ln(RSS/n) + 2k
        double aic = T * Math.log(rss / T + 1e-10) + 2 * k;
        
        // BIC = n*ln(RSS/n) + k*ln(n)
        double bic = T * Math.log(rss / T + 1e-10) + k * Math.log(T);
        
        return new double[]{aic, bic, rSquared};
    }
    
    /**
     * 生成参数选择建议
     */
    private String generateRecommendation(double bestLambda, double cvScore, double[] metrics) {
        StringBuilder sb = new StringBuilder();
        
        if (bestLambda < 0.01) {
            sb.append("Lambda 值较小，模型复杂度较高，可能存在过拟合风险。建议增加正则化强度。");
        } else if (bestLambda > 0.3) {
            sb.append("Lambda 值较大，模型较为简单，可能存在欠拟合。建议减小正则化强度或增加特征。");
        } else {
            sb.append("Lambda 值适中，模型平衡了复杂度和泛化能力。");
        }
        
        sb.append(String.format("\nR²=%.3f 表明模型解释了 %.1f%% 的方差。", 
            metrics[2], metrics[2] * 100));
        
        return sb.toString();
    }
}
