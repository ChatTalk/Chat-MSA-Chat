package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.dto.GraphqlDTO;
import com.example.chatserverchat.domain.entity.ChatRoom;
import com.example.chatserverchat.domain.repository.ChatRoomRepository;
import com.example.chatserverchat.global.facade.DistributedLockFacade;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_LOCKED;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatGraphqlServiceImpl implements ChatGraphqlService {

    private final ChatRoomRepository chatRoomRepository;
    private final DistributedLockFacade distributedLock;

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
    public GraphqlDTO incrementPersonnel(String id) {
        return getGraphqlDTO(id);
    }

    @Override
    public GraphqlDTO incrementPersonnelWithLock(String id) {
        String lockKey = "LOCK_" + id;
        boolean isLocked = distributedLock.tryLock(lockKey, 5, 2);

        if (!isLocked) {
            throw new ResponseStatusException(
                    HttpStatusCode.valueOf(SC_LOCKED), "락 획득 실패");
        }

        try {
            return getGraphqlDTO(id);
        } finally {
            distributedLock.unlock(lockKey);
        }
    }

    private GraphqlDTO getGraphqlDTO(String id) {
        ChatRoom chatRoom =
                chatRoomRepository.findById(Long.parseLong(id)).orElseThrow(
                        () -> new IllegalArgumentException("해당하는 채팅방이 조회되지 않음")
                );

        log.info("그래프큐엘 찾았냐(현재 인원 / 최대인원): {} / {}",
                chatRoom.getPersonnel(),
                chatRoom.getMaxPersonnel());

        if (chatRoom.getMaxPersonnel() <= chatRoom.getPersonnel()) {
            throw new ResponseStatusException(
                    HttpStatusCode.valueOf(SC_FORBIDDEN), "이미 정원이 초과된 채팅방");
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

    @Override
    public GraphqlDTO decrementPersonnel(String id) {
        ChatRoom chatRoom =
                chatRoomRepository.findById(Long.parseLong(id)).orElseThrow(null);

        log.info("감소 로직(현재 인원 / 최대인원): {} / {}",
                chatRoom.getPersonnel(),
                chatRoom.getMaxPersonnel());

        chatRoom.decreasePersonnel();

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
