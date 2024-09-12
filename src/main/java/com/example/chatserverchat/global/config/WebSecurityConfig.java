package com.example.chatserverchat.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 카프카
        http.csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                        .requestMatchers("/kafka/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/open-chats").permitAll()
                        // 임시 코드 추가, 얘를 추가하니 채팅창 조회가 작동됨
                        // 하지만 얘를 추가하는 건 해결책이 아님. 인증이 유명무실해지는 거니까
                        .anyRequest().authenticated()
        );

        return http.build();
    }

}
