package com.example.chatserverchat.global.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.example.chatserverchat.global.constant.Constants.KAFKA_USER_DATA_KEY;
import static com.example.chatserverchat.global.constant.Constants.KAFKA_USER_TO_CHAT_TOPIC;

@Slf4j
@Service
public class KafkaListenerService {

    private static final ConcurrentMap<String, String> emailData = new ConcurrentHashMap<>();

    // 필요한 다른 곳에서 동일한 어노테이션을 할당하고 메세지를 받으면 됨
    @KafkaListener(topics = KAFKA_USER_TO_CHAT_TOPIC, groupId = "user")
    public void listen(String message) {
        log.info("수신한 이메일: {} ", message);
        emailData.putIfAbsent(KAFKA_USER_DATA_KEY, message);
    }

    public static String getEmail() {
        return emailData.get(KAFKA_USER_DATA_KEY);
    }
}
