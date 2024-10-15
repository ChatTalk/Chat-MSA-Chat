package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.dto.GraphqlDTO;
import com.example.chatserverchat.domain.entity.ChatRoom;
import com.example.chatserverchat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatGraphqlServiceImpl implements ChatGraphqlService {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional(readOnly = true)
    public GraphqlDTO getChatRoomById(String id) {
        ChatRoom chatRoom =
                chatRoomRepository.findById(Long.parseLong(id)).orElseThrow(null);

        log.info("그래프큐엘 찾았냐(현재 인원): {}", chatRoom.getPersonnel());

        return new GraphqlDTO(
                chatRoom.getId().toString(),
                chatRoom.getTitle(),
                chatRoom.getOpenUsername(),
                chatRoom.getPersonnel(),
                chatRoom.getMaxPersonnel(),
                chatRoom.getCreatedAt().toString()
        );
    }

    @Override
    public GraphqlDTO incrementPersonnel(String id) throws HttpResponseException {
        log.info("여긴 들어오나?(채팅방 아이디: {})", id);

        ChatRoom chatRoom =
                chatRoomRepository.findById(Long.parseLong(id)).orElseThrow(null);

        log.info("그래프큐엘 찾았냐(현재 인원 / 최대인원): {} / {}",
                chatRoom.getPersonnel(),
                chatRoom.getMaxPersonnel());

        /**
         * 락 구현은 이 로직 내에서 이뤄져야 할듯? 분산락 써보자
         */
        if (chatRoom.getMaxPersonnel() <= chatRoom.getPersonnel()) {
            throw new HttpResponseException(HttpStatus.SC_FORBIDDEN, "이미 정원이 초과된 채팅방");
        }

        chatRoom.increasePersonnel();

        return new GraphqlDTO(
                chatRoom.getId().toString(),
                chatRoom.getTitle(),
                chatRoom.getOpenUsername(),
                chatRoom.getPersonnel(),
                chatRoom.getMaxPersonnel(),
                chatRoom.getCreatedAt().toString()
        );
    }
}
