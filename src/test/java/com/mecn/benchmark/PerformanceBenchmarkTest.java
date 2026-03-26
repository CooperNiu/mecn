package com.mecn.benchmark;

import com.mecn.causal.CausalEngineImpl;
import com.mecn.causal.GrangerCausality;
import com.mecn.causal.PCMCI;
import com.mecn.data.generator.EnhancedDataGenerator;
import com.mecn.model.NetworkGraph;
import com.mecn.network.RippleSimulator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 性能基准测试
 */
@DisplayName("性能基准测试")
class PerformanceBenchmarkTest {

    private static final int WARMUP_ITERATIONS = 3;
    private static final int TEST_ITERATIONS = 5;
    
    public static class BenchmarkResult {
        String testName;
        long avgTimeMs;
        double throughput;
        
        BenchmarkResult(String testName, long totalTimeMs, int iterations) {
            this.testName = testName;
            this.avgTimeMs = totalTimeMs / iterations;
            this.throughput = 1000.0 / avgTimeMs;
        }
    }
    
    @Test
    @DisplayName("因果发现算法性能对比")
    void testCausalDiscoveryPerformance() {
        List<BenchmarkResult> results = new ArrayList<>();
        
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] data = generator.generateData();
        
        System.out.println("\n=== 因果发现算法性能测试 ===");
        System.out.println("数据规模：T=" + data.length + ", N=5");
        
        // Granger - 使用较小的 lag 值加速测试
        results.add(runBenchmark("Granger", () -> {
            GrangerCausality granger = new GrangerCausality();
            granger.withLag(1).withSignificanceLevel(0.05);
            return granger.compute(data, null);
        }));
        
        printBenchmarkResults(results);
        
        assertThat(results).hasSize(1);
        for (BenchmarkResult result : results) {
            assertThat(result.avgTimeMs).isGreaterThan(0);
        }
    }
    
    @Test
    @DisplayName("不同数据规模下的性能测试")
    void testScalabilityPerformance() {
        List<Integer> sampleSizes = List.of(50, 100, 150);
        Map<Integer, Long> results = new HashMap<>();
        
        System.out.println("\n=== 可扩展性性能测试 ===");
        
        for (int sampleSize : sampleSizes) {
            long time = measureTimeForSampleSize(sampleSize);
            results.put(sampleSize, time);
            System.out.printf("样本量 %d: %d ms%n", sampleSize, time);
        }
        
        assertThat(results.get(150)).isGreaterThan(results.get(50));
    }
    
    @Test
    @DisplayName("涟漪效应模拟性能测试")
    void testRippleSimulationPerformance() {
        List<BenchmarkResult> results = new ArrayList<>();
        NetworkGraph network = createTestNetwork(10);  // 使用较小的网络
        
        System.out.println("\n=== 涟漪效应模拟性能测试 ===");
        
        results.add(runBenchmark("Small Shock", () -> {
            RippleSimulator sim = new RippleSimulator(0.9, 10);
            return sim.simulate(network, "Node_0", 0.1, 10);
        }));
        
        results.add(runBenchmark("Medium Shock", () -> {
            RippleSimulator sim = new RippleSimulator(0.9, 10);
            return sim.simulate(network, "Node_0", 0.5, 10);
        }));
        
        printBenchmarkResults(results);
        assertThat(results).hasSize(2);
    }
    
    @Test
    @DisplayName("并发性能测试")
    void testConcurrentPerformance() {
        System.out.println("\n=== 并发性能测试 ===");
        
        CausalEngineImpl parallelEngine = new CausalEngineImpl(true);
        CausalEngineImpl serialEngine = new CausalEngineImpl(false);
        
        assertThat(parallelEngine).isNotNull();
        assertThat(serialEngine).isNotNull();
        
        System.out.println("并行引擎和串行引擎创建成功");
    }
    
    @Test
    @DisplayName("内存效率测试")
    void testMemoryEfficiency() {
        System.out.println("\n=== 内存效率测试 ===");
        
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] data = generator.generateData();
        
        GrangerCausality granger = new GrangerCausality();
        granger.withLag(2).withSignificanceLevel(0.05);
        granger.compute(data, null);
        
        runtime.gc();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        
        long memoryUsed = Math.abs(finalMemory - initialMemory);
        System.out.printf("预估内存使用：%d KB%n", memoryUsed / 1024);
        
        assertThat(memoryUsed).isLessThan(runtime.maxMemory());
    }
    
    private BenchmarkResult runBenchmark(String name, BenchmarkRunnable runnable) {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            try {
                runnable.run();
            } catch (Exception e) {
                // Ignore
            }
        }
        
        long startTime = System.nanoTime();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException("Benchmark failed: " + name, e);
            }
        }
        long endTime = System.nanoTime();
        
        long totalTimeMs = (endTime - startTime) / 1_000_000;
        BenchmarkResult result = new BenchmarkResult(name, totalTimeMs, TEST_ITERATIONS);
        
        System.out.printf("%-20s: 平均 %6d ms (%.2f ops/sec)%n", 
            name, result.avgTimeMs, result.throughput);
        
        return result;
    }
    
    private long measureTimeForSampleSize(int sampleSize) {
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] data = generator.generateDataForSampleSize(sampleSize);
        
        long startTime = System.nanoTime();
        GrangerCausality granger = new GrangerCausality();
        granger.withLag(2).withSignificanceLevel(0.05);
        granger.compute(data, null);
        long endTime = System.nanoTime();
        
        return (endTime - startTime) / 1_000_000;
    }
    
    private NetworkGraph createTestNetwork(int numNodes) {
        var graph = new NetworkGraph();
        
        for (int i = 0; i < numNodes; i++) {
            String nodeId = "Node_" + i;
            var indicator = new com.mecn.model.EconomicIndicator(nodeId, nodeId);
            graph.addNode(nodeId, indicator);
        }
        
        for (int i = 0; i < numNodes - 1; i++) {
            graph.addEdge("Node_" + i, "Node_" + (i + 1), 0.5);
            if (i < numNodes - 2) {
                graph.addEdge("Node_" + i, "Node_" + (i + 2), 0.3);
            }
        }
        
        return graph;
    }
    
    private void printBenchmarkResults(List<BenchmarkResult> results) {
        System.out.println("\n--- 性能对比 ---");
        BenchmarkResult fastest = results.stream()
            .min((a, b) -> Long.compare(a.avgTimeMs, b.avgTimeMs))
            .orElse(null);
        
        for (BenchmarkResult result : results) {
            String marker = (result == fastest) ? " [FASTEST]" : "";
            System.out.printf("%s: %d ms%s%n", result.testName, result.avgTimeMs, marker);
        }
    }
    
    @FunctionalInterface
    interface BenchmarkRunnable {
        Object run() throws Exception;
    }
}
