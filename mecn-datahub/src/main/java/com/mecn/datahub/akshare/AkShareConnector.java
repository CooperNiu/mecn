package com.mecn.datahub.akshare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AkShare Data Connector
 * 
 * Connects to AkShare API for Chinese market data including:
 * - A-share stock indices (上证指数, 深证成指, 创业板指)
 * - Economic indicators (GDP, CPI, PMI, etc.)
 * - Commodity futures
 * - Foreign exchange rates
 * 
 * AkShare is an open-source financial data interface library for Python
 * This connector provides Java bindings for the REST API
 */
@Component
public class AkShareConnector {

    private static final Logger log = LoggerFactory.getLogger(AkShareConnector.class);
    
    private static final String BASE_URL = "https://api.akshare.xyz";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    private final RestTemplate restTemplate;
    private final Map<String, List<Map<String, Object>>> cache = new ConcurrentHashMap<>();
    
    // Key Chinese economic indicators
    public static final String INDICATOR_GDP = "CHINA_GDP";
    public static final String INDICATOR_CPI = "CHINA_CPI";
    public static final String INDICATOR_PMI = "CHINA_PMI";
    public static final String INDICATOR_PPI = "CHINA_PPI";
    public static final String INDICATOR_M2 = "CHINA_M2";
    public static final String INDICATOR_SHIBOR = "CHINA_SHIBOR";
    
    // Stock indices
    public static final String INDEX_SSE = "000001";      // 上证指数
    public static final String INDEX_SZSE = "399001";     // 深证成指
    public static final String INDEX_GEM = "399006";      // 创业板指
    
    public AkShareConnector() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Get Chinese GDP data
     * @return List of GDP records
     */
    public List<Map<String, Object>> getChinaGDP() {
        return fetchData("/macro/china/gdp", INDICATOR_GDP);
    }
    
    /**
     * Get Chinese CPI data
     * @return List of CPI records
     */
    public List<Map<String, Object>> getChinaCPI() {
        return fetchData("/macro/china/cpi", INDICATOR_CPI);
    }
    
    /**
     * Get Chinese PMI data
     * @return List of PMI records
     */
    public List<Map<String, Object>> getChinaPMI() {
        return fetchData("/macro/china/pmi", INDICATOR_PMI);
    }
    
    /**
     * Get Chinese PPI data
     * @return List of PPI records
     */
    public List<Map<String, Object>> getChinaPPI() {
        return fetchData("/macro/china/ppi", INDICATOR_PPI);
    }
    
    /**
     * Get Chinese M2 money supply
     * @return List of M2 records
     */
    public List<Map<String, Object>> getChinaM2() {
        return fetchData("/macro/china/m2_yearly", INDICATOR_M2);
    }
    
    /**
     * Get SHIBOR rates
     * @return List of SHIBOR records
     */
    public List<Map<String, Object>> getSHIBOR() {
        return fetchData("/macro/china/shibor", INDICATOR_SHIBOR);
    }
    
    /**
     * Get stock index historical data
     * @param symbol Index symbol (e.g., "000001" for SSE)
     * @param startDate Start date (yyyyMMdd)
     * @param endDate End date (yyyyMMdd)
     * @return List of index records
     */
    public List<Map<String, Object>> getStockIndexHistory(String symbol, String startDate, String endDate) {
        String url = String.format("/stock_zh_index_daily_em?symbol=%s&start_date=%s&end_date=%s", 
            symbol, startDate, endDate);
        return fetchData(url, "INDEX_" + symbol);
    }
    
    /**
     * Get all available Chinese economic indicators
     * @return Map of indicator names to their data
     */
    public Map<String, List<Map<String, Object>>> getAllChineseIndicators() {
        Map<String, List<Map<String, Object>>> indicators = new HashMap<>();
        
        indicators.put("GDP", getChinaGDP());
        indicators.put("CPI", getChinaCPI());
        indicators.put("PMI", getChinaPMI());
        indicators.put("PPI", getChinaPPI());
        indicators.put("M2", getChinaM2());
        indicators.put("SHIBOR", getSHIBOR());
        
        return indicators;
    }
    
    /**
     * Get indicator data for MECN network analysis
     * Converts AkShare data to MECN-compatible format
     * 
     * @param indicatorType Type of indicator
     * @return Time series data in MECN format
     */
    public Map<String, Object> getIndicatorForMECN(String indicatorType) {
        List<Map<String, Object>> rawData;
        
        switch (indicatorType) {
            case "GDP":
                rawData = getChinaGDP();
                break;
            case "CPI":
                rawData = getChinaCPI();
                break;
            case "PMI":
                rawData = getChinaPMI();
                break;
            case "PPI":
                rawData = getChinaPPI();
                break;
            case "M2":
                rawData = getChinaM2();
                break;
            case "SHIBOR":
                rawData = getSHIBOR();
                break;
            default:
                throw new IllegalArgumentException("Unknown indicator type: " + indicatorType);
        }
        
        return convertToMECNFormat(indicatorType, rawData);
    }
    
    private List<Map<String, Object>> fetchData(String endpoint, String cacheKey) {
        if (cache.containsKey(cacheKey)) {
            log.debug("Returning cached data for: {}", cacheKey);
            return cache.get(cacheKey);
        }
        
        try {
            String url = BASE_URL + endpoint;
            log.info("Fetching data from: {}", url);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> data = restTemplate.getForObject(url, List.class);
            
            if (data != null) {
                cache.put(cacheKey, data);
                log.info("Successfully fetched {} records for: {}", data.size(), cacheKey);
            }
            
            return data != null ? data : new ArrayList<>();
            
        } catch (HttpClientErrorException e) {
            log.error("Failed to fetch data from {}: {}", endpoint, e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Unexpected error fetching data from {}: {}", endpoint, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private Map<String, Object> convertToMECNFormat(String indicatorType, List<Map<String, Object>> rawData) {
        Map<String, Object> result = new HashMap<>();
        result.put("indicator", indicatorType);
        result.put("source", "AKSHARE");
        result.put("records", rawData.size());
        
        // Convert to time series format for MECN
        List<Map<String, Object>> timeSeries = new ArrayList<>();
        for (Map<String, Object> record : rawData) {
            Map<String, Object> point = new HashMap<>();
            // AkShare typically returns date and value fields
            point.put("date", record.getOrDefault("日期", record.getOrDefault("date", "")));
            point.put("value", record.getOrDefault("值", record.getOrDefault("value", 0)));
            timeSeries.add(point);
        }
        
        result.put("timeSeries", timeSeries);
        return result;
    }
    
    /**
     * Clear cache
     */
    public void clearCache() {
        cache.clear();
        log.info("AkShare cache cleared");
    }
}
