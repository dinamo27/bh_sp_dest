package com.example.service;

/**
 * Service interface for cache management operations
 */
public interface CacheService {
    
    /**
     * Invalidates a cache entry with the specified key
     *
     * @param cacheKey the key of the cache entry to invalidate
     */
    void invalidate(String cacheKey);
    
    /**
     * Invalidates multiple cache entries with the specified keys
     *
     * @param cacheKeys the keys of the cache entries to invalidate
     */
    void invalidateAll(String... cacheKeys);
}