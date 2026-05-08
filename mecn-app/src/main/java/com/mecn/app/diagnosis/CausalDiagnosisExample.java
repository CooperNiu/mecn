package com.mecn.diagnosis;

import com.mecn.MECNTools;
import com.mecn.causal.CausalResult;
import com.mecn.causal.LassoRegression;

/**
 * 因果诊断示例
 * 
 * 演示如何使用诊断引擎评估因果发现结果的质量
 */
public class CausalDiagnosisExample {
    
    public static void main(String[] args) {
        System.out.println("=== MECN 因果诊断模块示例 ===\n");
        
        // 生成示例数据
        double[][] data = generateSampleData();
        
        // 执行因果发现
        System.out.println("1. 执行因果发现...");
        CausalResult result = new com.mecn.causal.CausalEngineBuilder()
            .data(data)
            .method(new LassoRegression().withLambda(0.1))
            .significanceLevel(0.05)
            .discover();
        
        System.out.println("   因果发现完成\n");
        
        // 执行诊断
        System.out.println("2. 执行质量诊断...");
        CausalDiagnoser diagnoser = new CausalDiagnoser();
        DiagnosticReport report = diagnoser.diagnose(result, data);
        
        // 输出诊断报告
        System.out.println("\n3. 诊断报告:");
        System.out.println(report.toDetailedReport());
        
        // 快速检查
        System.out.println("\n4. 快速检查结果:");
        quickCheck(report);
    }
    
    /**
     * 生成示例数据（带有一些质量问题）
     */
    private static double[][] generateSampleData() {
        int T = 50;   // 较少的样本量（用于触发警告）
        int N = 8;    // 8 个变量
        
        double[][] data = new double[T][N];
        java.util.Random random = new java.util.Random(42);
        
        for (int t = 0; t < T; t++) {
            if (t == 0) {
                for (int i = 0; i < N; i++) {
                    data[t][i] = random.nextGaussian();
                }
            } else {
                // 变量 0-1: 强相关（用于触发共线性警告）
                data[t][0] = 0.6 * data[t-1][0] + random.nextGaussian() * 0.5;
                data[t][1] = 0.9 * data[t][0] + 0.1 * random.nextGaussian();  // 高度相关
                
                // 变量 2-3: 因果关系
                data[t][2] = 0.5 * data[t-1][2] + random.nextGaussian() * 0.5;
                data[t][3] = 0.3 * data[t-1][3] + 0.4 * data[t-1][2] + random.nextGaussian() * 0.5;
                
                // 其他变量：随机过程
                for (int i = 4; i < N; i++) {
                    data[t][i] = 0.5 * data[t-1][i] + random.nextGaussian() * 0.5;
                }
            }
        }
        
        return data;
    }
    
    /**
     * 快速检查诊断结果
     */
    private static void quickCheck(DiagnosticReport report) {
        System.out.println("总体评分：" + report.getOverallScore().getLabel());
        System.out.println("质量指标数：" + report.getMetrics().size());
        System.out.println("警告数：" + report.getWarnings().size());
        System.out.println("建议数：" + report.getRecommendations().size());
        
        if (!report.getWarnings().isEmpty()) {
            System.out.println("\n主要警告:");
            for (DiagnosticReport.Warning warning : report.getWarnings()) {
                System.out.printf("  [%s] %s\n", 
                    warning.getSeverity().getLabel(), 
                    warning.getMessage());
            }
        }
        
        if (!report.getRecommendations().isEmpty()) {
            System.out.println("\n优先建议:");
            for (DiagnosticReport.Recommendation rec : report.getRecommendations()) {
                if (rec.getPriority() == DiagnosticReport.Priority.HIGH) {
                    System.out.printf("  - %s: %s\n", 
                        rec.getTitle(), rec.getDescription());
                }
            }
        }
    }
}
