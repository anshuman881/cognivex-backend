package com.cognivex.ai.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.List;

@SpringBootTest
class RagServiceTest {

    @Autowired
    private RagService ragService;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatModel chatModel;

    @Test
    void testAskModel() {
        // Mock vector store
        Document doc = new Document("Test content");
        Mockito.when(vectorStore.similaritySearch(Mockito.any(SearchRequest.class)))
            .thenReturn(List.of(doc));

        // Mock chat model
        ChatResponse response = Mockito.mock(ChatResponse.class);
        Mockito.when(response.getResult()).thenReturn(Mockito.mock());
        Mockito.when(response.getResult().getOutput()).thenReturn(Mockito.mock());
        Mockito.when(response.getResult().getOutput().getText()).thenReturn("Mocked response");
        Mockito.when(chatModel.call(Mockito.anyString())).thenReturn(String.valueOf(response));

        StepVerifier.create(ragService.askModel("Test question"))
            .expectNext("Mocked response")
            .verifyComplete();
    }
}