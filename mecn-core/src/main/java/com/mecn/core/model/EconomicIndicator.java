package com.mecn.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 经济指标定义
 * 
 * 表示一个宏观经济指标，包含其元数据信息
 */
public class EconomicIndicator {
    
    /**
     * 指标频率枚举
     */
    public enum Frequency {
        DAILY,      // 日度
        WEEKLY,     // 周度
        MONTHLY,    // 月度
        QUARTERLY,  // 季度
        ANNUAL      // 年度
    }
    
    /**
     * 指标单位枚举
     */
    public enum Unit {
        PERCENT,        // 百分比
        INDEX_POINTS,   // 指数点
        CURRENCY,       // 货币单位
        RATIO,          // 比率
        ABSOLUTE        // 绝对值
    }
    
    private String code;              // 指标代码 (如 "GDP", "CPI")
    private String name;              // 指标名称
    private String source;            // 数据来源
    private Frequency frequency;      // 频率
    private Unit unit;                // 单位
    private Map<String, String> metadata; // 元数据
    
    public EconomicIndicator() {
        this.metadata = new HashMap<>();
    }
    
    public EconomicIndicator(String code, String name) {
        this();
        this.code = code;
        this.name = name;
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Frequency getFrequency() {
        return frequency;
    }
    
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }
    
    public Unit getUnit() {
        return unit;
    }
    
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    
    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }
    
    @Override
    public String toString() {
        return "EconomicIndicator{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", frequency=" + frequency +
                ", unit=" + unit +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        EconomicIndicator that = (EconomicIndicator) o;
        return code != null && code.equals(that.code);
    }
    
    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}
