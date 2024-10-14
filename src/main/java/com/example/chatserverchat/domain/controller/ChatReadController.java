package com.example.chatserverchat.domain.controller;

import com.example.chatserverchat.domain.dto.ChatMessageDTO;
import com.example.chatserverchat.domain.service.ChatParticipantService;
import com.example.chatserverchat.domain.service.ChatReadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/open-chats")
public class ChatReadController {

    private final ChatReadService chatReadService;
    private final ChatParticipantService chatParticipantService;

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
        chatParticipantService.exitChatParticipant(chatId.toString(), userDetails.getUsername());
    }
}
