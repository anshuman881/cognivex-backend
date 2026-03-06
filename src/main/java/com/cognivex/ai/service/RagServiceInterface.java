package com.cognivex.ai.service;

import reactor.core.publisher.Mono;

public interface RagServiceInterface {
    Mono<String> askModel(String question);
}