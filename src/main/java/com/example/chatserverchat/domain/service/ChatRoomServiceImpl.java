package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.document.UserSubscription;
import com.example.chatserverchat.domain.dto.ChatRoomDTO;
import com.example.chatserverchat.domain.dto.ChatSubscriptionDTO;
import com.example.chatserverchat.domain.entity.ChatRoom;
import com.example.chatserverchat.domain.mapper.ChatRoomMapper;
import com.example.chatserverchat.domain.repository.ChatReadRepository;
import com.example.chatserverchat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.example.chatserverchat.global.constant.Constants.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatReadRepository chatReadRepository;
//    private final RedisTemplate<String, String> subscribeTemplate;

    @Override
    public ChatRoomDTO.Info createOpenChat(ChatRoomDTO chatRoomDTO, String openUsername) {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoomMapper.toEntity(chatRoomDTO, openUsername));
        return ChatRoomMapper.toDTO(chatRoom, chatRoom.getOpenUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDTO.Info> getAllOpenChats() {
        return chatRoomRepository.findAll().stream()
                .map(e -> ChatRoomMapper.toDTO(e, e.getOpenUsername())).toList();
    }

    @Override
    public List<ChatRoomDTO.Info> getSubscribedChatRooms(String email) {
//        Set<String> subscribedChatIdsSet =
//                subscribeTemplate.opsForSet().members(REDIS_SUBSCRIBE_KEY + email);

        UserSubscription userSubscription =
                chatReadRepository
                        .findByUsername(email)
                        .orElseThrow(() -> new IllegalArgumentException("mongoDB에 email 없음"));

        List<String> subscribeChatIds =
                userSubscription.getSubscribedChats()
                        .stream().map(ChatSubscriptionDTO::getChatId).toList();
//
//        List<String> subscribedChatIds;
//        if (subscribedChatIdsSet == null || subscribedChatIdsSet.isEmpty()) {
//            subscribedChatIds = Collections.emptyList();
//        } else {
//            subscribedChatIds = new ArrayList<>(subscribedChatIdsSet);
//        }

        return chatRoomRepository.findAllById(subscribeChatIds.stream().map(Long::parseLong).toList())
                .stream().map(e -> ChatRoomMapper.toDTO(e, e.getOpenUsername())).toList();
    }
}
