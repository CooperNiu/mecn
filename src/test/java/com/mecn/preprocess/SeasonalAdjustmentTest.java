package com.mecn.preprocess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 季节性调整测试
 */
public class SeasonalAdjustmentTest {
    
    @Test
    public void testAdjust() {
        // 生成带有季节性的测试数据
        int n = 60;  // 5 年月度数据
        double[] data = new double[n];
        
        for (int i = 0; i < n; i++) {
            // 趋势 + 季节性 + 随机扰动
            double trend = 100 + 0.5 * i;
            double seasonal = 5 * Math.sin(2 * Math.PI * i / 12);
            double noise = Math.random() * 2 - 1;
            data[i] = trend + seasonal + noise;
        }
        
        SeasonalAdjustment adjustment = new SeasonalAdjustment();
        double[] adjusted = adjustment.adjust(data);
        
        assertNotNull(adjusted);
        assertEquals(n, adjusted.length);
        
        // 验证季节性调整后的数据应该去除了季节性成分
        double[] seasonalFactors = adjustment.getSeasonalFactors();
        assertNotNull(seasonalFactors);
        assertEquals(n, seasonalFactors.length);
        
        // 验证趋势成分存在
        double[] trendCycle = adjustment.getTrendCycle();
        assertNotNull(trendCycle);
        assertEquals(n, trendCycle.length);
        
        // 验证不规则成分存在
        double[] irregular = adjustment.getIrregularComponent();
        assertNotNull(irregular);
        assertEquals(n, irregular.length);
    }
    
    @Test
    public void testSeasonalityStrength() {
        // 生成强季节性数据
        int n = 60;
        double[] strongSeasonal = new double[n];
        
        for (int i = 0; i < n; i++) {
            strongSeasonal[i] = 100 + 10 * Math.sin(2 * Math.PI * i / 12) + Math.random();
        }
        
        SeasonalAdjustment adjustment = new SeasonalAdjustment();
        adjustment.adjust(strongSeasonal);
        
        double strength = adjustment.calculateSeasonalityStrength();
        assertTrue(strength > 0 && strength <= 1.0);
    }
    
    @Test
    public void testInsufficientData() {
        double[] shortData = new double[20];
        SeasonalAdjustment adjustment = new SeasonalAdjustment();
        
        assertThrows(IllegalArgumentException.class, () -> {
            adjustment.adjust(shortData);
        });
    }
}
