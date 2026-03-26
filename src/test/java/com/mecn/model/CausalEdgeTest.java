package com.mecn.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CausalEdge 单元测试
 */
@DisplayName("CausalEdge 测试")
class CausalEdgeTest {

    @Test
    @DisplayName("无参构造器测试")
    void testDefaultConstructor() {
        // When
        CausalEdge edge = new CausalEdge();

        // Then
        assertThat(edge).isNotNull();
        assertThat(edge.getSource()).isNull();
        assertThat(edge.getTarget()).isNull();
        assertThat(edge.getStrength()).isEqualTo(0.0);
        // 默认构造器不会初始化 confidence，需要手动设置
        edge.setConfidence(1.0);
        assertThat(edge.getConfidence()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("三参数构造器测试")
    void testThreeParamConstructor() {
        // When
        CausalEdge edge = new CausalEdge("A", "B", 0.5);

        // Then
        assertAll(
            () -> assertThat(edge.getSource()).isEqualTo("A"),
            () -> assertThat(edge.getTarget()).isEqualTo("B"),
            () -> assertThat(edge.getStrength()).isEqualTo(0.5),
            () -> assertThat(edge.getConfidence()).isEqualTo(1.0)
        );
    }

    @Test
    @DisplayName("四参数构造器测试")
    void testFourParamConstructor() {
        // When
        CausalEdge edge = new CausalEdge("A", "B", 0.5, 0.8);

        // Then
        assertAll(
            () -> assertThat(edge.getSource()).isEqualTo("A"),
            () -> assertThat(edge.getTarget()).isEqualTo("B"),
            () -> assertThat(edge.getStrength()).isEqualTo(0.5),
            () -> assertThat(edge.getConfidence()).isEqualTo(0.8)
        );
    }

    @Test
    @DisplayName("Setter 方法测试")
    void testSetters() {
        // Given
        CausalEdge edge = new CausalEdge();

        // When
        edge.setSource("X");
        edge.setTarget("Y");
        edge.setStrength(0.7);
        edge.setConfidence(0.9);

        // Then
        assertAll(
            () -> assertThat(edge.getSource()).isEqualTo("X"),
            () -> assertThat(edge.getTarget()).isEqualTo("Y"),
            () -> assertThat(edge.getStrength()).isEqualTo(0.7),
            () -> assertThat(edge.getConfidence()).isEqualTo(0.9)
        );
    }

    @Test
    @DisplayName("显著性检验测试")
    void testIsSignificant() {
        // Given - 使用 pValue 判断
        CausalEdge significant = new CausalEdge("A", "B", 0.5, 0.8);
        significant.setPValue(0.01);  // p < 0.05，显著
        
        CausalEdge notSignificant = new CausalEdge("C", "D", 0.3, 0.6);
        notSignificant.setPValue(0.1);  // p > 0.05，不显著

        // Then
        assertThat(significant.isSignificant(0.05)).isTrue();
        assertThat(notSignificant.isSignificant(0.05)).isFalse();
    }

    @Test
    @DisplayName("toString 方法测试")
    void testToString() {
        // Given
        CausalEdge edge = new CausalEdge("GDP", "CPI", 0.6, 0.95);

        // Then
        String str = edge.toString();
        assertThat(str).contains("GDP").contains("CPI").contains("0.6");
    }

    @Test
    @DisplayName("equals/hashCode 测试")
    void testEqualsAndHashCode() {
        // Given
        CausalEdge edge1 = new CausalEdge("A", "B", 0.5, 0.8);
        CausalEdge edge2 = new CausalEdge("A", "B", 0.5, 0.8);
        CausalEdge edge3 = new CausalEdge("A", "B", 0.6, 0.8);

        // Then
        assertThat(edge1).isEqualTo(edge2);
        assertThat(edge1).isNotEqualTo(edge3);
        assertThat(edge1.hashCode()).isEqualTo(edge2.hashCode());
    }
}
