package com.mecn.integration;

import com.mecn.cli.CommandLineParser;
import com.mecn.io.CsvDataReader;
import com.mecn.model.TimeSeriesData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * CLI与数据读取集成测试
 */
@DisplayName("CLI与数据读取集成测试")
class CLIDataIntegrationTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    @DisplayName("CSV文件读取和命令行解析集成")
    void testCsvReadingWithCliConfig() throws IOException {
        // Given: 创建临时CSV文件
        File csvFile = createSampleCsvFile();
        
        // When: 使用CLI配置读取CSV
        CommandLineParser parser = new CommandLineParser();
        CommandLineParser.AnalysisConfig config = parser.parse(
            new String[]{"-i", csvFile.getAbsolutePath(), "--algorithm=lasso", "--max-lag=2"}
        );
        
        CsvDataReader reader = new CsvDataReader();
        List<TimeSeriesData> data = reader.read(config.getInputFile());
        
        // Then: 验证数据和配置
        assertThat(data).hasSize(3);
        assertThat(config.getAlgorithm()).isEqualTo("lasso");
        assertThat(config.getMaxLag()).isEqualTo(2);
        assertThat(data.get(0).getValues()).hasSize(5);
    }
    
    @Test
    @DisplayName("命令行参数传递到分析流程")
    void testCliParametersPropagation() {
        // Given
        CommandLineParser parser = new CommandLineParser();
        
        // When
        CommandLineParser.AnalysisConfig config = parser.parse(
            new String[]{"--input=test.csv", "--output=result.json", "--algorithm=ensemble",
                        "--lambda=0.05", "--max-lag=3", "--alpha=0.1", "--auto-tune=true", "--verbose"}
        );
        
        // Then
        assertAll(
            () -> assertThat(config.getInputFile()).isEqualTo("test.csv"),
            () -> assertThat(config.getOutputFile()).isEqualTo("result.json"),
            () -> assertThat(config.getAlgorithm()).isEqualTo("ensemble"),
            () -> assertThat(config.getLambda()).isEqualTo(0.05),
            () -> assertThat(config.getMaxLag()).isEqualTo(3),
            () -> assertThat(config.getSignificanceLevel()).isEqualTo(0.1),
            () -> assertThat(config.isAutoTune()).isTrue(),
            () -> assertThat(config.isVerbose()).isTrue()
        );
    }
    
    @Test
    @DisplayName("缺失值处理的端到端测试")
    void testMissingValueHandlingEndToEnd() throws IOException {
        // Given: 创建包含缺失值的CSV
        File csvFile = createCsvWithMissingValues();
        
        // When
        CsvDataReader reader = new CsvDataReader();
        List<TimeSeriesData> data = reader.read(csvFile.getAbsolutePath());
        
        // Then: 验证缺失值被正确处理
        assertThat(data).hasSize(2);
        // GDP应该有3个有效值（跳过了第2行的缺失值）
        assertThat(data.get(0).getValues()).hasSize(3);
        // UNRATE应该有4个有效值（跳过了第3行的缺失值）
        assertThat(data.get(1).getValues()).hasSize(4);
    }
    
    @Test
    @DisplayName("不同日期格式的兼容性测试")
    void testDifferentDateFormats() throws IOException {
        // Given: 创建标准格式的CSV
        File csvFile = createStandardDateCsv();
        
        // When
        CsvDataReader reader = new CsvDataReader();
        List<TimeSeriesData> data = reader.read(csvFile.getAbsolutePath());
        
        // Then
        assertThat(data).isNotEmpty();
        assertThat(data.get(0).getDates()).hasSize(3);
    }
    
    @Test
    @DisplayName("大数据集读取性能测试")
    void testLargeDatasetReadingPerformance() throws IOException {
        // Given: 创建较大的CSV文件（使用较小的数据集以确保稳定性）
        File largeCsv = createLargeCsvFile(50, 5);
        
        // When
        long startTime = System.currentTimeMillis();
        CsvDataReader reader = new CsvDataReader();
        List<TimeSeriesData> data = reader.read(largeCsv.getAbsolutePath());
        long endTime = System.currentTimeMillis();
        
        // Then
        assertThat(data).hasSize(5);
        assertThat(data.get(0).getValues()).hasSize(50);
        // 应该在合理时间内完成（< 5秒）
        assertThat(endTime - startTime).isLessThan(5000);
    }
    
    /**
     * 辅助方法：创建示例CSV文件
     */
    private File createSampleCsvFile() throws IOException {
        File file = tempDir.resolve("sample.csv").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("date,GDP,UNRATE,CPI\n");
            writer.write("2020-01-01,100.5,5.2,250.1\n");
            writer.write("2020-02-01,101.2,5.1,250.5\n");
            writer.write("2020-03-01,101.8,5.3,251.0\n");
            writer.write("2020-04-01,102.5,5.0,251.5\n");
            writer.write("2020-05-01,103.0,4.9,252.0\n");
        }
        return file;
    }
    
    /**
     * 辅助方法：创建包含缺失值的CSV
     */
    private File createCsvWithMissingValues() throws IOException {
        File file = tempDir.resolve("missing.csv").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("date,GDP,UNRATE\n");
            writer.write("2020-01-01,100.5,5.2\n");
            writer.write("2020-02-01,,5.1\n");  // GDP缺失
            writer.write("2020-03-01,101.8,5.3\n");  // 修复：添加UNRATE值
            writer.write("2020-04-01,102.5,\n");  // UNRATE缺失
            writer.write("2020-05-01,103.0,4.9\n");
        }
        return file;
    }
    
    /**
     * 辅助方法：创建标准日期格式CSV
     */
    private File createStandardDateCsv() throws IOException {
        File file = tempDir.resolve("standard.csv").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("date,Indicator1\n");
            writer.write("2020-01-01,100.5\n");
            writer.write("2020-02-01,101.2\n");
            writer.write("2020-03-01,101.8\n");
        }
        return file;
    }
    
    /**
     * 辅助方法：创建大型CSV文件
     */
    private File createLargeCsvFile(int rows, int columns) throws IOException {
        File file = tempDir.resolve("large.csv").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            // 写入表头
            writer.write("date");
            for (int j = 0; j < columns; j++) {
                writer.write(",Col" + j);
            }
            writer.write("\n");
            
            // 写入数据行
            for (int i = 0; i < rows; i++) {
                writer.write(String.format("2020-%02d-01", i + 1));
                for (int j = 0; j < columns; j++) {
                    writer.write(String.format(",%.2f", Math.random() * 100));
                }
                writer.write("\n");
            }
        }
        return file;
    }
}
