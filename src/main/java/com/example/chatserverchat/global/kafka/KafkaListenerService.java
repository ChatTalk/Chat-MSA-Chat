package com.example.chatserverchat.global.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaListenerService {

    // 필요한 다른 곳에서 동일한 어노테이션을 할당하고 메세지를 받으면 됨
    @KafkaListener(topics = "email", groupId = "user")
    public void listen(String message) {
        log.info("수신한 이메일: {} ", message);
    }
}
