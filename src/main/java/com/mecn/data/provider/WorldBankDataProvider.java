package com.mecn.data.provider;

import com.mecn.model.EconomicIndicator;
import com.mecn.model.TimeSeriesData;

import javax.json.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * World Bank (世界银行) API 数据提供者
 * 
 * 从世界银行公开数据库获取全球各国宏观经济指标数据
 * 
 * API 文档：https://datahelpdesk.worldbank.org/knowledgebase/topics/125589
 * 
 * @example
 * {@code
 * WorldBankDataProvider provider = new WorldBankDataProvider();
 * 
 * List<EconomicIndicator> indicators = Arrays.asList(
 *     new EconomicIndicator("NY.GDP.MKTP.CD", "GDP (current US$)"),
 *     new EconomicIndicator("SP.POP.TOTL", "Total Population"),
 *     new EconomicIndicator("FP.CPI.TOTL.ZG", "Inflation, consumer prices (annual %)")
 * );
 * 
 * // 获取中国的数据
 * List<TimeSeriesData> data = provider.fetch(indicators, 
 *     LocalDate.of(2020, 1, 1), 
 *     LocalDate.of(2023, 12, 31),
 *     "CHN");  // 国家代码
 * }
 */
public class WorldBankDataProvider implements DataProvider {
    
    private static final String BASE_URL = "https://api.worldbank.org/v2/country";
    private static final String FORMAT = "json";
    
    private final List<EconomicIndicator> supportedIndicators;
    
    public WorldBankDataProvider() {
        this.supportedIndicators = loadSupportedIndicators();
    }
    
    /**
     * 获取指定国家的数据
     * 
     * @param indicators 指标列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param countryCode 国家代码（如 "CHN", "USA", "JPN"）
     * @return 时间序列数据列表
     */
    public List<TimeSeriesData> fetch(List<EconomicIndicator> indicators,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       String countryCode) {
        List<TimeSeriesData> results = new ArrayList<>();
        
        for (EconomicIndicator indicator : indicators) {
            try {
                TimeSeriesData data = fetchIndicator(countryCode, indicator.getCode(), 
                    startDate.getYear(), endDate.getYear());
                if (data != null && data.getValues().length > 0) {
                    results.add(data);
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch data for " + indicator.getCode() + ": " + e.getMessage());
            }
        }
        
        return results;
    }
    
    @Override
    public List<TimeSeriesData> fetch(List<EconomicIndicator> indicators,
                                       LocalDate startDate,
                                       LocalDate endDate) {
        // 默认获取全世界汇总数据
        return fetch(indicators, startDate, endDate, "all");
    }
    
    /**
     * 获取单个指标数据
     */
    private TimeSeriesData fetchIndicator(String country, String indicatorId, 
                                          int startYear, int endYear) throws Exception {
        // 构建 API URL
        String urlString = String.format(
            "%s/%s/indicator/%s?date=%d:%d&format=%s",
            BASE_URL,
            country,
            indicatorId,
            startYear,
            endYear,
            FORMAT
        );
        
        // 发送 HTTP 请求
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("API request failed with code: " + responseCode);
        }
        
        // 读取响应
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        
        // 解析 JSON 响应
        return parseWorldBankResponse(response.toString(), indicatorId);
    }
    
    /**
     * 解析 World Bank API 响应
     * 
     * World Bank API 响应格式示例：
     * [
     *   {"page": 1, "pages": 1, "per_page": 50, "total": 30},
     *   [
     *     {
     *       "indicator": {"id": "NY.GDP.MKTP.CD", "value": "GDP"},
     *       "country": {"id": "US", "value": "United States"},
     *       "date": "2020",
     *       "value": 20893746.58
     *     }
     *   ]
     * ]
     */
    private TimeSeriesData parseWorldBankResponse(String jsonResponse, String indicatorId) {
        List<Double> values = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>();
        
        try {
            // 使用 javax.json 解析 JSON 响应
            JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse));
            JsonArray jsonArray = jsonReader.readArray();
            jsonReader.close();
            
            // World Bank API 返回格式：[metadata, data]
            // 数据在第二个元素（索引 1）
            if (jsonArray.size() < 2) {
                return new TimeSeriesData(indicatorId, new LocalDate[0], new double[0]);
            }
            
            JsonArray dataArray = jsonArray.getJsonArray(1);
            if (dataArray == null || dataArray.isEmpty()) {
                return new TimeSeriesData(indicatorId, new LocalDate[0], new double[0]);
            }
            
            for (JsonValue item : dataArray) {
                JsonObject dataItem = (JsonObject) item;
                String dateStr = dataItem.getString("date", null);
                JsonValue valueObj = dataItem.get("value");
                
                // 跳过缺失值
                if (dateStr != null && valueObj != null && !JsonValue.NULL.equals(valueObj)) {
                    try {
                        int year = Integer.parseInt(dateStr);
                        Double value = null;
                        
                        if (valueObj.getValueType() == JsonValue.ValueType.NUMBER) {
                            value = ((JsonNumber) valueObj).doubleValue();
                        } else if (valueObj.getValueType() == JsonValue.ValueType.STRING) {
                            // 尝试解析字符串形式的数字
                            String valueStr = ((JsonString) valueObj).getString();
                            if (!"null".equals(valueStr) && !valueStr.isEmpty()) {
                                value = Double.parseDouble(valueStr);
                            }
                        }
                        
                        if (value != null) {
                            dates.add(LocalDate.of(year, 1, 1));
                            values.add(value);
                        }
                    } catch (Exception e) {
                        // 跳过无法解析的数据
                        System.err.println("Failed to parse data item: " + dateStr + " = " + valueObj);
                    }
                }
            }
            
            // 转换为数组
            double[] valuesArray = values.stream().mapToDouble(Double::doubleValue).toArray();
            LocalDate[] datesArray = dates.toArray(new LocalDate[0]);
            
            return new TimeSeriesData(indicatorId, datesArray, valuesArray);
            
        } catch (JsonException e) {
            System.err.println("JSON parsing error for indicator " + indicatorId + ": " + e.getMessage());
            return new TimeSeriesData(indicatorId, new LocalDate[0], new double[0]);
        }
    }
    
    @Override
    public List<EconomicIndicator> getSupportedIndicators() {
        return new ArrayList<>(supportedIndicators);
    }
    
    @Override
    public String getName() {
        return "WorldBank";
    }
    
    /**
     * 加载支持的指标列表
     * 
     * 这里列出常用的 World Bank 经济指标
     */
    private List<EconomicIndicator> loadSupportedIndicators() {
        List<EconomicIndicator> indicators = new ArrayList<>();
        
        // 国民经济核算
        indicators.add(new EconomicIndicator("NY.GDP.MKTP.CD", "GDP (current US$)"));
        indicators.add(new EconomicIndicator("NY.GDP.MKTP.KD.ZG", "GDP growth (annual %)"));
        indicators.add(new EconomicIndicator("NY.GDP.PCAP.CD", "GDP per capita (current US$)"));
        indicators.add(new EconomicIndicator("NY.GNP.MKTP.CD", "GNI (current US$)"));
        
        // 人口与就业
        indicators.add(new EconomicIndicator("SP.POP.TOTL", "Total Population"));
        indicators.add(new EconomicIndicator("SP.URB.TOTL.IN.ZS", "Urban population (% of total)"));
        indicators.add(new EconomicIndicator("SL.UEM.TOTL.ZS", "Unemployment, total (% of total labor force)"));
        indicators.add(new EconomicIndicator("SL.TLF.TOTL.IN", "Labor force, total"));
        
        // 价格水平与通胀
        indicators.add(new EconomicIndicator("FP.CPI.TOTL.ZG", "Inflation, consumer prices (annual %)"));
        indicators.add(new EconomicIndicator("PA.NUS.FCRF", "Official exchange rate (LCU per US$)"));
        
        // 国际贸易与投资
        indicators.add(new EconomicIndicator("BN.CAB.XOKA.CD", "Current account balance (BoP, current US$)"));
        indicators.add(new EconomicIndicator("NE.EXP.GNFS.CD", "Exports of goods and services (current US$)"));
        indicators.add(new EconomicIndicator("NE.IMP.GNFS.CD", "Imports of goods and services (current US$)"));
        indicators.add(new EconomicIndicator("BX.KLT.DINV.CD.WD", "Foreign direct investment, net inflows (BoP, current US$)"));
        
        // 财政与金融
        indicators.add(new EconomicIndicator("GC.REV.TOTL.GD.ZS", "Revenue, general government (% of GDP)"));
        indicators.add(new EconomicIndicator("GC.XPN.TOTL.GD.ZS", "Expenses, general government (% of GDP)"));
        indicators.add(new EconomicIndicator("FM.AST.PRVT.GD.ZS", "Domestic credit to private sector (% of GDP)"));
        
        // 贫困与不平等
        indicators.add(new EconomicIndicator("SI.POV.DDAY", "Poverty headcount ratio at $2.15 a day (2017 PPP) (%)"));
        indicators.add(new EconomicIndicator("SI.GINI.COEFF", "Gini index"));
        
        // 环境与能源
        indicators.add(new EconomicIndicator("EN.ATM.CO2E.PC", "CO2 emissions (metric tons per capita)"));
        indicators.add(new EconomicIndicator("EG.USE.COMM.FO.ZS", "Fossil fuel energy consumption (% of total)"));
        indicators.add(new EconomicIndicator("AG.LND.FRST.ZS", "Forest area (% of land area)"));
        
        return indicators;
    }
}
