package com.mecn.causal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CausalConfig 单元测试
 */
class CausalConfigTest {

    private CausalConfig config;

    @BeforeEach
    void setUp() {
        config = new CausalConfig();
    }

    @Test
    void testDefaultValues() {
        assertEquals(12, config.getMaxLag());
        assertEquals(0.05, config.getSignificanceLevel(), 0.001);
        assertTrue(config.isParallel());
        assertNotNull(config.getMethodParams());
        assertTrue(config.getMethodParams().isEmpty());
    }

    @Test
    void testSetMaxLag() {
        config.setMaxLag(20);
        assertEquals(20, config.getMaxLag());
    }

    @Test
    void testSetSignificanceLevel() {
        config.setSignificanceLevel(0.01);
        assertEquals(0.01, config.getSignificanceLevel(), 0.001);
    }

    @Test
    void testSetParallel() {
        config.setParallel(false);
        assertFalse(config.isParallel());
    }

    @Test
    void testSetMethodParams() {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("lasso.alpha", 0.1);
        config.setMethodParams(params);
        assertEquals(params, config.getMethodParams());
    }

    @Test
    void testPutMethodParam() {
        config.putMethodParam("lasso", "alpha", 0.1);
        assertEquals(0.1, config.getMethodParam("lasso", "alpha"));
    }

    @Test
    void testGetMethodParamNotFound() {
        assertNull(config.getMethodParam("nonexistent", "key"));
    }
}
