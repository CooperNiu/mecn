package com.mecn.io;

import com.mecn.model.TimeSeriesData;
import java.io.IOException;
import java.util.List;

/**
 * 数据读取器接口
 * 
 * 定义从不同来源读取时间序列数据的标准契约
 */
interface DataReader {
    
    /**
     * 从指定源读取数据
     * 
     * @param source 数据源（文件路径、URL等）
     * @return 时间序列数据列表
     * @throws IOException 读取异常
     */
    List<TimeSeriesData> read(String source) throws IOException;
    
    /**
     * 读取器类型
     * 
     * @return 读取器类型名称
     */
    String getType();
    
    /**
     * 检查是否支持该数据源
     * 
     * @param source 数据源
     * @return true 如果支持
     */
    boolean supports(String source);
}

/**
 * 数据写入器接口
 * 
 * 定义将时间序列数据写入不同目标的标凈契约
 */
interface DataWriter {
    
    /**
     * 将数据写入指定目标
     * 
     * @param data 时间序列数据
     * @param target 目标（文件路径、URL等）
     * @throws IOException 写入异常
     */
    void write(List<TimeSeriesData> data, String target) throws IOException;
    
    /**
     * 写入器类型
     * 
     * @return 写入器类型名称
     */
    String getType();
    
    /**
     * 检查是否支持该目标
     * 
     * @param target 目标
     * @return true 如果支持
     */
    boolean supports(String target);
}
