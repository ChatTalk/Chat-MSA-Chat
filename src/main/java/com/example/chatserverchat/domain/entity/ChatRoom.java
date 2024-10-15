package com.example.chatserverchat.domain.entity;

import com.example.chatserverchat.domain.dto.ChatRoomDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "openchats")
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "open_username")
    private String openUsername;

    @Column(name = "personnel")
    private Integer personnel;

    @Column(name = "max_personnel")
    private Integer maxPersonnel;

    @CreatedDate
    @Column(name = "created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    public ChatRoom(ChatRoomDTO dto, String openUsername) {
        this.title = dto.getTitle();
        this.openUsername = openUsername;
        this.personnel = 0;
        this.maxPersonnel = dto.getMaxPersonnel();
    }

    public void increasePersonnel() {
        this.personnel++;
    }

    public void decreasePersonnel() {
        this.personnel--;
    }
}
