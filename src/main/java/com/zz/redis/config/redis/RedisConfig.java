package com.zz.redis.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.net.UnknownHostException;

/**
 * 
 * @Description: redis配置
 *
 * @author zhouzhou
 * @date 2019年12月31日 17:19:31
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig extends CachingConfigurerSupport {

	protected final static Logger log = LoggerFactory.getLogger(RedisConfig.class);

	/**
	 * redisTemplate缓存操作类 LettuceConnectionFactory
	 * 
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.setKeySerializer(jackson2JsonRedisSerializer);

		redisTemplate.afterPropertiesSet();

		return redisTemplate;
	}
}
