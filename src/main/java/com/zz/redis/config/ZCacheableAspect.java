package com.zz.redis.config;

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
 * @Description: AOP实现Redis缓存处理ZCacheable
 * @date 2019-06-03 17:27:11
 */
@Component
@Aspect
public class ZCacheableAspect {

    private static final Logger log = LoggerFactory.getLogger(ZCacheableAspect.class);

    @Autowired
    protected RedisUtil redisUtil;

    /**
     * 拦截所有元注解ZCacheable注解的方法
     */
    @Pointcut("@annotation(ZCacheable)")
    public void pointcutCacheable() {

    }

    /**
     * 环绕处理，在其被调用后将其返回值缓存起来，以保证下次利用同样的参数来执行该方法时可以直接从缓存中获取结果，而不需要再次执行该方法
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("pointcutCacheable()")
    public Object aroundCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("**********pointcutCacheable执行**********");
        Method method = ZCacheUtil.getMethod(joinPoint);
        ZCacheable cacheable = method.getAnnotation(ZCacheable.class);
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
        log.info("cacheable.key()=" + key);
        Object obj = redisUtil.get(key);
        if (obj != null) {
            log.info("**********从Redis中查到了数据**********");
            log.info("Redis的KEY值:" + key+"|REDIS的VALUE值:" + obj.toString());
            return obj;
        }
        log.info("**********从Redis中不存在数据**********");
        obj = joinPoint.proceed();
        log.info("**********开始将数据保存到Redis缓存**********");
        if (key != null) {
            if (expireTime > 0) { // 有过期时间
                redisUtil.set(key, obj, expireTime);

            }else{
                redisUtil.set(key, obj);
            }
            log.info("**********数据成功保存到Redis缓存!!!**********");
            log.info("Redis的KEY值:" + key+"|REDIS的VALUE值:" + obj.toString());

        }
        return obj;
    }

}
