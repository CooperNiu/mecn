package com.mecn.data.provider;

import com.mecn.data.generator.EnhancedDataGenerator;
import com.mecn.model.EconomicIndicator;
import com.mecn.model.TimeSeriesData;

import java.time.LocalDate;
import java.util.List;

/**
 * 模拟数据提供者
 * 
 * 使用增强版模拟数据生成器提供经济指标数据
 */
public class SimulatedDataProvider implements DataProvider {
    
    private final EnhancedDataGenerator generator;
    private final String name;
    
    public SimulatedDataProvider() {
        this.generator = new EnhancedDataGenerator();
        this.name = "Simulated";
    }
    
    public SimulatedDataProvider(long seed) {
        this.generator = new EnhancedDataGenerator(seed);
        this.name = "Simulated (seed=" + seed + ")";
    }
    
    @Override
    public List<TimeSeriesData> fetch(List<EconomicIndicator> indicators, 
                                       LocalDate startDate, 
                                       LocalDate endDate) {
        // 使用增强版生成器生成所有指标数据
        List<TimeSeriesData> allData = generator.generateTimeSeriesData(startDate, endDate);
        
        // 如果指定了具体指标，则过滤
        if (indicators != null && !indicators.isEmpty()) {
            return filterByIndicators(allData, indicators);
        }
        
        return allData;
    }
    
    /**
     * 根据指标列表过滤数据
     */
    private List<TimeSeriesData> filterByIndicators(List<TimeSeriesData> allData, 
                                                      List<EconomicIndicator> indicators) {
        return allData.stream()
            .filter(ts -> indicators.stream()
                .anyMatch(ind -> ind.getCode().equals(ts.getIndicatorCode())))
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<EconomicIndicator> getSupportedIndicators() {
        return generator.getSupportedIndicators();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * 获取生成器配置信息
     */
    public Object getConfigInfo() {
        return generator.getConfigInfo();
    }
}
