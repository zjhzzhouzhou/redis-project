package com.zz.redis.rediscluster.redis.support;


import com.zz.redis.rediscluster.redis.cluster.JedisClusterCache;
import com.zz.redis.rediscluster.redis.cluster.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

public abstract class SeniorGenericCacheManager<V> implements SeniorGenericCache<V>{
    protected final static Logger log = LoggerFactory.getLogger(SeniorGenericCacheManager.class);
    @Autowired
    protected JedisClusterCache jedisClusterCache;
    protected long expireTime = 0;

    /**
     * 获取失效时间配置值
     * @return
     */
    protected abstract long getExpireTime();
    protected abstract  String getKey(Map param);
    protected abstract V getObject(Map param);


    private Class<V> getClassV(){
        Class<V> clazzV = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return clazzV;
    }
    protected V getCache(Map param) {
        V cacheResult =  jedisClusterCache.get(getKey(param), getClassV());
        return cacheResult;
    }

    @Override
    public V get(Map param) {
        try {
            V object = getCache(param);
            if (object != null) {
                return object;
            }

            // 缓存中没有,从数据源中获取
            object = getObject(param);

            // 从数据源中获取或新建完成后,更新缓存
            if (object != null) {
                this.update(param, object);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public void del(Map param) {
        jedisClusterCache.del(getKey(param));
    }

    @Override
    public void update(Map param, V object) {
        if(getExpireTime()<=0){
            jedisClusterCache.set(getKey(param),object);
            jedisClusterCache.persist(getKey(param));
        }else{
            if(exists(param)){
                jedisClusterCache.set(getKey(param), object, RedisCache.KEY_XX,RedisCache.EXPIRE_TIME_UNIT_EX,getExpireTime());
            }else{
                jedisClusterCache.set(getKey(param),object,RedisCache.KEY_NX,RedisCache.EXPIRE_TIME_UNIT_EX,getExpireTime());
            }
        }
    }

    @Override
    public Boolean exists(Map param) {
        return jedisClusterCache.exists(getKey(param));
    }

    @Override
    public Long expire(Map param, int seconds) {
        return jedisClusterCache.expire(getKey(param), seconds);
    }

    @Override
    public Long pexpire(Map param, long milliseconds) {
        return jedisClusterCache.pexpire(getKey(param), milliseconds);
    }
}
