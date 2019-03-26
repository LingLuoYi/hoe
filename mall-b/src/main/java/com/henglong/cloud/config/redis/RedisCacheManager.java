package com.henglong.cloud.config.redis;

import com.henglong.cloud.util.EhCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Service
public class RedisCacheManager implements CacheManager {


    @Autowired
    private RedisTemplate redisTemplate;

    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        Cache cache = caches.get(s);
        if(cache == null){
            cache = new EhCacheManager(redisTemplate);
            caches.put(s,cache);
        }
        return cache;
    }
}
