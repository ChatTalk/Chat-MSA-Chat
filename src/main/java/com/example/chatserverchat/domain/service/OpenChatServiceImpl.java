package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.dto.OpenChatDTO;
import com.example.chatserverchat.domain.entity.OpenChat;
import com.example.chatserverchat.domain.mapper.OpenChatMapper;
import com.example.chatserverchat.domain.repository.OpenChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OpenChatServiceImpl implements OpenChatService {

    private final OpenChatRepository openChatRepository;

    @Override
    public OpenChatDTO.Info createOpenChat(OpenChatDTO openChatDTO, String openUsername) {
        OpenChat openChat = OpenChatMapper.toEntity(openChatDTO, openUsername);
        openChatRepository.save(openChat);

        return OpenChatMapper.toDTO(openChat, openChat.getOpenUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpenChatDTO.Info> getAllOpenChats() {
        return openChatRepository.findAll().stream()
                .map(e -> OpenChatMapper.toDTO(e, e.getOpenUsername())).toList();
    }
}
