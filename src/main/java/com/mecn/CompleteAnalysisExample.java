package com.mecn;

import com.mecn.causal.*;
import com.mecn.data.provider.DataProvider;
import com.mecn.data.provider.SimulatedDataProvider;
import com.mecn.model.EconomicIndicator;
import com.mecn.model.NetworkGraph;
import com.mecn.model.TimeSeriesData;
import com.mecn.network.CentralityAnalyzer;
import com.mecn.network.NetworkBuilder;
import com.mecn.preprocess.Preprocessor;
import com.mecn.preprocess.SeasonalAdjustment;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * MECN 完整示例：从数据到因果网络分析
 * 
 * 演示完整的分析流程：
 * 1. 数据获取
 * 2. 数据预处理
 * 3. 超参数自动调优
 * 4. 因果发现
 * 5. 网络构建
 * 6. 中心性分析
 * 7. 结果输出
 * 
 * @author MECN Team
 * @since 1.0.0
 */
public class CompleteAnalysisExample {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  MECN 完整分析示例");
        System.out.println("  Macro Economic Causal Network Analysis");
        System.out.println("=================================================\n");
        
        try {
            // 步骤1: 准备数据
            System.out.println("【步骤1】准备数据...");
            List<TimeSeriesData> rawData = prepareData();
            System.out.println("✓ 获取到 " + rawData.size() + " 个指标的时间序列数据\n");
            
            // 步骤2: 数据预处理
            System.out.println("【步骤2】数据预处理...");
            double[][] processedData = preprocessData(rawData);
            System.out.println("✓ 数据预处理完成，维度: " + processedData.length + " x " + processedData[0].length + "\n");
            
            // 步骤3: LASSO 超参数自动调优
            System.out.println("【步骤3】LASSO 超参数自动调优...");
            HyperparameterResult lassoResult = tuneLassoParameters(processedData);
            System.out.println(lassoResult.generateReport());
            
            // 步骤4: Granger 超参数自动调优
            System.out.println("\n【步骤4】Granger 超参数自动调优...");
            HyperparameterResult grangerResult = tuneGrangerParameters(processedData);
            System.out.println(grangerResult.generateReport());
            
            // 步骤5: 执行因果发现
            System.out.println("\n【步骤5】执行因果发现（集成方法）...");
            CausalResult causalResult = performCausalDiscovery(processedData, lassoResult, grangerResult);
            System.out.println("✓ 因果发现完成");
            System.out.println("  - 检测到的因果关系数量: " + countCausalEdges(causalResult));
            System.out.println("  - 使用的方法: Ensemble (LASSO + Granger)\n");
            
            // 步骤6: 构建网络
            System.out.println("【步骤6】构建因果网络...");
            List<String> nodeNames = getNodeNames(rawData);
            NetworkGraph network = buildNetwork(causalResult, nodeNames);
            System.out.println("✓ 网络构建完成");
            System.out.println("  - 节点数: " + network.getGraph().vertexSet().size());
            System.out.println("  - 边数: " + network.getGraph().edgeSet().size() + "\n");
            
            // 步骤7: 中心性分析
            System.out.println("【步骤7】中心性分析...");
            CentralityAnalyzer analyzer = new CentralityAnalyzer(network.getGraph());
            var centralities = analyzer.analyze();
            
            // 显示 Top 5 重要节点
            System.out.println("✓ 中心性分析完成");
            System.out.println("\nTop 5 系统重要性节点:");
            centralities.stream()
                .sorted((a, b) -> Double.compare(b.getCompositeScore(), a.getCompositeScore()))
                .limit(5)
                .forEach(result -> {
                    System.out.printf("  %-15s 综合得分: %.4f  (度中心性: %.4f)%n",
                        result.getNodeId(),
                        result.getCompositeScore(),
                        result.getDegreeCentrality());
                });
            
            // 步骤8: 生成总结报告
            System.out.println("\n【步骤8】分析总结");
            generateSummaryReport(lassoResult, grangerResult, causalResult, network, centralities);
            
            System.out.println("\n=================================================");
            System.out.println("  分析完成！");
            System.out.println("=================================================");
            
        } catch (Exception e) {
            System.err.println("分析过程中出现错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 步骤1: 准备数据
     */
    private static List<TimeSeriesData> prepareData() {
        // 使用模拟数据提供者
        DataProvider provider = new SimulatedDataProvider();
        
        LocalDate startDate = LocalDate.of(2010, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 6, 30);  // 150个月
        
        // 获取所有支持的指标
        List<TimeSeriesData> allData = provider.fetch(null, startDate, endDate);
        
        // 只取前5个指标用于演示（避免计算复杂度太高）
        return allData.subList(0, Math.min(5, allData.size()));
    }
    
    /**
     * 步骤2: 数据预处理
     */
    private static double[][] preprocessData(List<TimeSeriesData> rawData) {
        int T = rawData.get(0).getValues().length;
        int N = rawData.size();
        
        double[][] data = new double[T][N];
        
        // 将 TimeSeriesData 转换为矩阵格式
        for (int i = 0; i < N; i++) {
            double[] values = rawData.get(i).getValues();
            for (int t = 0; t < T; t++) {
                data[t][i] = values[t];
            }
        }
        
        // 这里可以添加更多的预处理步骤
        // 例如：缺失值处理、季节性调整、标准化等
        
        return data;
    }
    
    /**
     * 步骤3: LASSO 超参数自动调优
     */
    private static HyperparameterResult tuneLassoParameters(double[][] data) {
        LassoRegression lasso = new LassoRegression();
        
        System.out.println("  正在执行交叉验证...");
        HyperparameterResult result = lasso.autoTuneLambda(data);
        
        // 自动应用最优参数
        lasso.withThreshold(0.1);
        
        return result;
    }
    
    /**
     * 步骤4: Granger 超参数自动调优
     */
    private static HyperparameterResult tuneGrangerParameters(double[][] data) {
        GrangerCausality granger = new GrangerCausality();
        
        System.out.println("  正在计算信息准则...");
        HyperparameterResult result = granger.autoTuneLag(data);
        
        // 自动应用最优参数
        int optimalLag = (int) result.getBestLambda();
        granger.withLag(optimalLag);
        
        return result;
    }
    
    /**
     * 步骤5: 执行因果发现
     */
    private static CausalResult performCausalDiscovery(double[][] data, 
                                                        HyperparameterResult lassoResult,
                                                        HyperparameterResult grangerResult) {
        // 创建因果引擎（禁用并行以避免大数据集问题）
        CausalEngine engine = new CausalEngineImpl(false);
        
        // 添加方法
        CausalMethod lasso = new LassoRegression().withWeight(1.0);
        CausalMethod granger = new GrangerCausality().withWeight(1.2);
        
        engine.registerMethod(lasso);
        engine.registerMethod(granger);
        
        // 配置
        CausalConfig config = new CausalConfig();
        
        // 执行因果发现
        return engine.discover(data, config);
    }
    
    /**
     * 步骤6: 构建网络
     */
    private static NetworkGraph buildNetwork(CausalResult causalResult, List<String> nodeNames) {
        NetworkBuilder builder = new NetworkBuilder(0.08);  // 边权重阈值
        return builder.build(causalResult, nodeNames);
    }
    
    /**
     * 步骤7: 生成总结报告
     */
    private static void generateSummaryReport(HyperparameterResult lassoResult,
                                              HyperparameterResult grangerResult,
                                              CausalResult causalResult,
                                              NetworkGraph network,
                                              List<?> centralities) {
        System.out.println("\n┌─────────────────────────────────────────────┐");
        System.out.println("│           分 析 结 果 总 结                  │");
        System.out.println("├─────────────────────────────────────────────┤");
        
        System.out.println("│ 超参数优化:                                 │");
        System.out.printf("│   LASSO 最优 λ:      %8.4f              │%n", lassoResult.getBestLambda());
        System.out.printf("│   Granger 最优 Lag:  %8.0f              │%n", grangerResult.getBestLambda());
        
        System.out.println("│                                             │");
        System.out.println("│ 模型性能:                                   │");
        System.out.printf("│   R²:                %8.4f              │%n", lassoResult.getRSquared());
        System.out.printf("│   AIC:               %8.2f              │%n", lassoResult.getAIC());
        System.out.printf("│   BIC:               %8.2f              │%n", lassoResult.getBIC());
        
        System.out.println("│                                             │");
        System.out.println("│ 网络特征:                                   │");
        System.out.printf("│   节点数:            %8d              │%n", network.getGraph().vertexSet().size());
        System.out.printf("│   边数:              %8d              │%n", network.getGraph().edgeSet().size());
        
        double density = network.getGraph().edgeSet().size() / 
            (double)(network.getGraph().vertexSet().size() * (network.getGraph().vertexSet().size() - 1));
        System.out.printf("│   网络密度:          %8.4f              │%n", density);
        
        System.out.println("└─────────────────────────────────────────────┘");
    }
    
    /**
     * 获取节点名称列表
     */
    private static List<String> getNodeNames(List<TimeSeriesData> rawData) {
        return rawData.stream()
            .map(TimeSeriesData::getIndicatorCode)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 统计因果关系边的数量
     */
    private static int countCausalEdges(CausalResult result) {
        double[][] matrix = result.getAdjacencyMatrix();
        int count = 0;
        for (double[] row : matrix) {
            for (double val : row) {
                if (Math.abs(val) > 0.01) {
                    count++;
                }
            }
        }
        return count;
    }
}
