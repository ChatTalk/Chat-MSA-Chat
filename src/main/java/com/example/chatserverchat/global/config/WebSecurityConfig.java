package com.example.chatserverchat.global.config;

import com.example.chatserverchat.global.security.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, String> cacheTemplate;

    public WebSecurityConfig(
            UserDetailsService userDetailsService,
            KafkaTemplate<String, String> kafkaTemplate,
            @Qualifier("cacheTemplate") RedisTemplate<String, String> cacheTemplate) {
        this.userDetailsService = userDetailsService;
        this.kafkaTemplate = kafkaTemplate;
        this.cacheTemplate = cacheTemplate;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.formLogin(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
//                        .requestMatchers(HttpMethod.GET, "/api/open-chats").permitAll()
                        // 임시 코드 추가, 얘를 추가하니 채팅창 조회가 작동됨
                        // 하지만 얘를 추가하는 건 해결책이 아님. 인증이 유명무실해지는 거니까
                        .anyRequest().authenticated()
        );

        http.addFilterBefore(new AuthenticationFilter(
                userDetailsService, kafkaTemplate, cacheTemplate
        ), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
