package com.cognivex.ai.controller;

import com.cognivex.ai.service.MetricsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MetricsWebSocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(MetricsWebSocketHandler.class);

    private final ObjectMapper objectMapper;
    private final MetricsService metricsService;
    private final CopyOnWriteArrayList<WebSocketSession> sessions =
            new CopyOnWriteArrayList<>();

    private final Sinks.Many<Map<String, Object>> metricsSink = Sinks.many().multicast().directBestEffort();

    public MetricsWebSocketHandler(MetricsService metricsService, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.metricsService = metricsService;

        // Subscribe to metrics and broadcast to WebSocket sessions
        metricsService.subscribe(metrics -> {
            metricsSink.emitNext(metrics, Sinks.EmitFailureHandler.FAIL_FAST);
        });
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();
        logger.info("New WebSocket connection: {}", sessionId);
        // Send metrics to client
        Flux<WebSocketMessage> messageFlux = metricsSink.asFlux()
                .map(metrics -> {
                    try {
                        String json = objectMapper.writeValueAsString(metrics);
                        return session.textMessage(json);
                    } catch (Exception e) {
                        logger.error("Error serializing metrics: {}", e.getMessage());
                        return session.textMessage("{\"error\":\"Serialization error\"}");
                    }
                })
                .doOnCancel(() -> {
                    logger.info("WebSocket connection closed: {}", sessionId);
                });
        return session.send(messageFlux);
    }
}
