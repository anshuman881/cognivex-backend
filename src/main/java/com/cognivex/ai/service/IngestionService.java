package com.cognivex.ai.service;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@Service
public class IngestionService {

    private final VectorStore vectorStore;
    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestDocument() throws IOException {
        // Read the sample text file from resources
        ClassPathResource resource = new ClassPathResource("files/rag-sample.txt");
        String content = new String(resource.getInputStream().readAllBytes());
        
        // Split content into chunks for better retrieval
        String[] paragraphs = content.split("\n\n");
        List<Document> docs = new ArrayList<>();
        
        for (String paragraph : paragraphs) {
            if (!paragraph.trim().isEmpty()) {
                docs.add(new Document(paragraph.trim()));
            }
        }
        vectorStore.add(docs);
    }
}
