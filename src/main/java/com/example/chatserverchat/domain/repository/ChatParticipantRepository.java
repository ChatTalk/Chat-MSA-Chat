package com.example.chatserverchat.domain.repository;

import com.example.chatserverchat.domain.document.ChatParticipant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantRepository extends MongoRepository<ChatParticipant, Long> {
}
