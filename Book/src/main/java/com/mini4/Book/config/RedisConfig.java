package com.mini4.Book.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Key Serializer: String (JWT 토큰 ID, 사용자 ID 등)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // Value Serializer: JSON (RefreshToken, 사용자 정보 등 복잡한 객체)
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        // Hash Key Serializer: String
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // Hash Value Serializer: JSON
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
