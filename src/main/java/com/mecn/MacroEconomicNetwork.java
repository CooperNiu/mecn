package com.mecn;

import com.mecn.causal.CausalConfig;
import com.mecn.causal.CausalEngineImpl;
import com.mecn.causal.CausalResult;
import com.mecn.causal.LassoRegression;
import com.mecn.data.generator.EnhancedDataGenerator;
import com.mecn.model.NetworkGraph;
import com.mecn.network.NetworkBuilder;

/**
 * 宏观经济网络分析 - 简化的入口（移除 Swing 依赖）
 * 
 * 演示 MECN 核心功能，输出到控制台
 */
public class MacroEconomicNetwork {
    
    // 配置参数（与原版一致）
    private static final int NUM_INDICATORS = 40;
    private static final int NUM_PERIODS = 150;
    private static final double EDGE_THRESHOLD = 0.08;
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("MECN - 高维宏观经济因果网络联动模型");
        System.out.println("增强版 (Enhanced Data Generator + LASSO)");
        System.out.println("=".repeat(60));
        
        // 1. 生成经济数据
        System.out.println("\n📊 [步骤 1/4] 正在生成高维经济时间序列数据...");
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] economicData = generator.generateData();
        String[] indicatorNames = getIndicatorNames();
        System.out.println("   ✓ 生成了 " + NUM_PERIODS + " 期 × " + NUM_INDICATORS + " 个指标的数据");
        
        // 2. 基于 LASSO 算法挖掘因果关系
        System.out.println("\n🔗 [步骤 2/4] 正在挖掘指标间的动态因果关系...");
        CausalEngineImpl causalEngine = new CausalEngineImpl(false); // 串行执行
        causalEngine.registerMethod(new LassoRegression());
        
        CausalConfig config = new CausalConfig();
        config.setParallel(false);
        
        CausalResult causalResult = causalEngine.discover(economicData, config);
        System.out.println("   ✓ 完成因果发现 (LASSO 回归)");
        
        // 3. 构建 JGraphT 网络拓扑
        System.out.println("\n🌐 [步骤 3/4] 正在构建宏观网络图谱...");
        NetworkBuilder networkBuilder = new NetworkBuilder(EDGE_THRESHOLD);
        NetworkGraph network = networkBuilder.build(
            causalResult, 
            java.util.Arrays.asList(indicatorNames)
        );
        System.out.println("   ✓ 网络构建完成：" + 
            network.getGraph().vertexSet().size() + " 个节点，" + 
            network.getGraph().edgeSet().size() + " 条边");
        
        // 4. 输出网络统计信息
        System.out.println("\n📈 [步骤 4/4] 网络统计分析:");
        printNetworkStatistics(network);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ 分析完成！");
        System.out.println();
        System.out.println("💡 提示：启动 Web 服务器访问交互式可视化界面");
        System.out.println("   运行：mvn spring-boot:run");
        System.out.println("   然后访问：http://localhost:8080");
        System.out.println("=".repeat(60));
    }
    
    /**
     * 获取指标名称（与原版一致）
     */
    private static String[] getIndicatorNames() {
        String[] names = new String[NUM_INDICATORS];
        int idx = 0;
        for(int i=0; i<10; i++) names[idx++] = "CMD_" + i; // Commodity (大宗商品)
        for(int i=0; i<15; i++) names[idx++] = "MACRO_" + i; // Macro (宏观指标)
        for(int i=0; i<10; i++) names[idx++] = "FIN_" + i;   // Finance (金融市场)
        for(int i=0; i<5; i++)  names[idx++] = "EMPL_" + i;  // Employment (就业)
        return names;
    }
    
    /**
     * 打印网络统计信息
     */
    private static void printNetworkStatistics(NetworkGraph network) {
        var stats = network.getNetworkStatistics();
        
        System.out.println("   节点数：" + stats.get("nodeCount"));
        System.out.println("   边数：" + stats.get("edgeCount"));
        System.out.println(String.format("   网络密度：%.4f", stats.get("density")));
        
        // 输出度最高的前 5 个节点
        System.out.println("\n   影响力最大的指标 (Top 5):");
        var nodes = network.getNodes();
        var nodeDegrees = new java.util.ArrayList<java.util.Map.Entry<String, Integer>>();
        
        for (String node : nodes) {
            int outDegree = network.getOutDegree(node);
            int inDegree = network.getInDegree(node);
            nodeDegrees.add(new java.util.AbstractMap.SimpleEntry<>(
                node, outDegree + inDegree
            ));
        }
        
        nodeDegrees.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        for (int i = 0; i < Math.min(5, nodeDegrees.size()); i++) {
            var entry = nodeDegrees.get(i);
            System.out.println(String.format("      %d. %s (总度数：%d)", 
                i + 1, entry.getKey(), entry.getValue()));
        }
    }
}
