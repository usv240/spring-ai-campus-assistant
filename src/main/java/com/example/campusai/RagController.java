package com.example.campusai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * DEMO 4 - Retrieval Augmented Generation (RAG).
 * The model has never seen our course handbook. The advisor finds the most
 * relevant chunks in the vector store and adds them to the prompt.
 */
@RestController
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagController(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    // Without RAG: the model guesses (or admits it does not know).
    // GET /ask/no-rag?question=What is the minimum attendance to sit for the final exam?
    @GetMapping("/ask/no-rag")
    public String askWithoutRag(@RequestParam String question) {
        return chatClient.prompt().user(question).call().content();
    }

    // With RAG: grounded answer from the handbook.
    // GET /ask?question=What is the minimum attendance to sit for the final exam?
    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        return chatClient.prompt()
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().topK(4).build())
                        .build())
                .user(question)
                .call()
                .content();
    }
}
