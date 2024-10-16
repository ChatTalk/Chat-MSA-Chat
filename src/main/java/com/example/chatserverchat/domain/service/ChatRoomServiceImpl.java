package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.dto.ChatRoomDTO;
import com.example.chatserverchat.domain.entity.ChatRoom;
import com.example.chatserverchat.domain.mapper.ChatRoomMapper;
import com.example.chatserverchat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final GraphqlClientService graphqlClientService;

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
    public List<ChatRoomDTO.Info> getSubscribedChatRooms(String email, String role) {

        List<Long> chatIdList = graphqlClientService.getChatRoomById(email, role)
                .stream().map(e -> Long.parseLong(e.chatId())).toList();

        return chatRoomRepository.findByIdIn(chatIdList).stream()
                .map(e -> ChatRoomMapper.toDTO(e, e.getOpenUsername())).toList();
    }
}
