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

    @PostMapping(value = "/stream/{request}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Flux<String> streamChat(@PathVariable String request) {
        return chatService.generateResponse(request);
    }

    @GetMapping(value = "/download-stream")
    public ResponseEntity<Flux<DataBuffer>> downloadFileStream() throws IOException {
        ClassPathResource resource =
                new ClassPathResource("files/sample.pdf");
        Flux<DataBuffer> data = DataBufferUtils.read(
                resource,
                new DefaultDataBufferFactory(),
                4096
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=sample.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(resource.contentLength())
                .body(data);
    }
}
