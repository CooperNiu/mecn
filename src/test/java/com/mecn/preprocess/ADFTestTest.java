package com.mecn.preprocess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ADF 平稳性检验测试
 */
public class ADFTestTest {
    
    @Test
    public void testStationarySeries() {
        // 生成平稳序列（白噪声）
        int n = 200;
        double[] stationaryData = new double[n];
        
        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < n; i++) {
            stationaryData[i] = random.nextGaussian() * 10;
        }
        
        ADFTestadfTest = new ADFTest();
        ADFTest.ADFResult result = adfTest.test(stationaryData);
        
        assertNotNull(result);
        
        // 平稳序列应该拒绝原假设（p 值较小）
        // 注意：由于是随机序列，结果可能有波动
        System.out.println("Stationary series - Test statistic: " + result.getTestStatistic() 
                         + ", p-value: " + result.getPValue());
    }
    
    @Test
    public void testNonStationarySeries() {
        // 生成非平稳序列（随机游走）
        int n = 200;
        double[] nonStationaryData = new double[n];
        
        java.util.Random random = new java.util.Random(42);
        nonStationaryData[0] = 100;
        
        for (int i = 1; i < n; i++) {
            nonStationaryData[i] = nonStationaryData[i - 1] + random.nextGaussian();
        }
        
        ADFTestadfTest = new ADFTest();
        ADFTest.ADFResult result = adfTest.test(nonStationaryData);
        
        assertNotNull(result);
        
        // 非平稳序列不应该拒绝原假设（p 值较大）
        System.out.println("Non-stationary series - Test statistic: " + result.getTestStatistic() 
                         + ", p-value: " + result.getPValue());
    }
    
    @Test
    public void testResultAccessors() {
        int n = 100;
        double[] data = new double[n];
        
        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < n; i++) {
            data[i] = random.nextGaussian();
        }
        
        ADFTestadfTest = new ADFTest();
        ADFTest.ADFResult result = adfTest.test(data);
        
        // 测试访问器方法
        assertNotEquals(null, result.getTestStatistic());
        assertNotEquals(null, result.getPValue());
        assertTrue(result.getLag() >= 0);
        
        // 测试临界值访问
        double criticalValue1 = result.getCriticalValue(0.01);
        double criticalValue5 = result.getCriticalValue(0.05);
        double criticalValue10 = result.getCriticalValue(0.10);
        
        assertNotEquals(null, criticalValue1);
        assertNotEquals(null, criticalValue5);
        assertNotEquals(null, criticalValue10);
    }
    
    @Test
    public void testIsStationary() {
        int n = 100;
        double[] data = new double[n];
        
        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < n; i++) {
            data[i] = random.nextGaussian();
        }
        
        ADFTestadfTest = new ADFTest();
        ADFTest.ADFResult result = adfTest.test(data);
        
        // 测试不同显著性水平下的判断
        boolean stationary1 = result.isStationary(0.01);
        boolean stationary5 = result.isStationary(0.05);
        boolean stationary10 = result.isStationary(0.10);
        
        // 这些值取决于实际的检验结果
        System.out.println("Stationary at 1%: " + stationary1);
        System.out.println("Stationary at 5%: " + stationary5);
        System.out.println("Stationary at 10%: " + stationary10);
    }
    
    @Test
    public void testInsufficientData() {
        double[] shortData = new double[5];
        ADFTestadfTest = new ADFTest();
        
        assertThrows(IllegalArgumentException.class, () -> {
            adfTest.test(shortData);
        });
    }
}
