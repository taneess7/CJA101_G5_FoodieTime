package com.foodietime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用 StringRedisSerializer 來序列化和反序列化 redis 的 key 值
        template.setKeySerializer(new StringRedisSerializer());
        // Hash 的 key 也採用 String 的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value 使用 Jackson 的序列化器
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // Hash 的 value 也使用 Jackson
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
