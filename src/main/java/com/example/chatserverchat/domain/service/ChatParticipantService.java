package com.example.chatserverchat.domain.service;

public interface ChatParticipantService {
    void createChatParticipant(String chatId);

    void exitChatParticipant(String chatId, String email);
}
