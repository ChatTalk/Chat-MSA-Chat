package com.example.chatserverchat.global.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.chatserverchat.global.constant.Constants.*;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 쿠키에 담긴 엑세스 토큰을 받아와서 가져와야 한다.
        String beforeToken = findAccessToken(request.getCookies());
        log.info("초기 토큰값: {}", beforeToken);

        /**
         * 여기서 바로 카프카로 전송해야 되는 걸까?
         */

        if (beforeToken != null) {
            kafkaTemplate.send(KAFKA_OTHER_TO_USER_TOPIC, beforeToken);
        } else {
            throw new IllegalArgumentException("엑세스 토큰이 존재하지 않음");
        }

        // 향후 로직... 인증 정보 받아오기...

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
