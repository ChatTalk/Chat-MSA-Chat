package com.example.chatserverchat.domain.controller;

import com.example.chatserverchat.domain.dto.ChatMessageDTO;
//import com.example.chatserverchat.domain.dto.UserReadDTO;
import com.example.chatserverchat.domain.service.ChatReadService;
import com.example.chatserverchat.domain.service.RedisPublishService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static com.example.chatserverchat.global.constant.Constants.REDIS_CHAT_READ_KEY;
import static com.example.chatserverchat.global.constant.Constants.REDIS_PARTICIPATED_KEY;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/open-chats")
public class ChatReadController {

    private final ChatReadService chatReadService;
    private final RedisPublishService redisPublishService;

    @GetMapping("/read/{chatId}")
    public ResponseEntity<List<ChatMessageDTO>> getChatMessages(
            @PathVariable Long chatId, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("안 읽은 메세지 갖고 오기");
        List<ChatMessageDTO> unreadMessages =
                chatReadService.getUnreadMessages(userDetails.getUsername(), chatId.toString());

        return ResponseEntity.ok(unreadMessages);
    }

    // 메뉴 돌아가기 버트 눌렀을 때
    @PutMapping("/read/{chatId}")
    public void unParticipate(
            @PathVariable Long chatId, @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
        log.info("돌아가기 누름");
        redisPublishService.updateRedisParticipatedHash(chatId, userDetails.getUsername());
    }
}
