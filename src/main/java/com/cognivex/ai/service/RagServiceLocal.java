package com.cognivex.ai.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service("ragService")
@Profile("local")
public class RagServiceLocal implements RagServiceInterface {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    public RagServiceLocal(@Qualifier("ollamaChatModel") ChatModel chatModel, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
    }

    @Override
    public Mono<String> askModel(String question) {
        return Mono.fromCallable(() -> {
                    SearchRequest searchRequest = SearchRequest.builder()
                            .query(question)
                            .build();
                    List<Document> docs = vectorStore.similaritySearch(searchRequest);
                    String context = docs.stream()
                            .map(Document::getText)
                            .collect(Collectors.joining("\n"));
                    String prompt = """
                            Answer the question using only the context below.

                            Context:
                            %s

                            Question:
                            %s
                            """.formatted(context, question);
                    return prompt;
                })
                .subscribeOn(Schedulers.boundedElastic())  // blocking vector search
                .map(prompt -> chatModel.call(prompt))      // normal call
                .map(response -> response);
    }
}