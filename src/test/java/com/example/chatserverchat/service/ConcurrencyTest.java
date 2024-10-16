package com.example.chatserverchat.service;

import com.example.chatserverchat.domain.dto.ChatRoomDTO;
import com.example.chatserverchat.domain.dto.GraphqlDTO;
import com.example.chatserverchat.domain.entity.ChatRoom;
import com.example.chatserverchat.domain.repository.ChatRoomRepository;
import com.example.chatserverchat.domain.service.ChatGraphqlServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

// 인메모리 데이터베이스 h2 기반 동시성 제어 테스트
// 메모리 기반이기 떄문에 테스트 종료시, 자동 휘발됨(데이터베이스까지!)
@Slf4j
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({ChatGraphqlServiceImpl.class})
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
        // given & when, then
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(1L);
        assertThat(chatRoomOptional.isPresent()).isTrue();

        // given & when, then
        ChatRoom chatRoom = chatRoomOptional.get();
        assertThat(chatRoom.getTitle()).isEqualTo(TITLE);
    }

    @Test
    @DisplayName("트래픽이 몰리는 상황에서 정합성이 지켜지는지 여부")
    void testWithNoLock() throws InterruptedException {
        // given & when
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(1L);
        assertThat(chatRoomOptional.isPresent()).isTrue();

        ChatRoom chatRoom = chatRoomOptional.get();

        // 스레드 풀 및 동시 시작 장치
        ExecutorService executorService = Executors.newFixedThreadPool(CLIENT);
        CountDownLatch countDownLatch = new CountDownLatch(CLIENT);

        // 성공 결과를 담을 자료구조
        Set<GraphqlDTO> result = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < CLIENT; i++) {
            executorService.submit(() -> {
               try {
                   GraphqlDTO success =
                           chatGraphqlService
                                   .incrementPersonnel(chatRoom.getId().toString());
                   result.add(success);
                   log.info("입장 성공: {}", success);
               } catch (Exception e) {
                   log.error("입장 실패! : {}", e.getMessage());
               } finally {
                   countDownLatch.countDown(); // 카운트 감소
               }
            });
        }

        countDownLatch.await(); // 스레드 종료 대기
        executorService.shutdown(); // 서비스 종료

        // then
        log.info("예상 결과: {}", MAX_PERSONNEL);
        log.info("실제 결과: {}", result.size());
        assertThat(result.size()).isEqualTo(MAX_PERSONNEL);
    }
}
