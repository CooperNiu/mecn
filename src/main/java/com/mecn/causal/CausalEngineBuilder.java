package com.mecn.causal;

import java.util.ArrayList;
import java.util.List;

/**
 * 因果引擎构建器 - 流式 API 设计
 * 
 * 参考 Tigramite 的简洁风格，提供流畅的链式调用
 * 
 * @example
 * {@code
 * CausalEngine engine = CausalEngine.builder()
 *     .data(timeSeriesData)
 *     .method(CausalMethod.LASSO.withLambda(0.1))
 *     .method(CausalMethod.GRANGER.withLag(2))
 *     .significanceLevel(0.05)
 *     .maxLag(12)
 *     .parallel(true)
 *     .fusionStrategy(FusionStrategy.VOTING.minVotes(2))
 *     .build();
 *     
 * CausalResult result = engine.discover();
 * }
 */
public class CausalEngineBuilder {
    
    private double[][] data;                          // 时间序列数据
    private final List<CausalMethod> methods;         // 因果方法列表
    private int maxLag = 12;                          // 最大滞后阶数
    private double significanceLevel = 0.05;          // 显著性水平
    private boolean parallel = true;                  // 是否并行计算
    private FusionStrategy fusionStrategy = FusionStrategy.HYBRID;  // 融合策略
    private int minVotes = 2;                         // 最小投票数
    
    public CausalEngineBuilder() {
        this.methods = new ArrayList<>();
    }
    
    /**
     * 设置时间序列数据
     * 
     * @param data 数据矩阵 [T][N]，T 为时间点数，N 为指标数
     * @return this builder
     */
    public CausalEngineBuilder data(double[][] data) {
        this.data = data;
        return this;
    }
    
    /**
     * 添加因果发现方法
     * 
     * @param method 因果方法实例
     * @return this builder
     */
    public CausalEngineBuilder method(CausalMethod method) {
        this.methods.add(method);
        return this;
    }
    
    /**
     * 添加多个因果发现方法
     * 
     * @param methods 因果方法列表
     * @return this builder
     */
    public CausalEngineBuilder methods(List<CausalMethod> methods) {
        this.methods.addAll(methods);
        return this;
    }
    
    /**
     * 设置最大滞后阶数
     * 
     * @param maxLag 最大滞后阶数
     * @return this builder
     */
    public CausalEngineBuilder maxLag(int maxLag) {
        this.maxLag = maxLag;
        return this;
    }
    
    /**
     * 设置显著性水平
     * 
     * @param significanceLevel 显著性水平（默认 0.05）
     * @return this builder
     */
    public CausalEngineBuilder significanceLevel(double significanceLevel) {
        this.significanceLevel = significanceLevel;
        return this;
    }
    
    /**
     * 设置是否并行计算
     * 
     * @param parallel true 表示并行计算
     * @return this builder
     */
    public CausalEngineBuilder parallel(boolean parallel) {
        this.parallel = parallel;
        return this;
    }
    
    /**
     * 设置融合策略
     * 
     * @param strategy 融合策略（VOTING, WEIGHTED_AVERAGE, HYBRID）
     * @return this builder
     */
    public CausalEngineBuilder fusionStrategy(FusionStrategy strategy) {
        this.fusionStrategy = strategy;
        return this;
    }
    
    /**
     * 设置最小投票数（用于 VOTING 和 HYBRID 策略）
     * 
     * @param minVotes 最小投票数
     * @return this builder
     */
    public CausalEngineBuilder minVotes(int minVotes) {
        this.minVotes = minVotes;
        return this;
    }
    
    /**
     * 构建并执行因果发现
     * 
     * @return 因果发现结果
     */
    public CausalResult discover() {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be empty");
        }
        
        if (methods.isEmpty()) {
            throw new IllegalArgumentException("At least one causal method must be specified");
        }
        
        // 创建配置
        CausalConfig config = new CausalConfig();
        config.setMaxLag(maxLag);
        config.setSignificanceLevel(significanceLevel);
        config.setParallel(parallel);
        
        // 创建引擎
        CausalEngineImpl engine = new CausalEngineImpl(parallel);
        
        // 注册所有方法
        for (CausalMethod method : methods) {
            engine.registerMethod(method);
        }
        
        // 执行发现
        return engine.discover(data, config);
    }
    
    /**
     * 构建引擎但不立即执行
     * 
     * @return 配置好的因果引擎
     */
    public CausalEngine build() {
        if (methods.isEmpty()) {
            throw new IllegalArgumentException("At least one causal method must be specified");
        }
        
        CausalEngineImpl engine = new CausalEngineImpl(parallel);
        for (CausalMethod method : methods) {
            engine.registerMethod(method);
        }
        
        return engine;
    }
    
    /**
     * 使用配置执行发现
     * 
     * @param config 因果配置
     * @return 因果发现结果
     */
    public CausalResult discoverWith(CausalConfig config) {
        if (data == null) {
            throw new IllegalArgumentException("Data must be set");
        }
        
        CausalEngine engine = build();
        return engine.discover(data, config);
    }
}
