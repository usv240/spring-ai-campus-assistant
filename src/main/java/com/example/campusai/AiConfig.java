package com.example.campusai;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AiConfig {

    /**
     * One shared ChatClient for the app. The builder is auto-configured from
     * whichever model starter is on the classpath (Ollama here). Swap the
     * starter to OpenAI / Gemini / Anthropic and this code does not change.
     */
    // EXERCISE 1: change the system prompt below and restart (see EXERCISES.md)
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        You are Campus Assistant, a helpful assistant for university students.
                        Answer clearly and briefly. If you do not know something, say so
                        instead of guessing.
                        """)
                .build();
    }

    /** In-memory vector store: perfect for a demo, swap for PGvector/Redis/etc. in production. */
    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * ETL pipeline for RAG: Extract (read the handbook) -> Transform (split into chunks)
     * -> Load (embed and store). Runs once at startup.
     */
    @Bean
    ApplicationRunner ingestHandbook(VectorStore vectorStore,
                                     @Value("classpath:docs/course-handbook.md") Resource handbook) {
        return args -> {
            List<Document> documents = new TextReader(handbook).get();
            List<Document> chunks = TokenTextSplitter.builder().withChunkSize(100).build().apply(documents);
            vectorStore.add(chunks);
            System.out.println("Ingested " + chunks.size() + " handbook chunks into the vector store");
        };
    }
}
