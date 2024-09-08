package com.example.chatserverchat.domain.mapper;


import com.example.chatserverchat.domain.dto.OpenChatDTO;
import com.example.chatserverchat.domain.entity.OpenChat;

public class OpenChatMapper {

    public static OpenChat toEntity(OpenChatDTO dto, String openUsername) {
        return new OpenChat(dto, openUsername);
    }

    public static OpenChatDTO.Info toDTO(OpenChat chat, String username) {
        return new OpenChatDTO.Info(chat.getId().toString(), chat.getTitle(), username, chat.getMaxPersonnel());
    }
}
