package com.mecn.data.generator;

import com.mecn.model.EconomicIndicator;
import com.mecn.model.TimeSeriesData;

import java.time.LocalDate;
import java.util.*;

/**
 * 增强版模拟数据生成器
 * 
 * 基于现有 MacroEconomicNetwork 的逻辑，加入更多经济学合理性约束
 */
public class EnhancedDataGenerator {
    
    // 配置参数
    private static final int NUM_INDICATORS = 40;  // 40 个常规经济指标
    private static final int NUM_PERIODS = 150;     // 150 个月度历史数据
    private static final long DEFAULT_SEED = 42;    // 固定种子保证可复现
    
    // 经济周期参数
    private static final double BUSINESS_CYCLE_AMPLITUDE = 2.0;  // 经济周期振幅
    private static final int BUSINESS_CYCLE_PERIOD = 40;         // 经济周期长度（月）
    
    private final Random random;
    private final List<EconomicIndicator> indicators;
    
    public EnhancedDataGenerator() {
        this(DEFAULT_SEED);
    }
    
    public EnhancedDataGenerator(long seed) {
        this.random = new Random(seed);
        this.indicators = createIndicators();
    }
    
    /**
     * 创建经济指标列表
     */
    private List<EconomicIndicator> createIndicators() {
        List<EconomicIndicator> indicators = new ArrayList<>(NUM_INDICATORS);
        int idx = 0;
        
        // 10 个大宗商品指标 (CMD_0 - CMD_9)
        for (int i = 0; i < 10; i++) {
            EconomicIndicator indicator = new EconomicIndicator("CMD_" + i, "Commodity Index " + i);
            indicator.setSource("SIMULATED");
            indicator.setFrequency(EconomicIndicator.Frequency.MONTHLY);
            indicator.setUnit(EconomicIndicator.Unit.INDEX_POINTS);
            indicator.addMetadata("category", "commodity");
            indicators.add(indicator);
        }
        
        // 15 个宏观指标 (MACRO_0 - MACRO_14)
        for (int i = 0; i < 15; i++) {
            EconomicIndicator indicator = new EconomicIndicator("MACRO_" + i, "Macro Indicator " + i);
            indicator.setSource("SIMULATED");
            indicator.setFrequency(EconomicIndicator.Frequency.MONTHLY);
            indicator.setUnit(EconomicIndicator.Unit.PERCENT);
            indicator.addMetadata("category", "macro");
            indicators.add(indicator);
        }
        
        // 10 个金融市场指标 (FIN_0 - FIN_9)
        for (int i = 0; i < 10; i++) {
            EconomicIndicator indicator = new EconomicIndicator("FIN_" + i, "Financial Indicator " + i);
            indicator.setSource("SIMULATED");
            indicator.setFrequency(EconomicIndicator.Frequency.MONTHLY);
            indicator.setUnit(EconomicIndicator.Unit.RATIO);
            indicator.addMetadata("category", "financial");
            indicators.add(indicator);
        }
        
        // 5 个就业指标 (EMPL_0 - EMPL_4)
        for (int i = 0; i < 5; i++) {
            EconomicIndicator indicator = new EconomicIndicator("EMPL_" + i, "Employment Indicator " + i);
            indicator.setSource("SIMULATED");
            indicator.setFrequency(EconomicIndicator.Frequency.MONTHLY);
            indicator.setUnit(EconomicIndicator.Unit.PERCENT);
            indicator.addMetadata("category", "employment");
            indicators.add(indicator);
        }
        
        return indicators;
    }
    
    /**
     * 生成模拟经济数据（固定规模）
     * 
     * @return 时间序列数据矩阵 [NUM_PERIODS][NUM_INDICATORS]
     */
    public double[][] generateData() {
        return generateData(NUM_PERIODS, NUM_INDICATORS);
    }
    
    /**
     * 生成指定样本量的模拟数据（用于性能测试）
     * 
     * @param sampleSize 样本量（期数）
     * @return 时间序列数据矩阵
     */
    public double[][] generateDataForSampleSize(int sampleSize) {
        int numIndicators = Math.max(5, sampleSize / 10);
        return generateData(sampleSize, numIndicators);
    }
    
    /**
     * 生成模拟经济数据
     * 
     * @param numPeriods 期数
     * @param numIndicators 指标数
     * @return 时间序列数据矩阵
     */
    private double[][] generateData(int numPeriods, int numIndicators) {
        double[][] data = new double[numPeriods][numIndicators];
        
        // 初始化基期值（加入更多经济学约束）
        for (int j = 0; j < numIndicators; j++) {
            data[0][j] = initializeBaseline(j);
        }
        
        // 生成带自回归、交叉关联和经济周期的时间序列
        for (int t = 1; t < numPeriods; t++) {
            // 计算当前时期的经济周期位置
            double cycleComponent = BUSINESS_CYCLE_AMPLITUDE * 
                Math.sin(2 * Math.PI * t / BUSINESS_CYCLE_PERIOD);
            
            for (int i = 0; i < numIndicators; i++) {
                // 自回归项（经济惯性）
                double autoReg = 0.8 * data[t - 1][i];
                
                // 随机冲击项
                double shock = random.nextGaussian() * 0.5;
                
                // 构造部分指标间的强关联（例如前 5 个指标是大宗商品，影响中间 10 个宏观指标）
                if (i >= 10 && i < 25 && numIndicators > 10) {
                    // 大宗商品对宏观指标的传导效应（滞后 1 期）
                    for (int k = 0; k < Math.min(5, numIndicators); k++) {
                        autoReg += 0.06 * data[t - 1][k]; // 模拟上游对下游的传导
                    }
                }
                
                // 宏观指标之间的相互影响
                if (i >= 15 && i < 25 && numIndicators > 15) {
                    autoReg += 0.03 * data[t - 1][10]; // 受第一个宏观指标影响
                }
                
                // 金融市场对宏观经济的反馈
                if (i >= 25 && i < 35 && numIndicators > 25) {
                    autoReg += 0.02 * data[t - 1][12]; // 受宏观指标影响
                }
                
                // 加入经济周期成分
                double cycleEffect = cycleComponent * (0.5 + 0.5 * (i % 5) / 5.0);
                
                data[t][i] = autoReg + shock + cycleEffect;
            }
        }
        
        return data;
    }
    
    /**
     * 初始化基期值
     * 
     * 根据指标类型设置不同的初始值范围
     */
    private double initializeBaseline(int indicatorIndex) {
        int category = indicatorIndex / 10;
        
        switch (category) {
            case 0: // 大宗商品 (CMD)
                return 50 + random.nextGaussian() * 5; // 基准 50
            case 1: // 宏观指标 (MACRO)
                return 3 + random.nextGaussian() * 0.5; // 基准 3%（类似通胀率）
            case 2: // 金融市场 (FIN)
                return 100 + random.nextGaussian() * 10; // 基准 100（指数）
            case 3: // 就业指标 (EMPL)
                return 5 + random.nextGaussian() * 0.3; // 基准 5%（失业率）
            default:
                return random.nextGaussian() * 5;
        }
    }
    
    /**
     * 生成 TimeSeriesData 列表
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 时间序列数据列表
     */
    public List<TimeSeriesData> generateTimeSeriesData(LocalDate startDate, LocalDate endDate) {
        double[][] data = generateData();
        List<TimeSeriesData> result = new ArrayList<>(NUM_INDICATORS);
        
        // 计算月份数
        int months = (endDate.getYear() - startDate.getYear()) * 12 + 
                     (endDate.getMonthValue() - startDate.getMonthValue()) + 1;
        
        // 确保有足够的月份
        if (months > NUM_PERIODS) {
            throw new IllegalArgumentException(
                "Requested period (" + months + " months) exceeds maximum available data (" + NUM_PERIODS + " months)"
            );
        }
        
        // 预先创建日期数组
        LocalDate[] allDates = new LocalDate[months];
        LocalDate d = startDate;
        for (int m = 0; m < months; m++) {
            allDates[m] = d;
            d = d.plusMonths(1);
        }
        
        // 为每个指标创建时间序列
        for (int i = 0; i < NUM_INDICATORS; i++) {
            TimeSeriesData ts = new TimeSeriesData(
                indicators.get(i).getCode(),
                months,
                allDates.clone()
            );
            
            // 复制数据
            double[] values = new double[months];
            for (int t = 0; t < months; t++) {
                values[t] = data[t][i];
            }
            ts.setValues(values);
            
            result.add(ts);
        }
        
        // 添加元数据
        for (TimeSeriesData ts : result) {
            ts.addMetadata("generated", true);
            ts.addMetadata("seed", DEFAULT_SEED);
            ts.addMetadata("method", "enhanced_simulation");
        }
        
        return result;
    }
    
    /**
     * 获取支持的指标列表
     */
    public List<EconomicIndicator> getSupportedIndicators() {
        return new ArrayList<>(indicators);
    }
    
    /**
     * 获取数据生成器配置信息
     */
    public Map<String, Object> getConfigInfo() {
        Map<String, Object> config = new HashMap<>();
        config.put("numIndicators", NUM_INDICATORS);
        config.put("numPeriods", NUM_PERIODS);
        config.put("seed", DEFAULT_SEED);
        config.put("businessCycleAmplitude", BUSINESS_CYCLE_AMPLITUDE);
        config.put("businessCyclePeriod", BUSINESS_CYCLE_PERIOD);
        config.put("categories", Arrays.asList("commodity", "macro", "financial", "employment"));
        return config;
    }
}
