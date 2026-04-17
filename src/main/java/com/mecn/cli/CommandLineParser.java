package com.mecn.cli;

import java.util.*;

/**
 * 命令行参数解析器
 * 
 * 支持MECN工具的命令行参数解析和验证
 */
public class CommandLineParser {
    
    /**
     * 支持的命令类型
     */
    public enum Command {
        ANALYZE,      // 执行因果分析
        PREPROCESS,   // 数据预处理
        VISUALIZE,    // 可视化
        HELP,         // 显示帮助
        VERSION       // 显示版本
    }
    
    /**
     * 解析后的配置
     */
    public static class AnalysisConfig {
        // 基本参数
        private Command command = Command.ANALYZE;
        private String inputFile;
        private String outputFile;
        private String format = "csv";
        
        // 算法参数
        private String algorithm = "ensemble";  // lasso, granger, pcmci, ensemble
        private double lambda = 0.01;           // LASSO正则化参数
        private int maxLag = 5;                 // 最大滞后阶数
        private double significanceLevel = 0.05; // 显著性水平
        
        // 数据处理参数
        private boolean autoTune = true;        // 自动调优
        private boolean seasonalAdjustment = true; // 季节调整
        private boolean stationarityTest = true;   // 平稳性检验
        
        // 网络分析参数
        private String centralityMethod = "eigenvector"; // degree, betweenness, eigenvector
        private boolean detectCommunities = true;
        
        // 输出参数
        private boolean verbose = false;
        private boolean generateReport = true;
        private String reportFormat = "pdf";  // pdf, json, txt
        
        // Getters and Setters
        public Command getCommand() { return command; }
        public void setCommand(Command command) { this.command = command; }
        
        public String getInputFile() { return inputFile; }
        public void setInputFile(String inputFile) { this.inputFile = inputFile; }
        
        public String getOutputFile() { return outputFile; }
        public void setOutputFile(String outputFile) { this.outputFile = outputFile; }
        
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        
        public String getAlgorithm() { return algorithm; }
        public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
        
        public double getLambda() { return lambda; }
        public void setLambda(double lambda) { this.lambda = lambda; }
        
        public int getMaxLag() { return maxLag; }
        public void setMaxLag(int maxLag) { this.maxLag = maxLag; }
        
        public double getSignificanceLevel() { return significanceLevel; }
        public void setSignificanceLevel(double significanceLevel) { 
            this.significanceLevel = significanceLevel; 
        }
        
        public boolean isAutoTune() { return autoTune; }
        public void setAutoTune(boolean autoTune) { this.autoTune = autoTune; }
        
        public boolean isSeasonalAdjustment() { return seasonalAdjustment; }
        public void setSeasonalAdjustment(boolean seasonalAdjustment) { 
            this.seasonalAdjustment = seasonalAdjustment; 
        }
        
        public boolean isStationarityTest() { return stationarityTest; }
        public void setStationarityTest(boolean stationarityTest) { 
            this.stationarityTest = stationarityTest; 
        }
        
        public String getCentralityMethod() { return centralityMethod; }
        public void setCentralityMethod(String centralityMethod) { 
            this.centralityMethod = centralityMethod; 
        }
        
        public boolean isDetectCommunities() { return detectCommunities; }
        public void setDetectCommunities(boolean detectCommunities) { 
            this.detectCommunities = detectCommunities; 
        }
        
        public boolean isVerbose() { return verbose; }
        public void setVerbose(boolean verbose) { this.verbose = verbose; }
        
        public boolean isGenerateReport() { return generateReport; }
        public void setGenerateReport(boolean generateReport) { 
            this.generateReport = generateReport; 
        }
        
        public String getReportFormat() { return reportFormat; }
        public void setReportFormat(String reportFormat) { 
            this.reportFormat = reportFormat; 
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== MECN 分析配置 ===\n");
            sb.append(String.format("命令: %s\n", command));
            sb.append(String.format("输入文件: %s\n", inputFile != null ? inputFile : "模拟数据"));
            sb.append(String.format("输出文件: %s\n", outputFile != null ? outputFile : "自动生成"));
            sb.append(String.format("算法: %s\n", algorithm));
            sb.append(String.format("自动调优: %s\n", autoTune));
            sb.append(String.format("最大滞后: %d\n", maxLag));
            sb.append(String.format("显著性水平: %.3f\n", significanceLevel));
            sb.append(String.format("季节调整: %s\n", seasonalAdjustment));
            sb.append(String.format("平稳性检验: %s\n", stationarityTest));
            sb.append(String.format("中心性方法: %s\n", centralityMethod));
            sb.append(String.format("社区检测: %s\n", detectCommunities));
            sb.append(String.format("生成报告: %s (%s)\n", generateReport, reportFormat));
            sb.append(String.format("详细模式: %s\n", verbose));
            return sb.toString();
        }
    }
    
    /**
     * 解析命令行参数
     * 
     * @param args 命令行参数数组
     * @return 解析后的配置
     * @throws IllegalArgumentException 如果参数无效
     */
    public AnalysisConfig parse(String[] args) {
        AnalysisConfig config = new AnalysisConfig();
        
        if (args == null || args.length == 0) {
            // 无参数时显示帮助
            config.setCommand(Command.HELP);
            return config;
        }
        
        // 解析第一个参数作为命令
        String firstArg = args[0].toLowerCase();
        
        // 特殊处理短格式命令 -h 和 -v
        if (firstArg.equals("-h") || firstArg.equals("--help") || firstArg.equals("help")) {
            config.setCommand(Command.HELP);
            return config;
        } else if (firstArg.equals("-v") || firstArg.equals("--version") || firstArg.equals("version")) {
            config.setCommand(Command.VERSION);
            return config;
        }
        
        // 如果第一个参数是选项（以-开头），默认为ANALYZE命令
        if (firstArg.startsWith("-")) {
            config.setCommand(Command.ANALYZE);
        } else {
            // 检查是否是已知命令
            List<String> knownCommands = Arrays.asList("analyze", "preprocess", "visualize");
            if (knownCommands.contains(firstArg)) {
                switch (firstArg) {
                    case "analyze":
                        config.setCommand(Command.ANALYZE);
                        break;
                    case "preprocess":
                        config.setCommand(Command.PREPROCESS);
                        break;
                    case "visualize":
                        config.setCommand(Command.VISUALIZE);
                        break;
                }
            } else {
                // 不是命令，可能是文件名，默认为ANALYZE
                config.setCommand(Command.ANALYZE);
            }
        }
        
        // 解析其他参数
        int startIndex;
        if (firstArg.startsWith("-")) {
            // 第一个参数是选项，从0开始解析
            startIndex = 0;
        } else if (Arrays.asList("analyze", "preprocess", "visualize").contains(firstArg)) {
            // 第一个参数是命令，从1开始解析
            startIndex = 1;
        } else {
            // 第一个参数可能是文件名，从0开始解析
            startIndex = 0;
        }
        
        for (int i = startIndex; i < args.length; i++) {
            String arg = args[i];
            
            if (arg.startsWith("--")) {
                // 长参数
                parseLongOption(arg, i, args, config);
            } else if (arg.startsWith("-") && arg.length() == 2) {
                // 短参数
                parseShortOption(arg, i, args, config);
            } else if (config.getInputFile() == null) {
                // 第一个非选项参数作为输入文件
                config.setInputFile(arg);
            }
        }
        
        // 验证配置
        validateConfig(config);
        
        return config;
    }
    
    /**
     * 解析长选项 (--option=value 或 --option value)
     */
    private void parseLongOption(String arg, int index, String[] args, AnalysisConfig config) {
        String[] parts = arg.split("=", 2);
        String option = parts[0];
        String value = parts.length > 1 ? parts[1] : null;
        
        // 如果值没有在 = 后面提供，尝试从下一个参数获取
        if (value == null && index + 1 < args.length && !args[index + 1].startsWith("-")) {
            value = args[index + 1];
        }
        
        switch (option) {
            case "--input":
            case "--input-file":
                config.setInputFile(value);
                break;
            case "--output":
            case "--output-file":
                config.setOutputFile(value);
                break;
            case "--format":
                config.setFormat(value);
                break;
            case "--algorithm":
            case "--algo":
                config.setAlgorithm(value);
                break;
            case "--lambda":
                config.setLambda(parseDouble(value, "lambda"));
                break;
            case "--max-lag":
            case "--lag":
                config.setMaxLag(parseInt(value, "max-lag"));
                break;
            case "--significance":
            case "--alpha":
                config.setSignificanceLevel(parseDouble(value, "significance"));
                break;
            case "--auto-tune":
                config.setAutoTune(parseBoolean(value, "auto-tune"));
                break;
            case "--seasonal-adjustment":
                config.setSeasonalAdjustment(parseBoolean(value, "seasonal-adjustment"));
                break;
            case "--stationarity-test":
                config.setStationarityTest(parseBoolean(value, "stationarity-test"));
                break;
            case "--centrality":
                config.setCentralityMethod(value);
                break;
            case "--communities":
                config.setDetectCommunities(parseBoolean(value, "communities"));
                break;
            case "--report":
                config.setGenerateReport(parseBoolean(value, "report"));
                break;
            case "--report-format":
                config.setReportFormat(value);
                break;
            case "--verbose":
            case "-V":
                config.setVerbose(true);
                break;
            default:
                System.err.println("警告: 未知选项 " + option);
        }
    }
    
    /**
     * 解析短选项 (-o value)
     */
    private void parseShortOption(String arg, int index, String[] args, AnalysisConfig config) {
        char option = arg.charAt(1);
        String value = null;
        
        if (index + 1 < args.length && !args[index + 1].startsWith("-")) {
            value = args[index + 1];
        }
        
        switch (option) {
            case 'i':
                config.setInputFile(value);
                break;
            case 'o':
                config.setOutputFile(value);
                break;
            case 'a':
                config.setAlgorithm(value);
                break;
            case 'l':
                config.setLambda(parseDouble(value, "lambda"));
                break;
            case 'm':
                config.setMaxLag(parseInt(value, "max-lag"));
                break;
            case 'f':
                config.setFormat(value);
                break;
            case 'r':
                config.setReportFormat(value);
                break;
            case 'V':
                config.setVerbose(true);
                break;
            default:
                System.err.println("警告: 未知选项 -" + option);
        }
    }
    
    /**
     * 验证配置
     */
    private void validateConfig(AnalysisConfig config) {
        // 验证算法
        List<String> validAlgorithms = Arrays.asList("lasso", "granger", "pcmci", "ensemble");
        if (!validAlgorithms.contains(config.getAlgorithm().toLowerCase())) {
            throw new IllegalArgumentException(
                "无效的算法: " + config.getAlgorithm() + 
                ". 有效选项: " + String.join(", ", validAlgorithms)
            );
        }
        
        // 验证lambda范围
        if (config.getLambda() <= 0 || config.getLambda() > 1) {
            throw new IllegalArgumentException("lambda 必须在 (0, 1] 范围内");
        }
        
        // 验证maxLag
        if (config.getMaxLag() <= 0 || config.getMaxLag() > 20) {
            throw new IllegalArgumentException("max-lag 必须在 [1, 20] 范围内");
        }
        
        // 验证显著性水平
        if (config.getSignificanceLevel() <= 0 || config.getSignificanceLevel() >= 1) {
            throw new IllegalArgumentException("significance 必须在 (0, 1) 范围内");
        }
        
        // 验证中心性方法
        List<String> validCentralities = Arrays.asList("degree", "betweenness", "eigenvector", "closeness");
        if (!validCentralities.contains(config.getCentralityMethod().toLowerCase())) {
            throw new IllegalArgumentException(
                "无效的中心性方法: " + config.getCentralityMethod() + 
                ". 有效选项: " + String.join(", ", validCentralities)
            );
        }
        
        // 验证报告格式
        List<String> validFormats = Arrays.asList("pdf", "json", "txt");
        if (!validFormats.contains(config.getReportFormat().toLowerCase())) {
            throw new IllegalArgumentException(
                "无效的报告格式: " + config.getReportFormat() + 
                ". 有效选项: " + String.join(", ", validFormats)
            );
        }
    }
    
    /**
     * 解析整数
     */
    private int parseInt(String value, String paramName) {
        if (value == null) {
            throw new IllegalArgumentException("参数 --" + paramName + " 需要提供一个值");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("参数 --" + paramName + " 必须是整数: " + value);
        }
    }
    
    /**
     * 解析双精度浮点数
     */
    private double parseDouble(String value, String paramName) {
        if (value == null) {
            throw new IllegalArgumentException("参数 --" + paramName + " 需要提供一个值");
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("参数 --" + paramName + " 必须是数字: " + value);
        }
    }
    
    /**
     * 解析布尔值
     */
    private boolean parseBoolean(String value, String paramName) {
        if (value == null) {
            return true;  // 默认true
        }
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equals("1");
    }
    
    /**
     * 显示帮助信息
     */
    public static void printHelp() {
        System.out.println("MECN - 高维宏观经济因果网络联动模型");
        System.out.println("=====================================\n");
        System.out.println("用法: mecn [命令] [选项] [输入文件]\n");
        System.out.println("命令:");
        System.out.println("  analyze              执行因果网络分析 (默认)");
        System.out.println("  preprocess           数据预处理");
        System.out.println("  visualize            可视化分析结果");
        System.out.println("  help, -h             显示此帮助信息");
        System.out.println("  version, -v          显示版本信息\n");
        System.out.println("选项:");
        System.out.println("  -i, --input FILE     输入数据文件 (CSV格式)");
        System.out.println("  -o, --output FILE    输出文件路径");
        System.out.println("  -f, --format FMT     数据格式 (csv, json) [默认: csv]");
        System.out.println("  -a, --algorithm ALG  因果发现算法");
        System.out.println("                       (lasso, granger, pcmci, ensemble) [默认: ensemble]");
        System.out.println("  -l, --lambda VAL     LASSO正则化参数 [默认: 0.01]");
        System.out.println("  -m, --max-lag N      最大滞后阶数 [默认: 5]");
        System.out.println("      --alpha VAL      显著性水平 [默认: 0.05]");
        System.out.println("      --auto-tune      启用超参数自动调优 [默认: true]");
        System.out.println("      --seasonal       启用季节调整 [默认: true]");
        System.out.println("      --stationarity   启用平稳性检验 [默认: true]");
        System.out.println("      --centrality M   中心性分析方法");
        System.out.println("                       (degree, betweenness, eigenvector, closeness)");
        System.out.println("      --communities    启用社区检测 [默认: true]");
        System.out.println("  -r, --report-format  报告格式 (pdf, json, txt) [默认: pdf]");
        System.out.println("      --no-report      不生成报告");
        System.out.println("  -V, --verbose        详细输出模式\n");
        System.out.println("示例:");
        System.out.println("  # 使用默认配置分析");
        System.out.println("  mecn data.csv\n");
        System.out.println("  # 指定算法和参数");
        System.out.println("  mecn --algorithm lasso --lambda 0.05 --max-lag 3 data.csv\n");
        System.out.println("  # 完整配置示例");
        System.out.println("  mecn analyze -i data.csv -o results/ \\\\");
        System.out.println("       --algorithm ensemble --auto-tune \\\\");
        System.out.println("       --centrality eigenvector --report-format pdf\n");
        System.out.println("更多信息请访问: https://github.com/CooperNiu/mecn");
    }
    
    /**
     * 显示版本信息
     */
    public static void printVersion() {
        System.out.println("MECN v1.0.0");
        System.out.println("高维宏观经济因果网络联动模型");
        System.out.println("Copyright (c) 2026 CooperNiu");
        System.out.println("License: MIT");
    }
}
