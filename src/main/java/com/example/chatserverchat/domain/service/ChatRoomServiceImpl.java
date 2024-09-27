package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.dto.ChatRoomDTO;
import com.example.chatserverchat.domain.entity.ChatRoom;
import com.example.chatserverchat.domain.mapper.ChatRoomMapper;
import com.example.chatserverchat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.example.chatserverchat.global.constant.Constants.REDIS_MAX_PERSONNEL_KEY;
import static com.example.chatserverchat.global.constant.Constants.REDIS_PARTICIPATED_KEY;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisTemplate<String, Integer> maxPersonnelTemplate;
    private final RedisTemplate<String, String> participatedTemplate;

    @Override
    public ChatRoomDTO.Info createOpenChat(ChatRoomDTO chatRoomDTO, String openUsername) {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoomMapper.toEntity(chatRoomDTO, openUsername));
        // 채팅방 최대 인원 저장
        maxPersonnelTemplate.opsForSet()
                .add(REDIS_MAX_PERSONNEL_KEY + chatRoom.getId(), chatRoom.getMaxPersonnel());
        // 빈 리스트 할당
        participatedTemplate.opsForList()
                .rightPushAll(REDIS_PARTICIPATED_KEY + chatRoom.getId(), Collections.emptyList());

        return ChatRoomMapper.toDTO(chatRoom, chatRoom.getOpenUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDTO.Info> getAllOpenChats() {
        return chatRoomRepository.findAll().stream()
                .map(e -> ChatRoomMapper.toDTO(e, e.getOpenUsername())).toList();
    }
}
