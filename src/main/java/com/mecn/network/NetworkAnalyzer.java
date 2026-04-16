package com.mecn.network;

import com.mecn.model.NetworkGraph;
import java.util.Map;

/**
 * 网络分析器接口
 * 
 * 定义网络分析的标准契约，所有网络分析算法都应实现此接口
 */
public interface NetworkAnalyzer {
    
    /**
     * 执行网络分析
     * 
     * @param graph 网络图
     * @return 分析结果
     */
    AnalysisResult analyze(NetworkGraph graph);
    
    /**
     * 获取分析器名称
     * 
     * @return 分析器名称
     */
    String getName();
    
    /**
     * 设置分析参数
     * 
     * @param key 参数键
     * @param value 参数值
     */
    void setParameter(String key, Object value);
    
    /**
     * 获取分析参数
     * 
     * @return 参数映射
     */
    Map<String, Object> getParameters();
}

/**
 * 网络分析结果接口
 */
interface AnalysisResult {
    
    /**
     * 获取分析类型
     * 
     * @return 分析类型名称
     */
    String getAnalysisType();
    
    /**
     * 获取分析元数据
     * 
     * @return 元数据映射
     */
    Map<String, Object> getMetadata();
    
    /**
     * 添加元数据
     * 
     * @param key 键
     * @param value 值
     */
    void addMetadata(String key, Object value);
}
