package com.example.campusai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

/**
 * DEMO 1 - Your first AI endpoint.
 * A normal Spring REST controller; the only new thing is ChatClient.
 */
@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // GET /chat?message=Explain dependency injection in 3 lines
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    // Same call, but tokens arrive as they are generated (like ChatGPT typing).
    // GET /chat/stream?message=Write a haiku about Java
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

    // Per-request system prompt: same model, different behaviour.
    // GET /explain?topic=recursion&level=a 10 year old
    @GetMapping("/explain")
    public String explain(@RequestParam String topic,
                          @RequestParam(defaultValue = "a first-year engineering student") String level) {
        return chatClient.prompt()
                .system(s -> s.text("Explain concepts to {level}. Use one short analogy and one code example if relevant.")
                        .param("level", level))
                .user("Explain: " + topic)
                .call()
                .content();
    }
}
