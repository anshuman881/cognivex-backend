package com.cognivex.ai.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RagServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatModel chatModel;

    @InjectMocks
    private RagService ragService;

    @Test
    void testAskModel() {
        // Mock vector store
        Document doc = new Document("Test content");
        Mockito.when(vectorStore.similaritySearch(Mockito.any(SearchRequest.class)))
            .thenReturn(List.of(doc));

        // Mock chat model
        Mockito.when(chatModel.call(Mockito.anyString())).thenReturn("Mocked response");

        StepVerifier.create(ragService.askModel("Test question"))
            .expectNext("Mocked response")
            .verifyComplete();
    }
}