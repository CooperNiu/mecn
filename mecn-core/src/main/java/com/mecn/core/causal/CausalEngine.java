package com.mecn.causal;

import java.util.List;
import java.util.Map;

/**
 * 因果发现引擎接口
 * 
 * 定义因果挖掘的核心契约
 */
public interface CausalEngine {
    
    /**
     * 执行因果发现
     * 
     * @param data 时间序列数据矩阵 [T][N]，T 为时间点数，N 为指标数
     * @param config 因果发现配置
     * @return 因果图（包含邻接矩阵和置信度）
     */
    CausalResult discover(double[][] data, CausalConfig config);
    
    /**
     * 注册因果方法
     * 
     * @param method 因果方法实例
     */
    void registerMethod(CausalMethod method);
    
    /**
     * 获取已注册的因果方法列表
     * 
     * @return 因果方法列表
     */
    List<CausalMethod> getRegisteredMethods();
    
    /**
     * 取消注册因果方法
     * 
     * @param methodName 方法名称
     */
    void unregisterMethod(String methodName);
}
