package com.example.campusai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * DEMO 3 - Memory + tools = a small agent.
 * LLMs are stateless; the memory advisor replays recent messages per conversation id.
 * Tools let the model take actions (check and book lab slots).
 */
@RestController
public class AssistantController {

    private final ChatClient chatClient;
    private final LabBookingTools labTools;

    public AssistantController(ChatClient chatClient, ChatMemory chatMemory, LabBookingTools labTools) {
        this.labTools = labTools;
        this.chatClient = chatClient.mutate()
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    // GET /assistant?user=21BCE1234&message=My reg no is 21BCE1234. Which AI Lab slots are free?
    // GET /assistant?user=21BCE1234&message=Book the 2 PM one for me
    @GetMapping("/assistant")
    public String assistant(@RequestParam String user, @RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .tools(labTools)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user))
                .call()
                .content();
    }
}
