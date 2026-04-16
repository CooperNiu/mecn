package com.mecn.preprocess;

import com.mecn.model.TimeSeriesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 预处理器抽象基类
 * 
 * 提供通用的预处理流程和工具方法
 */
public abstract class BasePreprocessor implements Preprocessor {
    
    protected static final Logger logger = LoggerFactory.getLogger(BasePreprocessor.class);
    
    protected PreprocessConfig config;
    
    public BasePreprocessor() {
        this.config = new PreprocessConfig();
    }
    
    public BasePreprocessor(PreprocessConfig config) {
        this.config = config != null ? config : new PreprocessConfig();
    }
    
    /**
     * 完整的预处理流程（模板方法模式）
     */
    @Override
    public TimeSeriesData process(TimeSeriesData rawData, PreprocessConfig config) {
        if (rawData == null) {
            throw new IllegalArgumentException("原始数据不能为空");
        }
        
        this.config = config != null ? config : this.config;
        
        logger.info("开始预处理流程，数据点数量: {}", rawData.getValues().length);
        
        // 创建数据的深拷贝
        TimeSeriesData processed = new TimeSeriesData();
        processed.setIndicatorCode(rawData.getIndicatorCode());
        processed.setValues(Arrays.copyOf(rawData.getValues(), rawData.getValues().length));
        processed.setDates(Arrays.copyOf(rawData.getDates(), rawData.getDates().length));
        processed.setMetadata(new java.util.HashMap<>(rawData.getMetadata()));
        
        // 步骤1: 处理缺失值
        if (this.config.isHandleMissingValues()) {
            processed = handleMissingValues(processed);
            logger.debug("完成缺失值处理");
        }
        
        // 步骤2: 季节性调整
        if (this.config.isSeasonalAdjustment()) {
            processed = adjustSeasonality(processed);
            logger.debug("完成季节性调整");
        }
        
        // 步骤3: 平稳性检验和差分
        if (this.config.isStationarityTest()) {
            processed = ensureStationarity(processed);
            logger.debug("完成平稳性处理");
        }
        
        // 步骤4: 标准化
        if (this.config.isStandardization()) {
            processed = standardize(processed);
            logger.debug("完成标准化");
        }
        
        logger.info("预处理流程完成");
        return processed;
    }
    
    /**
     * 处理缺失值（子类可实现具体逻辑）
     */
    protected abstract TimeSeriesData handleMissingValues(TimeSeriesData data);
    
    /**
     * 季节性调整（子类可实现具体逻辑）
     */
    protected abstract TimeSeriesData adjustSeasonality(TimeSeriesData data);
    
    /**
     * 确保平稳性（子类可实现具体逻辑）
     */
    protected abstract TimeSeriesData ensureStationarity(TimeSeriesData data);
    
    /**
     * 标准化处理（子类可实现具体逻辑）
     */
    protected abstract TimeSeriesData standardize(TimeSeriesData data);
    
    /**
     * 验证数据质量
     * 
     * @param data 待验证的数据
     * @return true 如果数据有效
     */
    protected boolean validateData(TimeSeriesData data) {
        if (data == null || data.getValues() == null || data.getValues().length == 0) {
            logger.warn("数据为空或长度为0");
            return false;
        }
        
        double[] values = data.getValues();
        int nanCount = 0;
        int infCount = 0;
        
        for (double val : values) {
            if (Double.isNaN(val)) {
                nanCount++;
            }
            if (Double.isInfinite(val)) {
                infCount++;
            }
        }
        
        if (nanCount > 0) {
            logger.warn("检测到 {} 个 NaN 值", nanCount);
        }
        if (infCount > 0) {
            logger.warn("检测到 {} 个无穷大值", infCount);
        }
        
        return true;
    }
    
    /**
     * 计算均值
     */
    protected double calculateMean(double[] values) {
        double sum = 0.0;
        for (double v : values) {
            if (!Double.isNaN(v) && !Double.isInfinite(v)) {
                sum += v;
            }
        }
        return sum / values.length;
    }
    
    /**
     * 计算标准差
     */
    protected double calculateStdDev(double[] values, double mean) {
        double sumSquaredDiff = 0.0;
        int count = 0;
        
        for (double v : values) {
            if (!Double.isNaN(v) && !Double.isInfinite(v)) {
                sumSquaredDiff += Math.pow(v - mean, 2);
                count++;
            }
        }
        
        return Math.sqrt(sumSquaredDiff / count);
    }
    
    /**
     * 获取配置
     */
    public PreprocessConfig getConfig() {
        return config;
    }
    
    /**
     * 设置配置
     */
    public void setConfig(PreprocessConfig config) {
        this.config = config;
    }
}
