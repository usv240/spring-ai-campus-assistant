package com.example.campusai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * DEMO 4 - RAG (Retrieval Augmented Generation), which means answering from your own documents.
 * The AI has never seen our course handbook. So before it answers, we find the
 * handbook pieces closest to the question and add them to the prompt, like an open-book exam.
 */
@RestController
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagController(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    // Without RAG: the AI has no handbook, so it guesses or says it does not know.
    // Try: GET /ask/no-rag?question=What is the minimum attendance to sit for the final exam?
    @GetMapping("/ask/no-rag")
    public String askWithoutRag(@RequestParam String question) {
        return chatClient.prompt().user(question).call().content();
    }

    // With RAG: the answer comes from the handbook.
    // Try: GET /ask?question=What is the minimum attendance to sit for the final exam?
    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        return chatClient.prompt()
                // The advisor looks up the 4 handbook pieces closest to the question and adds them to the prompt
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().topK(4).build())
                        .build())
                .user(question)
                .call()
                .content();
    }
}
