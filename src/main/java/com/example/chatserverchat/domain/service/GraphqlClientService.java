package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.dto.ChatUserSubscriptionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.WebGraphQlClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j(topic = "GRAPHQL_SERVICE")
@Service
@RequiredArgsConstructor
public class GraphqlClientService {

    private final WebGraphQlClient webGraphQlClient;

    public List<ChatUserSubscriptionDTO> getChatRoomById(String email, String role) {
        String query = """
                query SubscriptionsByEmail($email: String!) {
                    subscriptionsByEmail(email: $email) {
                        id
                        chatId
                        email
                    }
                }
                """;

        // GraphQL 요청 보내기
        List<ChatUserSubscriptionDTO> response =  webGraphQlClient
                .mutate()
                .header("email", email)
                .header("role", role)
                .build()
                .document(query)
                .variable("email", email)
                .retrieve("subscriptionsByEmail") // 반환할 필드를 지정
                .toEntityList(ChatUserSubscriptionDTO.class) // DTO 리스트로 매핑
                .block(); // STOMP 구조에 따른 톰캣으로 인한 블로킹 처리

        log.info("graphql 응답; {}", response);

        return response;
    }

}
