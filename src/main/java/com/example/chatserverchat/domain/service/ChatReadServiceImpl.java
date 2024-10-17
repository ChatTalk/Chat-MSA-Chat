package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.document.UserSubscription;
import com.example.chatserverchat.domain.dto.ChatMessageDTO;
import com.example.chatserverchat.domain.repository.ChatReadRepository;
import com.example.chatserverchat.domain.utility.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatReadServiceImpl implements ChatReadService {

    private final GraphqlClientService graphqlClientService;

    // 유저가 구독한 채팅방의 읽지 않은 메시지 가져오기
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getUnreadMessages(String username, String chatId, String role) {

        // 1. 해당 채팅방 id, 사용자 이메일을 전달해서 **참여자 인스턴스**에서 접속 종료 시간 갖고오기
        String exit = graphqlClientService.getExitTime(chatId, username, role);
        LocalDateTime exitTime = DateTimeUtil.toLocalDateTime(exit);

        // 2. 해당 채팅방 id를 기반으로 **메세지 인스턴스**에서 접속 종료 시간부터 현재 시간까지의 메세지 리스트 들고와서 반환
        List<ChatMessageDTO> messages =
                graphqlClientService.getChatMessages(chatId, exitTime, username, role);

        log.info("안 읽은 메세지: {}", messages.stream().map(ChatMessageDTO::getMessage).toList());

        return messages;
    }
}
