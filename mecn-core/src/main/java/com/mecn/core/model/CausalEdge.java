package com.mecn.model;

/**
 * 因果边数据结构
 * 
 * 表示两个经济指标之间的因果关系
 */
public class CausalEdge {
    
    private String source;        // 源节点（原因）
    private String target;        // 目标节点（结果）
    private double strength;      // 影响强度
    private double confidence;    // 置信度 (0-1)
    private String method;        // 发现方法 (LASSO/Granger/PCMCI)
    private Double pValue;        // P 值（如果适用）
    
    public CausalEdge() {
    }
    
    public CausalEdge(String source, String target, double strength) {
        this.source = source;
        this.target = target;
        this.strength = strength;
        this.confidence = 1.0; // 默认完全置信
    }
    
    public CausalEdge(String source, String target, double strength, double confidence) {
        this.source = source;
        this.target = target;
        this.strength = strength;
        this.confidence = confidence;
    }
    
    // Getters and Setters
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getTarget() {
        return target;
    }
    
    public void setTarget(String target) {
        this.target = target;
    }
    
    public double getStrength() {
        return strength;
    }
    
    public void setStrength(double strength) {
        this.strength = strength;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public Double getPValue() {
        return pValue;
    }
    
    public void setPValue(Double pValue) {
        this.pValue = pValue;
    }
    
    /**
     * 判断是否为显著因果关系
     * @param threshold 显著性阈值
     */
    public boolean isSignificant(double threshold) {
        // 如果有 pValue，使用 pValue 判断
        if (pValue != null) {
            return pValue < threshold;
        }
        // 否则使用 confidence 判断
        return confidence >= (1.0 - threshold);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CausalEdge that = (CausalEdge) o;
        
        if (Double.compare(that.strength, strength) != 0) return false;
        if (Double.compare(that.confidence, confidence) != 0) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        return pValue != null ? pValue.equals(that.pValue) : that.pValue == null;
    }
    
    @Override
    public int hashCode() {
        int result;
        long temp;
        result = source != null ? source.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        temp = Double.doubleToLongBits(strength);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(confidence);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (pValue != null ? pValue.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "CausalEdge{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", strength=" + strength +
                ", confidence=" + confidence +
                ", method='" + method + '\'' +
                ", pValue=" + pValue +
                '}';
    }
}
