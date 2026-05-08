package com.mecn.diagnosis;

import com.mecn.causal.CausalResult;
import com.mecn.diagnosis.DiagnosticReport.*;

import java.util.*;

/**
 * 因果诊断引擎
 * 
 * 对因果发现结果进行全面质量评估，生成诊断报告
 */
public class CausalDiagnoser {
    
    private static final double MIN_SAMPLE_RATIO = 0.1;      // 最小样本比例
    private static final double MAX_DENSITY = 0.5;           // 最大网络密度阈值
    private static final double MIN_AVG_STRENGTH = 0.1;      // 最小平均因果强度
    private static final double HIGH_PVALUE_THRESHOLD = 0.1; // 高 p 值阈值
    
    /**
     * 对因果发现结果进行诊断
     * 
     * @param result 因果发现结果
     * @param data 原始数据
     * @return 诊断报告
     */
    public DiagnosticReport diagnose(CausalResult result, double[][] data) {
        Builder reportBuilder = new Builder();
        
        int T = data.length;       // 时间点数
        int N = data[0].length;    // 变量数
        
        List<QualityMetric> metrics = calculateMetrics(result, data, T, N);
        List<Warning> warnings = detectWarnings(result, data, T, N);
        List<Recommendation> recommendations = generateRecommendations(warnings, result, data);
        
        // 计算总体评分
        OverallScore overallScore = calculateOverallScore(metrics, warnings);
        
        // 生成摘要
        String summary = generateSummary(overallScore, metrics, warnings);
        
        return reportBuilder
            .overallScore(overallScore)
            .metrics(metrics)
            .warnings(warnings)
            .recommendations(recommendations)
            .summary(summary)
            .build();
    }
    
    /**
     * 计算质量指标
     */
    private List<QualityMetric> calculateMetrics(CausalResult result, double[][] data, int T, int N) {
        List<QualityMetric> metrics = new ArrayList<>();
        
        // 1. 数据充足性指标
        double sampleRatio = (double) T / (N * 10);  // 样本数与变量数的比例
        Severity sampleStatus = sampleRatio >= 1.0 ? Severity.LOW :
                              sampleRatio >= 0.5 ? Severity.MEDIUM :
                              sampleRatio >= 0.2 ? Severity.HIGH : Severity.CRITICAL;
        
        metrics.add(new QualityMetric(
            "数据充足性",
            Math.min(1.0, sampleRatio),
            String.format("样本量/变量数比值：%.2f (建议 >1.0)", sampleRatio),
            sampleStatus
        ));
        
        // 2. 网络稀疏性指标
        double[][] matrix = result.getAdjacencyMatrix();
        int edgeCount = countEdges(matrix);
        int maxEdges = N * (N - 1);
        double density = maxEdges > 0 ? (double) edgeCount / maxEdges : 0.0;
        
        Severity densityStatus = density <= 0.2 ? Severity.LOW :
                                density <= 0.3 ? Severity.MEDIUM :
                                density <= 0.5 ? Severity.HIGH : Severity.CRITICAL;
        
        metrics.add(new QualityMetric(
            "网络稀疏性",
            1.0 - density,
            String.format("网络密度：%.2f%% (边数：%d)", density * 100, edgeCount),
            densityStatus
        ));
        
        // 3. 因果强度指标
        double avgStrength = calculateAverageStrength(matrix);
        Severity strengthStatus = avgStrength >= 0.3 ? Severity.LOW :
                                 avgStrength >= 0.2 ? Severity.MEDIUM :
                                 avgStrength >= 0.1 ? Severity.HIGH : Severity.CRITICAL;
        
        metrics.add(new QualityMetric(
            "因果强度",
            avgStrength * 3,  // 归一化到 0-1
            String.format("平均因果强度：%.4f", avgStrength),
            strengthStatus
        ));
        
        // 4. 统计显著性指标
        double significantRatio = calculateSignificantRatio(result, N);
        Severity sigStatus = significantRatio >= 0.8 ? Severity.LOW :
                            significantRatio >= 0.6 ? Severity.MEDIUM :
                            significantRatio >= 0.4 ? Severity.HIGH : Severity.CRITICAL;
        
        metrics.add(new QualityMetric(
            "统计显著性",
            significantRatio,
            String.format("显著因果比例：%.2f%% (p < 0.05)", significantRatio * 100),
            sigStatus
        ));
        
        // 5. 数值稳定性指标
        double stability = calculateNumericalStability(matrix);
        Severity stabilityStatus = stability >= 0.9 ? Severity.LOW :
                                  stability >= 0.7 ? Severity.MEDIUM : Severity.HIGH;
        
        metrics.add(new QualityMetric(
            "数值稳定性",
            stability,
            String.format("数值稳定性指数：%.4f", stability),
            stabilityStatus
        ));
        
        return metrics;
    }
    
    /**
     * 检测警告
     */
    private List<Warning> detectWarnings(CausalResult result, double[][] data, int T, int N) {
        List<Warning> warnings = new ArrayList<>();
        
        // 警告 1: 样本量不足
        if (T < N * 5) {
            warnings.add(new Warning(
                "DATA_001",
                String.format("样本量严重不足（%d 个时间点，%d 个变量）", T, N),
                Severity.HIGH,
                "数据质量"
            ));
        }
        
        // 警告 2: 网络过密
        double[][] matrix = result.getAdjacencyMatrix();
        int edgeCount = countEdges(matrix);
        double density = (double) edgeCount / (N * (N - 1));
        
        if (density > MAX_DENSITY) {
            warnings.add(new Warning(
                "NET_001",
                String.format("网络过于稠密（密度：%.2f%%）", density * 100),
                Severity.MEDIUM,
                "网络结构"
            ));
        }
        
        // 警告 3: 存在异常强的因果关系
        double maxStrength = findMaxStrength(matrix);
        if (maxStrength > 0.9) {
            warnings.add(new Warning(
                "CAU_001",
                String.format("检测到异常强的因果关系（强度：%.4f）", maxStrength),
                Severity.MEDIUM,
                "因果强度"
            ));
        }
        
        // 警告 4: p 值普遍偏高
        int highPValueCount = countHighPValues(result, N);
        if (highPValueCount > edgeCount * 0.5) {
            warnings.add(new Warning(
                "STAT_001",
                String.format("超过 %.0f%% 的因果关系 p 值较高", (double) highPValueCount / edgeCount * 100),
                Severity.HIGH,
                "统计检验"
            ));
        }
        
        // 警告 5: 可能存在共线性
        if (detectMulticollinearity(data)) {
            warnings.add(new Warning(
                "DATA_002",
                "数据可能存在多重共线性问题",
                Severity.MEDIUM,
                "数据质量"
            ));
        }
        
        return warnings;
    }
    
    /**
     * 生成改进建议
     */
    private List<Recommendation> generateRecommendations(List<Warning> warnings, CausalResult result, double[][] data) {
        List<Recommendation> recommendations = new ArrayList<>();
        
        for (Warning warning : warnings) {
            switch (warning.getCode()) {
                case "DATA_001":
                    recommendations.add(new Recommendation(
                        "增加样本量",
                        "建议收集更多时间序列数据，至少达到变量数的 10 倍以上",
                        Priority.HIGH,
                        warning.getCode()
                    ));
                    break;
                    
                case "NET_001":
                    recommendations.add(new Recommendation(
                        "调整因果发现参数",
                        "提高显著性水平阈值或增加正则化强度，以减少虚假因果连接",
                        Priority.MEDIUM,
                        warning.getCode()
                    ));
                    break;
                    
                case "CAU_001":
                    recommendations.add(new Recommendation(
                        "验证强因果关系",
                        "结合领域知识验证异常强的因果关系是否合理，排除数据质量问题",
                        Priority.MEDIUM,
                        warning.getCode()
                    ));
                    break;
                    
                case "STAT_001":
                    recommendations.add(new Recommendation(
                        "改进统计检验方法",
                        "考虑使用更稳健的统计检验方法，如 Bootstrap 或置换检验",
                        Priority.HIGH,
                        warning.getCode()
                    ));
                    break;
                    
                case "DATA_002":
                    recommendations.add(new Recommendation(
                        "处理多重共线性",
                        "使用主成分分析 (PCA) 或岭回归等方法降低变量间的相关性",
                        Priority.MEDIUM,
                        warning.getCode()
                    ));
                    break;
            }
        }
        
        // 通用建议
        if (warnings.isEmpty()) {
            recommendations.add(new Recommendation(
                "持续监控",
                "当前因果发现结果质量良好，建议定期重新评估以保持模型时效性",
                Priority.LOW,
                null
            ));
        }
        
        return recommendations;
    }
    
    /**
     * 计算总体评分
     */
    private OverallScore calculateOverallScore(List<QualityMetric> metrics, List<Warning> warnings) {
        // 计算指标平均分
        double metricScore = 0.0;
        for (QualityMetric metric : metrics) {
            metricScore += metric.getValue();
        }
        metricScore /= metrics.size();
        
        // 警告扣分
        double penalty = 0.0;
        for (Warning warning : warnings) {
            switch (warning.getSeverity()) {
                case CRITICAL: penalty += 20; break;
                case HIGH: penalty += 10; break;
                case MEDIUM: penalty += 5; break;
                case LOW: penalty += 2; break;
                default: break;
            }
        }
        
        // 最终得分
        int finalScore = (int) Math.max(0, Math.min(100, metricScore * 100 - penalty));
        
        return OverallScore.fromScore(finalScore);
    }
    
    /**
     * 生成摘要
     */
    private String generateSummary(OverallScore score, List<QualityMetric> metrics, List<Warning> warnings) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("因果发现结果整体质量为【").append(score.getLabel()).append("】。\n");
        
        // 亮点
        Optional<QualityMetric> bestMetric = metrics.stream()
            .max(Comparator.comparingDouble(QualityMetric::getValue));
        
        if (bestMetric.isPresent() && bestMetric.get().getValue() > 0.8) {
            sb.append(bestMetric.get().getName()).append("表现优异。");
        }
        
        // 主要问题
        if (!warnings.isEmpty()) {
            long criticalCount = warnings.stream()
                .filter(w -> w.getSeverity() == Severity.CRITICAL || w.getSeverity() == Severity.HIGH)
                .count();
            
            if (criticalCount > 0) {
                sb.append("存在 ").append(criticalCount).append(" 个严重问题需要优先处理。");
            }
        }
        
        return sb.toString();
    }
    
    // ===== 辅助方法 =====
    
    private int countEdges(double[][] matrix) {
        int count = 0;
        for (double[] row : matrix) {
            for (double val : row) {
                if (Math.abs(val) > 0.01) {
                    count++;
                }
            }
        }
        return count;
    }
    
    private double calculateAverageStrength(double[][] matrix) {
        double sum = 0.0;
        int count = 0;
        
        for (double[] row : matrix) {
            for (double val : row) {
                if (Math.abs(val) > 0.01) {
                    sum += Math.abs(val);
                    count++;
                }
            }
        }
        
        return count > 0 ? sum / count : 0.0;
    }
    
    private double calculateSignificantRatio(CausalResult result, int N) {
        double[][] pValues = null;
        try {
            pValues = result.getConfidenceMatrix() != null ? result.getConfidenceMatrix() : null;
        } catch (Exception e) {
            // 忽略异常，返回默认值
        }
        
        if (pValues == null) {
            return 1.0;  // 如果没有 p 值，假设都显著
        }
        
        int significant = 0;
        int total = 0;
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i != j && Math.abs(result.getAdjacencyMatrix()[i][j]) > 0.01) {
                    total++;
                    if (pValues[i][j] < 0.05) {
                        significant++;
                    }
                }
            }
        }
        
        return total > 0 ? (double) significant / total : 1.0;
    }
    
    private double calculateNumericalStability(double[][] matrix) {
        // 检查是否有 NaN 或无穷大
        int unstableCount = 0;
        int totalCount = 0;
        
        for (double[] row : matrix) {
            for (double val : row) {
                totalCount++;
                if (Double.isNaN(val) || Double.isInfinite(val)) {
                    unstableCount++;
                }
            }
        }
        
        return totalCount > 0 ? 1.0 - (double) unstableCount / totalCount : 0.0;
    }
    
    private double findMaxStrength(double[][] matrix) {
        double max = 0.0;
        
        for (double[] row : matrix) {
            for (double val : row) {
                if (Math.abs(val) > max) {
                    max = Math.abs(val);
                }
            }
        }
        
        return max;
    }
    
    private int countHighPValues(CausalResult result, int N) {
        double[][] confidenceMatrix = result.getConfidenceMatrix();
        
        if (confidenceMatrix == null) {
            return 0;
        }
        
        int count = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // 置信度低表示 p 值高
                if (confidenceMatrix[i][j] < (1.0 - HIGH_PVALUE_THRESHOLD)) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    private boolean detectMulticollinearity(double[][] data) {
        int N = data[0].length;
        
        // 简化实现：检查变量间的相关系数
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                double corr = calculateCorrelation(data, i, j);
                if (Math.abs(corr) > 0.9) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private double calculateCorrelation(double[][] data, int var1, int var2) {
        int T = data.length;
        double[] x = new double[T];
        double[] y = new double[T];
        
        for (int t = 0; t < T; t++) {
            x[t] = data[t][var1];
            y[t] = data[t][var2];
        }
        
        double meanX = Arrays.stream(x).average().orElse(0.0);
        double meanY = Arrays.stream(y).average().orElse(0.0);
        
        double sumXY = 0.0, sumXX = 0.0, sumYY = 0.0;
        
        for (int t = 0; t < T; t++) {
            double dx = x[t] - meanX;
            double dy = y[t] - meanY;
            sumXY += dx * dy;
            sumXX += dx * dx;
            sumYY += dy * dy;
        }
        
        double denom = Math.sqrt(sumXX * sumYY);
        return denom > 1e-10 ? sumXY / denom : 0.0;
    }
}
