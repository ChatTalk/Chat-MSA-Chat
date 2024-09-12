package com.example.chatserverchat.global.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.chatserverchat.global.constant.Constants.*;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, String> cacheTemplate;

    public AuthenticationFilter(
            KafkaTemplate<String, String> kafkaTemplate,
            @Qualifier("cacheTemplate") RedisTemplate<String, String> cacheTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.cacheTemplate = cacheTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 쿠키에 담긴 엑세스 토큰을 받아와서 가져와야 한다.
        String beforeToken = findAccessToken(request.getCookies());
        log.info("초기 토큰값: {}", beforeToken);

        if (beforeToken == null) {
            throw new IllegalArgumentException("토큰 조회 불능. 중간 과정에서 소실 가능성 있음.");
        }

        /**
         * 여기서 바로 카프카로 전송해야 되는 걸까?
         */
        kafkaTemplate.send(KAFKA_OTHER_TO_USER_TOPIC, beforeToken);

        // 향후 로직... 인증 정보 받아오기...
        // 락 걸어서 대기타다가....
        // 조회되는지 확인하기


    }

    // 엑세스 토큰 찾기
    private String findAccessToken(Cookie[] cookies){
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_AUTH_HEADER)) return cookie.getValue();
        }
        return null;
    }
}
