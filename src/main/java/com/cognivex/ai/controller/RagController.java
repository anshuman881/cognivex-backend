package com.cognivex.ai.controller;

import com.cognivex.ai.service.ingestion.RagService;
import com.cognivex.ai.service.ingestion.DocumentIngestionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
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

    @GetMapping("/ask")
    public Mono<String> ask(@RequestParam String question) {
        return ragService.askModel(question);
    }

    @GetMapping(value="/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<String> chat(String question) {
        return ragService.askModel(question);
    }

    @GetMapping("/ingest")
    public Mono<String> ingest() {
        return Mono.fromRunnable(() -> documentIngestionService.ingestDocument())
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn("Documents ingested");
    }

}
