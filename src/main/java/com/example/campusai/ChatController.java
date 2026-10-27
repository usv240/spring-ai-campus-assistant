package com.example.campusai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

/**
 * DEMO 1 - Your first AI endpoint.
 * An endpoint is a web address your app answers, like /chat.
 * This is a normal Spring controller; the only new thing is ChatClient.
 */
@RestController
public class ChatController {

    // The object we use to talk to the AI
    private final ChatClient chatClient;

    // Spring hands us the shared ChatClient from AiConfig
    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // Send the student's message to the AI and return its reply as text.
    // Try: GET /chat?message=Explain dependency injection in 3 lines
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()   // start a new request to the AI
                .user(message)       // the student's message
                .call()              // send it and wait for the full reply
                .content();          // take the reply text
    }

    // Same thing, but the reply arrives word by word, like ChatGPT typing.
    // Try: GET /chat/stream?message=Write a haiku about Java
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .stream()            // stream = send the reply in small pieces as it is written
                .content();
    }

    // Same AI, different instructions for this one request, so the answer changes.
    // Try: GET /explain?topic=recursion&level=a 10 year old
    @GetMapping("/explain")
    public String explain(@RequestParam String topic,
                          @RequestParam(defaultValue = "a first-year engineering student") String level) {
        return chatClient.prompt()
                // {level} is a blank that we fill in with the value from the request
                .system(s -> s.text("Explain concepts to {level}. Use one short analogy and one code example if relevant.")
                        .param("level", level))
                .user("Explain: " + topic)
                .call()
                .content();
    }
}
