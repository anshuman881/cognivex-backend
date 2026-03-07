package com.cognivex.ai.controller;

import com.cognivex.ai.service.IngestionService;
import com.cognivex.ai.service.RagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotBlank;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class RagController {

    private final RagService ragService;
    private final IngestionService ingestionService;

    public RagController(RagService ragService, IngestionService ingestionService) {
        this.ragService = ragService;
        this.ingestionService = ingestionService;
    }

    @GetMapping("/v1/chat")
    public Mono<String> chat(@RequestParam @NotBlank(message = "Question cannot be blank") String question) {
        return ragService.askModel(question);
    }

    @GetMapping("/v1/ingest")
    public Mono<String> ingest() {
        return Mono.fromCallable(() -> {
                    ingestionService.ingestDocument();
                    return "Documents ingested successfully";
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> Mono.just("Error ingesting documents: " + e.getMessage()));
    }

}
