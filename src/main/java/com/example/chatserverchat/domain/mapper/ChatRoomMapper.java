package com.example.chatserverchat.domain.mapper;


import com.example.chatserverchat.domain.dto.ChatRoomDTO;
import com.example.chatserverchat.domain.entity.ChatRoom;

public class ChatRoomMapper {

    public static ChatRoom toEntity(ChatRoomDTO dto, String openUsername) {
        return new ChatRoom(dto, openUsername);
    }

    public static ChatRoomDTO.Info toDTO(ChatRoom chat, String username) {
        return new ChatRoomDTO.Info(
                chat.getId().toString(),
                chat.getTitle(),
                username,
                chat.getPersonnel(),
                chat.getMaxPersonnel());
    }
}
