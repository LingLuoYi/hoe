package com.henglong.cloud.util;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 假装有ehcache
 * 实际存redis
 */
public class EhCacheManager implements Cache{

    private RedisTemplate redisTemplate;

    public EhCacheManager (){
        super();
    }

    public EhCacheManager(RedisTemplate redisTemplate){
        super();
        this.redisTemplate = redisTemplate;
    }

    @Override//获取
    public Object get(Object o) throws CacheException {
        return redisTemplate.opsForValue().get(o);
    }

    @Override//添加
    public Object put(Object o, Object o2) throws CacheException {
        redisTemplate.opsForValue().set("CLOUD_"+o, o2, 120, TimeUnit.SECONDS);
        return o;
    }

    @Override//删除
    public Object remove(Object o) throws CacheException {
        Object o1 = redisTemplate.opsForValue().get(o);
        redisTemplate.opsForValue().getOperations().delete(o);
        return o1;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set keys() {
        return null;
    }

    @Override
    public Collection values() {
        return null;
    }
}
