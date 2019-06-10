package com.zz.redis.rediscluster.redis.support;


import com.zz.redis.rediscluster.redis.cluster.JedisClusterCache;
import com.zz.redis.rediscluster.redis.cluster.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GenericCacheManager<PK, V> implements
		GenericCache<PK, V> {
	protected final static Logger log = LoggerFactory.getLogger(GenericCacheManager.class);
    @Autowired
	protected JedisClusterCache jedisClusterCache;
	protected long expireTime = 0;

    /**
     * 获取redis key
     * @param id
     * @return
     */
	protected abstract String getKey(PK id);

    /**
     * 获取失效时间配置值
     * @return
     */
	protected abstract long getExpireTime();

	protected V getCache(PK id) {
		V cacheResult =  jedisClusterCache.get(getKey(id), getClassV());
		return cacheResult;
	}

	@Override
	public V get(PK id) {
		try {
			V object = getCache(id);
			if (object != null) {
                return object;
            }

			// 缓存中没有,从数据源中获取
			object = getObject(id);

			// 从数据源中获取或新建完成后,更新缓存
			if (object != null) {
                this.update(id, object);
            }
			return object;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}

	protected abstract V getObject(PK id);

    @Override
    public void del(PK id) {
        jedisClusterCache.del(getKey(id));
    }

    @Override
    public List<V> get(PK[] ids) {
        return jedisClusterCache.mgetFromCache(getKeys(ids), getClassV());
    }

    @Override
    public Map<PK, V> getBatchMap(PK[] ids) {
        Map<PK, V> result = new HashMap<PK, V>(ids.length);
        for(PK id : ids){
            result.put(id, get(id));
        }
        return result;
    }

    @Override
    public void update(PK id, V object) {
        if(getExpireTime()<=0){
            jedisClusterCache.set(getKey(id),object);
            jedisClusterCache.persist(getKey(id));
        }else{
            if(exists(id)){
                jedisClusterCache.set(getKey(id), object, RedisCache.KEY_XX,RedisCache.EXPIRE_TIME_UNIT_EX,getExpireTime());
            }else{
                jedisClusterCache.set(getKey(id),object,RedisCache.KEY_NX,RedisCache.EXPIRE_TIME_UNIT_EX,getExpireTime());
            }
        }

    }

    private String[] getKeys(PK[] ids){
        String[] keys = new String[ids.length];
        for(int i=0 ;i<ids.length;i++){
            keys[i]=getKey(ids[i]);
        }
        return keys;
    }

    private Class<V> getClassV(){
        Class<V> clazzV = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        return clazzV;
    }

    @Override
    public Boolean exists(PK id) {
        return jedisClusterCache.exists(getKey(id));
    }

    @Override
    public Long expire(PK id, int seconds) {
        return jedisClusterCache.expire(getKey(id), seconds);
    }

    @Override
    public Long pexpire(PK id, long milliseconds) {
        return jedisClusterCache.pexpire(getKey(id), milliseconds);
    }
}
