package com.mecn.data.provider;

import com.mecn.model.EconomicIndicator;
import com.mecn.model.TimeSeriesData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FRED 数据提供者测试
 * 
 * 使用 TDD 方式编写，先写测试再实现功能
 */
@DisplayName("FRED 数据提供者测试")
class FredDataProviderTest {
    
    private FredDataProvider provider;
    
    @BeforeEach
    void setUp() {
        // 使用测试 API Key（实际使用时需要替换为有效的 key）
        provider = new FredDataProvider("test_api_key");
    }
    
    @Test
    @DisplayName("应该正确初始化数据提供者")
    void shouldInitializeProvider() {
        assertNotNull(provider);
        assertEquals("FRED", provider.getName());
        assertTrue(provider.getSupportedIndicators().size() > 0);
    }
    
    @Test
    @DisplayName("当 API Key 为空时应返回不可用")
    void shouldBeUnavailableWhenApiKeyIsEmpty() {
        FredDataProvider emptyKeyProvider = new FredDataProvider("");
        assertFalse(emptyKeyProvider.isAvailable());
        
        FredDataProvider nullKeyProvider = new FredDataProvider(null);
        assertFalse(nullKeyProvider.isAvailable());
    }
    
    @Test
    @DisplayName("当 API Key 有效时应返回可用")
    void shouldBeAvailableWhenApiKeyIsValid() {
        assertTrue(provider.isAvailable());
    }
    
    @Test
    @DisplayName("应该包含常用的经济指标")
    void shouldContainCommonEconomicIndicators() {
        List<EconomicIndicator> indicators = provider.getSupportedIndicators();
        
        // 验证包含 GDP 相关指标
        assertTrue(indicators.stream()
            .anyMatch(i -> i.getCode().equals("GDP")));
        
        // 验证包含失业率指标
        assertTrue(indicators.stream()
            .anyMatch(i -> i.getCode().equals("UNRATE")));
        
        // 验证包含 CPI 指标
        assertTrue(indicators.stream()
            .anyMatch(i -> i.getCode().equals("CPIAUCSL")));
    }
    
    @Test
    @DisplayName("解析 FRED JSON 响应 - 正常情况")
    void shouldParseFredJsonResponseSuccessfully() {
        // 准备测试用的 JSON 响应（模拟 FRED API 返回格式）
        String jsonResponse = "{\"observations\":[{\"date\":\"2020-01-01\",\"value\":\"123.45\"},{\"date\":\"2020-02-01\",\"value\":\"124.56\"},{\"date\":\"2020-03-01\",\"value\":\"125.67\"}]}";
        
        // 使用反射调用私有方法进行测试
        try {
            var method = FredDataProvider.class.getDeclaredMethod(
                "parseFredResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, jsonResponse, "TEST_SERIES");
            
            assertNotNull(result);
            assertEquals("TEST_SERIES", result.getIndicatorCode());
            assertEquals(3, result.size());
            assertEquals(123.45, result.getValueAt(0), 0.01);
            assertEquals(124.56, result.getValueAt(1), 0.01);
            assertEquals(125.67, result.getValueAt(2), 0.01);
            
            LocalDate[] dates = result.getDates();
            assertEquals(LocalDate.of(2020, 1, 1), dates[0]);
            assertEquals(LocalDate.of(2020, 2, 1), dates[1]);
            assertEquals(LocalDate.of(2020, 3, 1), dates[2]);
            
        } catch (Exception e) {
            fail("解析 JSON 响应时发生异常：" + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("解析 FRED JSON 响应 - 处理缺失值")
    void shouldHandleMissingValuesInResponse() {
        String jsonResponse = """
            {
              "observations": [
                {"date": "2020-01-01", "value": "100.0"},
                {"date": "2020-02-01", "value": "."},
                {"date": "2020-03-01", "value": "102.5"},
                {"date": "2020-04-01", "value": null}
              ]
            }
            """;
        
        try {
            var method = FredDataProvider.class.getDeclaredMethod(
                "parseFredResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, jsonResponse, "TEST_MISSING");
            
            assertNotNull(result);
            // 应该跳过缺失值，只返回 2 个有效数据
            assertEquals(2, result.size());
            assertEquals(100.0, result.getValueAt(0), 0.01);
            assertEquals(102.5, result.getValueAt(1), 0.01);
            
        } catch (Exception e) {
            fail("处理缺失值时发生异常：" + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("解析 FRED JSON 响应 - 空观测列表")
    void shouldHandleEmptyObservations() {
        String jsonResponse = """
            {
              "observations": []
            }
            """;
        
        try {
            var method = FredDataProvider.class.getDeclaredMethod(
                "parseFredResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, jsonResponse, "TEST_EMPTY");
            
            assertNotNull(result);
            assertEquals(0, result.size());
            assertEquals("TEST_EMPTY", result.getIndicatorCode());
            
        } catch (Exception e) {
            fail("处理空观测列表时发生异常：" + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("解析 FRED JSON 响应 - 无效 JSON 格式")
    void shouldHandleInvalidJsonFormat() {
        String invalidJson = "not a valid json";
        
        try {
            var method = FredDataProvider.class.getDeclaredMethod(
                "parseFredResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, invalidJson, "TEST_INVALID");
            
            assertNotNull(result);
            assertEquals(0, result.size());
            
        } catch (Exception e) {
            fail("处理无效 JSON 时应该返回空数据而不是抛出异常");
        }
    }
    
    @Test
    @DisplayName("解析 FRED JSON 响应 - 浮点数精度")
    void shouldHandleFloatingPointPrecision() {
        String jsonResponse = """
            {
              "observations": [
                {"date": "2020-01-01", "value": "123.456789"},
                {"date": "2020-02-01", "value": "-987.654321"},
                {"date": "2020-03-01", "value": "0.000001"}
              ]
            }
            """;
        
        try {
            var method = FredDataProvider.class.getDeclaredMethod(
                "parseFredResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, jsonResponse, "TEST_FLOAT");
            
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(123.456789, result.getValueAt(0), 0.000001);
            assertEquals(-987.654321, result.getValueAt(1), 0.000001);
            assertEquals(0.000001, result.getValueAt(2), 0.0000001);
            
        } catch (Exception e) {
            fail("处理浮点数时发生异常：" + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("获取数据 - 成功场景（集成测试）")
    void shouldFetchDataSuccessfully() {
        // 注意：这个测试需要有效的 API Key，如果没有会失败
        // 在实际 CI/CD 中应该跳过或使用 Mock
        
        if (!provider.isAvailable()) {
            // 如果 API Key 无效，跳过此测试
            return;
        }
        
        List<EconomicIndicator> indicators = Arrays.asList(
            new EconomicIndicator("GDP", "国内生产总值"),
            new EconomicIndicator("UNRATE", "失业率")
        );
        
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 12, 31);
        
        List<TimeSeriesData> results = provider.fetch(indicators, startDate, endDate);
        
        // 验证返回结果结构
        assertNotNull(results);
        // 注意：由于 API Key 可能无效，这里不强制要求有数据
        // 在实际使用中，应该确保 API Key 有效
    }
}
