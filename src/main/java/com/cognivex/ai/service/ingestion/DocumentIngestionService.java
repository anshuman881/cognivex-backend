package com.cognivex.ai.service.ingestion;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;

import java.util.List;

@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;
    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestDocument() {
        List<Document> docs = List.of(
                new Document("Cognivex is an enterprise RAG platform."),
                new Document("Spring AI helps integrate LLMs into Spring Boot apps.")
        );

        vectorStore.add(docs);
    }
}
