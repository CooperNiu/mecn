package com.mecn.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 命令行参数解析器测试
 * 
 * TDD实践：先写测试，再验证实现
 */
@DisplayName("CommandLineParser 测试")
class CommandLineParserTest {
    
    private CommandLineParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new CommandLineParser();
    }
    
    @Test
    @DisplayName("无参数时返回HELP命令")
    void testNoArgsReturnsHelp() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{});
        assertEquals(CommandLineParser.Command.HELP, config.getCommand());
    }
    
    @Test
    @DisplayName("help命令正确识别")
    void testHelpCommand() {
        String[][] helpArgs = {{"help"}, {"-h"}, {"--help"}};
        
        for (String[] args : helpArgs) {
            CommandLineParser.AnalysisConfig config = parser.parse(args);
            assertEquals(CommandLineParser.Command.HELP, config.getCommand());
        }
    }
    
    @Test
    @DisplayName("version命令正确识别")
    void testVersionCommand() {
        String[][] versionArgs = {{"version"}, {"-v"}, {"--version"}};
        
        for (String[] args : versionArgs) {
            CommandLineParser.AnalysisConfig config = parser.parse(args);
            assertEquals(CommandLineParser.Command.VERSION, config.getCommand());
        }
    }
    
    @Test
    @DisplayName("默认命令为ANALYZE")
    void testDefaultCommandIsAnalyze() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{"data.csv"});
        assertEquals(CommandLineParser.Command.ANALYZE, config.getCommand());
    }
    
    @Test
    @DisplayName("解析输入文件参数")
    void testInputFileParsing() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{"-i", "data.csv"});
        assertEquals("data.csv", config.getInputFile());
    }
    
    @Test
    @DisplayName("解析输出文件参数")
    void testOutputFileParsing() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{"-o", "output.pdf"});
        assertEquals("output.pdf", config.getOutputFile());
    }
    
    @Test
    @DisplayName("位置参数作为输入文件")
    void testPositionalArgumentAsInput() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{"data.csv"});
        assertEquals("data.csv", config.getInputFile());
    }
    
    @Test
    @DisplayName("解析算法参数")
    void testAlgorithmParsing() {
        String[] algorithms = {"lasso", "granger", "pcmci", "ensemble"};
        
        for (String algo : algorithms) {
            CommandLineParser.AnalysisConfig config = parser.parse(new String[]{"--algorithm", algo});
            assertEquals(algo, config.getAlgorithm());
        }
    }
    
    @Test
    @DisplayName("无效算法抛出异常")
    void testInvalidAlgorithmThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(new String[]{"--algorithm", "invalid"});
        });
    }
    
    @Test
    @DisplayName("解析lambda参数")
    void testLambdaParsing() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{"--lambda", "0.05"});
        assertEquals(0.05, config.getLambda(), 0.0001);
    }
    
    @Test
    @DisplayName("解析max-lag参数")
    void testMaxLagParsing() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{"--max-lag", "3"});
        assertEquals(3, config.getMaxLag());
    }
    
    @Test
    @DisplayName("lambda超出范围抛出异常")
    void testLambdaOutOfRangeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(new String[]{"--lambda", "1.5"});
        });
    }
    
    @Test
    @DisplayName("max-lag超出范围抛出异常")
    void testMaxLagOutOfRangeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(new String[]{"--max-lag", "25"});
        });
    }
    
    @Test
    @DisplayName("非数字值抛出异常")
    void testNonNumericValueThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(new String[]{"--lambda", "abc"});
        });
    }
    
    @Test
    @DisplayName("解析auto-tune参数")
    void testAutoTuneParsing() {
        CommandLineParser.AnalysisConfig config1 = parser.parse(new String[]{"--auto-tune", "true"});
        assertTrue(config1.isAutoTune());
        
        CommandLineParser.AnalysisConfig config2 = parser.parse(new String[]{"--auto-tune", "false"});
        assertFalse(config2.isAutoTune());
    }
    
    @Test
    @DisplayName("布尔参数默认为true")
    void testBooleanParameterDefaultsToTrue() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{"--auto-tune"});
        assertTrue(config.isAutoTune());
    }
    
    @Test
    @DisplayName("解析verbose标志")
    void testVerboseFlag() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{"--verbose"});
        assertTrue(config.isVerbose());
    }
    
    @Test
    @DisplayName("解析中心性方法参数")
    void testCentralityMethodParsing() {
        String[] methods = {"degree", "betweenness", "eigenvector", "closeness"};
        
        for (String method : methods) {
            CommandLineParser.AnalysisConfig config = parser.parse(new String[]{"--centrality", method});
            assertEquals(method, config.getCentralityMethod());
        }
    }
    
    @Test
    @DisplayName("无效中心性方法抛出异常")
    void testInvalidCentralityMethodThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(new String[]{"--centrality", "invalid"});
        });
    }
    
    @Test
    @DisplayName("解析报告格式参数")
    void testReportFormatParsing() {
        String[] formats = {"pdf", "json", "txt"};
        
        for (String format : formats) {
            CommandLineParser.AnalysisConfig config = parser.parse(new String[]{"--report-format", format});
            assertEquals(format, config.getReportFormat());
        }
    }
    
    @Test
    @DisplayName("无效报告格式抛出异常")
    void testInvalidReportFormatThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(new String[]{"--report-format", "xml"});
        });
    }
    
    @Test
    @DisplayName("组合参数解析")
    void testCombinedParameters() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{
            "analyze",
            "-i", "data.csv",
            "-o", "results/",
            "--algorithm", "lasso",
            "--lambda", "0.05",
            "--max-lag", "3",
            "--auto-tune", "true",
            "--verbose"
        });
        
        assertEquals(CommandLineParser.Command.ANALYZE, config.getCommand());
        assertEquals("data.csv", config.getInputFile());
        assertEquals("results/", config.getOutputFile());
        assertEquals("lasso", config.getAlgorithm());
        assertEquals(0.05, config.getLambda(), 0.0001);
        assertEquals(3, config.getMaxLag());
        assertTrue(config.isAutoTune());
        assertTrue(config.isVerbose());
    }
    
    @Test
    @DisplayName("默认配置值正确")
    void testDefaultConfigurationValues() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{});
        
        assertEquals("ensemble", config.getAlgorithm());
        assertEquals(0.01, config.getLambda(), 0.0001);
        assertEquals(5, config.getMaxLag());
        assertEquals(0.05, config.getSignificanceLevel(), 0.0001);
        assertTrue(config.isAutoTune());
        assertTrue(config.isSeasonalAdjustment());
        assertTrue(config.isStationarityTest());
        assertEquals("eigenvector", config.getCentralityMethod());
        assertTrue(config.isDetectCommunities());
        assertTrue(config.isGenerateReport());
        assertEquals("pdf", config.getReportFormat());
        assertFalse(config.isVerbose());
    }
    
    @Test
    @DisplayName("等号格式长参数")
    void testEqualsSignFormat() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{
            "--algorithm=granger",
            "--lambda=0.1",
            "--max-lag=4"
        });
        
        assertEquals("granger", config.getAlgorithm());
        assertEquals(0.1, config.getLambda(), 0.0001);
        assertEquals(4, config.getMaxLag());
    }
    
    @Test
    @DisplayName("printHelp不抛出异常")
    void testPrintHelpDoesNotThrow() {
        assertDoesNotThrow(() -> CommandLineParser.printHelp());
    }
    
    @Test
    @DisplayName("printVersion不抛出异常")
    void testPrintVersionDoesNotThrow() {
        assertDoesNotThrow(() -> CommandLineParser.printVersion());
    }
    
    @Test
    @DisplayName("config toString正常输出")
    void testConfigToString() {
        CommandLineParser.AnalysisConfig config = parser.parse(new String[]{
            "-i", "test.csv",
            "--algorithm", "lasso"
        });
        
        String output = config.toString();
        assertNotNull(output);
        assertTrue(output.contains("MECN 分析配置"));
        assertTrue(output.contains("test.csv"));
        assertTrue(output.contains("lasso"));
    }
}
