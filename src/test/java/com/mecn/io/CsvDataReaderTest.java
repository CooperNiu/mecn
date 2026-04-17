package com.mecn.io;

import com.mecn.model.TimeSeriesData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CsvDataReader 测试")
class CsvDataReaderTest {
    
    private CsvDataReader reader;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        reader = new CsvDataReader();
    }
    
    @Test
    @DisplayName("支持CSV文件")
    void testSupportsCsvFiles() {
        assertTrue(reader.supports("data.csv"));
        assertTrue(reader.supports("/path/to/data.CSV"));
        assertFalse(reader.supports("data.json"));
        assertFalse(reader.supports(null));
    }
    
    @Test
    @DisplayName("返回正确的类型")
    void testGetType() {
        assertEquals("csv", reader.getType());
    }
    
    @Test
    @DisplayName("读取简单CSV文件")
    void testReadSimpleCsv() throws IOException {
        // 创建测试CSV文件
        Path csvFile = tempDir.resolve("test.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("date,GDP,UNRATE\n");
            writer.write("2020-01-01,100.5,5.2\n");
            writer.write("2020-02-01,101.3,5.1\n");
            writer.write("2020-03-01,102.1,5.0\n");
        }
        
        List<TimeSeriesData> result = reader.read(csvFile.toString());
        
        assertEquals(2, result.size());
        
        TimeSeriesData gdp = result.stream()
            .filter(ts -> ts.getIndicatorCode().equals("GDP"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(gdp);
        assertEquals(3, gdp.getValues().length);
        assertEquals(100.5, gdp.getValues()[0], 0.001);
        assertEquals(101.3, gdp.getValues()[1], 0.001);
        assertEquals(102.1, gdp.getValues()[2], 0.001);
    }
    
    @Test
    @DisplayName("处理缺失值(NaN)")
    void testHandleMissingValues() throws IOException {
        Path csvFile = tempDir.resolve("missing.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("date,value\n");
            writer.write("2020-01-01,10.0\n");
            writer.write("2020-02-01,\n");  // 缺失值
            writer.write("2020-03-01,12.0\n");
        }
        
        List<TimeSeriesData> result = reader.read(csvFile.toString());
        
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getValues().length);  // NaN被过滤
    }
    
    @Test
    @DisplayName("空文件抛出异常")
    void testEmptyFileThrowsException() throws IOException {
        Path csvFile = tempDir.resolve("empty.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            // 空文件
        }
        
        assertThrows(IOException.class, () -> {
            reader.read(csvFile.toString());
        });
    }
    
    @Test
    @DisplayName("不存在的文件抛出异常")
    void testNonExistentFileThrowsException() {
        assertThrows(IOException.class, () -> {
            reader.read("/nonexistent/file.csv");
        });
    }
    
    @Test
    @DisplayName("null路径抛出异常")
    void testNullPathThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            reader.read(null);
        });
    }
    
    @Test
    @DisplayName("列数不匹配的行被跳过")
    void testSkipMismatchedRows() throws IOException {
        Path csvFile = tempDir.resolve("mismatch.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("date,value\n");
            writer.write("2020-01-01,10.0\n");
            writer.write("2020-02-01,11.0,extra\n");  // 列数不匹配
            writer.write("2020-03-01,12.0\n");
        }
        
        List<TimeSeriesData> result = reader.read(csvFile.toString());
        
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getValues().length);  // 跳过了不匹配的行
    }
}
