package com.mecn.preprocess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.mecn.preprocess.DataQualityChecker.QualityReport;
import com.mecn.preprocess.DataQualityChecker.DataIssue;
import com.mecn.preprocess.DataQualityChecker.IssueType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 数据质量检查器测试
 * 
 * TDD: 先写测试，再实现功能
 */
class DataQualityCheckerTest {
    
    private DataQualityChecker checker;
    
    @BeforeEach
    void setUp() {
        checker = new DataQualityChecker();
    }
    
    @Test
    void testCheckQuality_WithCleanData() {
        // Given - 干净的数据
        double[][] cleanData = generateCleanData(100, 5);
        
        // When
        QualityReport report = checker.checkQuality(cleanData);
        
        // Then - 允许少量低级别问题
        assertThat(report).isNotNull();
        assertThat(report.getQuality()).isIn(
            QualityReport.OverallQuality.EXCELLENT,
            QualityReport.OverallQuality.GOOD
        );
    }
    
    @Test
    void testCheckQuality_WithMissingValues() {
        // Given - 包含缺失值的数据
        double[][] data = generateCleanData(100, 3);
        data[10][0] = Double.NaN;
        data[20][0] = Double.POSITIVE_INFINITY;
        
        // When
        QualityReport report = checker.checkQuality(data);
        
        // Then
        assertThat(report.getIssueCount(IssueType.MISSING_VALUES)).isGreaterThan(0);
        assertThat(report.getQuality()).isIn(
            QualityReport.OverallQuality.GOOD,
            QualityReport.OverallQuality.FAIR
        );
    }
    
    @Test
    void testCheckQuality_WithConstantSeries() {
        // Given - 常量序列
        double[][] data = new double[50][2];
        for (int t = 0; t < 50; t++) {
            data[t][0] = 5.0;  // 常量
            data[t][1] = Math.random();  // 正常
        }
        
        // When
        QualityReport report = checker.checkQuality(data);
        
        // Then
        assertThat(report.getIssueCount(IssueType.ZERO_VARIANCE)).isGreaterThan(0);
    }
    
    @Test
    void testCheckQuality_WithOutliers() {
        // Given - 包含异常值的数据
        double[][] data = generateCleanData(100, 2);
        data[50][0] = 1000.0;  // 极端异常值
        data[60][0] = -1000.0;
        
        // When
        QualityReport report = checker.checkQuality(data);
        
        // Then
        assertThat(report.getIssueCount(IssueType.OUTLIERS)).isGreaterThan(0);
        assertThat(report.getIssueCount(IssueType.EXTREME_VALUES)).isGreaterThan(0);
    }
    
    @Test
    void testCheckQuality_WithNegativeValues() {
        // Given - 包含负值的数据
        double[][] data = generateCleanData(100, 2);
        data[10][0] = -5.0;
        data[20][0] = -10.0;
        
        // When
        QualityReport report = checker.checkQuality(data);
        
        // Then
        assertThat(report.getIssueCount(IssueType.NEGATIVE_VALUES)).isGreaterThan(0);
    }
    
    @Test
    void testCheckQuality_WithHighMissingRate() {
        // Given - 高缺失率
        double[][] data = generateCleanData(100, 2);
        for (int t = 0; t < 20; t++) {
            data[t][0] = Double.NaN;  // 20% 缺失
        }
        
        // When
        QualityReport report = checker.checkQuality(data);
        
        // Then
        assertThat(report.getQuality()).isIn(
            QualityReport.OverallQuality.POOR,
            QualityReport.OverallQuality.CRITICAL
        );
    }
    
    @Test
    void testCheckQuality_WithNullData() {
        // When
        QualityReport report = checker.checkQuality(null);
        
        // Then
        assertThat(report).isNotNull();
        assertThat(report.getQuality()).isEqualTo(QualityReport.OverallQuality.CRITICAL);
        assertThat(report.getIssueCount()).isGreaterThan(0);
    }
    
    @Test
    void testCheckQuality_WithEmptyData() {
        // Given
        double[][] emptyData = new double[0][0];
        
        // When
        QualityReport report = checker.checkQuality(emptyData);
        
        // Then
        assertThat(report).isNotNull();
        assertThat(report.getQuality()).isEqualTo(QualityReport.OverallQuality.CRITICAL);
    }
    
    @Test
    void testQualityReport_GenerateReport() {
        // Given
        double[][] data = generateCleanData(50, 2);
        data[10][0] = Double.NaN;
        
        // When
        QualityReport report = checker.checkQuality(data);
        String reportText = report.generateReport();
        
        // Then
        assertThat(reportText).contains("数据质量报告");
        assertThat(reportText).contains("整体质量");
        assertThat(reportText).contains("问题总数");
    }
    
    @Test
    void testQualityReport_Statistics() {
        // Given
        double[][] data = generateCleanData(100, 5);
        
        // When
        QualityReport report = checker.checkQuality(data);
        
        // Then
        assertThat(report.getStatistics()).containsKey("numTimePoints");
        assertThat(report.getStatistics()).containsKey("numVariables");
        assertThat(report.getStatistics()).containsKey("totalDataPoints");
        assertThat(report.getStatistics().get("numTimePoints")).isEqualTo(100);
        assertThat(report.getStatistics().get("numVariables")).isEqualTo(5);
    }
    
    @Test
    void testDataIssue_ToString() {
        // Given
        DataIssue issue = new DataIssue(
            IssueType.OUTLIERS,
            "Test issue",
            0, 10, 100.0,
            DataIssue.Severity.HIGH
        );
        
        // When
        String issueStr = issue.toString();
        
        // Then
        assertThat(issueStr).contains("OUTLIERS");
        assertThat(issueStr).contains("HIGH");
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 生成干净的测试数据
     */
    private double[][] generateCleanData(int timePoints, int numVariables) {
        double[][] data = new double[timePoints][numVariables];
        
        for (int t = 0; t < timePoints; t++) {
            for (int i = 0; i < numVariables; i++) {
                // 生成平滑的时间序列
                data[t][i] = Math.sin(t * 0.1 + i) + Math.random() * 0.1;
            }
        }
        
        return data;
    }
}
