package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.dto.ChatMessageDTO;

import java.util.List;

public interface ChatReadService {
    List<ChatMessageDTO> getUnreadMessages(String username, String chatId, String role);
}
