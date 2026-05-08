package com.mecn.preprocess;

import com.mecn.model.TimeSeriesData;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据预处理器接口
 * 
 * 定义数据预处理的标准流程
 */
public interface Preprocessor {
    
    /**
     * 完整预处理流程
     * 
     * @param rawData 原始时间序列数据
     * @param config 预处理配置
     * @return 处理后的时间序列数据
     */
    TimeSeriesData process(TimeSeriesData rawData, PreprocessConfig config);
}

/**
 * 预处理配置类
 */
class PreprocessConfig {
    
    private boolean handleMissingValues = true;     // 是否处理缺失值
    private MissingValueMethod missingValueMethod = MissingValueMethod.LINEAR_INTERPOLATION;
    
    private boolean seasonalAdjustment = false;     // 是否季节性调整
    private SeasonalMethod seasonalMethod = SeasonalMethod.STL;
    
    private boolean stationarityTest = true;        // 是否平稳性检验
    private boolean autoDifferencing = false;       // 是否自动差分
    
    private boolean standardization = true;         // 是否标准化（Z-score）
    
    private Map<String, Object> customParams = new HashMap<>();
    
    // Getters and Setters
    public boolean isHandleMissingValues() {
        return handleMissingValues;
    }
    
    public void setHandleMissingValues(boolean handleMissingValues) {
        this.handleMissingValues = handleMissingValues;
    }
    
    public MissingValueMethod getMissingValueMethod() {
        return missingValueMethod;
    }
    
    public void setMissingValueMethod(MissingValueMethod missingValueMethod) {
        this.missingValueMethod = missingValueMethod;
    }
    
    public boolean isSeasonalAdjustment() {
        return seasonalAdjustment;
    }
    
    public void setSeasonalAdjustment(boolean seasonalAdjustment) {
        this.seasonalAdjustment = seasonalAdjustment;
    }
    
    public SeasonalMethod getSeasonalMethod() {
        return seasonalMethod;
    }
    
    public void setSeasonalMethod(SeasonalMethod seasonalMethod) {
        this.seasonalMethod = seasonalMethod;
    }
    
    public boolean isStationarityTest() {
        return stationarityTest;
    }
    
    public void setStationarityTest(boolean stationarityTest) {
        this.stationarityTest = stationarityTest;
    }
    
    public boolean isAutoDifferencing() {
        return autoDifferencing;
    }
    
    public void setAutoDifferencing(boolean autoDifferencing) {
        this.autoDifferencing = autoDifferencing;
    }
    
    public boolean isStandardization() {
        return standardization;
    }
    
    public void setStandardization(boolean standardization) {
        this.standardization = standardization;
    }
    
    public Map<String, Object> getCustomParams() {
        return customParams;
    }
    
    public void setCustomParams(Map<String, Object> customParams) {
        this.customParams = customParams;
    }
    
    /**
     * 缺失值处理方法枚举
     */
    public enum MissingValueMethod {
        LINEAR_INTERPOLATION,   // 线性插值
        FORWARD_FILL,           // 前向填充
        BACKWARD_FILL,          // 后向填充
        MULTIPLE_IMPUTATION     // 多重插补
    }
    
    /**
     * 季节性调整方法枚举
     */
    public enum SeasonalMethod {
        STL,                    // STL 分解
        X13ARIMA,               // X-13ARIMA-SEATS
        DIFFERENCING            // 简单差分
    }
}
