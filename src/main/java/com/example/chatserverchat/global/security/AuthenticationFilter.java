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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.chatserverchat.global.constant.Constants.*;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, String> cacheTemplate;

    public AuthenticationFilter(
            UserDetailsService userDetailsService,
            KafkaTemplate<String, String> kafkaTemplate,
            @Qualifier("cacheTemplate") RedisTemplate<String, String> cacheTemplate) {
        this.userDetailsService = userDetailsService;
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

        // 캐시에서 조회됨
        if (Boolean.TRUE.equals(cacheTemplate.hasKey(REDIS_ACCESS_KEY + beforeToken))) {
            String email = cacheTemplate.opsForValue().get(REDIS_ACCESS_KEY + beforeToken);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(createAuthentication(email));
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(request, response);
            return;
        }

        /**
         * 여기서 바로 카프카로 전송해야 되는 걸까?
         */
        kafkaTemplate.send(KAFKA_OTHER_TO_USER_TOPIC, beforeToken);

        // 향후 로직... 인증 정보 받아오기...
        // 조회되는지 확인하기
        // 캐시 조회 및 대기
        /**
         * 솔직히 락이 필요할 것 같진 않음. 순수히 이메일 조회의 문제니까, 다만 타임아웃 설정은 필요하겠지
         * 하여 캐시 조회를 폴링으로 구현함에 그치고, 동시성 제어는 채팅방 동시 입장에서 한번 처리해보자.
         */
        String email = null;
        long startTime = System.currentTimeMillis();
        long timeout = 60 * 1000; // 1분 대기

        while (email == null && (System.currentTimeMillis() - startTime) < timeout) {
            // 데이터 조회
            email = cacheTemplate.opsForValue().get(beforeToken);

            if (email == null) {
                // 데이터가 없으면 잠시 대기 후 다시 조회
                try {
                    Thread.sleep(1000); // 1초 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("스레드 인터럽트 발생", e);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 서버 에러 응답
                    return;
                }
            }
        }

        if (email != null) {
            // 데이터가 있으면 정상적으로 처리
            log.info("조회된 이메일: {}", email);
            cacheTemplate.opsForValue().set(REDIS_ACCESS_KEY + beforeToken, email);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(createAuthentication(email));
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(request, response);
        } else {
            log.warn("지정된 시간 내에 이메일을 찾을 수 없음.");
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE); // 서비스 불가 응답
        }
    }

    // 엑세스 토큰 찾기
    private String findAccessToken(Cookie[] cookies){
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_AUTH_HEADER)) return cookie.getValue();
        }
        return null;
    }

    // Authentication 객체 생성 (UPAT 생성)
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
