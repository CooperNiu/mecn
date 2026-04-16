package com.mecn.data.provider;

import com.mecn.model.EconomicIndicator;
import com.mecn.model.TimeSeriesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 数据提供者抽象基类
 * 
 * 提供通用的数据获取功能和工具方法
 */
public abstract class BaseDataProvider implements DataProvider {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseDataProvider.class);
    
    protected String name;
    protected Map<String, Object> configuration;
    protected boolean available;
    
    public BaseDataProvider(String name) {
        this.name = name;
        this.configuration = new HashMap<>();
        this.available = true;
    }
    
    @Override
    public List<TimeSeriesData> fetch(List<EconomicIndicator> indicators, 
                                       LocalDate startDate, 
                                       LocalDate endDate) {
        logger.info("从 {} 获取数据: {} 个指标, 时间范围: {} 至 {}", 
                    name, indicators.size(), startDate, endDate);
        
        // 参数验证
        if (indicators == null || indicators.isEmpty()) {
            throw new IllegalArgumentException("指标列表不能为空");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("日期范围不能为空");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
        
        try {
            List<TimeSeriesData> result = fetchData(indicators, startDate, endDate);
            logger.info("成功获取 {} 条时间序列数据", result.size());
            return result;
        } catch (Exception e) {
            logger.error("从 {} 获取数据失败: {}", name, e.getMessage(), e);
            throw new RuntimeException("数据获取失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取数据的抽象方法（子类实现）
     * 
     * @param indicators 指标列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 时间序列数据列表
     */
    protected abstract List<TimeSeriesData> fetchData(List<EconomicIndicator> indicators,
                                                       LocalDate startDate,
                                                       LocalDate endDate);
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * 设置可用性状态
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    /**
     * 添加配置项
     */
    public void addConfiguration(String key, Object value) {
        this.configuration.put(key, value);
    }
    
    /**
     * 获取配置项
     */
    public Object getConfiguration(String key) {
        return this.configuration.get(key);
    }
    
    /**
     * 获取所有配置
     */
    public Map<String, Object> getConfiguration() {
        return new HashMap<>(this.configuration);
    }
    
    /**
     * 验证指标是否受支持
     */
    protected boolean isIndicatorSupported(EconomicIndicator indicator) {
        List<EconomicIndicator> supported = getSupportedIndicators();
        return supported.stream()
                .anyMatch(s -> s.getCode().equals(indicator.getCode()));
    }
    
    /**
     * 创建空的时间序列数据（用于错误处理或占位）
     */
    protected TimeSeriesData createEmptySeries(EconomicIndicator indicator, 
                                                LocalDate startDate, 
                                                LocalDate endDate) {
        TimeSeriesData series = new TimeSeriesData();
        series.setIndicatorCode(indicator.getCode());
        series.setValues(new double[0]);
        series.setDates(new LocalDate[0]);
        return series;
    }
}
