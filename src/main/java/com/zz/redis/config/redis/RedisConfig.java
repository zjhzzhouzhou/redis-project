package com.zz.redis.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @Description: redis配置
 *
 * @author yl
 * @date 2018年5月2日
 */
//@Configuration
//@EnableCaching
//@ConditionalOnClass({ JedisCluster.class })
public class RedisConfig extends CachingConfigurerSupport {

	protected final static Logger log = LoggerFactory.getLogger(RedisConfig.class);
	
	@Value("${spring.redis.cluster.nodes}")
	private String clusterNodes;
	@Value("${spring.redis.timeout}")
	private int timeout;
	@Value("${spring.redis.pool.max-idle}")
	private int maxIdle;
	@Value("${spring.redis.pool.max-wait}")
	private long maxWaitMillis;
	@Value("${spring.redis.commandTimeout}")
	private int commandTimeout;

	/**
	 * 自定义key,此方法会根据类名+方法名+所有参数的值生成一个 唯一的key，即@Cacheable中的key
	 */
	@Override
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object o, Method method, Object... objects) {
				StringBuilder sb = new StringBuilder();
				sb.append(o.getClass().getName());
				sb.append(method.getName());
				for (Object obj : objects) {
					sb.append(obj.toString());
				}
				return sb.toString();
			}
		};
	}

	/**
	 * redisTemplate缓存操作类
	 * 
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
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

	
	//@Bean
	public JedisCluster getJedisCluster() {
		String[] cNodes = clusterNodes.split(",");
		Set<HostAndPort> nodes = new HashSet<>();
		// 分割出集群节点
		for (String node : cNodes) {
			String[] hp = node.split(":");
			nodes.add(new HostAndPort(hp[0], Integer.parseInt(hp[1])));
		}
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		// 创建集群对象
		// JedisCluster jedisCluster = new JedisCluster(nodes,commandTimeout);
		return new JedisCluster(nodes, commandTimeout, jedisPoolConfig);
	}
	
}
