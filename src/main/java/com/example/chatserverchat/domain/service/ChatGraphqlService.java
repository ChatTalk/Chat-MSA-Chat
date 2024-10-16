package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.dto.GraphqlDTO;

public interface ChatGraphqlService {

    GraphqlDTO getChatRoomById(String id);

    GraphqlDTO incrementPersonnel(String id);

    GraphqlDTO incrementPersonnelWithLock(String id);

    GraphqlDTO decrementPersonnel(String id);
}
