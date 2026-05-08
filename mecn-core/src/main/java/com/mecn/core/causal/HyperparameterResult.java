package com.mecn.causal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 超参数调优结果
 * 
 * 封装超参数搜索的结果和模型评估指标
 */
public class HyperparameterResult {
    
    private String methodName;                    // 方法名称
    private double bestLambda;                     // 最优 lambda 值
    private double crossValidationScore;           // 交叉验证得分
    private double[] lambdaCandidates;             // 候选 lambda 值列表
    private double[] cvScores;                     // 每个候选的CV得分
    private int kFolds;                            // K折交叉验证的K值
    private double[] foldScores;                   // 每折的得分
    private double aic;                            // AIC 信息准则
    private double bic;                            // BIC 信息准则
    private double rSquared;                       // R² 决定系数
    private String tuningMethod;                   // 调优方法
    private long executionTimeMs;                  // 执行时间（毫秒）
    private String recommendation;                 // 参数选择建议
    
    public HyperparameterResult(String methodName) {
        this.methodName = methodName;
        this.tuningMethod = "cross_validation";
    }
    
    // Getters and Setters
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public double getBestLambda() {
        return bestLambda;
    }
    
    public void setBestLambda(double bestLambda) {
        this.bestLambda = bestLambda;
    }
    
    public double getCrossValidationScore() {
        return crossValidationScore;
    }
    
    public void setCrossValidationScore(double crossValidationScore) {
        this.crossValidationScore = crossValidationScore;
    }
    
    public double[] getLambdaCandidates() {
        return lambdaCandidates;
    }
    
    public void setLambdaCandidates(double[] lambdaCandidates) {
        this.lambdaCandidates = lambdaCandidates;
    }
    
    public List<Double> getLambdaCandidatesList() {
        return Arrays.stream(lambdaCandidates)
            .boxed()
            .collect(Collectors.toList());
    }
    
    public int getNumCandidates() {
        return lambdaCandidates != null ? lambdaCandidates.length : 0;
    }
    
    public double[] getCvScores() {
        return cvScores;
    }
    
    public void setCvScores(double[] cvScores) {
        this.cvScores = cvScores;
    }
    
    public int getKFolds() {
        return kFolds;
    }
    
    public void setKFolds(int kFolds) {
        this.kFolds = kFolds;
    }
    
    public double[] getFoldScores() {
        return foldScores;
    }
    
    public void setFoldScores(double[] foldScores) {
        this.foldScores = foldScores;
    }
    
    public double getAIC() {
        return aic;
    }
    
    public void setAIC(double aic) {
        this.aic = aic;
    }
    
    public double getBIC() {
        return bic;
    }
    
    public void setBIC(double bic) {
        this.bic = bic;
    }
    
    public double getRSquared() {
        return rSquared;
    }
    
    public void setRSquared(double rSquared) {
        this.rSquared = rSquared;
    }
    
    public String getTuningMethod() {
        return tuningMethod;
    }
    
    public void setTuningMethod(String tuningMethod) {
        this.tuningMethod = tuningMethod;
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public String getRecommendation() {
        return recommendation;
    }
    
    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
    
    /**
     * 生成详细的调优报告
     */
    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 超参数调优报告 ===\n");
        sb.append("方法: ").append(methodName).append("\n");
        sb.append("调优方式: ").append(tuningMethod).append("\n");
        sb.append(String.format("最优 Lambda: %.4f\n", bestLambda));
        sb.append(String.format("交叉验证得分: %.4f\n", crossValidationScore));
        sb.append(String.format("AIC: %.4f\n", aic));
        sb.append(String.format("BIC: %.4f\n", bic));
        sb.append(String.format("R²: %.4f\n", rSquared));
        sb.append(String.format("执行时间: %d ms\n", executionTimeMs));
        sb.append("\n候选 Lambda 值:\n");
        
        for (int i = 0; i < lambdaCandidates.length; i++) {
            sb.append(String.format("  λ=%.4f, CV Score=%.4f\n", 
                lambdaCandidates[i], cvScores[i]));
        }
        
        sb.append("\n建议: ").append(recommendation).append("\n");
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("HyperparameterResult{method=%s, bestLambda=%.4f, cvScore=%.4f, R²=%.4f}",
            methodName, bestLambda, crossValidationScore, rSquared);
    }
}
