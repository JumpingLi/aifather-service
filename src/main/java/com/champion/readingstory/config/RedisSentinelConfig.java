package com.champion.readingstory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 14:10
 */
@Configuration
@Slf4j
public class RedisSentinelConfig extends CachingConfigurerSupport{
    @Value("${spring.redis.sentinel.nodes}")
    private String redisNodes;
    @Value("${spring.redis.sentinel.master}")
    private String master;

    /**
     * 配置 Redis 连接池信息
     */
//    @Bean
    public JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        return jedisPoolConfig;
    }

    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration(){
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
        String[] host = redisNodes.split(",");
        for(String redisHost : host){
            String[] item = redisHost.split(":");
            String ip = item[0];
            String port = item[1];
            configuration.addSentinel(new RedisNode(ip, Integer.parseInt(port)));
        }
        configuration.setMaster(master);
        return configuration;
    }

//    @Bean
    public RedisClusterConfiguration redisClusterConfiguration(){
        RedisClusterConfiguration configuration = new RedisClusterConfiguration();
        List<RedisNode> nodeList = new ArrayList<>();
        String[] cNodes = redisNodes.split(",");
        //分割出集群节点
        for(String node : cNodes) {
            String[] hp = node.split(":");
            nodeList.add(new RedisNode(hp[0], Integer.parseInt(hp[1])));
        }
        configuration.setClusterNodes(nodeList);
        return configuration;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(redisSentinelConfiguration());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        //StringRedisTemplate的构造方法中默认设置了stringSerializer
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //设置开启事务
        redisTemplate.setEnableTransactionSupport(true);
        //set key serializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 比之 默认的jdk序列化不需要对象实现serial
        // Jackson2JsonRedisSerializer 反序列化对象问题
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
