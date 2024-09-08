package com.example.chatserverchat.domain.repository;

import com.example.chatserverchat.domain.entity.OpenChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenChatRepository extends JpaRepository<OpenChat, Long> {
}
