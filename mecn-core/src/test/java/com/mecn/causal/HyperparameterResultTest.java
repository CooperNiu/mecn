package com.mecn.causal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HyperparameterResult 单元测试
 */
class HyperparameterResultTest {

    private HyperparameterResult result;

    @BeforeEach
    void setUp() {
        result = new HyperparameterResult("LASSO");
    }

    @Test
    void testInitialization() {
        assertEquals("LASSO", result.getMethodName());
        assertEquals("cross_validation", result.getTuningMethod());
    }

    @Test
    void testSetMethodName() {
        result.setMethodName("Granger");
        assertEquals("Granger", result.getMethodName());
    }

    @Test
    void testSetBestLambda() {
        result.setBestLambda(0.01);
        assertEquals(0.01, result.getBestLambda(), 0.001);
    }

    @Test
    void testSetCrossValidationScore() {
        result.setCrossValidationScore(0.85);
        assertEquals(0.85, result.getCrossValidationScore(), 0.001);
    }

    @Test
    void testLambdaCandidates() {
        double[] candidates = {0.001, 0.01, 0.1, 1.0};
        result.setLambdaCandidates(candidates);
        
        assertEquals(4, result.getNumCandidates());
        assertArrayEquals(candidates, result.getLambdaCandidates());
    }

    @Test
    void testGetLambdaCandidatesList() {
        double[] candidates = {0.001, 0.01, 0.1};
        result.setLambdaCandidates(candidates);
        
        List<Double> list = result.getLambdaCandidatesList();
        assertEquals(3, list.size());
        assertEquals(0.001, list.get(0), 0.001);
    }

    @Test
    void testGetNumCandidatesNull() {
        assertEquals(0, result.getNumCandidates());
    }

    @Test
    void testSetCvScores() {
        double[] scores = {0.8, 0.85, 0.9};
        result.setCvScores(scores);
        assertArrayEquals(scores, result.getCvScores());
    }

    @Test
    void testSetKFolds() {
        result.setKFolds(5);
        assertEquals(5, result.getKFolds());
    }

    @Test
    void testSetFoldScores() {
        double[] scores = {0.82, 0.85, 0.88, 0.84, 0.86};
        result.setFoldScores(scores);
        assertArrayEquals(scores, result.getFoldScores());
    }

    @Test
    void testSetAic() {
        result.setAIC(150.5);
        assertEquals(150.5, result.getAIC(), 0.001);
    }

    @Test
    void testSetBic() {
        result.setBIC(160.2);
        assertEquals(160.2, result.getBIC(), 0.001);
    }

    @Test
    void testSetRSquared() {
        result.setRSquared(0.92);
        assertEquals(0.92, result.getRSquared(), 0.001);
    }

    @Test
    void testSetTuningMethod() {
        result.setTuningMethod("grid_search");
        assertEquals("grid_search", result.getTuningMethod());
    }

    @Test
    void testSetExecutionTimeMs() {
        result.setExecutionTimeMs(1500);
        assertEquals(1500, result.getExecutionTimeMs());
    }

    @Test
    void testSetRecommendation() {
        result.setRecommendation("Use lambda=0.01 for best performance");
        assertEquals("Use lambda=0.01 for best performance", result.getRecommendation());
    }

    @Test
    void testToString() {
        result.setBestLambda(0.01);
        result.setCrossValidationScore(0.85);
        
        String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("LASSO"));
        assertTrue(str.contains("0.01"));
    }
}
