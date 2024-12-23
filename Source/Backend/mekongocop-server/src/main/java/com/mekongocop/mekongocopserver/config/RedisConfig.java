package com.mekongocop.mekongocopserver.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${REDIS_HOST}")
    private String redisHost;

    @Value("${REDIS_PORT}")
    private int redisPort;

    @Value("${REDIS_PASSWORD}")
    private String redisPassword;

    @Bean
    public JedisPool jedisPool() {
        logger.info("Connecting to Redis at {}:{}", redisHost, redisPort);
        GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setJmxEnabled(false);
        poolConfig.setMaxTotal(50); // Số lượng kết nối tối đa
        poolConfig.setMaxIdle(20); // Số kết nối nhàn rỗi tối đa
        poolConfig.setMinIdle(10); // Số kết nối nhàn rỗi tối thiểu
        poolConfig.setTestOnBorrow(true); // Kiểm tra kết nối khi mượn
        poolConfig.setTestOnReturn(true); // Kiểm tra kết nối khi trả về
        poolConfig.setTestWhileIdle(true); // Kiểm tra định kỳ các kết nối nhàn rỗi
        poolConfig.setBlockWhenExhausted(true); // Đợi khi pool hết kết nối

        return new JedisPool(poolConfig, redisHost, redisPort, 2000, redisPassword);
    }
}
