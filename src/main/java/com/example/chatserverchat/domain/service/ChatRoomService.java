package com.example.chatserverchat.domain.service;


import com.example.chatserverchat.domain.dto.ChatRoomDTO;

import java.util.List;

public interface ChatRoomService {
    ChatRoomDTO.Info createOpenChat(ChatRoomDTO chatRoomDTO, String openUsername);

    List<ChatRoomDTO.Info> getAllOpenChats();

    List<ChatRoomDTO.Info> getSubscribedChatRooms(String email);
}
