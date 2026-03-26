package com.mecn;

import com.mecn.causal.CausalEngineBuilder;
import com.mecn.causal.CausalMethod;
import com.mecn.causal.CausalResult;
import com.mecn.causal.FusionStrategy;
import com.mecn.causal.LassoRegression;
import com.mecn.data.provider.SimulatedDataProvider;
import com.mecn.model.EconomicIndicator;
import com.mecn.model.TimeSeriesData;
import com.mecn.network.NetworkBuilder;
import com.mecn.model.NetworkGraph;
import com.mecn.network.RippleSimulator;
import com.mecn.network.RippleResult;

import java.time.LocalDate;
import java.util.List;

/**
 * MECN 快捷工具类 - 简化常用工作流
 * 
 * 提供一站式接口，无需手动组装各个模块
 * 
 * @example
 * {@code
 * // 最简单的使用方式
 * CausalResult result = MECNTools.discoverCausalStructure(data, 0.05);
 * 
 * // 完整的端到端分析
 * AnalysisResult analysis = MECNTools.analyze(indicators, startDate, endDate)
 *     .significanceLevel(0.05)
 *     .maxLag(12)
 *     .execute();
 * 
 * // 模拟冲击传播
 * RippleResult ripple = MECNTools.simulateShock(network, "GDP", -0.1);
 * }
 */
public class MECNTools {
    
    private MECNTools() {
        // 私有构造函数，防止实例化
    }
    
    /**
     * 快速因果发现（使用默认配置）
     * 
     * @param data 时间序列数据 [T][N]
     * @param significanceLevel 显著性水平
     * @return 因果发现结果
     */
    public static CausalResult discoverCausalStructure(double[][] data, double significanceLevel) {
        return new CausalEngineBuilder()
            .data(data)
            .method(new LassoRegression())
            .significanceLevel(significanceLevel)
            .discover();
    }
    
    /**
     * 快速因果发现（多方法集成）
     * 
     * @param data 时间序列数据 [T][N]
     * @param methods 因果方法列表
     * @param significanceLevel 显著性水平
     * @return 因果发现结果
     */
    public static CausalResult discoverCausalStructure(
            double[][] data, 
            List<CausalMethod> methods,
            double significanceLevel) {
        
        CausalEngineBuilder builder = new CausalEngineBuilder()
            .data(data)
            .methods(methods)
            .significanceLevel(significanceLevel)
            .fusionStrategy(FusionStrategy.HYBRID)
            .minVotes(2);
        
        return builder.discover();
    }
    
    /**
     * 构建经济网络
     * 
     * @param causalResult 因果发现结果
     * @param indicatorCodes 指标代码列表
     * @param edgeThreshold 边阈值（小于此值的边将被过滤）
     * @return 网络图
     */
    public static NetworkGraph buildNetwork(CausalResult causalResult, List<String> indicatorCodes, double edgeThreshold) {
        return new NetworkBuilder(edgeThreshold).build(causalResult, indicatorCodes);
    }
    
    /**
     * 模拟冲击传播
     * 
     * @param network 经济网络
     * @param shockNode 受冲击节点
     * @param shockMagnitude 冲击幅度（正数表示正向冲击，负数表示负向冲击）
     * @return 涟漪效应结果
     */
    public static RippleResult simulateShock(NetworkGraph network, String shockNode, double shockMagnitude) {
        RippleSimulator simulator = new RippleSimulator();
        return simulator.simulate(network, shockNode, shockMagnitude);
    }
    
    /**
     * 模拟冲击传播（自定义衰减因子）
     * 
     * @param network 经济网络
     * @param shockNode 受冲击节点
     * @param shockMagnitude 冲击幅度
     * @param decayFactor 衰减因子（0-1 之间，默认 0.9）
     * @param timeSteps 时间步数
     * @return 涟漪效应结果
     */
    public static RippleResult simulateShock(
            NetworkGraph network, 
            String shockNode, 
            double shockMagnitude,
            double decayFactor,
            int timeSteps) {
        
        RippleSimulator simulator = new RippleSimulator(decayFactor, timeSteps);
        return simulator.simulate(network, shockNode, shockMagnitude);
    }
    
    /**
     * 创建分析器（用于更复杂的分析场景）
     * 
     * @return 分析器构建器
     */
    public static AnalyzerBuilder analyze() {
        return new AnalyzerBuilder();
    }
    
    /**
     * 分析器构建器
     */
    public static class AnalyzerBuilder {
        private List<EconomicIndicator> indicators;
        private LocalDate startDate;
        private LocalDate endDate;
        private double significanceLevel = 0.05;
        private int maxLag = 12;
        private double edgeThreshold = 0.1;
        private List<CausalMethod> methods;
        
        /**
         * 设置指标列表
         */
        public AnalyzerBuilder indicators(List<EconomicIndicator> indicators) {
            this.indicators = indicators;
            return this;
        }
        
        /**
         * 设置时间范围
         */
        public AnalyzerBuilder timeRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            return this;
        }
        
        /**
         * 设置显著性水平
         */
        public AnalyzerBuilder significanceLevel(double significanceLevel) {
            this.significanceLevel = significanceLevel;
            return this;
        }
        
        /**
         * 设置最大滞后阶数
         */
        public AnalyzerBuilder maxLag(int maxLag) {
            this.maxLag = maxLag;
            return this;
        }
        
        /**
         * 设置边阈值
         */
        public AnalyzerBuilder edgeThreshold(double edgeThreshold) {
            this.edgeThreshold = edgeThreshold;
            return this;
        }
        
        /**
         * 设置因果方法
         */
        public AnalyzerBuilder methods(List<CausalMethod> methods) {
            this.methods = methods;
            return this;
        }
        
        /**
         * 执行完整分析流程
         * 
         * @return 分析结果
         */
        public AnalysisResult execute() {
            if (indicators == null || indicators.isEmpty()) {
                throw new IllegalArgumentException("Indicators cannot be empty");
            }
            
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("Time range must be specified");
            }
            
            // 1. 获取数据
            SimulatedDataProvider provider = new SimulatedDataProvider();
            List<TimeSeriesData> timeSeriesData = provider.fetch(indicators, startDate, endDate);
            
            // 2. 转换为数据矩阵
            int T = timeSeriesData.get(0).getValues().length;
            int N = indicators.size();
            double[][] data = new double[T][N];
            
            for (int i = 0; i < N; i++) {
                System.arraycopy(timeSeriesData.get(i).getValues(), 0, data[0], 0, T);
                for (int t = 0; t < T; t++) {
                    data[t][i] = timeSeriesData.get(i).getValues()[t];
                }
            }
            
            // 3. 因果发现
            CausalEngineBuilder engineBuilder = new CausalEngineBuilder()
                .data(data)
                .significanceLevel(significanceLevel)
                .maxLag(maxLag);
            
            if (methods != null && !methods.isEmpty()) {
                engineBuilder.methods(methods);
            } else {
                engineBuilder.method(new LassoRegression());
            }
            
            CausalResult causalResult = engineBuilder.discover();
            
            // 4. 构建网络
            List<String> indicatorCodes = indicators.stream()
                .map(EconomicIndicator::getCode)
                .collect(java.util.stream.Collectors.toList());
            
            NetworkGraph network = new NetworkBuilder(edgeThreshold)
                .build(causalResult, indicatorCodes);
            
            return new AnalysisResult(causalResult, network, timeSeriesData);
        }
    }
    
    /**
     * 分析结果封装
     */
    public static class AnalysisResult {
        private final CausalResult causalResult;
        private final NetworkGraph network;
        private final List<TimeSeriesData> timeSeriesData;
        
        public AnalysisResult(CausalResult causalResult, NetworkGraph network, List<TimeSeriesData> timeSeriesData) {
            this.causalResult = causalResult;
            this.network = network;
            this.timeSeriesData = timeSeriesData;
        }
        
        public CausalResult getCausalResult() {
            return causalResult;
        }
        
        public NetworkGraph getNetwork() {
            return network;
        }
        
        public List<TimeSeriesData> getTimeSeriesData() {
            return timeSeriesData;
        }
        
        /**
         * 模拟冲击并返回结果
         */
        public RippleResult simulateShock(String shockNode, double shockMagnitude) {
            return MECNTools.simulateShock(network, shockNode, shockMagnitude);
        }
    }
}
