package com.zz.redis.rediscluster.redis.base;


import com.zz.redis.rediscluster.redis.cluster.JedisClusterCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

// T set 中的单个值。 PK 主键。
public abstract class BaseSortSet<T, PK extends Serializable> {

    public static final String DESC = "desc";

    public static final String ASC = "asc";

    protected final Log log = LogFactory.getLog(getClass());

    /**
     * 实际加载数据
     *
     * @param id
     * @return
     */
    abstract protected Map<String, Double> getAll(PK id);

    protected JedisClusterCache redisManager;

    // 主键
    protected String key;

    // 超时时间，秒
    protected int expireTime;

    /**
     * key的生成策略
     **/
    abstract protected String getBaseKey(PK id);

    // 获取key
    protected String getCacheKey(PK id) {
        return getBaseKey(id);
    }

    protected String getTempCacheKey(PK id) {
        return "Temp:" + getBaseKey(id);
    }

    protected boolean exists(PK id) {
        return redisManager.exists(getCacheKey(id));
    }

    public Double getScoreById(PK id, T t) {
        return redisManager.zscore(this.getCacheKey(id), t.toString());
    }

    /**
     * 往set中添加值。这个地方不异步处理，交给熔断器做熔断。
     *
     * @param id
     * @param score
     * @param t
     */
    public void update(PK id, Double score, T t) {
        if (t == null) {
            log.error("update the t is null");
            return;
        }
        try {
            if (!exists(id)) {
                // 没有的时候，全量刷新。
                loadAll(id);
            }
            else {
                // 已经存在了，增量添加。
                redisManager.zadd(this.getCacheKey(id), score, t.toString());
            }
        }
        catch (Exception e) {
            log.error(String
                    .format("redisManager.zadd.exception, params(id:%s, score:%s, t:%s)", this.getCacheKey(id), score, t),
                e);
        }
    }

    /**
     * 正序，元素按score从小到大排序， 获取其中的值。
     *
     * @param id
     * @param start
     * @param end
     * @return
     */
    public Set<String> getSortSet(PK id, int start, int end) {
        try {
             log.info("shm " +!exists(id));
            if (!exists(id)) {
                loadAll(id);
            }

            Set<String> set = redisManager.zrange(this.getCacheKey(id), start, end);

            return set;
        }
        catch (Exception e) {
            log.error(String
                .format("redisManager.zrange.exception, params(id:%s, start:%s, end:%s)", this.getCacheKey(id), start,
                    end), e);
            return null;
        }
    }

    /**
     * 倒序，元素按score从大到小排序， 获取其中的值。
     *
     * @param id
     * @param start
     * @param end
     * @return
     */
    public Set<String> getSortSetDesc(PK id, int start, int end) {
        try {

            if (!exists(id)) {
                loadAll(id);
            }

            Set<String> set = redisManager.zrevrange(this.getCacheKey(id), start, end);

            return set;
        }
        catch (Exception e) {
            log.error(String
                .format("redisManager.zrevrange.exception, params(id:%s, start:%s, end:%s)", this.getCacheKey(id),
                    start, end), e);
            return null;
        }
    }

    /**
     * 倒序，根据score顺序加载值，设置了score的最大最小值。 元素按score从大到小排序
     *
     * @param id
     * @param max
     * @param min
     * @param offset
     * @param count
     * @return
     */
    public Set<String> getSortSetDescByScore(PK id, Double max, Double min, int offset, int count) {
        try {
            if (max == null) {
                max = Double.MAX_VALUE;
            }
            if (min == null) {
                min = 0d;
            }

            if (!exists(id)) {
                loadAll(id);
            }

            Set<String> set = redisManager.zrevrangeByScore(this.getCacheKey(id), max, min, offset, count);

            return set;
        }
        catch (Exception e) {
            log.error(String
                .format("redisManager.zrevrangeByScore.exception, params(id:%s, max:%s, min:%s, offset:%s, count:%s)",
                    this.getCacheKey(id), max, min, offset, count), e);
            return null;
        }
    }



    public  <T> List<T> mgetFromCache(String[] keys, Class<T>... type) {
        return redisManager.mgetFromCache(keys, type);
    }


    public  <T> T getFromCache(String key, Class<T>... type) {
        return redisManager.getFromCache(key, type);
    }

    public  <T> void msetInfoCache(String[] keys, T[] values) {

         redisManager.msetInfoCache(keys, values);
    }

    public  <T> String setIntoCacheWithTime(String key, T value, int time) {
        return redisManager.setex(key, value, time);
    }

    /**
     * 正序，根据score顺序加载值，设置了score的最大最小值。 元素按score从小到大排序
     *
     * @param id
     * @param max
     * @param min
     * @param offset
     * @param count
     * @return
     */
    public Set<String> getSortSetByScore(PK id, Double max, Double min, int offset, int count) {
        try {
            if (max == null) {
                max = Double.MAX_VALUE;
            }
            if (min == null) {
                min = 0d;
            }

            if (!exists(id)) {
                loadAll(id);
            }

            Set<String> set = redisManager.zrangeByScore(this.getCacheKey(id), max, min, offset, count);

            return set;
        }
        catch (Exception e) {
            log.error(String
                .format("redisManager.zrangeByScore.exception, params(id:%s, max:%s, min:%s, offset:%s, count:%s)",
                    this.getCacheKey(id), max, min, offset, count), e);
            return null;
        }
    }

    public long countSortSet(PK id) {
        try {

            if (!exists(id)) {
                loadAll(id);
            }

            long count = redisManager.zcard(this.getCacheKey(id));

            return count;
        }
        catch (Exception e) {
            log.error(String.format("redisManager.zcard.exception, params(id:%s)", this.getCacheKey(id)), e);
            return 0;
        }
    }

    public void remove(PK id, T t) {
        try {
            redisManager.zrem(getCacheKey(id), t.toString());
            if (this.countSortSet(id) <= 0) {
                this.remove(id);
            }
        }
        catch (Exception e) {
            log.error(String.format("redisManager.zrem.exception, params(id:%s, t:%s)", this.getCacheKey(id), t), e);
        }
    }

    public void remove(PK id) {
        try {
            redisManager.del(new String[] { getCacheKey(id) });
        }
        catch (Exception e) {
            log.error(String.format("redisManager.del.exception, params(id:%s)", this.getCacheKey(id)), e);
        }
    }

    public synchronized void loadAll(PK id) {
        try {
            // 加载之前判断下，如果加载过了，就不处理了。
            if (!exists(id)) {
                myLoadAll(id);
            }
        }
        catch (Exception e) {
            log.error(String.format("BaseSortSet.loadAll.exception, params(id:%s)", id), e);
        }
    }

    private void myLoadAll(PK id) {
        try {
            Map<String, Double> scoreMembers = this.getAll(id);

            if (scoreMembers != null) {
                if (!scoreMembers.isEmpty()) {
                    this.redisManager.zadd(this.getTempCacheKey(id), scoreMembers);
                    this.remove(id);
                    this.redisManager.rename(this.getTempCacheKey(id), this.getCacheKey(id));
                    this.redisManager.expire(this.getCacheKey(id), expireTime);
                }
                else {
                    this.remove(id);
                }
            }
        }
        catch (Exception e) {
            log.error(String.format("BaseSortSet.myLoadAll.exception, params(id:%s)", id), e);
            throw e;
        }
    }

    public JedisClusterCache getRedisManager() {
        return redisManager;
    }

    public void setRedisManager(JedisClusterCache redisManager) {
        this.redisManager = redisManager;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public void removeAll(String pattern) {
        try{
            this.redisManager.removeKeys(pattern);
        } catch (Exception e){
            log.error(String.format("BaseSortSet.removeAll.exception, params(pattern:%s)", pattern), e);
        }


    }


}
