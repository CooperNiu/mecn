package com.mecn.network;

import com.mecn.causal.CausalResult;
import com.mecn.model.EconomicIndicator;
import com.mecn.model.NetworkGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * NetworkBuilder 单元测试
 */
@DisplayName("NetworkBuilder 测试")
class NetworkBuilderTest {

    @Test
    @DisplayName("默认构造器测试")
    void testDefaultConstructor() {
        // When
        NetworkBuilder builder = new NetworkBuilder();

        // Then
        assertThat(builder.getEdgeThreshold()).isEqualTo(0.08);
    }

    @Test
    @DisplayName("指定阈值构造器测试")
    void testConstructorWithThreshold() {
        // When
        NetworkBuilder builder = new NetworkBuilder(0.1);

        // Then
        assertThat(builder.getEdgeThreshold()).isEqualTo(0.1);
    }

    @Test
    @DisplayName("从因果结果构建网络测试")
    void testBuildFromCausalResult() {
        // Given
        NetworkBuilder builder = new NetworkBuilder(0.05);
        
        CausalResult causalResult = new CausalResult(3);
        double[][] adjacencyMatrix = {
            {0.0, 0.1, 0.0},
            {0.2, 0.0, 0.0},
            {0.0, 0.3, 0.0}
        };
        causalResult.setAdjacencyMatrix(adjacencyMatrix);
        
        List<String> nodeNames = Arrays.asList("A", "B", "C");

        // When
        NetworkGraph network = builder.build(causalResult, nodeNames);

        // Then
        assertAll(
            () -> assertThat(network.getNodes()).hasSize(3),
            () -> assertThat(network.getEdges()).hasSize(3),
            () -> assertThat(network.getEdgeWeight("A", "B")).isEqualTo(0.1),
            () -> assertThat(network.getEdgeWeight("B", "A")).isEqualTo(0.2)
        );
    }

    @Test
    @DisplayName("边阈值过滤测试")
    void testEdgeThresholdFiltering() {
        // Given
        NetworkBuilder builder = new NetworkBuilder(0.15);
        
        CausalResult causalResult = new CausalResult(2);
        double[][] adjacencyMatrix = {
            {0.0, 0.1},
            {0.2, 0.0}
        };
        causalResult.setAdjacencyMatrix(adjacencyMatrix);
        
        List<String> nodeNames = Arrays.asList("X", "Y");

        // When
        NetworkGraph network = builder.build(causalResult, nodeNames);

        // Then - 只有 0.2 的边被保留（>=0.15），方向是 Y->X
        assertThat(network.getEdges()).hasSize(1);
        assertThat(network.getEdgeWeight("Y", "X")).isEqualTo(0.2);
    }

    @Test
    @DisplayName("从邻接矩阵直接构建网络测试")
    void testBuildFromAdjacencyMatrix() {
        // Given
        NetworkBuilder builder = new NetworkBuilder(0.0);
        
        double[][] adjacencyMatrix = {
            {0.0, 0.5, 0.0},
            {0.3, 0.0, 0.4},
            {0.0, 0.0, 0.0}
        };
        
        List<String> nodeNames = Arrays.asList("N1", "N2", "N3");

        // When
        NetworkGraph network = builder.build(adjacencyMatrix, nodeNames);

        // Then - 验证节点和非自环边
        assertAll(
            () -> assertThat(network.getNodes()).hasSize(3),
            () -> {
                // 矩阵中有 6 条非零边：N1->N2(0.5), N2->N1(0.3), N2->N3(0.4)，以及 3 个自环
                int nonSelfLoopEdges = 0;
                var graph = network.getGraph();
                for (var edge : network.getEdges()) {
                    String source = graph.getEdgeSource(edge);
                    String target = graph.getEdgeTarget(edge);
                    if (!source.equals(target)) {
                        nonSelfLoopEdges++;
                    }
                }
                assertThat(nonSelfLoopEdges).isEqualTo(6);
            }
        );
    }

    @Test
    @DisplayName("过滤弱连接测试")
    void testPruneWeakEdges() {
        // Given
        NetworkBuilder builder = new NetworkBuilder(0.0);
        NetworkGraph graph = new NetworkGraph();
        
        graph.addNode("A", new EconomicIndicator("A", "A"));
        graph.addNode("B", new EconomicIndicator("B", "B"));
        graph.addEdge("A", "B", 0.05);
        graph.addEdge("B", "A", 0.15);

        // When
        builder.pruneWeakEdges(graph.getGraph(), 0.1);

        // Then - 只有权重>=0.1 的边被保留
        assertThat(graph.getEdges()).hasSize(1);
        assertThat(graph.getEdgeWeight("B", "A")).isEqualTo(0.15);
    }

    @Test
    @DisplayName("设置边阈值测试")
    void testSetEdgeThreshold() {
        // Given
        NetworkBuilder builder = new NetworkBuilder();

        // When
        builder.setEdgeThreshold(0.2);

        // Then
        assertThat(builder.getEdgeThreshold()).isEqualTo(0.2);
    }

    @Test
    @DisplayName("网络元数据验证测试")
    void testNetworkMetadataValidation() {
        // Given
        NetworkBuilder builder = new NetworkBuilder(0.1);
        
        CausalResult causalResult = new CausalResult(2);
        causalResult.setAdjacencyMatrix(new double[][]{
            {0.0, 0.2},
            {0.3, 0.0}
        });
        
        List<String> nodeNames = Arrays.asList("M1", "M2");

        // When
        NetworkGraph network = builder.build(causalResult, nodeNames);

        // Then
        assertThat(network.getMetadata())
            .containsEntry("edgeThreshold", 0.1)
            .containsKey("numNodes")
            .containsKey("numEdges");
    }
}
