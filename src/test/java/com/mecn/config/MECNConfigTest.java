package com.mecn.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MECNConfig 单元测试
 * 
 * 测试全局配置管理器的功能
 */
class MECNConfigTest {
    
    private MECNConfig config;
    
    @BeforeEach
    void setUp() {
        config = MECNConfig.getInstance();
        // 每次测试前重置为默认配置
        config.resetToDefaults();
    }
    
    @Test
    void testGetInstance_ReturnsSameInstance() {
        // Given & When
        MECNConfig instance1 = MECNConfig.getInstance();
        MECNConfig instance2 = MECNConfig.getInstance();
        
        // Then
        assertThat(instance1).isSameAs(instance2);
    }
    
    @Test
    void testGetDefaultCausalEdgeThreshold() {
        // When
        Double threshold = config.getDouble("causal.edgeThreshold", 0.0);
        
        // Then
        assertThat(threshold).isEqualTo(0.08);
    }
    
    @Test
    void testGetDefaultMaxLag() {
        // When
        int maxLag = config.getInt("causal.maxLag", 0);
        
        // Then
        assertThat(maxLag).isEqualTo(4);
    }
    
    @Test
    void testSetAndGetCustomValue() {
        // Given
        String key = "test.custom.key";
        String value = "test_value";
        
        // When
        config.set(key, value);
        String retrieved = config.getString(key, null);
        
        // Then
        assertThat(retrieved).isEqualTo(value);
    }
    
    @Test
    void testGetWithDefaultValue() {
        // When
        String value = config.getString("non.existent.key", "default");
        
        // Then
        assertThat(value).isEqualTo("default");
    }
    
    @Test
    void testGetIntConversion() {
        // Given
        config.set("test.int", 42);
        
        // When
        int value = config.getInt("test.int", 0);
        
        // Then
        assertThat(value).isEqualTo(42);
    }
    
    @Test
    void testGetDoubleConversion() {
        // Given
        config.set("test.double", 3.14);
        
        // When
        double value = config.getDouble("test.double", 0.0);
        
        // Then
        assertThat(value).isEqualTo(3.14);
    }
    
    @Test
    void testGetBooleanConversion() {
        // Given
        config.set("test.boolean", true);
        
        // When
        boolean value = config.getBoolean("test.boolean", false);
        
        // Then
        assertThat(value).isTrue();
    }
    
    @Test
    void testResetToDefaults() {
        // Given
        config.set("causal.edgeThreshold", 0.5);
        
        // When
        config.resetToDefaults();
        Double threshold = config.getDouble("causal.edgeThreshold", 0.0);
        
        // Then
        assertThat(threshold).isEqualTo(0.08);
    }
    
    @Test
    void testGetAllConfig() {
        // When
        var allConfig = config.getAllConfig();
        
        // Then
        assertThat(allConfig).isNotEmpty();
        assertThat(allConfig).containsKey("causal.edgeThreshold");
        assertThat(allConfig).containsKey("network.minEdgeWeight");
    }
    
    @Test
    void testLoadFromMap() {
        // Given
        java.util.Map<String, Object> customConfig = new java.util.HashMap<>();
        customConfig.put("custom.key1", "value1");
        customConfig.put("custom.key2", 123);
        
        // When
        config.loadFromMap(customConfig);
        
        // Then
        assertThat(config.getString("custom.key1", null)).isEqualTo("value1");
        assertThat(config.getInt("custom.key2", 0)).isEqualTo(123);
    }
    
    @Test
    void testPerformanceThreadPoolSize() {
        // When
        int threadPoolSize = config.getInt("performance.threadPoolSize", 1);
        
        // Then
        assertThat(threadPoolSize).isGreaterThan(0);
        assertThat(threadPoolSize).isEqualTo(Runtime.getRuntime().availableProcessors());
    }
}
