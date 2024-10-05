package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.dto.UserReadDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.example.chatserverchat.global.constant.Constants.*;

@Slf4j(topic = "RedisPublishService")
@Service
@RequiredArgsConstructor
public class RedisPublishService {

    private final RedisTemplate<String, String> pubSubTemplate;
    private final RedisTemplate<String, String> readTemplate;
    private final RedisTemplate<String, Boolean> participatedTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void updateRedisParticipatedHash(Long chatId, String email) throws JsonProcessingException {
        readTemplate.delete(REDIS_CHAT_READ_KEY + email);
        participatedTemplate.opsForHash()
                .put(REDIS_PARTICIPATED_KEY + chatId, email, false);

        Map<Object, Object> entries =
                participatedTemplate.opsForHash().entries(REDIS_PARTICIPATED_KEY + chatId);

        // UserReadDTO 리스트 생성
        List<UserReadDTO> userReadList = entries.entrySet().stream()
                .map(e -> new UserReadDTO((String) e.getKey(), (Boolean) e.getValue()))
                .toList();

        // redis 송신
        // 메뉴로 돌아가는 시점에도 결국 구독 종료니까
        // 채팅창으로 다시 들어와야 구독 시작
        // 그냥 구독하게 놔두는 게 좋을 것 같기도?
        String userReadListString = objectMapper.writeValueAsString(userReadList);
        pubSubTemplate.convertAndSend(REDIS_CHAT_PREFIX + chatId.toString(), userReadListString);
    }
}
