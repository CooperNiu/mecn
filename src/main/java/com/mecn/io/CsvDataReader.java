package com.mecn.io;

import com.mecn.model.TimeSeriesData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * CSV格式数据读取器
 * 
 * 支持从CSV文件读取时间序列数据
 * 预期格式: 第一列为日期，后续列为各个指标的值
 */
public class CsvDataReader implements DataReader {

    private static final Logger log = LoggerFactory.getLogger(CsvDataReader.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public List<TimeSeriesData> read(String source) throws IOException {
        if (source == null || source.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源路径不能为空");
        }
        
        File file = new File(source);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + source);
        }
        
        List<TimeSeriesData> result = new ArrayList<>();
        Map<String, List<Double>> dataMap = new LinkedHashMap<>();
        List<LocalDate> dates = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // 读取表头
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("CSV文件为空");
            }
            
            String[] headers = headerLine.split(",");
            if (headers.length < 2) {
                throw new IOException("CSV文件格式错误：至少需要日期列和一列数据");
            }
            
            // 初始化数据列表（跳过第一列日期）
            for (int i = 1; i < headers.length; i++) {
                String columnName = headers[i].trim();
                dataMap.put(columnName, new ArrayList<>());
            }
            
            // 读取数据行
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length != headers.length) {
                    log.warn("第{}行列数不匹配 (预期{}列, 实际{}列), 已跳过", lineNumber, headers.length, values.length);
                    continue;
                }
                
                // 解析日期
                try {
                    LocalDate date = LocalDate.parse(values[0].trim(), DATE_FORMATTER);
                    dates.add(date);
                } catch (Exception e) {
                    log.warn("第{}行日期格式错误: {}, 已跳过", lineNumber, values[0]);
                    continue;
                }
                
                // 解析数据值
                for (int i = 1; i < values.length; i++) {
                    String columnName = headers[i].trim();
                    try {
                        double value = Double.parseDouble(values[i].trim());
                        dataMap.get(columnName).add(value);
                    } catch (NumberFormatException e) {
                        log.warn("第{}行数据格式错误: {}, 按NaN处理", lineNumber, values[i]);
                        dataMap.get(columnName).add(Double.NaN);
                    }
                }
            }
        }
        
        if (dates.isEmpty()) {
            throw new IOException("CSV文件中没有有效数据");
        }
        
        // 转换为TimeSeriesData
        for (Map.Entry<String, List<Double>> entry : dataMap.entrySet()) {
            String indicatorName = entry.getKey();
            List<Double> values = entry.getValue();
            
            // 过滤NaN值
            List<LocalDate> validDates = new ArrayList<>();
            List<Double> validValues = new ArrayList<>();
            
            for (int i = 0; i < dates.size(); i++) {
                if (!Double.isNaN(values.get(i))) {
                    validDates.add(dates.get(i));
                    validValues.add(values.get(i));
                }
            }
            
            if (!validValues.isEmpty()) {
                double[] valueArray = validValues.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();
                
                TimeSeriesData tsData = new TimeSeriesData(
                    indicatorName,
                    validDates.toArray(new LocalDate[0]),
                    valueArray
                );
                result.add(tsData);
            }
        }
        
        return result;
    }
    
    @Override
    public String getType() {
        return "csv";
    }
    
    @Override
    public boolean supports(String source) {
        return source != null && source.toLowerCase().endsWith(".csv");
    }
}
