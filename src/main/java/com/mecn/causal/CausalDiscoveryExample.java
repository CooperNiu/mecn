package com.mecn.causal;

import java.util.Arrays;
import java.util.List;

/**
 * PCMCI 和 Granger 因果发现算法使用示例
 */
public class CausalDiscoveryExample {
    
    public static void main(String[] args) {
        // 生成示例数据
        double[][] data = generateSampleData();
        
        System.out.println("=== MECN 因果发现算法示例 ===\n");
        
        // 示例 1: 使用 LASSO
        System.out.println("1. LASSO 回归方法");
        causalDiscoveryWithLasso(data);
        
        // 示例 2: 使用 Granger 因果检验
        System.out.println("\n2. Granger 因果检验");
        causalDiscoveryWithGranger(data);
        
        // 示例 3: 使用 PCMCI
        System.out.println("\n3. PCMCI 算法");
        causalDiscoveryWithPCMCI(data);
        
        // 示例 4: 多方法集成
        System.out.println("\n4. 多方法集成 (Ensemble)");
        causalDiscoveryWithEnsemble(data);
    }
    
    /**
     * 生成示例数据
     * 模拟简单的因果关系：X -> Y -> Z
     */
    private static double[][] generateSampleData() {
        int T = 200;  // 200 个时间点
        int N = 4;    // 4 个变量：X, Y, Z, W
        
        double[][] data = new double[T][N];
        
        // 初始化随机数生成器
        java.util.Random random = new java.util.Random(42);
        
        // 生成数据
        for (int t = 0; t < T; t++) {
            if (t == 0) {
                // 初始值
                data[t][0] = random.nextGaussian();  // X
                data[t][1] = random.nextGaussian();  // Y
                data[t][2] = random.nextGaussian();  // Z
                data[t][3] = random.nextGaussian();  // W
            } else {
                // X: 自回归过程
                data[t][0] = 0.5 * data[t-1][0] + random.nextGaussian() * 0.5;
                
                // Y: 受 X 的滞后影响 (X -> Y)
                data[t][1] = 0.3 * data[t-1][1] + 0.4 * data[t-1][0] + random.nextGaussian() * 0.5;
                
                // Z: 受 Y 的滞后影响 (Y -> Z)
                data[t][2] = 0.3 * data[t-1][2] + 0.5 * data[t-1][1] + random.nextGaussian() * 0.5;
                
                // W: 独立过程
                data[t][3] = 0.6 * data[t-1][3] + random.nextGaussian() * 0.5;
            }
        }
        
        return data;
    }
    
    /**
     * LASSO 回归因果发现
     */
    private static void causalDiscoveryWithLasso(double[][] data) {
        CausalResult result = new CausalEngineBuilder()
            .data(data)
            .method(new LassoRegression()
                .withLambda(0.1)
                .withThreshold(0.05))
            .significanceLevel(0.05)
            .discover();
        
        printTopCausalLinks(result, 5);
    }
    
    /**
     * Granger 因果检验
     */
    private static void causalDiscoveryWithGranger(double[][] data) {
        CausalResult result = new CausalEngineBuilder()
            .data(data)
            .method(new GrangerCausality()
                .withLag(2)
                .withSignificanceLevel(0.05))
            .significanceLevel(0.05)
            .discover();
        
        printTopCausalLinks(result, 5);
    }
    
    /**
     * PCMCI 算法
     */
    private static void causalDiscoveryWithPCMCI(double[][] data) {
        CausalResult result = new CausalEngineBuilder()
            .data(data)
            .method(new PCMCI()
                .withTauMax(5)
                .withPcAlpha(0.05)
                .withConfidenceLevel(0.95))
            .significanceLevel(0.05)
            .discover();
        
        printTopCausalLinks(result, 5);
    }
    
    /**
     * 多方法集成
     */
    private static void causalDiscoveryWithEnsemble(double[][] data) {
        List<CausalMethod> methods = Arrays.asList(
            new LassoRegression().withLambda(0.1),
            new GrangerCausality().withLag(2),
            new PCMCI().withTauMax(3)
        );
        
        CausalResult result = new CausalEngineBuilder()
            .data(data)
            .methods(methods)
            .significanceLevel(0.05)
            .maxLag(5)
            .fusionStrategy(FusionStrategy.HYBRID)
            .minVotes(2)
            .discover();
        
        printTopCausalLinks(result, 5);
        
        // 打印元数据
        System.out.println("   使用方法数：" + result.getMetadata().get("numMethods"));
        System.out.println("   融合策略：" + result.getMetadata().get("fusionStrategy"));
    }
    
    /**
     * 打印最强的因果链接
     */
    private static void printTopCausalLinks(CausalResult result, int topK) {
        double[][] matrix = result.getAdjacencyMatrix();
        int N = matrix.length;
        
        // 收集所有非零边
        List<double[]> edges = new java.util.ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i != j && Math.abs(matrix[i][j]) > 0.01) {
                    edges.add(new double[]{j, i, Math.abs(matrix[i][j])});
                }
            }
        }
        
        // 按强度排序
        edges.sort((a, b) -> Double.compare(b[2], a[2]));
        
        // 打印前 K 个
        String[] varNames = {"X", "Y", "Z", "W"};
        System.out.println("   最强的 " + Math.min(topK, edges.size()) + " 条因果链接:");
        
        for (int k = 0; k < Math.min(topK, edges.size()); k++) {
            double[] edge = edges.get(k);
            int source = (int) edge[0];
            int target = (int) edge[1];
            double strength = edge[2];
            
            System.out.printf("   %d. %s → %s (强度：%.4f)%n", 
                k + 1, varNames[source], varNames[target], strength);
        }
    }
}
