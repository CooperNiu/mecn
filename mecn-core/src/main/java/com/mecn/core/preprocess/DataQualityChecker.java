package com.mecn.preprocess;

import java.util.*;

/**
 * 数据质量检查器
 * 
 * 检测和报告时间序列数据中的异常、缺失值等问题
 */
public class DataQualityChecker {
    
    /**
     * 数据质量问题类型
     */
    public enum IssueType {
        MISSING_VALUES,      // 缺失值
        OUTLIERS,           // 异常值
        ZERO_VARIANCE,      // 零方差（常量）
        NEGATIVE_VALUES,    // 负值（对某些指标不合理）
        DUPLICATE_DATES,    // 重复日期
        IRREGULAR_INTERVAL, // 不规则时间间隔
        EXTREME_VALUES      // 极端值
    }
    
    /**
     * 数据质量问题
     */
    public static class DataIssue {
        private IssueType type;
        private String description;
        private int variableIndex;
        private int timeIndex;
        private double value;
        private Severity severity;
        
        public enum Severity {
            LOW, MEDIUM, HIGH, CRITICAL
        }
        
        public DataIssue(IssueType type, String description, int varIdx, int timeIdx, 
                        double value, Severity severity) {
            this.type = type;
            this.description = description;
            this.variableIndex = varIdx;
            this.timeIndex = timeIdx;
            this.value = value;
            this.severity = severity;
        }
        
        // Getters
        public IssueType getType() { return type; }
        public String getDescription() { return description; }
        public int getVariableIndex() { return variableIndex; }
        public int getTimeIndex() { return timeIndex; }
        public double getValue() { return value; }
        public Severity getSeverity() { return severity; }
        
        @Override
        public String toString() {
            return String.format("[%s] %s (var=%d, t=%d, value=%.4f)", 
                severity, type, variableIndex, timeIndex, value);
        }
    }
    
    /**
     * 数据质量报告
     */
    public static class QualityReport {
        private List<DataIssue> issues;
        private Map<String, Object> statistics;
        private OverallQuality quality;
        
        public enum OverallQuality {
            EXCELLENT, GOOD, FAIR, POOR, CRITICAL
        }
        
        public QualityReport() {
            this.issues = new ArrayList<>();
            this.statistics = new HashMap<>();
            this.quality = OverallQuality.EXCELLENT;
        }
        
        public void addIssue(DataIssue issue) {
            issues.add(issue);
            updateQuality();
        }
        
        public List<DataIssue> getIssues() {
            return Collections.unmodifiableList(issues);
        }
        
        public Map<String, Object> getStatistics() {
            return Collections.unmodifiableMap(statistics);
        }
        
        public OverallQuality getQuality() {
            return quality;
        }
        
        public int getIssueCount() {
            return issues.size();
        }
        
        public int getIssueCount(IssueType type) {
            return (int) issues.stream().filter(i -> i.getType() == type).count();
        }
        
        private void updateQuality() {
            int criticalCount = getIssueCountBySeverity(DataIssue.Severity.CRITICAL);
            int highCount = getIssueCountBySeverity(DataIssue.Severity.HIGH);
            
            if (criticalCount > 0) {
                quality = OverallQuality.CRITICAL;
            } else if (highCount > 5) {
                quality = OverallQuality.POOR;
            } else if (issues.size() > 10) {
                quality = OverallQuality.FAIR;
            } else if (issues.size() > 0) {
                quality = OverallQuality.GOOD;
            } else {
                quality = OverallQuality.EXCELLENT;
            }
        }
        
        private int getIssueCountBySeverity(DataIssue.Severity severity) {
            return (int) issues.stream().filter(i -> i.getSeverity() == severity).count();
        }
        
        public String generateReport() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== 数据质量报告 ===\n");
            sb.append(String.format("整体质量: %s\n", quality));
            sb.append(String.format("问题总数: %d\n\n", issues.size()));
            
            // 按类型统计
            Map<IssueType, Long> typeCounts = new HashMap<>();
            for (DataIssue issue : issues) {
                typeCounts.merge(issue.getType(), 1L, Long::sum);
            }
            
            sb.append("问题分布:\n");
            for (Map.Entry<IssueType, Long> entry : typeCounts.entrySet()) {
                sb.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            
            sb.append("\n详细问题:\n");
            for (DataIssue issue : issues) {
                sb.append(String.format("  %s\n", issue));
            }
            
            return sb.toString();
        }
    }
    
    /**
     * 检查数据质量
     * 
     * @param data 时间序列数据 [T][N]
     * @return 质量报告
     */
    public QualityReport checkQuality(double[][] data) {
        QualityReport report = new QualityReport();
        
        if (data == null || data.length == 0) {
            report.addIssue(new DataIssue(
                IssueType.MISSING_VALUES,
                "数据为空",
                -1, -1, 0,
                DataIssue.Severity.CRITICAL
            ));
            return report;
        }
        
        int T = data.length;
        int N = data[0].length;
        
        // 添加统计信息
        report.statistics.put("numTimePoints", T);
        report.statistics.put("numVariables", N);
        report.statistics.put("totalDataPoints", T * N);
        
        // 检查每个变量
        for (int i = 0; i < N; i++) {
            checkVariable(data, i, T, report);
        }
        
        return report;
    }
    
    /**
     * 检查单个变量
     */
    private void checkVariable(double[][] data, int varIdx, int T, QualityReport report) {
        double[] series = new double[T];
        for (int t = 0; t < T; t++) {
            series[t] = data[t][varIdx];
        }
        
        // 1. 检查缺失值 (NaN 或 Infinity)
        checkMissingValues(series, varIdx, report);
        
        // 2. 检查零方差
        checkZeroVariance(series, varIdx, report);
        
        // 3. 检查异常值
        checkOutliers(series, varIdx, report);
        
        // 4. 检查负值
        checkNegativeValues(series, varIdx, report);
        
        // 5. 检查极端值
        checkExtremeValues(series, varIdx, T, report);
    }
    
    /**
     * 检查缺失值
     */
    private void checkMissingValues(double[] series, int varIdx, QualityReport report) {
        int missingCount = 0;
        for (int t = 0; t < series.length; t++) {
            if (Double.isNaN(series[t]) || Double.isInfinite(series[t])) {
                missingCount++;
                report.addIssue(new DataIssue(
                    IssueType.MISSING_VALUES,
                    String.format("变量 %d 在时间点 %d 存在缺失值", varIdx, t),
                    varIdx, t, series[t],
                    DataIssue.Severity.HIGH
                ));
            }
        }
        
        if (missingCount > 0) {
            double missingRate = (double) missingCount / series.length;
            if (missingRate > 0.1) {
                report.addIssue(new DataIssue(
                    IssueType.MISSING_VALUES,
                    String.format("变量 %d 缺失率过高: %.1f%%", varIdx, missingRate * 100),
                    varIdx, -1, missingRate,
                    DataIssue.Severity.CRITICAL
                ));
            }
        }
    }
    
    /**
     * 检查零方差
     */
    private void checkZeroVariance(double[] series, int varIdx, QualityReport report) {
        double mean = Arrays.stream(series).average().orElse(0);
        double variance = Arrays.stream(series)
            .map(x -> Math.pow(x - mean, 2))
            .average()
            .orElse(0);
        
        if (variance < 1e-10) {
            report.addIssue(new DataIssue(
                IssueType.ZERO_VARIANCE,
                String.format("变量 %d 方差接近零（常量序列）", varIdx),
                varIdx, -1, variance,
                DataIssue.Severity.MEDIUM
            ));
        }
    }
    
    /**
     * 检查异常值（使用 IQR 方法）
     */
    private void checkOutliers(double[] series, int varIdx, QualityReport report) {
        Arrays.sort(series);
        int n = series.length;
        
        double q1 = series[n / 4];
        double q3 = series[3 * n / 4];
        double iqr = q3 - q1;
        
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;
        
        int outlierCount = 0;
        for (int t = 0; t < series.length; t++) {
            if (series[t] < lowerBound || series[t] > upperBound) {
                outlierCount++;
                report.addIssue(new DataIssue(
                    IssueType.OUTLIERS,
                    String.format("变量 %d 在时间点 %d 检测到异常值: %.4f", varIdx, t, series[t]),
                    varIdx, t, series[t],
                    DataIssue.Severity.MEDIUM
                ));
            }
        }
        
        if (outlierCount > series.length * 0.05) {
            report.addIssue(new DataIssue(
                IssueType.OUTLIERS,
                String.format("变量 %d 异常值比例过高: %d/%d", varIdx, outlierCount, series.length),
                varIdx, -1, outlierCount,
                DataIssue.Severity.HIGH
            ));
        }
    }
    
    /**
     * 检查负值
     */
    private void checkNegativeValues(double[] series, int varIdx, QualityReport report) {
        long negativeCount = Arrays.stream(series).filter(v -> v < 0).count();
        
        if (negativeCount > 0) {
            report.addIssue(new DataIssue(
                IssueType.NEGATIVE_VALUES,
                String.format("变量 %d 包含 %d 个负值", varIdx, negativeCount),
                varIdx, -1, negativeCount,
                DataIssue.Severity.LOW
            ));
        }
    }
    
    /**
     * 检查极端值（超过 3 个标准差）
     */
    private void checkExtremeValues(double[] series, int varIdx, int T, QualityReport report) {
        double mean = Arrays.stream(series).average().orElse(0);
        double std = Math.sqrt(Arrays.stream(series)
            .map(x -> Math.pow(x - mean, 2))
            .average()
            .orElse(0));
        
        if (std < 1e-10) return;  // 避免除以零
        
        for (int t = 0; t < T; t++) {
            double zScore = Math.abs(series[t] - mean) / std;
            if (zScore > 3.0) {
                report.addIssue(new DataIssue(
                    IssueType.EXTREME_VALUES,
                    String.format("变量 %d 在时间点 %d 检测到极端值 (z=%.2f)", varIdx, t, zScore),
                    varIdx, t, series[t],
                    DataIssue.Severity.MEDIUM
                ));
            }
        }
    }
}
