package com.mecn;

import com.mecn.cli.CommandLineParser;

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
        
        try {
            // 解析命令行参数
            CommandLineParser.AnalysisConfig config = parser.parse(args);
            
            // 根据命令执行相应操作
            switch (config.getCommand()) {
                case HELP:
                    CommandLineParser.printHelp();
                    break;
                    
                case VERSION:
                    CommandLineParser.printVersion();
                    break;
                    
                case ANALYZE:
                    System.out.println(config.toString());
                    System.out.println("\n开始执行因果网络分析...");
                    // TODO: 集成完整的分析流程
                    System.out.println("分析功能开发中...");
                    break;
                    
                case PREPROCESS:
                    System.out.println(config.toString());
                    System.out.println("\n开始数据预处理...");
                    // TODO: 集成预处理功能
                    System.out.println("预处理功能开发中...");
                    break;
                    
                case VISUALIZE:
                    System.out.println(config.toString());
                    System.out.println("\n开始可视化...");
                    // TODO: 集成功能
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
            e.printStackTrace();
            System.exit(1);
        }
    }
}
