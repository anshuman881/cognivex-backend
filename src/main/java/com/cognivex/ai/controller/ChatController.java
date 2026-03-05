package com.cognivex.ai.controller;

import com.cognivex.ai.service.ingestion.ChatService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
public class ChatController {

    private final ChatService chatService;
    private ChatController(ChatService chatService){
        this.chatService = chatService;
    }
}
