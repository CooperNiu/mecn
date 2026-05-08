package com.mecn.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间序列数据结构
 * 
 * 存储单个经济指标的时间序列数据
 */
public class TimeSeriesData {
    
    private String indicatorCode;           // 指标代码
    private LocalDate[] dates;              // 日期数组
    private double[] values;                // 数值数组
    private Map<String, Object> metadata;   // 元数据（包含原始性、季节性调整标记等）
    
    public TimeSeriesData() {
        this.metadata = new HashMap<>();
    }
    
    public TimeSeriesData(String indicatorCode, int size) {
        this();
        this.indicatorCode = indicatorCode;
        this.dates = new LocalDate[size];
        this.values = new double[size];
    }
    
    public TimeSeriesData(String indicatorCode, int size, LocalDate[] dates) {
        this();
        this.indicatorCode = indicatorCode;
        this.dates = dates;
        this.values = new double[size];
    }
    
    public TimeSeriesData(String indicatorCode, LocalDate[] dates, double[] values) {
        this();
        this.indicatorCode = indicatorCode;
        this.dates = dates;
        this.values = values;
    }
    
    // Getters and Setters
    public String getIndicatorCode() {
        return indicatorCode;
    }
    
    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }
    
    public LocalDate[] getDates() {
        return dates;
    }
    
    public void setDates(LocalDate[] dates) {
        this.dates = dates;
    }
    
    public double[] getValues() {
        return values;
    }
    
    public void setValues(double[] values) {
        this.values = values;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    /**
     * 获取数据长度
     */
    public int size() {
        return dates != null ? dates.length : 0;
    }
    
    /**
     * 获取指定索引的日期和值
     */
    public double getValueAt(int index) {
        if (index >= 0 && index < values.length) {
            return values[index];
        }
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + values.length);
    }
    
    /**
     * 转换为二维数组格式（用于因果分析）
     * @return double[][] {dates, values}
     */
    public double[][] toDoubleArray() {
        double[][] result = new double[2][size()];
        for (int i = 0; i < size(); i++) {
            result[0][i] = dates[i].toEpochDay(); // 日期转为天数
            result[1][i] = values[i];
        }
        return result;
    }
    
    /**
     * 提取纯数值数组（用于矩阵运算）
     */
    public double[] toArray() {
        return Arrays.copyOf(values, values.length);
    }
    
    @Override
    public String toString() {
        return "TimeSeriesData{" +
                "indicatorCode='" + indicatorCode + '\'' +
                ", size=" + size() +
                ", metadata=" + metadata +
                '}';
    }
}
