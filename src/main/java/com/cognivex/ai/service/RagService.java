package com.cognivex.ai.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    public RagService(ChatModel chatModel, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
    }

    public Mono<String> askModel(String question) {
        return Mono.fromCallable(() -> {
                    SearchRequest searchRequest = SearchRequest.builder()
                            .query(question)
                            .build();
                    List<Document> docs = vectorStore.similaritySearch(searchRequest);
                    String context = docs.stream()
                            .map(Document::getText)
                            .collect(Collectors.joining("\n"));
                    String promptTxt = """
                            Answer the question using only the context below.
                            
                            Context:
                            %s
                            
                            Question:
                            %s
                            """.formatted(context, question);
                    return new Prompt(List.of(
                            new SystemMessage("You are a helpful assistant."),
                            new UserMessage(promptTxt)
                    ));
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(prompt -> chatModel.call(prompt))
                .map(response -> response.getResult().getOutput().getText());
    }
}