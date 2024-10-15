package com.example.chatserverchat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomDTO {
    private String title;
    private Integer maxPersonnel;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private String chatId;
        private String title;
        private String openUsername;
        private Integer personnel;
        private Integer maxPersonnel;
    }
}
