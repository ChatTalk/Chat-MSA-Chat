package com.example.chatserverchat.domain.service;


import com.example.chatserverchat.domain.dto.OpenChatDTO;

import java.util.List;

public interface OpenChatService {
    OpenChatDTO.Info createOpenChat(OpenChatDTO openChatDTO, String openUsername);

    List<OpenChatDTO.Info> getAllOpenChats();
}
