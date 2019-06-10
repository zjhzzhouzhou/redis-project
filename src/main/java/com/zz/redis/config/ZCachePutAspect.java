package com.zz.redis.config;

import com.zz.redis.rediscluster.redis.cluster.JedisClusterCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author zhouhou
 * @Description: AOP实现Redis缓存处理ZCachePut
 * @date 2019-06-03 17:27:11
 */
@Component
@Aspect
public class ZCachePutAspect {

    private static final Logger log = LoggerFactory.getLogger(ZCachePutAspect.class);

    @Autowired
    private JedisClusterCache redisUtil;

    /**
     * 拦截所有元注解ZCachePut注解的方法
     */
    @Pointcut("@annotation(ZCachePut)")
    public void pointcutCachePut() {

    }

    /**
     * 环绕处理，在其被调用后将其返回值缓存起来，以保证下次利用同样的参数来执行该方法时可以直接从缓存中获取结果，而不需要再次执行该方法
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("pointcutCachePut()")
    public Object aroundCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("**********pointcutCachPut执行**********");
        Method method = ZCacheUtil.getMethod(joinPoint);
        ZCachePut cacheable = method.getAnnotation(ZCachePut.class);
        // key值
        String key = cacheable.key();
        // 缓存名
        String cacheName = cacheable.cacheName();
        // 过期时间
        int expireTime = cacheable.expireTime();

        if (StringUtils.isEmpty(key)) {
            //如果key为空，重新生成缓存key
            key = cacheName + ":" + ZCacheUtil.keyGenerator(joinPoint);
        } else {
            key = cacheName + ":" + ZCacheUtil.parseKey(key, method, joinPoint.getArgs());
        }
        log.info("cachePut.key()=" + key);
        Object obj = joinPoint.proceed();
        log.info("**********开始将数据更新到Redis缓存**********");
        if (key != null) {
            if (expireTime > 0) { // 有过期时间
                redisUtil.setex(key, obj, expireTime);

            } else {
                redisUtil.set(key, obj);

            }
            log.info("**********数据成功更新到Redis缓存!!!**********");
            log.info("Redis的KEY值:" + key);
            log.info("REDIS的VALUE值:" + obj.toString());

        }
        return obj;
    }

}
