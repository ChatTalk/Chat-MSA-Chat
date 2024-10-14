package com.example.chatserverchat.domain.document;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Data
@Getter
@Document(collection = "participants")
public class ChatParticipant {

    @Id
    private String id; // MongoDB 기본 키

    private String chatId;

    private Map<String, Boolean> participant;

    /**
     * 생성자
     * @param chatId
     * 맵은 해시 구조
     */
    public ChatParticipant(String chatId) {
        this.chatId = chatId;
        this.participant = new HashMap<>();
    }
}
