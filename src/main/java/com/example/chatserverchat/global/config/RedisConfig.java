package com.example.chatserverchat.global.config;

import com.example.chatserverchat.domain.dto.ChatUserReadDTO;
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
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
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

    // 회원이 지급 '읽고'있는 채팅방 확인용
    @Bean(name = "readTemplate")
    public RedisTemplate<String, String> readTemplate(RedisConnectionFactory redisConnectionFactory) {
        return getStringStringRedisTemplate(redisConnectionFactory);
    }

    /**
     * 삭제 예정입니다
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "participatedTemplate")
    public RedisTemplate<String, Boolean> participatedTemplate(RedisConnectionFactory redisConnectionFactory) {
        return getStringBooleanTemplate(redisConnectionFactory);
    }

    // 접속자 현황 펍 섭 실시간 업데이트용
    @Bean(name = "pubSubTemplate")
    public RedisTemplate<String, ChatUserReadDTO> pubSubTemplate(RedisConnectionFactory redisConnectionFactory) {
        return getStringChatUserReadDTOTemplate(redisConnectionFactory);
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

    private RedisTemplate<String, Boolean> getStringBooleanTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Boolean> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 키와 해시 키는 String으로 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 해시 값은 Boolean으로 설정
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Boolean.class));
        // 값을 Boolean으로 설정
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Boolean.class));

        return redisTemplate;
    }

    private RedisTemplate<String, ChatUserReadDTO> getStringChatUserReadDTOTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ChatUserReadDTO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 키와 해시 키는 String으로 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 해시 값은 Boolean으로 설정
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(ChatUserReadDTO.class));
        // 값을 Boolean으로 설정
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatUserReadDTO.class));

        return redisTemplate;
    }


    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        return container;
    }
}
