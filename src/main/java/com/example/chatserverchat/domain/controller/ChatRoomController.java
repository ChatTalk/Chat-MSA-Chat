package com.example.chatserverchat.domain.controller;

import com.example.chatserverchat.domain.dto.ChatRoomDTO;
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

    @PostMapping("/create")
    public ResponseEntity<ChatRoomDTO.Info> createOpenChat(@RequestBody ChatRoomDTO dto, @AuthenticationPrincipal UserDetails userDetails) {
        ChatRoomDTO.Info chatInfo = chatRoomService.createOpenChat(dto, userDetails.getUsername());
        return ResponseEntity.ok(chatInfo);
    }

    @GetMapping()
    public ResponseEntity<List<ChatRoomDTO.Info>> getAllOpenChats() {
        List<ChatRoomDTO.Info> chats = chatRoomService.getAllOpenChats();
        return ResponseEntity.ok(chats);
    }
}
