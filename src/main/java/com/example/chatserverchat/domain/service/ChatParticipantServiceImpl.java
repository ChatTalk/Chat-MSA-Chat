package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.document.ChatParticipant;
import com.example.chatserverchat.domain.dto.ChatUserReadDTO;
import com.example.chatserverchat.domain.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.example.chatserverchat.global.constant.Constants.REDIS_CHAT_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatParticipantServiceImpl implements ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final RedisTemplate<String, ChatUserReadDTO> pubSubTemplate;

    @Override
    public void createChatParticipant(String chatId) {
        chatParticipantRepository.save(new ChatParticipant(chatId));
    }

    @Override
    public void exitChatParticipant(String chatId, String email) {
        pubSubTemplate.convertAndSend(
                REDIS_CHAT_PREFIX + chatId,
                new ChatUserReadDTO(chatId, email, false, false));
    }
}
