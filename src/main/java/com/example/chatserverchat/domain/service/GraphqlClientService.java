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

    // 채팅 인스턴스(grpahql 클라)에서 메세지 인스턴스(graphql 서버)에서 특정 유저의 구독 리스트 조회
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

    // 채팅 인스턴스(grpahql 클라)에서 메세지 인스턴스(graphql 서버)에서
    // 특정 유저의 특정 채팅방 읽지 않은 메세지 조회

    // 채팅 인스턴스(graphql 클라)에서 참여자 인스턴스(graphql 서버)에서
    // 특정 유저의 특정 채팅방 마지막 퇴장 시간 확인

}
