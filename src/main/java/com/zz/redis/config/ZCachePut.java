package com.zz.redis.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:自定义缓存注解(获取更新)
 * User: zhouzhou
 * Date: 2019-06-03
 * Time: 17:03
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZCachePut {

    String cacheName() default "";
    // key前缀值
    String key() default "";
    // 过期时间,默认秒
    int expireTime() default 0;


}
