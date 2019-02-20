package com.champion.readingstory.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: JiangPing Li
 * @date: 2018-09-07 14:19
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.redis.jedis.pool")
public class RedisProperties {
    private Integer maxActive;

    private Integer maxWait;

    private Integer maxIdle;

    private Integer minIdle;

}
