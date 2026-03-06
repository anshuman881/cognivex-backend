package com.cognivex.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognivex.ai.service.ingestion.DocumentIngestionService;
import com.cognivex.ai.service.ingestion.RagService;

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

    @GetMapping("/chat")
    public Mono<String> chat(@RequestParam String question) {
        return ragService.askModel(question);
    }

    @GetMapping("/ingest")
    public Mono<String> ingest() {
        return Mono.fromCallable(() -> {
                    documentIngestionService.ingestDocument();
                    return "Documents ingested";
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> Mono.just("Error ingesting documents: " + e.getMessage()));
    }

}
