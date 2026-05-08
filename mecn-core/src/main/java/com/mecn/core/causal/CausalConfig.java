package com.mecn.causal;

import java.util.HashMap;
import java.util.Map;

/**
 * 因果发现配置类
 */
public class CausalConfig {
    
    private int maxLag = 12;                    // 最大滞后阶数
    private double significanceLevel = 0.05;    // 显著性水平
    private boolean parallel = true;            // 是否并行计算
    private Map<String, Object> methodParams;   // 各方法的特定参数
    
    public CausalConfig() {
        this.methodParams = new HashMap<>();
    }
    
    // Getters and Setters
    public int getMaxLag() {
        return maxLag;
    }
    
    public void setMaxLag(int maxLag) {
        this.maxLag = maxLag;
    }
    
    public double getSignificanceLevel() {
        return significanceLevel;
    }
    
    public void setSignificanceLevel(double significanceLevel) {
        this.significanceLevel = significanceLevel;
    }
    
    public boolean isParallel() {
        return parallel;
    }
    
    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }
    
    public Map<String, Object> getMethodParams() {
        return methodParams;
    }
    
    public void setMethodParams(Map<String, Object> methodParams) {
        this.methodParams = methodParams;
    }
    
    public void putMethodParam(String method, String key, Object value) {
        this.methodParams.put(method + "." + key, value);
    }
    
    public Object getMethodParam(String method, String key) {
        return this.methodParams.get(method + "." + key);
    }
}
