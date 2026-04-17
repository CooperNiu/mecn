package com.mecn.service;

import com.mecn.causal.*;
import com.mecn.data.provider.DataProvider;
import com.mecn.data.provider.SimulatedDataProvider;
import com.mecn.io.CsvDataReader;
import com.mecn.model.NetworkGraph;
import com.mecn.model.TimeSeriesData;
import com.mecn.network.CentralityAnalyzer;
import com.mecn.network.NetworkBuilder;
import com.mecn.preprocess.Preprocessor;
import com.mecn.preprocess.SeasonalAdjustment;

import java.io.IOException;
import java.util.List;

/**
 * MECN 分析服务
 * 
 * 整合数据读取、预处理、因果发现、网络分析等组件
 * 提供端到端的分析流程
 */
public class AnalysisService {
    
    /**
     * 执行完整的因果网络分析
     * 
     * @param inputFile 输入文件路径（CSV格式），null则使用模拟数据
     * @param config CLI配置参数
     * @return 分析结果摘要
     * @throws IOException 文件读取错误
     */
    public AnalysisResult execute(String inputFile, com.mecn.cli.CommandLineParser.AnalysisConfig config) 
            throws IOException {
        
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║     MECN 宏观经济因果网络分析系统         ║");
        System.out.println("╚═══════════════════════════════════════════╝\n");
        
        // 步骤1: 加载数据
        System.out.println("【步骤1/6】加载数据...");
        List<TimeSeriesData> rawData = loadData(inputFile);
        System.out.println("✓ 成功加载 " + rawData.size() + " 个指标\n");
        
        // 步骤2: 数据预处理
        System.out.println("【步骤2/6】数据预处理...");
        double[][] processedData = preprocessData(rawData, config);
        System.out.println("✓ 预处理完成，数据维度: " + processedData.length + " x " + processedData[0].length + "\n");
        
        // 步骤3: 超参数自动调优
        System.out.println("【步骤3/6】超参数自动调优...");
        HyperparameterResult tuningResult = autoTuneParameters(processedData, config);
        System.out.println(tuningResult.generateReport() + "\n");
        
        // 步骤4: 因果发现
        System.out.println("【步骤4/6】执行因果发现...");
        CausalResult causalResult = discoverCausality(processedData, config);
        int edgeCount = countEdges(causalResult);
        System.out.println("✓ 发现 " + edgeCount + " 条因果关系\n");
        
        // 步骤5: 构建网络
        System.out.println("【步骤5/6】构建因果网络...");
        List<String> nodeNames = getNodeNames(rawData);
        NetworkGraph network = buildNetwork(causalResult, nodeNames);
        System.out.println("✓ 网络构建完成");
        System.out.println("  - 节点数: " + network.getGraph().vertexSet().size());
        System.out.println("  - 边数: " + network.getGraph().edgeSet().size() + "\n");
        
        // 步骤6: 中心性分析
        System.out.println("【步骤6/6】中心性分析...");
        CentralityAnalyzer analyzer = new CentralityAnalyzer(network.getGraph());
        var centralities = analyzer.analyze();
        
        // 获取Top 5重要节点
        var topNodes = centralities.stream()
            .sorted((a, b) -> Double.compare(b.getCompositeScore(), a.getCompositeScore()))
            .limit(5)
            .toList();
        
        System.out.println("✓ 中心性分析完成\n");
        System.out.println("Top 5 系统重要性节点:");
        for (var result : topNodes) {
            System.out.printf("  %-15s 综合得分: %.4f  (度中心性: %.4f)%n",
                result.getNodeId(),
                result.getCompositeScore(),
                result.getDegreeCentrality());
        }
        
        // 构建分析结果
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setIndicatorCount(rawData.size());
        analysisResult.setDataPoints(processedData.length);
        analysisResult.setCausalEdges(edgeCount);
        analysisResult.setNodeCount(network.getGraph().vertexSet().size());
        analysisResult.setEdgeCount(network.getGraph().edgeSet().size());
        analysisResult.setTopNodes(topNodes);
        analysisResult.setTuningResult(tuningResult);
        
        return analysisResult;
    }
    
    /**
     * 加载数据
     */
    private List<TimeSeriesData> loadData(String inputFile) throws IOException {
        if (inputFile != null && !inputFile.isEmpty()) {
            // 从CSV文件读取
            System.out.println("  从文件读取: " + inputFile);
            CsvDataReader reader = new CsvDataReader();
            return reader.read(inputFile);
        } else {
            // 使用模拟数据
            System.out.println("  使用模拟数据生成器");
            DataProvider provider = new SimulatedDataProvider();
            java.time.LocalDate startDate = java.time.LocalDate.of(2010, 1, 1);
            java.time.LocalDate endDate = java.time.LocalDate.of(2022, 6, 30);
            return provider.fetch(null, startDate, endDate);
        }
    }
    
    /**
     * 数据预处理
     */
    private double[][] preprocessData(List<TimeSeriesData> rawData, 
                                     com.mecn.cli.CommandLineParser.AnalysisConfig config) {
        // 转换为矩阵格式 [T][N]
        int T = rawData.get(0).getValues().length;
        int N = rawData.size();
        double[][] dataMatrix = new double[T][N];
        
        for (int i = 0; i < N; i++) {
            double[] values = rawData.get(i).getValues();
            for (int t = 0; t < T; t++) {
                dataMatrix[t][i] = values[t];
            }
        }
        
        System.out.println("  数据转换完成: " + T + " 时间点 x " + N + " 指标");
        
        return dataMatrix;
    }
    
    /**
     * 超参数自动调优
     */
    private HyperparameterResult autoTuneParameters(double[][] data, 
                                                    com.mecn.cli.CommandLineParser.AnalysisConfig config) {
        // LASSO自动调优
        System.out.println("  执行LASSO超参数调优...");
        LassoRegression lasso = new LassoRegression();
        HyperparameterResult lassoResult = lasso.autoTuneLambda(data);
        
        return lassoResult;
    }
    
    /**
     * 因果发现
     */
    private CausalResult discoverCausality(double[][] data, 
                                          com.mecn.cli.CommandLineParser.AnalysisConfig config) {
        String algorithm = config.getAlgorithm().toLowerCase();
        
        // 创建因果引擎并注册方法
        CausalEngineBuilder builder = new CausalEngineBuilder()
            .data(data)
            .maxLag(config.getMaxLag())
            .significanceLevel(config.getSignificanceLevel());
        
        // 根据算法配置注册相应的方法
        switch (algorithm) {
            case "lasso":
                builder.method(new LassoRegression());
                break;
            case "granger":
                builder.method(new GrangerCausality());
                break;
            case "ensemble":
            default:
                // 集成方法：同时使用LASSO和Granger
                builder.method(new LassoRegression());
                builder.method(new GrangerCausality());
                break;
        }
        
        CausalEngine engine = builder.build();
        
        CausalConfig causalConfig = new CausalConfig();
        causalConfig.setMaxLag(config.getMaxLag());
        causalConfig.setSignificanceLevel(config.getSignificanceLevel());
        
        System.out.println("  使用算法: " + algorithm);
        System.out.println("  最大滞后阶数: " + config.getMaxLag());
        System.out.println("  显著性水平: " + config.getSignificanceLevel());
        
        return engine.discover(data, causalConfig);
    }
    
    /**
     * 统计因果边数量
     */
    private int countEdges(CausalResult result) {
        if (result == null || result.getAdjacencyMatrix() == null) {
            return 0;
        }
        
        int count = 0;
        double[][] matrix = result.getAdjacencyMatrix();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != 0) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * 获取节点名称列表
     */
    private List<String> getNodeNames(List<TimeSeriesData> rawData) {
        return rawData.stream()
            .map(TimeSeriesData::getIndicatorCode)
            .toList();
    }
    
    /**
     * 构建网络
     */
    private NetworkGraph buildNetwork(CausalResult causalResult, List<String> nodeNames) {
        NetworkBuilder builder = new NetworkBuilder();
        return builder.build(causalResult, nodeNames);
    }
    
    /**
     * 分析结果封装类
     */
    public static class AnalysisResult {
        private int indicatorCount;
        private int dataPoints;
        private int causalEdges;
        private int nodeCount;
        private int edgeCount;
        private List<?> topNodes;
        private HyperparameterResult tuningResult;
        
        // Getters and Setters
        public int getIndicatorCount() { return indicatorCount; }
        public void setIndicatorCount(int indicatorCount) { this.indicatorCount = indicatorCount; }
        
        public int getDataPoints() { return dataPoints; }
        public void setDataPoints(int dataPoints) { this.dataPoints = dataPoints; }
        
        public int getCausalEdges() { return causalEdges; }
        public void setCausalEdges(int causalEdges) { this.causalEdges = causalEdges; }
        
        public int getNodeCount() { return nodeCount; }
        public void setNodeCount(int nodeCount) { this.nodeCount = nodeCount; }
        
        public int getEdgeCount() { return edgeCount; }
        public void setEdgeCount(int edgeCount) { this.edgeCount = edgeCount; }
        
        public List<?> getTopNodes() { return topNodes; }
        public void setTopNodes(List<?> topNodes) { this.topNodes = topNodes; }
        
        public HyperparameterResult getTuningResult() { return tuningResult; }
        public void setTuningResult(HyperparameterResult tuningResult) { this.tuningResult = tuningResult; }
        
        /**
         * 生成分析报告
         */
        public String generateReport() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n═══════════════════════════════════════════\n");
            sb.append("           分析结果摘要\n");
            sb.append("═══════════════════════════════════════════\n");
            sb.append(String.format("指标数量:       %d\n", indicatorCount));
            sb.append(String.format("数据点数:       %d\n", dataPoints));
            sb.append(String.format("因果关系数:     %d\n", causalEdges));
            sb.append(String.format("网络节点数:     %d\n", nodeCount));
            sb.append(String.format("网络边数:       %d\n", edgeCount));
            sb.append("═══════════════════════════════════════════\n");
            return sb.toString();
        }
    }
}
