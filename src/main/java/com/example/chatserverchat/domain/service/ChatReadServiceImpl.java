package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.document.UserSubscription;
import com.example.chatserverchat.domain.dto.ChatMessageDTO;
import com.example.chatserverchat.domain.repository.ChatReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatReadServiceImpl implements ChatReadService {

    private final ChatReadRepository chatReadRepository;

    // 유저가 구독한 채팅방의 읽지 않은 메시지 가져오기
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getUnreadMessages(String username, String chatId) {
        Optional<UserSubscription> userOptional =  chatReadRepository.findByUsername(username);

        return userOptional
                .map(userSubscription -> userSubscription.getSubscribedChats().stream()
                        .filter(sub -> sub.getChatId().equals(chatId))
                        .findFirst()
                        .map(chatSubscription -> {
                            // 읽지 않은 메시지 리스트를 반환
                            List<ChatMessageDTO> messages
                                    = new ArrayList<>(chatSubscription.getUnreadMessages());

                            // 읽지 않은 메시지 리스트를 빈 배열로 초기화
                            chatSubscription.setUnreadMessages(Collections.emptyList());

                            // 변경사항을 저장소에 반영 (MongoDB에 반영)
                            chatReadRepository.save(userSubscription);

                            return messages;
                        })
                        .orElse(Collections.emptyList()))
                .orElse(Collections.emptyList());
    }
}
