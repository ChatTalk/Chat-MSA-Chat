package com.example.chatserverchat.global.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

import static com.example.chatserverchat.global.constant.Constants.KAFKA_USER_TO_CHAT_GROUP_ID;
import static com.example.chatserverchat.global.constant.Constants.KAFKA_USER_TO_CHAT_TOPIC;

@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    @KafkaListener(topics = KAFKA_USER_TO_CHAT_TOPIC, groupId = KAFKA_USER_TO_CHAT_GROUP_ID)
    public void consume(String message) {
        log.info("consume message: {}", message);
    }
}
