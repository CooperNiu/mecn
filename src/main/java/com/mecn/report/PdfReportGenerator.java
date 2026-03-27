package com.mecn.report;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.text.pdf.PdfFontFactory;
import com.mecn.causal.CausalResult;
import com.mecn.model.NetworkGraph;
import com.mecn.network.CentralityResult;
import com.mecn.network.SystemicImportance;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * PDF 报告生成器
 * 
 * 生成宏观经济网络分析的 PDF 报告
 * 
 * @example
 * {@code
 * PdfReportGenerator generator = new PdfReportGenerator();
 * byte[] pdfBytes = generator.generateCausalAnalysisReport(
 *     causalResult,
 *     network,
 *     centralityResults,
 *     systemicImportanceList,
 *     "宏观经济网络分析报告"
 * );
 * 
 * // 保存到文件
 * Files.write(Paths.get("report.pdf"), pdfBytes);
 * }
 */
public class PdfReportGenerator {
    
    private static final String TITLE_FONT = "Helvetica-Bold";
    private static final String NORMAL_FONT = "Helvetica";
    
    /**
     * 生成因果分析报告
     */
    public byte[] generateCausalAnalysisReport(CausalResult causalResult,
                                                NetworkGraph network,
                                                List<CentralityResult> centralityResults,
                                                List<SystemicImportance> systemicImportance,
                                                String reportTitle) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {
            
            // 添加标题
            addTitle(document, reportTitle);
            
            // 添加报告元信息
            addMetadata(document, causalResult);
            
            // 添加执行摘要
            addExecutiveSummary(document, causalResult, network);
            
            // 添加网络统计信息
            addNetworkStatistics(document, network);
            
            // 添加中心性分析结果
            addCentralityAnalysis(document, centralityResults);
            
            // 添加系统重要性分析
            addSystemicImportanceAnalysis(document, systemicImportance);
            
            // 添加关键发现和建议
            addKeyFindings(document, causalResult, network, centralityResults);
        }
        
        return baos.toByteArray();
    }
    
    /**
     * 添加标题
     */
    private void addTitle(Document document, String title) {
        Paragraph titleParagraph = new Paragraph(title)
            .setFont(PdfFontFactory.createRegisteredFont(TITLE_FONT))
            .setFontSize(24)
            .setTextAlignment(TextAlignment.CENTER)
            .setBold()
            .setMarginBottom(20);
        
        document.add(titleParagraph);
        
        // 添加副标题/日期
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日"));
        Paragraph subtitle = new Paragraph("报告生成日期：" + dateStr)
            .setFont(PdfFontFactory.createRegisteredFont(NORMAL_FONT))
            .setFontSize(12)
            .setTextAlignment(TextAlignment.CENTER)
            .setItalic()
            .setMarginBottom(30);
        
        document.add(subtitle);
    }
    
    /**
     * 添加报告元信息
     */
    private void addMetadata(Document document, CausalResult causalResult) {
        document.add(new Paragraph("报告元信息")
            .setFont(PdfFontFactory.createRegisteredFont(TITLE_FONT))
            .setFontSize(16)
            .setBold()
            .setMarginTop(10)
            .setMarginBottom(10));
        
        Table table = new Table(2);
        table.setWidth(100);
        
        Map<String, Object> metadata = causalResult.getMetadata();
        
        addTableRow(table, "分析方法", (String) metadata.get("method"));
        addTableRow(table, "变量数量", String.valueOf(metadata.get("numVariables")));
        addTableRow(table, "样本数量", String.valueOf(metadata.get("numSamples")));
        
        if (metadata.containsKey("significanceLevel")) {
            addTableRow(table, "显著性水平", String.valueOf(metadata.get("significanceLevel")));
        }
        
        document.add(table);
    }
    
    /**
     * 添加执行摘要
     */
    private void addExecutiveSummary(Document document, CausalResult causalResult, NetworkGraph network) {
        document.add(new Paragraph("执行摘要")
            .setFont(PdfFontFactory.createRegisteredFont(TITLE_FONT))
            .setFontSize(16)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10));
        
        double[][] adjacencyMatrix = causalResult.getAdjacencyMatrix();
        int numEdges = countNonZeroEdges(adjacencyMatrix);
        
        Paragraph summary = new Paragraph()
            .setFont(PdfFontFactory.createRegisteredFont(NORMAL_FONT))
            .setFontSize(11)
            .add("本次分析共识别出 ")
            .add(String.valueOf(numEdges))
            .add(" 条显著的因果关系边，涉及 ")
            .add(String.valueOf(network.getNodes().size()))
            .add(" 个经济指标。网络密度为 ")
            .add(String.format("%.4f", getNetworkDensity(network)))
            .add("。");
        
        document.add(summary);
    }
    
    /**
     * 添加网络统计信息
     */
    private void addNetworkStatistics(Document document, NetworkGraph network) {
        document.add(new Paragraph("网络统计信息")
            .setFont(PdfFontFactory.createRegisteredFont(TITLE_FONT))
            .setFontSize(16)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10));
        
        Table table = new Table(2);
        table.setWidth(100);
        
        Map<String, Object> stats = network.getNetworkStatistics();
        
        addTableRow(table, "节点数量", String.valueOf(stats.get("nodeCount")));
        addTableRow(table, "边数量", String.valueOf(stats.get("edgeCount")));
        addTableRow(table, "网络密度", String.format("%.4f", stats.get("density")));
        addTableRow(table, "平均度", String.format("%.2f", stats.get("averageDegree")));
        
        document.add(table);
    }
    
    /**
     * 添加中心性分析结果
     */
    private void addCentralityAnalysis(Document document, List<CentralityResult> centralityResults) {
        document.add(new Paragraph("中心性分析")
            .setFont(PdfFontFactory.createRegisteredFont(TITLE_FONT))
            .setFontSize(16)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10));
        
        // 添加 Top 10 重要节点
        document.add(new Paragraph("Top 10 重要节点（按综合得分）")
            .setFont(PdfFontFactory.createRegisteredFont(NORMAL_FONT))
            .setFontSize(14)
            .setBold()
            .setMarginBottom(10));
        
        Table table = new Table(6);
        table.setWidth(100);
        
        // 表头
        table.addHeaderCell("排名");
        table.addHeaderCell("节点 ID");
        table.addHeaderCell("度中心性");
        table.addHeaderCell("中介中心性");
        table.addHeaderCell("PageRank");
        table.addHeaderCell("综合得分");
        
        // 数据行
        int rank = 1;
        for (CentralityResult result : centralityResults) {
            if (rank > 10) break;
            
            table.addCell(String.valueOf(rank++));
            table.addCell(result.getNodeId());
            table.addCell(formatDouble(result.getDegreeCentrality()));
            table.addCell(formatDouble(result.getBetweennessCentrality()));
            table.addCell(formatDouble(result.getPageRank()));
            table.addCell(formatDouble(result.getCompositeScore()));
        }
        
        document.add(table);
    }
    
    /**
     * 添加系统重要性分析
     */
    private void addSystemicImportanceAnalysis(Document document, List<SystemicImportance> importanceList) {
        document.add(new Paragraph("系统重要性分析")
            .setFont(PdfFontFactory.createRegisteredFont(TITLE_FONT))
            .setFontSize(16)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10));
        
        document.add(new Paragraph("以下节点对网络整体效率影响最大：")
            .setFont(PdfFontFactory.createRegisteredFont(NORMAL_FONT))
            .setFontSize(11)
            .setMarginBottom(10));
        
        Table table = new Table(4);
        table.setWidth(100);
        
        // 表头
        table.addHeaderCell("排名");
        table.addHeaderCell("节点 ID");
        table.addHeaderCell("重要性得分");
        table.addHeaderCell("网络效率损失");
        
        // 数据行
        int rank = 1;
        for (SystemicImportance importance : importanceList) {
            if (rank > 10) break;
            
            table.addCell(String.valueOf(rank++));
            table.addCell(importance.getNodeId());
            table.addCell(formatDouble(importance.getImportanceScore()));
            table.addCell(formatDouble(importance.getNetworkEfficiencyLoss()));
        }
        
        document.add(table);
    }
    
    /**
     * 添加关键发现和建议
     */
    private void addKeyFindings(Document document, CausalResult causalResult, 
                               NetworkGraph network, List<CentralityResult> centralityResults) {
        document.add(new Paragraph("关键发现与建议")
            .setFont(PdfFontFactory.createRegisteredFont(TITLE_FONT))
            .setFontSize(16)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10));
        
        // 找出最重要的节点
        CentralityResult topNode = centralityResults.stream()
            .max((a, b) -> Double.compare(a.getCompositeScore(), b.getCompositeScore()))
            .orElse(null);
        
        if (topNode != null) {
            Paragraph finding = new Paragraph()
                .setFont(PdfFontFactory.createRegisteredFont(NORMAL_FONT))
                .setFontSize(11)
                .add("1. 关键节点识别：")
                .add(topNode.getNodeId())
                .add(" 是网络中最重要的节点，其综合得分为 ")
                .add(String.format("%.4f", topNode.getCompositeScore()))
                .add("。建议对该指标进行重点监测。");
            
            document.add(finding);
        }
        
        // 网络密度分析
        double density = getNetworkDensity(network);
        String densityAssessment = density > 0.3 ? "较高" : (density < 0.1 ? "较低" : "中等");
        
        Paragraph densityFinding = new Paragraph()
            .setFont(PdfFontFactory.createRegisteredFont(NORMAL_FONT))
            .setFontSize(11)
            .add("2. 网络结构特征：网络密度为 ")
            .add(String.format("%.4f", density))
            .add("，属于")
            .add(densityAssessment)
            .add("水平。");
        
        document.add(densityFinding);
        
        // 风险管理建议
        Paragraph recommendation = new Paragraph()
            .setFont(PdfFontFactory.createRegisteredFont(NORMAL_FONT))
            .setFontSize(11)
            .add("3. 风险管理建议：建立对关键节点的实时监控机制，制定风险预警和应对预案，")
            .add("防止系统性风险的传播和扩散。");
        
        document.add(recommendation);
    }
    
    /**
     * 辅助方法：添加表格行
     */
    private void addTableRow(Table table, String label, String value) {
        table.addCell(new Cell()
            .add(new Paragraph(label)
                .setFont(PdfFontFactory.createRegisteredFont(NORMAL_FONT))
                .setFontSize(11)
                .setBold()));
        
        table.addCell(new Cell()
            .add(new Paragraph(value != null ? value : "N/A")
                .setFont(PdfFontFactory.createRegisteredFont(NORMAL_FONT))
                .setFontSize(11)));
    }
    
    /**
     * 辅助方法：格式化浮点数
     */
    private String formatDouble(Double value) {
        return value != null ? String.format("%.4f", value) : "N/A";
    }
    
    /**
     * 辅助方法：统计非零边数量
     */
    private int countNonZeroEdges(double[][] matrix) {
        int count = 0;
        for (double[] row : matrix) {
            for (double value : row) {
                if (value != 0) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * 辅助方法：计算网络密度
     */
    private double getNetworkDensity(NetworkGraph network) {
        Map<String, Object> stats = network.getNetworkStatistics();
        Object densityObj = stats.get("density");
        return densityObj instanceof Number ? ((Number) densityObj).doubleValue() : 0.0;
    }
}
