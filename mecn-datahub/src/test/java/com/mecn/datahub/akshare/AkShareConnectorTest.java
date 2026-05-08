package com.mecn.datahub.akshare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AkShareConnector 单元测试
 */
class AkShareConnectorTest {

    private AkShareConnector connector;

    @BeforeEach
    void setUp() {
        connector = new AkShareConnector();
    }

    @Test
    void testConstants() {
        assertEquals("CHINA_GDP", AkShareConnector.INDICATOR_GDP);
        assertEquals("CHINA_CPI", AkShareConnector.INDICATOR_CPI);
        assertEquals("CHINA_PMI", AkShareConnector.INDICATOR_PMI);
        assertEquals("CHINA_PPI", AkShareConnector.INDICATOR_PPI);
        assertEquals("CHINA_M2", AkShareConnector.INDICATOR_M2);
        assertEquals("CHINA_SHIBOR", AkShareConnector.INDICATOR_SHIBOR);
        assertEquals("000001", AkShareConnector.INDEX_SSE);
        assertEquals("399001", AkShareConnector.INDEX_SZSE);
        assertEquals("399006", AkShareConnector.INDEX_GEM);
    }

    @Test
    void testClearCache() {
        connector.clearCache();
        // No exception means success
    }

    @Test
    void testGetAllChineseIndicators() {
        Map<String, List<Map<String, Object>>> indicators = connector.getAllChineseIndicators();
        assertNotNull(indicators);
        assertTrue(indicators.containsKey("GDP"));
        assertTrue(indicators.containsKey("CPI"));
        assertTrue(indicators.containsKey("PMI"));
        assertTrue(indicators.containsKey("PPI"));
        assertTrue(indicators.containsKey("M2"));
        assertTrue(indicators.containsKey("SHIBOR"));
    }
}
