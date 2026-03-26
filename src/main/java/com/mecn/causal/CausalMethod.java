package com.mecn.causal;

import java.util.Map;

/**
 * 因果方法抽象基类
 * 
 * 所有因果发现算法的父类
 */
public abstract class CausalMethod {
    
    protected String name;                      // 方法名称
    protected double weight = 1.0;              // 方法权重（用于集成融合）
    protected Map<String, Object> parameters;   // 方法参数
    
    public CausalMethod(String name) {
        this.name = name;
        this.parameters = new java.util.HashMap<>();
    }
    
    /**
     * 设置方法权重
     * 
     * @param weight 权重值
     * @return this method for chaining
     */
    public CausalMethod withWeight(double weight) {
        this.weight = weight;
        return this;
    }
    
    /**
     * 添加参数
     * 
     * @param key 参数键
     * @param value 参数值
     * @return this method for chaining
     */
    public CausalMethod withParam(String key, Object value) {
        this.parameters.put(key, value);
        return this;
    }
    
    /**
     * 计算因果矩阵
     * 
     * @param data 时间序列数据矩阵 [T][N]
     * @param params 方法特定参数
     * @return 因果矩阵，matrix[i][j] 表示 j → i 的因果影响
     */
    public abstract CausalMatrix compute(double[][] data, Map<String, Object> params);
    
    /**
     * 获取特定因果关系的置信度
     * 
     * @param data 原始数据
     * @param source 源节点索引
     * @param target 目标节点索引
     * @return 置信度 (0-1)
     */
    public abstract double getConfidence(double[][] data, int source, int target);
    
    /**
     * 验证输入数据
     * 
     * @param data 数据矩阵
     * @return true 如果数据有效
     */
    protected boolean validateData(double[][] data) {
        if (data == null || data.length < 2) {
            return false;
        }
        
        int numCols = data[0].length;
        for (double[] row : data) {
            if (row == null || row.length != numCols) {
                return false;
            }
            // 检查 NaN 和无穷大
            for (double val : row) {
                if (Double.isNaN(val) || Double.isInfinite(val)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }
    
    public Object getParameter(String key) {
        return this.parameters.get(key);
    }
}

/**
 * 因果矩阵数据结构
 */
class CausalMatrix {
    
    double[][] matrix;                  // 因果影响强度矩阵（package-private）
    double[][] pValues;                 // P 值矩阵（如果适用，package-private）
    String methodName;                  // 方法名称
    Map<String, Object> metadata;       // 元数据
    
    public CausalMatrix(int size, String methodName) {
        this.matrix = new double[size][size];
        this.pValues = new double[size][size];
        this.methodName = methodName;
        this.metadata = new java.util.HashMap<>();
    }
    
    // Getters and Setters
    public double[][] getMatrix() {
        return matrix;
    }
    
    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }
    
    public double[][] getPValues() {
        return pValues;
    }
    
    public void setPValues(double[][] pValues) {
        this.pValues = pValues;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    /**
     * 添加元数据
     */
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    /**
     * 添加元数据（int 值）
     */
    public void addMetadata(String key, int value) {
        this.metadata.put(key, value);
    }
    
    /**
     * 添加元数据（double 值）
     */
    public void addMetadata(String key, double value) {
        this.metadata.put(key, value);
    }
    
    /**
     * 添加元数据（String 值）
     */
    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }
    
    /**
     * 添加元数据（List 值）
     */
    public void addMetadata(String key, java.util.List<?> value) {
        this.metadata.put(key, value);
    }
    
    /**
     * 设置某个位置的因果强度
     */
    public void setCausalEffect(int source, int target, double strength) {
        this.matrix[target][source] = strength; // 注意：target 行，source 列
    }
    
    /**
     * 获取某个位置的因果强度
     */
    public double getCausalEffect(int source, int target) {
        return this.matrix[target][source];
    }
}
