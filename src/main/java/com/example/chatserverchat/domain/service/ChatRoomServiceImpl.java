package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.dto.ChatRoomDTO;
import com.example.chatserverchat.domain.entity.ChatRoom;
import com.example.chatserverchat.domain.mapper.ChatRoomMapper;
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
    private final RedisTemplate<String, Integer> maxPersonnelTemplate;
    private final RedisTemplate<String, String> subscribeTemplate;

    @Override
    public ChatRoomDTO.Info createOpenChat(ChatRoomDTO chatRoomDTO, String openUsername) {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoomMapper.toEntity(chatRoomDTO, openUsername));
        // 채팅방 최대 인원 저장
        maxPersonnelTemplate.opsForValue()
                .set(REDIS_MAX_PERSONNEL_KEY + chatRoom.getId(), chatRoom.getMaxPersonnel());
        // 빈 리스트 할당(이 아니라 어차피 생성자가 거기 참여하겠다는 의도니까)
//        participatedTemplate.opsForList()
//                .rightPush(REDIS_PARTICIPATED_KEY + chatRoom.getId(), openUsername);

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
        Set<String> subscribedChatIdsSet =
                subscribeTemplate.opsForSet().members(REDIS_SUBSCRIBE_KEY + email);

        List<String> subscribedChatIds;
        if (subscribedChatIdsSet == null || subscribedChatIdsSet.isEmpty()) {
            subscribedChatIds = Collections.emptyList();
        } else {
            subscribedChatIds = new ArrayList<>(subscribedChatIdsSet);
        }

        return chatRoomRepository.findAllById(subscribedChatIds.stream().map(Long::parseLong).toList())
                .stream().map(e -> ChatRoomMapper.toDTO(e, e.getOpenUsername())).toList();
    }
}
