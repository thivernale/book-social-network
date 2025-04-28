package org.thivernale.booknetwork.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.data.redis.cache.RedisCache;

@RequiredArgsConstructor
public class CustomCacheInterceptor extends CacheInterceptor {
    private final CacheManager caffeineCacheManager;

    @Override
    protected Cache.ValueWrapper doGet(Cache cache, Object key) {
        Cache.ValueWrapper valueWrapper = super.doGet(cache, key);

        if (valueWrapper != null && cache.getClass()
            .equals(RedisCache.class)) {
            Cache caffeineCache = caffeineCacheManager.getCache(cache.getName());
            if (caffeineCache != null) {
                caffeineCache.putIfAbsent(key, valueWrapper.get());
            }
        }
        return valueWrapper;
    }
}
