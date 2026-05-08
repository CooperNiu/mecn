package com.mecn.data.provider;

import com.mecn.model.EconomicIndicator;
import com.mecn.model.TimeSeriesData;

import java.time.LocalDate;
import java.util.List;

/**
 * 数据提供者接口
 * 
 * 统一真实 API 和模拟数据的访问契约
 */
public interface DataProvider {
    
    /**
     * 获取经济指标时间序列数据
     * 
     * @param indicators 指标列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 时间序列数据列表
     */
    List<TimeSeriesData> fetch(List<EconomicIndicator> indicators, 
                                LocalDate startDate, 
                                LocalDate endDate);
    
    /**
     * 获取支持的指标列表
     * 
     * @return 支持的指标列表
     */
    List<EconomicIndicator> getSupportedIndicators();
    
    /**
     * 数据源名称
     * 
     * @return 数据源名称（如 "FRED", "WorldBank", "Simulated"）
     */
    String getName();
    
    /**
     * 检查数据源是否可用
     * 
     * @return true 如果数据源可用
     */
    default boolean isAvailable() {
        return true;
    }
}
