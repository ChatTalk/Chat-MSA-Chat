package com.example.chatserverchat.domain.dto;

import com.example.chatserverchat.domain.entity.ChatMessageType;

public record GraphqlMessageDTO(String chatId, ChatMessageType type, String username, String message, String createdAt) {
}