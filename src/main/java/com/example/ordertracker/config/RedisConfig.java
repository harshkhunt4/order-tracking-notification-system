package com.example.ordertracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.ordertracker.entity.Product;
import com.example.ordertracker.model.OrderStatus;

@Configuration
public class RedisConfig {

  @Bean("product")
  RedisTemplate<String,Product> redisProductTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String,Product> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(factory);
    
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    
    return redisTemplate;
  }
  
  @Bean("orderproductstatus")
  RedisTemplate<String,String> redisOrderProductTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String,String> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(factory);
    
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    
    return redisTemplate;
  }
}
