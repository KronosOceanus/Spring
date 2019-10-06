package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

// java 配置 Spring 缓存管理器
@Configuration
@EnableCaching //启用 Spring 缓存机制
public class RedisConfig {

    @Bean("redisTemplate")
    public RedisTemplate initRedisTemplate(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(50);
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxWaitMillis(20000);

        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setPoolConfig(poolConfig);
        connectionFactory.setHostName("localhost");
        connectionFactory.setPort(6379);
        connectionFactory.afterPropertiesSet(); //手动初始化 bean（如果是单独的 bean，Spring 会自动初始化）

        JdkSerializationRedisSerializer jdkSerializationRedisSerializer =
                new JdkSerializationRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);

        redisTemplate.setDefaultSerializer(stringRedisSerializer);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jdkSerializationRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jdkSerializationRedisSerializer);

        return redisTemplate;
    }

    @Bean(name = "redisCacheManager")
    public RedisCacheManager initRedisCacheManager(@Autowired RedisTemplate redisTemplate){
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        //设置超时时间，保证 java 在一定时间间隔后重新从数据读取数据
        redisCacheManager.setDefaultExpiration(600);
        //设置缓存名称
        List<String> cacheNames = new ArrayList<>();
        cacheNames.add("redisCacheManager");
        redisCacheManager.setCacheNames(cacheNames);
        return redisCacheManager;
    }
}
