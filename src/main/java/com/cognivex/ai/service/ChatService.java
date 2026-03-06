package com.cognivex.ai.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class ChatService {

    public ChatService(RagService ragService) {}

    public Flux<String> generateResponse(String userMessage) {
        String response = "Hello! This is a simulated streaming response for: " + userMessage;
        String[] words = response.split(" ");
        return Flux.fromArray(words)
                .delayElements(Duration.ofMillis(500))
                .map(word -> word + " ");
    }
}
