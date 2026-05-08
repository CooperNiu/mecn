package com.mecn.causal;

import java.util.*;

/**
 * PCMCI (Peter-Clark Momentary Conditional Independence) 因果发现算法
 * 
 * 基于条件独立性检验的时间序列因果发现方法
 * 参考：Runge, J., et al. (2019). "Detecting and quantifying causal associations in large nonlinear time series datasets."
 * 
 * 算法流程：
 * 1. PC 阶段：使用条件独立性检验识别潜在因果关系
 * 2. MCI 阶段：对识别的因果关系进行矩量条件独立性检验，计算因果强度
 * 
 * @example
 * {@code
 * PCMCI pcmci = new PCMCI()
 *     .withTauMax(5)           // 最大滞后阶数
 *     .withPcAlpha(0.05)       // PC 阶段显著性水平
 *     .withConfidenceLevel(0.95); // 置信水平
 *     
 * CausalMatrix result = pcmci.compute(data, null);
 * }
 */
public class PCMCI extends CausalMethod {
    
    private static final double DEFAULT_PC_ALPHA = 0.05;      // PC 阶段显著性水平
    private static final int DEFAULT_TAU_MAX = 5;             // 最大滞后阶数
    private static final double DEFAULT_CONFIDENCE = 0.95;    // 置信水平
    
    private int tauMax = DEFAULT_TAU_MAX;
    private double pcAlpha = DEFAULT_PC_ALPHA;
    private double confidenceLevel = DEFAULT_CONFIDENCE;
    
    public PCMCI() {
        super("PCMCI");
        this.weight = 1.5;  // PCMCI 通常更可靠，赋予更高权重
    }
    
    /**
     * 设置最大滞后阶数
     * 
     * @param tauMax 最大滞后阶数
     * @return this instance for chaining
     */
    public PCMCI withTauMax(int tauMax) {
        this.tauMax = tauMax;
        return this;
    }
    
    /**
     * 设置 PC 阶段显著性水平
     * 
     * @param pcAlpha 显著性水平（默认 0.05）
     * @return this instance for chaining
     */
    public PCMCI withPcAlpha(double pcAlpha) {
        this.pcAlpha = pcAlpha;
        return this;
    }
    
    /**
     * 设置置信水平
     * 
     * @param confidenceLevel 置信水平（默认 0.95）
     * @return this instance for chaining
     */
    public PCMCI withConfidenceLevel(double confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
        return this;
    }
    
    @Override
    public CausalMatrix compute(double[][] data, Map<String, Object> params) {
        if (!validateData(data)) {
            throw new IllegalArgumentException("Invalid input data");
        }
        
        // 从参数中获取配置（如果提供）
        if (params != null) {
            if (params.containsKey("tauMax")) {
                this.tauMax = ((Number) params.get("tauMax")).intValue();
            }
            if (params.containsKey("pcAlpha")) {
                this.pcAlpha = ((Number) params.get("pcAlpha")).doubleValue();
            }
            if (params.containsKey("confidenceLevel")) {
                this.confidenceLevel = ((Number) params.get("confidenceLevel")).doubleValue();
            }
        }
        
        int T = data.length - tauMax;  // 有效时间点数
        int N = data[0].length;        // 指标数
        
        CausalMatrix result = new CausalMatrix(N, getName());
        
        // 构建滞后数据集
        double[][][] laggedData = buildLaggedDataset(data, T, N);
        
        // PC 阶段：识别潜在因果关系
        Map<Integer, Set<Integer>> separationSets = new HashMap<>();
        boolean[][] adjacencyMatrix = pcPhase(laggedData, T, N, separationSets);
        
        // MCI 阶段：计算因果强度
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i != j && adjacencyMatrix[j][i]) {
                    // 计算 j -> i 的因果强度
                    double causalStrength = mciTest(laggedData, T, N, j, i, separationSets);
                    
                    if (Math.abs(causalStrength) > 0.01) {  // 最小阈值
                        result.setCausalEffect(j, i, causalStrength);
                        
                        // 计算 p 值
                        double pValue = calculatePValue(causalStrength, T);
                        result.pValues[i][j] = pValue;
                    }
                }
            }
        }
        
        // 添加元数据
        result.addMetadata("tauMax", tauMax);
        result.addMetadata("pcAlpha", pcAlpha);
        result.addMetadata("confidenceLevel", confidenceLevel);
        result.addMetadata("numSamples", T);
        result.addMetadata("numVariables", N);
        result.addMetadata("method", "PCMCI");
        result.addMetadata("algorithm_phase", "PC+MCI");
        
        return result;
    }
    
    /**
     * 构建滞后数据集
     * 
     * @param data 原始数据
     * @param T 有效时间点数
     * @param N 变量数
     * @return 三维数组 [T][N][tauMax+1]
     */
    private double[][][] buildLaggedDataset(double[][] data, int T, int N) {
        double[][][] laggedData = new double[T][N][tauMax + 1];
        
        for (int t = tauMax; t < data.length; t++) {
            int effectiveT = t - tauMax;
            for (int i = 0; i < N; i++) {
                for (int tau = 0; tau <= tauMax; tau++) {
                    laggedData[effectiveT][i][tau] = data[t - tau][i];
                }
            }
        }
        
        return laggedData;
    }
    
    /**
     * PC 阶段：使用条件独立性检验识别骨架
     * 
     * @param laggedData 滞后数据集
     * @param T 时间点数
     * @param N 变量数
     * @param separationSets 分离集（用于存储条件独立性检验结果）
     * @return 邻接矩阵
     */
    private boolean[][] pcPhase(double[][][] laggedData, int T, int N, Map<Integer, Set<Integer>> separationSets) {
        // 初始化完全图
        boolean[][] adjacency = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i != j) {
                    adjacency[i][j] = true;
                }
            }
        }
        
        // 逐步增加条件集大小进行条件独立性检验
        for (int condSize = 0; condSize <= Math.min(3, N - 2); condSize++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i != j && adjacency[i][j]) {
                        // 获取可能的条件变量
                        List<Integer> possibleConditions = getPossibleConditions(i, j, adjacency, N);
                        
                        if (possibleConditions.size() >= condSize) {
                            // 尝试所有大小为 condSize 的条件集组合
                            List<List<Integer>> combinations = generateCombinations(possibleConditions, condSize);
                            
                            for (List<Integer> conditionSet : combinations) {
                                // 进行条件独立性检验
                                double pValue = conditionalIndependenceTest(
                                    laggedData, T, i, j, conditionSet);
                                
                                if (pValue > pcAlpha) {
                                    // 条件独立，移除边
                                    adjacency[i][j] = false;
                                    adjacency[j][i] = false;
                                    
                                    // 记录分离集
                                    int key = i * N + j;
                                    separationSets.put(key, new HashSet<>(conditionSet));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return adjacency;
    }
    
    /**
     * MCI 阶段：矩量条件独立性检验
     * 
     * @param laggedData 滞后数据集
     * @param T 时间点数
     * @param N 变量数
     * @param source 源变量
     * @param target 目标变量
     * @param separationSets 分离集
     * @return 因果强度
     */
    private double mciTest(double[][][] laggedData, int T, int N, int source, int target,
                           Map<Integer, Set<Integer>> separationSets) {
        // 构建条件集：包括目标的过去值和分离集
        Set<Integer> conditionVars = new HashSet<>();
        
        // 添加目标变量的所有滞后项（除了当前测试的滞后）
        for (int k = 0; k < N; k++) {
            if (k != source) {
                conditionVars.add(k);
            }
        }
        
        // 添加分离集中的变量
        int key = source * N + target;
        if (separationSets.containsKey(key)) {
            conditionVars.addAll(separationSets.get(key));
        }
        
        // 计算偏相关系数作为因果强度
        return calculatePartialCorrelation(laggedData, T, source, target, new ArrayList<>(conditionVars));
    }
    
    /**
     * 计算偏相关系数
     * 
     * @param laggedData 滞后数据集
     * @param T 时间点数
     * @param X 变量 X
     * @param Y 变量 Y
     * @param Z 条件变量集
     * @return 偏相关系数
     */
    private double calculatePartialCorrelation(double[][][] laggedData, int T, int X, int Y, List<Integer> Z) {
        // 简化实现：使用递归公式计算偏相关
        if (Z.isEmpty()) {
            // 简单相关系数
            return calculateCorrelation(
                extractSeries(laggedData, T, X, 1),  // X(t-1)
                extractSeries(laggedData, T, Y, 0)   // Y(t)
            );
        }
        
        // 使用递归公式：rho_XY.Z = (rho_XY.Z' - rho_XZ.Z' * rho_YZ.Z') / sqrt((1 - rho_XZ.Z'^2)(1 - rho_YZ.Z'^2))
        int Z_last = Z.get(Z.size() - 1);
        List<Integer> Z_prime = Z.subList(0, Z.size() - 1);
        
        double rXY = calculatePartialCorrelation(laggedData, T, X, Y, Z_prime);
        double rXZ = calculatePartialCorrelation(laggedData, T, X, Z_last, Z_prime);
        double rYZ = calculatePartialCorrelation(laggedData, T, Y, Z_last, Z_prime);
        
        double denominator = Math.sqrt((1 - rXZ * rXZ) * (1 - rYZ * rYZ));
        if (denominator < 1e-10) {
            return 0.0;
        }
        
        return (rXY - rXZ * rYZ) / denominator;
    }
    
    /**
     * 计算皮尔逊相关系数
     */
    private double calculateCorrelation(double[] x, double[] y) {
        int n = x.length;
        if (n != y.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        
        double meanX = mean(x);
        double meanY = mean(y);
        
        double sumXY = 0.0;
        double sumXX = 0.0;
        double sumYY = 0.0;
        
        for (int i = 0; i < n; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;
            sumXY += dx * dy;
            sumXX += dx * dx;
            sumYY += dy * dy;
        }
        
        double denominator = Math.sqrt(sumXX * sumYY);
        if (denominator < 1e-10) {
            return 0.0;
        }
        
        return sumXY / denominator;
    }
    
    /**
     * 提取时间序列
     */
    private double[] extractSeries(double[][][] laggedData, int T, int varIndex, int lag) {
        double[] series = new double[T];
        for (int t = 0; t < T; t++) {
            series[t] = laggedData[t][varIndex][lag];
        }
        return series;
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
     * 条件独立性检验（简化实现）
     * 
     * 使用偏相关系数的 Fisher z 变换进行检验
     */
    private double conditionalIndependenceTest(double[][][] laggedData, int T, int X, int Y, List<Integer> Z) {
        double partialCorr = calculatePartialCorrelation(laggedData, T, X, Y, Z);
        
        // Fisher z 变换
        double z = 0.5 * Math.log((1 + partialCorr) / (1 - partialCorr));
        
        // 标准误
        double se = 1.0 / Math.sqrt(T - Z.size() - 3);
        
        // z 统计量
        double zStat = Math.abs(z) / se;
        
        // 使用正态分布近似 p 值
        return normalCDF(-zStat) * 2;  // 双尾检验
    }
    
    /**
     * 标准正态分布累积分布函数近似
     */
    private double normalCDF(double x) {
        // Abramowitz and Stegun approximation
        double t = 1.0 / (1.0 + 0.2316419 * Math.abs(x));
        double d = 0.3989423 * Math.exp(-x * x / 2.0);
        double prob = d * t * (0.3193815 + t * (-0.3565638 + t * (1.781478 + t * (-1.821256 + t * 1.330274))));
        
        return x > 0 ? 1.0 - prob : prob;
    }
    
    /**
     * 计算 p 值
     */
    private double calculatePValue(double causalStrength, int sampleSize) {
        // Fisher z 变换
        double z = 0.5 * Math.log((1 + Math.abs(causalStrength)) / (1 - Math.abs(causalStrength)));
        
        // 标准误
        double se = 1.0 / Math.sqrt(sampleSize - 3);
        
        // z 统计量
        double zStat = Math.abs(z) / se;
        
        // p 值
        return normalCDF(-zStat) * 2;
    }
    
    /**
     * 获取可能的条件变量
     */
    private List<Integer> getPossibleConditions(int i, int j, boolean[][] adjacency, int N) {
        List<Integer> conditions = new ArrayList<>();
        for (int k = 0; k < N; k++) {
            if (k != i && k != j && adjacency[k][j]) {
                conditions.add(k);
            }
        }
        return conditions;
    }
    
    /**
     * 生成组合
     */
    private List<List<Integer>> generateCombinations(List<Integer> elements, int size) {
        List<List<Integer>> result = new ArrayList<>();
        if (size == 0) {
            result.add(new ArrayList<>());
            return result;
        }
        
        if (elements.size() < size) {
            return result;
        }
        
        combine(elements, size, 0, new ArrayList<>(), result);
        return result;
    }
    
    private void combine(List<Integer> elements, int size, int start, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == size) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int i = start; i < elements.size(); i++) {
            current.add(elements.get(i));
            combine(elements, size, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
    
    @Override
    public double getConfidence(double[][] data, int source, int target) {
        // 基于偏相关系数的稳定性
        // 简化实现：返回一个估计值
        return confidenceLevel;
    }
}
