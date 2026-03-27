package com.mecn.data.provider;

import com.mecn.model.EconomicIndicator;
import com.mecn.model.TimeSeriesData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FRED (Federal Reserve Economic Data) API 数据提供者
 * 
 * 从圣路易斯联邦储备经济数据库获取宏观经济指标数据
 * 
 * API 文档：https://fred.stlouisfed.org/docs/api/fred/
 * 
 * @example
 * {@code
 * // 需要先在 FRED 官网申请 API Key
 * FredDataProvider provider = new FredDataProvider("your_api_key");
 * 
 * List<EconomicIndicator> indicators = Arrays.asList(
 *     new EconomicIndicator("GDP", "国内生产总值"),
 *     new EconomicIndicator("UNRATE", "失业率"),
 *     new EconomicIndicator("CPIAUCSL", "消费者价格指数")
 * );
 * 
 * List<TimeSeriesData> data = provider.fetch(indicators, 
 *     LocalDate.of(2020, 1, 1), 
 *     LocalDate.of(2023, 12, 31));
 * }
 */
public class FredDataProvider implements DataProvider {
    
    private static final String BASE_URL = "https://api.stlouisfed.org/fred/series/observations";
    private static final String FILE_TYPE = "json";
    
    private final String apiKey;
    private final List<EconomicIndicator> supportedIndicators;
    
    /**
     * 构造 FRED 数据提供者
     * 
     * @param apiKey FRED API Key（需要在 https://fred.stlouisfed.org/docs/api/api_key.html 申请）
     */
    public FredDataProvider(String apiKey) {
        this.apiKey = apiKey;
        this.supportedIndicators = loadSupportedIndicators();
    }
    
    @Override
    public List<TimeSeriesData> fetch(List<EconomicIndicator> indicators,
                                       LocalDate startDate,
                                       LocalDate endDate) {
        List<TimeSeriesData> results = new ArrayList<>();
        
        for (EconomicIndicator indicator : indicators) {
            try {
                TimeSeriesData data = fetchSeries(indicator.getCode(), startDate, endDate);
                if (data != null && data.getValues().length > 0) {
                    results.add(data);
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch data for " + indicator.getCode() + ": " + e.getMessage());
            }
        }
        
        return results;
    }
    
    /**
     * 获取单个时间序列数据
     */
    private TimeSeriesData fetchSeries(String seriesId, LocalDate startDate, LocalDate endDate) throws Exception {
        // 构建 API URL
        String urlString = String.format(
            "%s?series_id=%s&api_key=%s&file_type=%s&observation_start=%s&observation_end=%s",
            BASE_URL,
            seriesId,
            apiKey,
            FILE_TYPE,
            startDate.toString(),
            endDate.toString()
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
        
        // 解析 JSON 响应（简化实现，实际应该使用 JSON 库）
        return parseFredResponse(response.toString(), seriesId);
    }
    
    /**
     * 解析 FRED API 响应
     * 
     * 注意：这是一个简化实现，实际项目建议使用 Jackson 或 Gson 等 JSON 库
     */
    private TimeSeriesData parseFredResponse(String jsonResponse, String seriesId) {
        // 简化实现：这里应该使用 JSON 解析库
        // 示例代码展示了如何解析 FRED 的 JSON 响应
        // 实际使用时需要完善这部分代码
        
        List<Double> values = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>();
        
        // TODO: 使用 JSON 库解析响应
        // 伪代码示例：
        // JSONObject json = new JSONObject(jsonResponse);
        // JSONArray observations = json.getJSONArray("observations");
        // for (int i = 0; i < observations.length(); i++) {
        //     JSONObject obs = observations.getJSONObject(i);
        //     String dateStr = obs.getString("date");
        //     String valueStr = obs.getString("value");
        //     
        //     if (!".".equals(valueStr)) {
        //         dates.add(LocalDate.parse(dateStr));
        //         values.add(Double.parseDouble(valueStr));
        //     }
        // }
        
        if (values.isEmpty()) {
            return null;
        }
        
        TimeSeriesData data = new TimeSeriesData(seriesId, seriesId);
        data.setDates(dates.toArray(new LocalDate[0]));
        data.setValues(values.stream().mapToDouble(Double::doubleValue).toArray());
        
        return data;
    }
    
    @Override
    public List<EconomicIndicator> getSupportedIndicators() {
        return new ArrayList<>(supportedIndicators);
    }
    
    @Override
    public String getName() {
        return "FRED";
    }
    
    @Override
    public boolean isAvailable() {
        // 简单的可用性检查
        return apiKey != null && !apiKey.trim().isEmpty();
    }
    
    /**
     * 加载支持的指标列表
     * 
     * 这里列出常用的 FRED 经济指标
     */
    private List<EconomicIndicator> loadSupportedIndicators() {
        List<EconomicIndicator> indicators = new ArrayList<>();
        
        // 国民经济核算
        indicators.add(new EconomicIndicator("GDP", "国内生产总值"));
        indicators.add(new EconomicIndicator("GNP", "国民生产总值"));
        indicators.add(new EconomicIndicator("GDPC1", "实际国内生产总值"));
        
        // 就业与劳动力市场
        indicators.add(new EconomicIndicator("UNRATE", "失业率"));
        indicators.add(new EconomicIndicator("PAYEMS", "非农就业人数"));
        indicators.add(new EconomicIndicator("LNS14800000", "劳动参与率"));
        
        // 价格水平与通胀
        indicators.add(new EconomicIndicator("CPIAUCSL", "消费者价格指数"));
        indicators.add(new EconomicIndicator("PCEPI", "个人消费支出价格指数"));
        indicators.add(new EconomicIndicator("WPU050000", "生产者价格指数"));
        
        // 利率与货币
        indicators.add(new EconomicIndicator("FEDFUNDS", "联邦基金利率"));
        indicators.add(new EconomicIndicator("DGS10", "10 年期国债收益率"));
        indicators.add(new EconomicIndicator("M2SL", "M2 货币供应量"));
        
        // 国际贸易
        indicators.add(new EconomicIndicator("EXUSUK", "美元/英镑汇率"));
        indicators.add(new EconomicIndicator("DEXJPUS", "日元/美元汇率"));
        indicators.add(new EconomicIndicator("TWEXB", "贸易加权美元指数"));
        
        // 商业周期
        indicators.add(new EconomicIndicator("INDPRO", "工业生产指数"));
        indicators.add(new EconomicIndicator("MANEMP", "制造业就业人数"));
        indicators.add(new EconomicIndicator("UMCSENT", "密歇根大学消费者信心指数"));
        
        // 房地产市场
        indicators.add(new EconomicIndicator("CASESHILLER", "标普/CS 房价指数"));
        indicators.add(new EconomicIndicator("USSTHTR", "新屋开工数"));
        indicators.add(new EconomicIndicator("MSACR", "房贷逾期率"));
        
        return indicators;
    }
}
