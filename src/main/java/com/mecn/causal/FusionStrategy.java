package com.mecn.causal;

/**
 * 融合策略枚举
 * 
 * 用于多算法集成时的结果融合
 */
public enum FusionStrategy {
    
    /**
     * 投票策略：只保留获得足够多方法支持的因果关系
     */
    VOTING,
    
    /**
     * 加权平均：根据方法权重对因果强度进行加权平均
     */
    WEIGHTED_AVERAGE,
    
    /**
     * 混合策略：先投票筛选，再加权平均
     */
    HYBRID;
    
    /**
     * 带最小投票数的构建器（仅适用于 VOTING 和 HYBRID）
     */
    public static class ConfiguredStrategy {
        private final FusionStrategy strategy;
        private final int minVotes;
        
        ConfiguredStrategy(FusionStrategy strategy, int minVotes) {
            this.strategy = strategy;
            this.minVotes = minVotes;
        }
        
        public FusionStrategy getStrategy() {
            return strategy;
        }
        
        public int getMinVotes() {
            return minVotes;
        }
    }
    
    /**
     * 配置最小投票数
     * 
     * @param minVotes 最小投票数
     * @return 配置好的策略
     */
    public ConfiguredStrategy minVotes(int minVotes) {
        return new ConfiguredStrategy(this, minVotes);
    }
}
