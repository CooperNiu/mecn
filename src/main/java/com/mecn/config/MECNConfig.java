package com.mecn.config;

import java.util.HashMap;
import java.util.Map;

/**
 * MECN 全局配置管理器
 * 
 * 集中管理所有模块的配置参数
 */
public class MECNConfig {
    
    private static final MECNConfig INSTANCE = new MECNConfig();
    
    private Map<String, Object> globalConfig;
    
    private MECNConfig() {
        this.globalConfig = new HashMap<>();
        initializeDefaults();
    }
    
    public static MECNConfig getInstance() {
        return INSTANCE;
    }
    
    /**
     * 初始化默认配置
     */
    private void initializeDefaults() {
        // 因果分析默认配置
        globalConfig.put("causal.edgeThreshold", 0.08);
        globalConfig.put("causal.maxLag", 4);
        globalConfig.put("causal.significanceLevel", 0.05);
        
        // 网络分析默认配置
        globalConfig.put("network.minEdgeWeight", 0.1);
        globalConfig.put("network.maxNodes", 1000);
        
        // 预处理默认配置
        globalConfig.put("preprocess.handleMissingValues", true);
        globalConfig.put("preprocess.standardization", true);
        globalConfig.put("preprocess.seasonalAdjustment", false);
        
        // 性能配置
        globalConfig.put("performance.parallelEnabled", true);
        globalConfig.put("performance.threadPoolSize", 
            Runtime.getRuntime().availableProcessors());
        
        // 日志配置
        globalConfig.put("logging.level", "INFO");
        globalConfig.put("logging.enabled", true);
    }
    
    /**
     * 获取配置值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) globalConfig.get(key);
    }
    
    /**
     * 获取配置值，带默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        Object value = globalConfig.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    /**
     * 设置配置值
     */
    public void set(String key, Object value) {
        globalConfig.put(key, value);
    }
    
    /**
     * 获取整数配置
     */
    public int getInt(String key, int defaultValue) {
        Object value = globalConfig.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
    
    /**
     * 获取双精度配置
     */
    public double getDouble(String key, double defaultValue) {
        Object value = globalConfig.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
    
    /**
     * 获取布尔配置
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = globalConfig.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
    
    /**
     * 获取字符串配置
     */
    public String getString(String key, String defaultValue) {
        Object value = globalConfig.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    /**
     * 重置为默认配置
     */
    public void resetToDefaults() {
        globalConfig.clear();
        initializeDefaults();
    }
    
    /**
     * 获取所有配置
     */
    public Map<String, Object> getAllConfig() {
        return new HashMap<>(globalConfig);
    }
    
    /**
     * 从Map加载配置
     */
    public void loadFromMap(Map<String, Object> config) {
        if (config != null) {
            globalConfig.putAll(config);
        }
    }
}
