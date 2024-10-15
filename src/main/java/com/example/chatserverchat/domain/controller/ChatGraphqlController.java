package com.example.chatserverchat.domain.controller;

import com.example.chatserverchat.domain.dto.ChatRoomDTO;
import com.example.chatserverchat.domain.dto.GraphqlDTO;
import com.example.chatserverchat.domain.entity.ChatRoom;
import com.example.chatserverchat.domain.mapper.ChatRoomMapper;
import com.example.chatserverchat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/graphql")
public class ChatGraphqlController {

    private final ChatRoomRepository chatRoomRepository;

    @QueryMapping
    public GraphqlDTO getChatRoomById(@Argument String id) {
        ChatRoom chatRoom =
                chatRoomRepository.findById(Long.parseLong(id)).orElseThrow(null);

        log.info("그래프큐엘 찾았냐(현재 인원): {}", chatRoom.getPersonnel());

        return new GraphqlDTO(
                chatRoom.getId().toString(),
                chatRoom.getTitle(),
                chatRoom.getOpenUsername(),
                chatRoom.getPersonnel(),
                chatRoom.getMaxPersonnel(),
                chatRoom.getCreatedAt().toString()
        );
    }

}
