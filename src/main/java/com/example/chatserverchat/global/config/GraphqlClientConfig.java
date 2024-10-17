package com.example.chatserverchat.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

// 비동기 및 논블로킹, Reactive Streams 을 지원
@Configuration
public class GraphqlClientConfig {

    @Value("${graphql.url.message}")
    private String CHAT_MESSAGE_URL;

    @Value("${graphql.url.participant}")
    private String CHAT_PARTICIPANT_URL;

    @Bean(name = "messageGraphQlClient")
    public HttpGraphQlClient messageGraphQlClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(CHAT_MESSAGE_URL)
                .build();

        return HttpGraphQlClient.builder(webClient)
                .build();
    }

    @Bean(name = "participantGraphQlClient")
    public HttpGraphQlClient participantGraphQlClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(CHAT_PARTICIPANT_URL)
                .build();

        return HttpGraphQlClient.builder(webClient)
                .build();
    }
}
