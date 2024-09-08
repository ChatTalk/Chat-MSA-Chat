package com.example.chatserverchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ChatServerChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServerChatApplication.class, args);
    }

}
