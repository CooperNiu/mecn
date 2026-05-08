package com.mecn.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EconomicIndicator 单元测试
 */
class EconomicIndicatorTest {

    private EconomicIndicator indicator;

    @BeforeEach
    void setUp() {
        indicator = new EconomicIndicator("GDP", "国内生产总值");
    }

    @Test
    void testDefaultConstructor() {
        EconomicIndicator ind = new EconomicIndicator();
        assertNull(ind.getCode());
        assertNull(ind.getName());
        assertNotNull(ind.getMetadata());
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals("GDP", indicator.getCode());
        assertEquals("国内生产总值", indicator.getName());
        assertNotNull(indicator.getMetadata());
    }

    @Test
    void testSetCode() {
        indicator.setCode("CPI");
        assertEquals("CPI", indicator.getCode());
    }

    @Test
    void testSetName() {
        indicator.setName("消费者价格指数");
        assertEquals("消费者价格指数", indicator.getName());
    }

    @Test
    void testSetSource() {
        indicator.setSource("FRED");
        assertEquals("FRED", indicator.getSource());
    }

    @Test
    void testSetFrequency() {
        indicator.setFrequency(EconomicIndicator.Frequency.QUARTERLY);
        assertEquals(EconomicIndicator.Frequency.QUARTERLY, indicator.getFrequency());
    }

    @Test
    void testSetUnit() {
        indicator.setUnit(EconomicIndicator.Unit.PERCENT);
        assertEquals(EconomicIndicator.Unit.PERCENT, indicator.getUnit());
    }

    @Test
    void testAddMetadata() {
        indicator.addMetadata("country", "US");
        assertEquals("US", indicator.getMetadata().get("country"));
    }

    @Test
    void testSetMetadata() {
        var metadata = new HashMap<String, String>();
        metadata.put("key", "value");
        indicator.setMetadata(metadata);
        assertEquals(metadata, indicator.getMetadata());
    }

    @Test
    void testFrequencyValues() {
        assertEquals(5, EconomicIndicator.Frequency.values().length);
    }

    @Test
    void testUnitValues() {
        assertEquals(5, EconomicIndicator.Unit.values().length);
    }

    @Test
    void testToString() {
        String str = indicator.toString();
        assertNotNull(str);
        assertTrue(str.contains("GDP"));
        assertTrue(str.contains("国内生产总值"));
    }
}
