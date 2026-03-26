package com.mecn.causal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * CausalEngineImpl 单元测试
 */
@DisplayName("CausalEngineImpl 测试")
class CausalEngineImplTest {

    @Test
    @DisplayName("构造器初始化测试")
    void testConstructor() {
        // When
        CausalEngineImpl engine = new CausalEngineImpl();

        // Then
        assertThat(engine).isNotNull();
        assertThat(engine.getRegisteredMethods()).isEmpty();
    }

    @Test
    @DisplayName("注册方法测试")
    void testRegisterMethod() {
        // Given
        CausalEngineImpl engine = new CausalEngineImpl();
        CausalMethod method = new TestCausalMethod("TEST");

        // When
        engine.registerMethod(method);

        // Then
        assertThat(engine.getRegisteredMethods()).hasSize(1);
        assertThat(engine.getRegisteredMethods().get(0).getName()).isEqualTo("TEST");
    }

    @Test
    @DisplayName("注销方法测试")
    void testUnregisterMethod() {
        // Given
        CausalEngineImpl engine = new CausalEngineImpl();
        engine.registerMethod(new TestCausalMethod("TEST1"));
        engine.registerMethod(new TestCausalMethod("TEST2"));

        // When
        engine.unregisterMethod("TEST1");

        // Then
        assertThat(engine.getRegisteredMethods()).hasSize(1);
        assertThat(engine.getRegisteredMethods().get(0).getName()).isEqualTo("TEST2");
    }

    @Test
    @DisplayName("无方法时执行发现测试")
    void testDiscoverWithNoMethods() {
        // Given
        CausalEngineImpl engine = new CausalEngineImpl();
        double[][] data = new double[][]{{1.0, 2.0}, {3.0, 4.0}};
        CausalConfig config = new CausalConfig();

        // Then
        assertThatThrownBy(() -> engine.discover(data, config))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No causal methods registered");
    }

    @Test
    @DisplayName("单方法因果发现测试")
    void testDiscoverWithSingleMethod() {
        // Given
        CausalEngineImpl engine = new CausalEngineImpl(false); // 串行执行
        engine.registerMethod(new TestCausalMethod("TEST"));
        
        double[][] data = new double[][]{
            {1.0, 2.0, 3.0},
            {2.0, 3.0, 4.0},
            {3.0, 4.0, 5.0}
        };
        CausalConfig config = new CausalConfig();

        // When
        CausalResult result = engine.discover(data, config);

        // Then
        assertAll(
            () -> assertThat(result).isNotNull(),
            () -> assertThat(result.getAdjacencyMatrix()).isNotNull(),
            () -> assertThat(result.getMetadata()).containsKey("numMethods"),
            () -> assertThat(result.getMetadata().get("numMethods")).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("多方法集成测试")
    void testDiscoverWithMultipleMethods() {
        // Given
        CausalEngineImpl engine = new CausalEngineImpl(false);
        engine.registerMethod(new TestCausalMethod("TEST1"));
        engine.registerMethod(new TestCausalMethod("TEST2"));
        
        double[][] data = new double[][]{
            {1.0, 2.0},
            {2.0, 3.0},
            {3.0, 4.0}
        };
        CausalConfig config = new CausalConfig();

        // When
        CausalResult result = engine.discover(data, config);

        // Then
        assertAll(
            () -> assertThat(result.getMetadata().get("numMethods")).isEqualTo(2),
            () -> assertThat(result.getMetadata().get("fusionStrategy")).isEqualTo("hybrid")
        );
    }

    @Test
    @DisplayName("并行执行测试")
    void testParallelExecution() {
        // Given
        CausalEngineImpl engine = new CausalEngineImpl(true);
        engine.registerMethod(new TestCausalMethod("PARALLEL_TEST"));
        
        double[][] data = new double[][]{
            {1.0, 2.0, 3.0},
            {2.0, 3.0, 4.0}
        };
        CausalConfig config = new CausalConfig();
        config.setParallel(true);

        // When
        CausalResult result = engine.discover(data, config);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("配置参数传递测试")
    void testConfigParameterPassing() {
        // Given
        CausalEngineImpl engine = new CausalEngineImpl(false);
        TestCausalMethod method = new TestCausalMethod("CONFIG_TEST");
        engine.registerMethod(method);
        
        double[][] data = new double[][]{{1.0, 2.0}, {3.0, 4.0}};
        CausalConfig config = new CausalConfig();
        config.setMaxLag(5);
        config.setSignificanceLevel(0.01);
        config.putMethodParam("TEST", "param1", "value1");

        // When
        CausalResult result = engine.discover(data, config);

        // Then
        assertThat(result).isNotNull();
    }

    /**
     * 测试用的因果方法实现
     */
    private static class TestCausalMethod extends CausalMethod {
        public TestCausalMethod(String name) {
            super(name);
        }

        @Override
        public CausalMatrix compute(double[][] data, java.util.Map<String, Object> params) {
            int N = data[0].length;
            CausalMatrix matrix = new CausalMatrix(N, getName());
            
            // 简单的测试逻辑：设置一些非零值
            if (N >= 2) {
                matrix.setCausalEffect(0, 1, 0.5);
            }
            
            return matrix;
        }

        @Override
        public double getConfidence(double[][] data, int source, int target) {
            return 0.8;
        }
    }
}
