package com.example.chatserverchat.service;

import com.example.chatserverchat.domain.dto.ChatRoomDTO;
import com.example.chatserverchat.domain.entity.ChatRoom;
import com.example.chatserverchat.domain.repository.ChatRoomRepository;
import com.example.chatserverchat.domain.service.ChatGraphqlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// 인메모리 데이터베이스 h2 기반 동시성 제어 테스트
@SpringBootTest
@ActiveProfiles("test")
public class ConcurrencyTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatGraphqlServiceImpl chatGraphqlService;

    private static final String TITLE = "title";
    private static final Integer MAX_PERSONNEL = 5;
    private static final Integer CLIENT = 1_000;

    @BeforeEach
    @DisplayName("임의의 채팅방 객체 생성")
    void setUp() {
        ChatRoomDTO dto = new ChatRoomDTO(TITLE, MAX_PERSONNEL);
        ChatRoom chatRoom = new ChatRoom(dto, "openUser");

        chatRoomRepository.save(chatRoom);
    }

    @Test
    @DisplayName("H2 데이터베이스 저장 확인")
    void check() {
        // given & when
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(1L);

        // then
        assertThat(chatRoomOptional.isPresent()).isTrue();

        // given & when
        ChatRoom chatRoom = chatRoomOptional.get();

        // then
        assertThat(chatRoom.getTitle()).isEqualTo(TITLE);
    }
}
