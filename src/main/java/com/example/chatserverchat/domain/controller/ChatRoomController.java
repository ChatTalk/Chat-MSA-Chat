package com.example.chatserverchat.domain.controller;

import com.example.chatserverchat.domain.dto.ChatRoomDTO;
import com.example.chatserverchat.domain.service.ChatParticipantService;
import com.example.chatserverchat.domain.service.ChatRoomService;
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
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatParticipantService chatParticipantService;

    @PostMapping("/create")
    public ResponseEntity<ChatRoomDTO.Info> createOpenChat(@RequestBody ChatRoomDTO dto, @AuthenticationPrincipal UserDetails userDetails) {
        ChatRoomDTO.Info chatInfo = chatRoomService.createOpenChat(dto, userDetails.getUsername());
        chatParticipantService.createChatParticipant(chatInfo.getChatId());
        return ResponseEntity.ok(chatInfo);
    }

    @GetMapping()
    public ResponseEntity<List<ChatRoomDTO.Info>> getAllOpenChats() {
        List<ChatRoomDTO.Info> chats = chatRoomService.getAllOpenChats();
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/subscribe")
    public ResponseEntity<List<ChatRoomDTO.Info>> subscribe(@AuthenticationPrincipal UserDetails userDetails) {
        List<ChatRoomDTO.Info> subscribedChats = chatRoomService.getSubscribedChatRooms(userDetails.getUsername());
        return ResponseEntity.ok(subscribedChats);
    }
}
