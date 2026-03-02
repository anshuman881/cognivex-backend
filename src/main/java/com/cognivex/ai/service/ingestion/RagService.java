package com.cognivex.ai.service.ingestion;

import org.springframework.ai.document.Document;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    public RagService(VectorStore vectorStore, ChatModel chatModel) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
    }

    public String askModel(String question) {
        SearchRequest searchRequest = SearchRequest.builder().query(question).build();
        List<Document> docs = vectorStore.similaritySearch(searchRequest);

        String context = docs.stream().map(Document::getText)
                .collect(Collectors.joining("\n"));

        String prompt = """
                Answer the question using only the context below.

                Context:
                %s

                Question:
                %s
                """.formatted(context, question);

        return chatModel.call(prompt);

    }
}
