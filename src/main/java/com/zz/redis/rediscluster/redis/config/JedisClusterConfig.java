package com.zz.redis.rediscluster.redis.config;


import com.zz.redis.rediscluster.redis.cluster.JedisClusterCache;
import com.zz.redis.rediscluster.redis.serialize.kryo.KryoSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class JedisClusterConfig {

    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;
    @Value("${spring.redis.timeout}")
    private int connectionTimeout;
    @Value("${spring.redis.commandTimeout}")
    private int soTimeout;
    @Value("${spring.redis.cluster.max-redirects}")
    private int maxRedirections;

    @Value("${spring.redis.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.pool.max-wait}")
    private int maxWait;

    @Autowired
    private KryoSerializer kryoSerializer;


    @Bean("jedisClusterCache")
    public JedisClusterCache getJedisCluster() {

        String[] serverArray = clusterNodes.split(",");//获取服务器数组
        Set<HostAndPort> nodes = new HashSet<>();

        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxActive);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWait);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        return new JedisClusterCache(nodes, connectionTimeout, soTimeout, maxRedirections, config, kryoSerializer);
    }
}