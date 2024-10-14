package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.document.ChatParticipant;
import com.example.chatserverchat.domain.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatParticipantServiceImpl implements ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;

    @Override
    public void createChatParticipant(String chatId) {
        chatParticipantRepository.save(new ChatParticipant(chatId));
    }
}
