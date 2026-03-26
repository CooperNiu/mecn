package com.mecn.causal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * EnsembleFusionStrategy 单元测试
 */
@DisplayName("EnsembleFusionStrategy 测试")
class EnsembleFusionStrategyTest {

    private final EnsembleFusionStrategy fusionStrategy = new EnsembleFusionStrategy();

    @Test
    @DisplayName("投票机制 - 空列表测试")
    void testVoteWithEmptyList() {
        // Given
        List<CausalMatrix> matrices = new ArrayList<>();

        // Then
        assertThatThrownBy(() -> fusionStrategy.vote(matrices, 2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("empty");
    }

    @Test
    @DisplayName("投票机制 - 单矩阵测试")
    void testVoteWithSingleMatrix() {
        // Given
        CausalMatrix matrix1 = createTestMatrix(3, new double[][]{
            {0.0, 0.5, 0.0},
            {0.3, 0.0, 0.0},
            {0.0, 0.4, 0.0}
        });
        List<CausalMatrix> matrices = List.of(matrix1);

        // When
        CausalMatrix result = fusionStrategy.vote(matrices, 1);

        // Then
        assertThat(result.getMatrix()[0][1]).isEqualTo(0.5);
        assertThat(result.getMatrix()[1][0]).isEqualTo(0.3);
        assertThat(result.getMatrix()[2][1]).isEqualTo(0.4);
    }

    @Test
    @DisplayName("投票机制 - 多矩阵投票测试")
    void testVoteWithMultipleMatrices() {
        // Given
        CausalMatrix matrix1 = createTestMatrix(3, new double[][]{
            {0.0, 0.5, 0.0},
            {0.3, 0.0, 0.0},
            {0.0, 0.0, 0.0}
        });
        
        CausalMatrix matrix2 = createTestMatrix(3, new double[][]{
            {0.0, 0.6, 0.0},
            {0.0, 0.0, 0.0},
            {0.0, 0.4, 0.0}
        });
        
        CausalMatrix matrix3 = createTestMatrix(3, new double[][]{
            {0.0, 0.7, 0.0},
            {0.2, 0.0, 0.0},
            {0.0, 0.0, 0.0}
        });

        // When - 需要至少 2 票
        CausalMatrix result = fusionStrategy.vote(List.of(matrix1, matrix2, matrix3), 2);

        // Then - 只有 (0,1) 位置有 3 票，应该保留
        assertThat(result.getMatrix()[0][1]).isNotZero();
        // (1,0) 位置只有 2 票，应该保留
        assertThat(result.getMatrix()[1][0]).isNotZero();
        // (2,1) 位置只有 1 票，应该被过滤
        assertThat(result.getMatrix()[2][1]).isZero();
    }

    @Test
    @DisplayName("加权平均 - 等权重测试")
    void testWeightedAverageWithEqualWeights() {
        // Given
        CausalMatrix matrix1 = createTestMatrix(2, new double[][]{
            {0.0, 0.5},
            {0.3, 0.0}
        });
        
        CausalMatrix matrix2 = createTestMatrix(2, new double[][]{
            {0.0, 0.7},
            {0.1, 0.0}
        });

        // When
        CausalMatrix result = fusionStrategy.weightedAverage(
            List.of(matrix1, matrix2), 
            List.of(1.0, 1.0)
        );

        // Then - 平均值 (0.5+0.7)/2 = 0.6, (0.3+0.1)/2 = 0.2
        assertThat(result.getMatrix()[0][1]).isCloseTo(0.6, within(0.001));
        assertThat(result.getMatrix()[1][0]).isCloseTo(0.2, within(0.001));
    }

    @Test
    @DisplayName("加权平均 - 不同权重测试")
    void testWeightedAverageWithDifferentWeights() {
        // Given
        CausalMatrix matrix1 = createTestMatrix(2, new double[][]{
            {0.0, 0.5},
            {0.0, 0.0}
        });
        
        CausalMatrix matrix2 = createTestMatrix(2, new double[][]{
            {0.0, 0.9},
            {0.0, 0.0}
        });

        // When - matrix2 权重更高
        CausalMatrix result = fusionStrategy.weightedAverage(
            List.of(matrix1, matrix2), 
            List.of(1.0, 3.0)
        );

        // Then - 加权平均 (0.5*1 + 0.9*3)/(1+3) = 3.2/4 = 0.8
        assertThat(result.getMatrix()[0][1]).isCloseTo(0.8, within(0.001));
    }

    @Test
    @DisplayName("混合策略测试")
    void testHybridStrategy() {
        // Given
        CausalMatrix matrix1 = createTestMatrix(2, new double[][]{
            {0.0, 0.5},
            {0.3, 0.0}
        });
        
        CausalMatrix matrix2 = createTestMatrix(2, new double[][]{
            {0.0, 0.7},
            {0.0, 0.0}
        });

        // When - 需要至少 1 票
        CausalMatrix result = fusionStrategy.hybrid(
            List.of(matrix1, matrix2),
            List.of(1.0, 1.0),
            1
        );

        // Then
        assertThat(result.getMatrix()[0][1]).isNotZero();
        assertThat(result.getMetadata()).containsKey("fusionMethod");
        assertThat(result.getMetadata().get("fusionMethod")).isEqualTo("hybrid_vote_then_weight");
    }

    @Test
    @DisplayName("元数据验证测试")
    void testMetadataValidation() {
        // Given
        CausalMatrix matrix = createTestMatrix(2, new double[][]{
            {0.0, 0.5},
            {0.0, 0.0}
        });

        // When
        CausalMatrix voteResult = fusionStrategy.vote(List.of(matrix), 1);
        CausalMatrix weightedResult = fusionStrategy.weightedAverage(
            List.of(matrix), 
            List.of(1.0)
        );

        // Then
        assertAll(
            () -> assertThat(voteResult.getMetadata()).containsKey("votingMethod"),
            () -> assertThat(weightedResult.getMetadata()).containsKey("fusionMethod")
        );
    }

    /**
     * 辅助方法：创建测试矩阵
     */
    private CausalMatrix createTestMatrix(int size, double[][] values) {
        CausalMatrix matrix = new CausalMatrix(size, "TEST");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (values[i][j] != 0) {
                    matrix.setCausalEffect(j, i, values[i][j]);
                }
            }
        }
        return matrix;
    }
}
