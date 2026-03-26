package com.mecn.causal;

import java.util.List;
import java.util.Map;

/**
 * 集成融合策略
 * 
 * 融合多种因果发现方法的结果，提高准确性和鲁棒性
 */
public class EnsembleFusionStrategy {
    
    /**
     * 投票机制融合
     * 
     * @param matrices 各方法的因果矩阵列表
     * @param minVotes 最小投票数（至少多少个方法认为存在因果关系）
     * @return 融合后的因果矩阵
     */
    public CausalMatrix vote(List<CausalMatrix> matrices, int minVotes) {
        if (matrices == null || matrices.isEmpty()) {
            throw new IllegalArgumentException("Matrices list cannot be empty");
        }
        
        int size = matrices.get(0).getMatrix().length;
        CausalMatrix result = new CausalMatrix(size, "Ensemble-Vote");
        
        // 对每个位置进行投票统计
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int votes = 0;
                double totalStrength = 0.0;
                
                for (CausalMatrix matrix : matrices) {
                    double strength = matrix.getCausalEffect(j, i); // j -> i
                    if (strength != 0) {
                        votes++;
                        totalStrength += strength;
                    }
                }
                
                // 如果投票数达到阈值，则保留该因果关系
                if (votes >= minVotes) {
                    result.setCausalEffect(j, i, totalStrength / votes); // 平均强度
                }
            }
        }
        
        result.addMetadata("votingMethod", "majority");
        result.addMetadata("minVotes", minVotes);
        result.addMetadata("numMethods", matrices.size());
        
        return result;
    }
    
    /**
     * 置信度加权融合
     * 
     * @param matrices 各方法的因果矩阵列表
     * @param weights 各方法的权重列表（与 matrices 一一对应）
     * @return 融合后的因果矩阵
     */
    public CausalMatrix weightedAverage(List<CausalMatrix> matrices, List<Double> weights) {
        if (matrices == null || matrices.isEmpty()) {
            throw new IllegalArgumentException("Matrices list cannot be empty");
        }
        
        if (weights != null && weights.size() != matrices.size()) {
            throw new IllegalArgumentException("Weights size must match matrices size");
        }
        
        int size = matrices.get(0).getMatrix().length;
        CausalMatrix result = new CausalMatrix(size, "Ensemble-WeightedAvg");
        
        // 如果未提供权重，则默认等权重
        if (weights == null) {
            weights = new java.util.ArrayList<>();
            for (int i = 0; i < matrices.size(); i++) {
                weights.add(1.0);
            }
        }
        
        // 计算加权和
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double weightedSum = 0.0;
                double weightTotal = 0.0;
                
                for (int k = 0; k < matrices.size(); k++) {
                    double strength = matrices.get(k).getCausalEffect(j, i);
                    double weight = weights.get(k);
                    
                    if (strength != 0) {
                        weightedSum += strength * weight;
                        weightTotal += weight;
                    }
                }
                
                if (weightTotal > 0) {
                    result.setCausalEffect(j, i, weightedSum / weightTotal);
                }
            }
        }
        
        result.addMetadata("fusionMethod", "weighted_average");
        result.addMetadata("weights", weights);
        
        return result;
    }
    
    /**
     * 混合策略：先投票筛选，再加权平均
     * 
     * @param matrices 因果矩阵列表
     * @param weights 权重列表
     * @param minVotes 最小投票数
     * @return 融合后的因果矩阵
     */
    public CausalMatrix hybrid(List<CausalMatrix> matrices, List<Double> weights, int minVotes) {
        // 第一步：投票筛选
        CausalMatrix voted = vote(matrices, minVotes);
        
        // 第二步：在投票通过的关系上进行加权平均
        int size = voted.getMatrix().length;
        CausalMatrix result = new CausalMatrix(size, "Ensemble-Hybrid");
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (voted.getCausalEffect(j, i) != 0) {
                    double weightedSum = 0.0;
                    double weightTotal = 0.0;
                    
                    for (int k = 0; k < matrices.size(); k++) {
                        double strength = matrices.get(k).getCausalEffect(j, i);
                        double weight = weights != null ? weights.get(k) : 1.0;
                        
                        if (strength != 0) {
                            weightedSum += strength * weight;
                            weightTotal += weight;
                        }
                    }
                    
                    if (weightTotal > 0) {
                        result.setCausalEffect(j, i, weightedSum / weightTotal);
                    }
                }
            }
        }
        
        result.addMetadata("fusionMethod", "hybrid_vote_then_weight");
        result.addMetadata("minVotes", minVotes);
        
        return result;
    }
}
