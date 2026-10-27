package com.example.campusai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * DEMO 3 - Memory and tools.
 * An AI model forgets everything between messages, so the memory advisor re-sends
 * the recent conversation each time (one conversation per student).
 * Tools are Java methods the AI can ask us to run, like checking and booking lab slots.
 */
@RestController
public class AssistantController {

    private final ChatClient chatClient;
    private final LabBookingTools labTools;

    public AssistantController(ChatClient chatClient, ChatMemory chatMemory, LabBookingTools labTools) {
        this.labTools = labTools;
        // Start from the shared ChatClient, then add our own instructions and memory
        this.chatClient = chatClient.mutate()
                .defaultSystem("""
                        You are Campus Assistant. You can book lab slots.
                        The labs are AI Lab, Cloud Lab and IoT Lab.
                        Before booking, check the free slots with listFreeSlots.
                        Only say a slot is booked if bookSlot returned a confirmation id.
                        If bookSlot returned an error, say the booking failed and offer the free slots.
                        """)
                // The memory advisor remembers earlier messages from the same student
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    // Chat with the assistant. The "user" value tells us whose memory to use.
    // Try: GET /assistant?user=21BCE1234&message=My reg no is 21BCE1234. Which AI Lab slots are free?
    // Then: GET /assistant?user=21BCE1234&message=Book the 2 PM one for me
    @GetMapping("/assistant")
    public String assistant(@RequestParam String user, @RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                // Tell the AI which Java methods (tools) it may use
                .tools(labTools)
                // Use this student's own conversation as memory
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user))
                .call()
                .content();
    }
}
