package com.yianit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import tk.mybatis.mapper.util.StringUtil;
@Configuration
public class RedisConfig {
	@Value("${spring.redis.database}")
	private String	database;
	@Value("${spring.redis.host}")
	private String	host;
	@Value("${spring.redis.port}")
	private int	port;
	@Value("${spring.redis.password}")
	private String	password;
	@Value("${spring.redis.jedis.pool.max-active}")
	private String	maxActive;
	@Value("${spring.redis.jedis.pool.max-wait}")
	private String	maxWait;
	@Value("${spring.redis.jedis.pool.max-idle}")
	private String	maxIdle;
	@Value("${spring.redis.jedis.pool.min-idle}")
	private String	minIdle;
	@Value("${spring.redis.timeout}")
	private String	timeout;
	@Value("${spring.redis.block-when-exhausted}")
	private boolean	blockWhenExhausted;
	@Bean("jedisPool")
	public JedisPool createPool() {
		 // 建立连接池配置参数
        JedisPoolConfig config = new JedisPoolConfig();
        // 设置最大连接数
        // config.setMaxActive(100);
        // 设置最大阻塞时间，记住是毫秒数milliseconds
        // config.setMaxWait(1000);
        config.setMinEvictableIdleTimeMillis(1000000);
        config.setMaxWaitMillis(5000);
        config.setMaxTotal(100);
        // 设置空闲连接
        config.setMaxIdle(200);
        // 创建连接池
        if(StringUtil.isEmpty(password)) {
        	return new JedisPool(config, host, port, 10000);
        }else {
        	return new JedisPool(config, host, port, 10000,password);
        }
	}
}
