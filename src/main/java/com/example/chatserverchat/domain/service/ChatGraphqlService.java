package com.example.chatserverchat.domain.service;

import com.example.chatserverchat.domain.dto.GraphqlDTO;
import org.apache.http.client.HttpResponseException;

public interface ChatGraphqlService {

    GraphqlDTO getChatRoomById(String id);

    GraphqlDTO incrementPersonnel(String id) throws HttpResponseException;

    GraphqlDTO decrementPersonnel(String id);
}
