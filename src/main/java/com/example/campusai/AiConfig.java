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

// This class sets things up once, when the app starts.
@Configuration
public class AiConfig {

    /**
     * One shared ChatClient for the whole app. The ChatClient is the object we use
     * to talk to the AI model. Spring builds the builder for us, using whichever
     * model library is installed (Ollama here). If you swap Ollama for OpenAI,
     * Gemini or Claude, this code does not change.
     */
    // EXERCISE 1 START HERE: change the text inside defaultSystem below, then restart (see EXERCISES.md)
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        // defaultSystem = the standing instructions the AI follows for every question
        return builder
                .defaultSystem("""
                        You are Campus Assistant, a helpful assistant for university students.
                        Answer clearly and briefly. If you do not know something, say so
                        instead of guessing.
                        """)
                .build();
    }

    // The vector store is a small in-memory shelf that keeps the handbook pieces
    // so we can search them by meaning. Fine for a demo; real apps use a database.
    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * Getting the handbook ready for RAG (answering from your own documents).
     * Read the file, cut it into small pieces, then store each piece so it can be
     * found later. This runs once, when the app starts.
     */
    @Bean
    ApplicationRunner ingestHandbook(VectorStore vectorStore,
                                     @Value("classpath:docs/course-handbook.md") Resource handbook) {
        return args -> {
            // Read the handbook file
            List<Document> documents = new TextReader(handbook).get();
            // Cut it into small pieces (about 100 tokens each) so we can fetch just the relevant ones
            List<Document> chunks = TokenTextSplitter.builder().withChunkSize(100).build().apply(documents);
            // Store the pieces in the vector store
            vectorStore.add(chunks);
            System.out.println("Ingested " + chunks.size() + " handbook chunks into the vector store");
        };
    }
}
