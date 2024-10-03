package com.example.chatserverchat.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatSubscriptionDTO {

    private String chatId; // 구독한 채팅방 ID
    private List<ChatMessageDTO> unreadMessages;
}
