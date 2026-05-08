package com.mecn.datahub.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis Cache Service for MECN DataHub
 * 
 * Provides caching for:
 * - Economic indicator data from external APIs
 * - Causal network analysis results
 * - Processed time series data
 * 
 * Note: This service is optional and only active when Redis is available.
 */
@Service
@ConditionalOnBean(RedisTemplate.class)
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    
    private static final String PREFIX = "mecn:";
    private static final long DEFAULT_TTL_HOURS = 1;
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * Get cached data by key
     * @param key Cache key
     * @return Cached value or null
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(PREFIX + key);
        } catch (Exception e) {
            log.warn("Failed to get cache for key {}: {}", key, e.getMessage());
            return null;
        }
    }
    
    /**
     * Get cached data by key with type
     * @param key Cache key
     * @param type Expected type
     * @return Cached value or null
     */
    public <T> T get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(PREFIX + key);
            if (type.isInstance(value)) {
                return type.cast(value);
            }
            return null;
        } catch (Exception e) {
            log.warn("Failed to get cache for key {}: {}", key, e.getMessage());
            return null;
        }
    }
    
    /**
     * Put data to cache with default TTL
     * @param key Cache key
     * @param value Value to cache
     */
    public void put(String key, Object value) {
        put(key, value, DEFAULT_TTL_HOURS, TimeUnit.HOURS);
    }
    
    /**
     * Put data to cache with custom TTL
     * @param key Cache key
     * @param value Value to cache
     * @param ttl Time to live
     * @param unit Time unit
     */
    public void put(String key, Object value, long ttl, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(PREFIX + key, value, ttl, unit);
            log.debug("Cached data for key: {}", key);
        } catch (Exception e) {
            log.warn("Failed to cache data for key {}: {}", key, e.getMessage());
        }
    }
    
    /**
     * Remove cached data
     * @param key Cache key
     */
    public void evict(String key) {
        try {
            redisTemplate.delete(PREFIX + key);
            log.debug("Evicted cache for key: {}", key);
        } catch (Exception e) {
            log.warn("Failed to evict cache for key {}: {}", key, e.getMessage());
        }
    }
    
    /**
     * Check if key exists in cache
     * @param key Cache key
     * @return true if exists
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + key));
        } catch (Exception e) {
            log.warn("Failed to check cache existence for key {}: {}", key, e.getMessage());
            return false;
        }
    }
    
    /**
     * Clear all MECN cached data
     */
    public void clearAll() {
        try {
            var keys = redisTemplate.keys(PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Cleared {} cached entries", keys.size());
            }
        } catch (Exception e) {
            log.warn("Failed to clear cache: {}", e.getMessage());
        }
    }
    
    /**
     * Get cache key for economic indicator
     * @param source Data source (FRED, WORLDBANK, AKSHARE)
     * @param indicator Indicator code
     * @return Cache key
     */
    public String getIndicatorKey(String source, String indicator) {
        return String.format("indicator:%s:%s", source.toUpperCase(), indicator);
    }
    
    /**
     * Get cache key for causal network
     * @param networkId Network identifier
     * @return Cache key
     */
    public String getNetworkKey(String networkId) {
        return String.format("network:%s", networkId);
    }
}
