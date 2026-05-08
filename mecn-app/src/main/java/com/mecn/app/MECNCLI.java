package com.mecn;

import com.mecn.cli.CommandLineParser;
import com.mecn.service.AnalysisService;

import java.io.IOException;

/**
 * MECN 命令行工具入口
 * 
 * 用法示例:
 * java -jar mecn.jar analyze data.csv --algorithm lasso --lambda 0.05
 * java -jar mecn.jar --help
 * java -jar mecn.jar --version
 */
public class MECNCLI {
    
    public static void main(String[] args) {
        CommandLineParser parser = new CommandLineParser();
        CommandLineParser.AnalysisConfig config = null;
        
        try {
            // 解析命令行参数
            config = parser.parse(args);
            
            // 根据命令执行相应操作
            switch (config.getCommand()) {
                case HELP:
                    CommandLineParser.printHelp();
                    break;
                    
                case VERSION:
                    CommandLineParser.printVersion();
                    break;
                    
                case ANALYZE:
                    executeAnalysis(config);
                    break;
                    
                case PREPROCESS:
                    System.out.println(config.toString());
                    System.out.println("\n开始数据预处理...");
                    System.out.println("预处理功能开发中...");
                    break;
                    
                case VISUALIZE:
                    System.out.println(config.toString());
                    System.out.println("\n开始可视化...");
                    System.out.println("可视化功能开发中...");
                    break;
                    
                default:
                    System.err.println("未知命令: " + config.getCommand());
                    CommandLineParser.printHelp();
                    System.exit(1);
            }
            
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
            System.err.println("\n使用 --help 查看帮助信息");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("发生错误: " + e.getMessage());
            if (config != null && config.isVerbose()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
    
    /**
     * 执行分析流程
     */
    private static void executeAnalysis(CommandLineParser.AnalysisConfig config) throws IOException {
        System.out.println(config.toString());
        
        AnalysisService service = new AnalysisService();
        AnalysisService.AnalysisResult result = service.execute(config.getInputFile(), config);
        
        // 输出分析结果
        System.out.println(result.generateReport());
        
        System.out.println("\n✓ 分析完成！");
    }
}
