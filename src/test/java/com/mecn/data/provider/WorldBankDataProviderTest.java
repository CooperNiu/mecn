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
 * World Bank 数据提供者测试
 * 
 * 使用 TDD 方式编写，先写测试再实现功能
 */
@DisplayName("World Bank 数据提供者测试")
class WorldBankDataProviderTest {
    
    private WorldBankDataProvider provider;
    
    @BeforeEach
    void setUp() {
        provider = new WorldBankDataProvider();
    }
    
    @Test
    @DisplayName("应该正确初始化数据提供者")
    void shouldInitializeProvider() {
        assertNotNull(provider);
        assertEquals("WorldBank", provider.getName());
        assertTrue(provider.getSupportedIndicators().size() > 0);
    }
    
    @Test
    @DisplayName("应该包含世界银行经济指标")
    void shouldContainWorldBankIndicators() {
        List<EconomicIndicator> indicators = provider.getSupportedIndicators();
        
        // 验证包含 GDP 相关指标
        assertTrue(indicators.stream()
            .anyMatch(i -> i.getCode().equals("NY.GDP.MKTP.CD")));
        
        // 验证包含人口指标
        assertTrue(indicators.stream()
            .anyMatch(i -> i.getCode().equals("SP.POP.TOTL")));
        
        // 验证包含通胀指标
        assertTrue(indicators.stream()
            .anyMatch(i -> i.getCode().equals("FP.CPI.TOTL.ZG")));
        
        // 验证包含贫困指标
        assertTrue(indicators.stream()
            .anyMatch(i -> i.getCode().equals("SI.POV.DDAY")));
    }
    
    @Test
    @DisplayName("解析 World Bank JSON 响应 - 正常情况")
    void shouldParseWorldBankJsonResponseSuccessfully() {
        // 准备测试用的 JSON 响应（模拟 World Bank API 返回格式）
        String jsonResponse = """
            [
              {
                "page": 1,
                "pages": 1,
                "per_page": 50,
                "total": 3
              },
              [
                {
                  "indicator": {"id": "NY.GDP.MKTP.CD", "value": "GDP"},
                  "country": {"id": "US", "value": "United States"},
                  "date": "2020",
                  "value": 20893746.58
                },
                {
                  "indicator": {"id": "NY.GDP.MKTP.CD", "value": "GDP"},
                  "country": {"id": "US", "value": "United States"},
                  "date": "2019",
                  "value": 21427700.00
                },
                {
                  "indicator": {"id": "NY.GDP.MKTP.CD", "value": "GDP"},
                  "country": {"id": "US", "value": "United States"},
                  "date": "2018",
                  "value": 20544343.00
                }
              ]
            ]
            """;
        
        try {
            var method = WorldBankDataProvider.class.getDeclaredMethod(
                "parseWorldBankResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, jsonResponse, "NY.GDP.MKTP.CD");
            
            assertNotNull(result);
            assertEquals("NY.GDP.MKTP.CD", result.getIndicatorCode());
            assertEquals(3, result.size());
            
            // 验证数值
            assertEquals(20893746.58, result.getValueAt(0), 0.01);
            assertEquals(21427700.00, result.getValueAt(1), 0.01);
            assertEquals(20544343.00, result.getValueAt(2), 0.01);
            
            // 验证日期（World Bank 返回年份数据）
            LocalDate[] dates = result.getDates();
            assertEquals(LocalDate.of(2020, 1, 1), dates[0]);
            assertEquals(LocalDate.of(2019, 1, 1), dates[1]);
            assertEquals(LocalDate.of(2018, 1, 1), dates[2]);
            
        } catch (Exception e) {
            fail("解析 JSON 响应时发生异常：" + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("解析 World Bank JSON 响应 - 处理缺失值")
    void shouldHandleMissingValuesInWorldBankResponse() {
        String jsonResponse = """
            [
              {
                "page": 1,
                "pages": 1,
                "per_page": 50,
                "total": 3
              },
              [
                {
                  "indicator": {"id": "NY.GDP.MKTP.CD", "value": "GDP"},
                  "country": {"id": "US", "value": "United States"},
                  "date": "2020",
                  "value": 100.5
                },
                {
                  "indicator": {"id": "NY.GDP.MKTP.CD", "value": "GDP"},
                  "country": {"id": "US", "value": "United States"},
                  "date": "2019",
                  "value": null
                },
                {
                  "indicator": {"id": "NY.GDP.MKTP.CD", "value": "GDP"},
                  "country": {"id": "US", "value": "United States"},
                  "date": "2018",
                  "value": 98.2
                }
              ]
            ]
            """;
        
        try {
            var method = WorldBankDataProvider.class.getDeclaredMethod(
                "parseWorldBankResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, jsonResponse, "NY.GDP.MKTP.CD");
            
            assertNotNull(result);
            // 应该跳过 null 值，只返回 2 个有效数据
            assertEquals(2, result.size());
            assertEquals(100.5, result.getValueAt(0), 0.01);
            assertEquals(98.2, result.getValueAt(1), 0.01);
            
        } catch (Exception e) {
            fail("处理缺失值时发生异常：" + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("解析 World Bank JSON 响应 - 空数据数组")
    void shouldHandleEmptyDataArray() {
        String jsonResponse = """
            [
              {
                "page": 1,
                "pages": 1,
                "per_page": 50,
                "total": 0
              },
              []
            ]
            """;
        
        try {
            var method = WorldBankDataProvider.class.getDeclaredMethod(
                "parseWorldBankResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, jsonResponse, "TEST_EMPTY");
            
            assertNotNull(result);
            assertEquals(0, result.size());
            assertEquals("TEST_EMPTY", result.getIndicatorCode());
            
        } catch (Exception e) {
            fail("处理空数据数组时发生异常：" + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("解析 World Bank JSON 响应 - 数据结构不完整")
    void shouldHandleIncompleteDataStructure() {
        // 只有 metadata，没有 data
        String jsonResponse = """
            [
              {
                "page": 1,
                "pages": 1,
                "per_page": 50,
                "total": 0
              }
            ]
            """;
        
        try {
            var method = WorldBankDataProvider.class.getDeclaredMethod(
                "parseWorldBankResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, jsonResponse, "TEST_INCOMPLETE");
            
            assertNotNull(result);
            assertEquals(0, result.size());
            
        } catch (Exception e) {
            fail("处理不完整数据结构时发生异常：" + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("解析 World Bank JSON 响应 - 无效 JSON 格式")
    void shouldHandleInvalidJsonFormat() {
        String invalidJson = "not a valid json";
        
        try {
            var method = WorldBankDataProvider.class.getDeclaredMethod(
                "parseWorldBankResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, invalidJson, "TEST_INVALID");
            
            assertNotNull(result);
            assertEquals(0, result.size());
            
        } catch (Exception e) {
            fail("处理无效 JSON 时应该返回空数据而不是抛出异常");
        }
    }
    
    @Test
    @DisplayName("解析 World Bank JSON 响应 - 字符串类型的值")
    void shouldHandleStringValueInResponse() {
        String jsonResponse = """
            [
              {
                "page": 1,
                "pages": 1,
                "per_page": 50,
                "total": 2
              },
              [
                {
                  "indicator": {"id": "NY.GDP.MKTP.CD", "value": "GDP"},
                  "country": {"id": "US", "value": "United States"},
                  "date": "2020",
                  "value": "123.45"
                },
                {
                  "indicator": {"id": "NY.GDP.MKTP.CD", "value": "GDP"},
                  "country": {"id": "US", "value": "United States"},
                  "date": "2019",
                  "value": "67.89"
                }
              ]
            ]
            """;
        
        try {
            var method = WorldBankDataProvider.class.getDeclaredMethod(
                "parseWorldBankResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, jsonResponse, "NY.GDP.MKTP.CD");
            
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(123.45, result.getValueAt(0), 0.01);
            assertEquals(67.89, result.getValueAt(1), 0.01);
            
        } catch (Exception e) {
            fail("处理字符串类型的值时发生异常：" + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("解析 World Bank JSON 响应 - 负数值")
    void shouldHandleNegativeValues() {
        String jsonResponse = """
            [
              {
                "page": 1,
                "pages": 1,
                "per_page": 50,
                "total": 2
              },
              [
                {
                  "indicator": {"id": "GC.BAL.CASH.GD.ZS", "value": "Cash surplus/deficit"},
                  "country": {"id": "US", "value": "United States"},
                  "date": "2020",
                  "value": -15.5
                },
                {
                  "indicator": {"id": "GC.BAL.CASH.GD.ZS", "value": "Cash surplus/deficit"},
                  "country": {"id": "US", "value": "United States"},
                  "date": "2019",
                  "value": -12.3
                }
              ]
            ]
            """;
        
        try {
            var method = WorldBankDataProvider.class.getDeclaredMethod(
                "parseWorldBankResponse", String.class, String.class);
            method.setAccessible(true);
            
            TimeSeriesData result = (TimeSeriesData) method.invoke(provider, jsonResponse, "GC.BAL.CASH.GD.ZS");
            
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(-15.5, result.getValueAt(0), 0.01);
            assertEquals(-12.3, result.getValueAt(1), 0.01);
            
        } catch (Exception e) {
            fail("处理负数值时发生异常：" + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("获取数据 - 默认获取全世界数据")
    void shouldFetchDataWithDefaultCountry() {
        List<EconomicIndicator> indicators = Arrays.asList(
            new EconomicIndicator("NY.GDP.MKTP.CD", "GDP"),
            new EconomicIndicator("SP.POP.TOTL", "Population")
        );
        
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 12, 31);
        
        // 注意：这个测试会实际调用 API，在没有网络或 API 变更时可能失败
        // 在实际 CI/CD 中应该使用 Mock 或跳过
        try {
            List<TimeSeriesData> results = provider.fetch(indicators, startDate, endDate);
            
            assertNotNull(results);
            // 不强制要求有数据，因为 API 可能不可用
        } catch (Exception e) {
            // API 调用失败是可以接受的，不影响核心功能测试
        }
    }
    
    @Test
    @DisplayName("获取数据 - 指定国家代码")
    void shouldFetchDataWithSpecificCountry() {
        List<EconomicIndicator> indicators = Arrays.asList(
            new EconomicIndicator("NY.GDP.MKTP.CD", "GDP")
        );
        
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 12, 31);
        String countryCode = "CHN";
        
        try {
            List<TimeSeriesData> results = provider.fetch(indicators, startDate, endDate, countryCode);
            
            assertNotNull(results);
        } catch (Exception e) {
            // API 调用失败是可以接受的
        }
    }
}
