package com.mecn.diagnosis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 诊断报告 - 因果发现结果的质量评估
 * 
 * 提供数据质量、结果可靠性、统计显著性等维度的综合诊断
 */
public class DiagnosticReport {
    
    private final OverallScore overallScore;        // 总体评分
    private final List<QualityMetric> metrics;      // 质量指标列表
    private final List<Warning> warnings;           // 警告列表
    private final List<Recommendation> recommendations; // 建议列表
    private final String summary;                   // 诊断摘要
    
    private DiagnosticReport(Builder builder) {
        this.overallScore = builder.overallScore;
        this.metrics = builder.metrics;
        this.warnings = builder.warnings;
        this.recommendations = builder.recommendations;
        this.summary = builder.summary;
    }
    
    /**
     * 总体评分等级
     */
    public enum OverallScore {
        EXCELLENT(90, 100, "优秀", "因果发现结果非常可靠"),
        GOOD(75, 89, "良好", "因果发现结果较为可靠"),
        FAIR(60, 74, "一般", "因果发现结果需谨慎解读"),
        POOR(0, 59, "较差", "因果发现结果可靠性低");
        
        private final int minScore;
        private final int maxScore;
        private final String label;
        private final String description;
        
        OverallScore(int minScore, int maxScore, String label, String description) {
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.label = label;
            this.description = description;
        }
        
        public static OverallScore fromScore(int score) {
            for (OverallScore s : values()) {
                if (score >= s.minScore && score <= s.maxScore) {
                    return s;
                }
            }
            return POOR;
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 质量指标
     */
    public static class QualityMetric {
        private final String name;
        private final double value;
        private final String description;
        private final Severity status;
        
        public QualityMetric(String name, double value, String description, Severity status) {
            this.name = name;
            this.value = value;
            this.description = description;
            this.status = status;
        }
        
        public String getName() {
            return name;
        }
        
        public double getValue() {
            return value;
        }
        
        public String getDescription() {
            return description;
        }
        
        public Severity getStatus() {
            return status;
        }
    }
    
    /**
     * 警告信息
     */
    public static class Warning {
        private final String code;
        private final String message;
        private final Severity severity;
        private final String affectedComponent;
        
        public Warning(String code, String message, Severity severity, String affectedComponent) {
            this.code = code;
            this.message = message;
            this.severity = severity;
            this.affectedComponent = affectedComponent;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Severity getSeverity() {
            return severity;
        }
        
        public String getAffectedComponent() {
            return affectedComponent;
        }
    }
    
    /**
     * 建议信息
     */
    public static class Recommendation {
        private final String title;
        private final String description;
        private final Priority priority;
        private final String relatedWarning;
        
        public Recommendation(String title, String description, Priority priority, String relatedWarning) {
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.relatedWarning = relatedWarning;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public Priority getPriority() {
            return priority;
        }
        
        public String getRelatedWarning() {
            return relatedWarning;
        }
    }
    
    /**
     * 严重程度
     */
    public enum Severity {
        CRITICAL("严重", "需要立即处理"),
        HIGH("高", "建议尽快处理"),
        MEDIUM("中", "需要关注"),
        LOW("低", "可选优化"),
        INFO("信息", "仅供参考");
        
        private final String label;
        private final String description;
        
        Severity(String label, String description) {
            this.label = label;
            this.description = description;
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 优先级
     */
    public enum Priority {
        HIGH("高", "优先处理"),
        MEDIUM("中", "适当处理"),
        LOW("低", "可选处理");
        
        private final String label;
        private final String description;
        
        Priority(String label, String description) {
            this.label = label;
            this.description = description;
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public OverallScore getOverallScore() {
        return overallScore;
    }
    
    public List<QualityMetric> getMetrics() {
        return metrics;
    }
    
    public List<Warning> getWarnings() {
        return warnings;
    }
    
    public List<Recommendation> getRecommendations() {
        return recommendations;
    }
    
    public String getSummary() {
        return summary;
    }
    
    /**
     * 生成详细诊断报告文本
     */
    public String toDetailedReport() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== 因果发现诊断报告 ===\n\n");
        
        // 总体评分
        sb.append("【总体评分】: ").append(overallScore.getLabel())
          .append(" (").append(overallScore.getDescription()).append(")\n\n");
        
        // 质量指标
        sb.append("【质量指标】:\n");
        for (QualityMetric metric : metrics) {
            sb.append(String.format("  - %s: %.2f [%s] - %s\n",
                metric.getName(), metric.getValue(),
                metric.getStatus().getLabel(), metric.getDescription()));
        }
        sb.append("\n");
        
        // 警告信息
        if (!warnings.isEmpty()) {
            sb.append("【警告信息】:\n");
            for (Warning warning : warnings) {
                sb.append(String.format("  [%s] %s: %s (影响：%s)\n",
                    warning.getSeverity().getLabel(), warning.getCode(),
                    warning.getMessage(), warning.getAffectedComponent()));
            }
            sb.append("\n");
        }
        
        // 建议
        if (!recommendations.isEmpty()) {
            sb.append("【改进建议】:\n");
            for (Recommendation rec : recommendations) {
                sb.append(String.format("  [%s] %s: %s\n",
                    rec.getPriority().getLabel(), rec.getTitle(), rec.getDescription()));
            }
            sb.append("\n");
        }
        
        // 摘要
        sb.append("【诊断摘要】\n").append(summary).append("\n");
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("DiagnosticReport{score=%s, metrics=%d, warnings=%d, recommendations=%d}",
            overallScore.getLabel(), metrics.size(), warnings.size(), recommendations.size());
    }
    
    /**
     * Builder 模式构建诊断报告
     */
    public static class Builder {
        private OverallScore overallScore;
        private final List<QualityMetric> metrics = new ArrayList<>();
        private final List<Warning> warnings = new ArrayList<>();
        private final List<Recommendation> recommendations = new ArrayList<>();
        private String summary;
        
        public Builder overallScore(OverallScore score) {
            this.overallScore = score;
            return this;
        }
        
        public Builder addMetric(QualityMetric metric) {
            this.metrics.add(metric);
            return this;
        }
        
        public Builder addWarning(Warning warning) {
            this.warnings.add(warning);
            return this;
        }
        
        public Builder addRecommendation(Recommendation recommendation) {
            this.recommendations.add(recommendation);
            return this;
        }
        
        public Builder summary(String summary) {
            this.summary = summary;
            return this;
        }
        
        public Builder metrics(List<QualityMetric> metrics) {
            this.metrics.addAll(metrics);
            return this;
        }
        
        public Builder warnings(List<Warning> warnings) {
            this.warnings.addAll(warnings);
            return this;
        }
        
        public Builder recommendations(List<Recommendation> recommendations) {
            this.recommendations.addAll(recommendations);
            return this;
        }
        
        public DiagnosticReport build() {
            Objects.requireNonNull(overallScore, "Overall score is required");
            return new DiagnosticReport(this);
        }
    }
}
