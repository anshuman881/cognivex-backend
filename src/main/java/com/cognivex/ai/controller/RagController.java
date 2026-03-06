package com.cognivex.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognivex.ai.service.DocumentIngestionService;
import com.cognivex.ai.service.RagService;

import jakarta.validation.constraints.NotBlank;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class RagController {

    private final RagService ragService;
    private final DocumentIngestionService documentIngestionService;

    public RagController(RagService ragService, DocumentIngestionService documentIngestionService) {
        this.ragService = ragService;
        this.documentIngestionService = documentIngestionService;
    }

    @GetMapping("/v1/chat")
    public Mono<String> chat(@RequestParam @NotBlank(message = "Question cannot be blank") String question) {
        return ragService.askModel(question);
    }

    @GetMapping("/v1/ingest")
    public Mono<String> ingest() {
        return Mono.fromCallable(() -> {
                    documentIngestionService.ingestDocument();
                    return "Documents ingested successfully";
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> Mono.just("Error ingesting documents: " + e.getMessage()));
    }

}
