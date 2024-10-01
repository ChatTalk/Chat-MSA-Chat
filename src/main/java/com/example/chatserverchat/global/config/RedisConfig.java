package com.example.chatserverchat.global.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        redisConfiguration.setPassword(password);
        redisConfiguration.setDatabase(0);

        final SocketOptions socketoptions = SocketOptions.builder().connectTimeout(Duration.ofSeconds(10)).build();
        final ClientOptions clientoptions = ClientOptions.builder().socketOptions(socketoptions).build();

        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clientoptions)
                .commandTimeout(Duration.ofMinutes(1))
                .shutdownTimeout(Duration.ZERO)
                .build();

        return new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
    }

    @Bean(name = "maxPersonnelTemplate")
    public RedisTemplate<String, Integer> maxPersonnelTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Integer> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Integer.class));

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Integer.class));

        return redisTemplate;
    }

    // 채팅창의 접속자 인원 관리
    @Bean(name = "participatedTemplate")
    public RedisTemplate<String, String> participatedTemplate(RedisConnectionFactory redisConnectionFactory) {
        return getStringStringRedisTemplate(redisConnectionFactory);
    }

    // 회원이 지금 구독하고 있는 채팅방 확인용(set 구조화)
    @Bean(name = "subscribeTemplate")
    public RedisTemplate<String, String> subscribeTemplate(RedisConnectionFactory redisConnectionFactory) {
        return getStringStringRedisTemplate(redisConnectionFactory);
    }

    private RedisTemplate<String, String> getStringStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
