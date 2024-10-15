package com.example.chatserverchat.domain.controller;

import com.example.chatserverchat.domain.dto.GraphqlDTO;
import com.example.chatserverchat.domain.service.ChatGraphqlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/graphql")
public class ChatGraphqlController {

    private final ChatGraphqlService chatGraphqlService;

    @QueryMapping
    public GraphqlDTO getChatRoomById(@Argument String id) {
        return chatGraphqlService.getChatRoomById(id);
    }

    @MutationMapping
    public GraphqlDTO incrementPersonnel(@Argument String id) throws HttpResponseException {
        return chatGraphqlService.incrementPersonnel(id);
    }

    @MutationMapping
    public GraphqlDTO decrementPersonnel(@Argument String id) {
        return chatGraphqlService.decrementPersonnel(id);
    }
}
