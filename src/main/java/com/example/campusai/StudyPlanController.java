package com.example.campusai;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * DEMO 2 - Structured output.
 * The LLM returns JSON that Spring AI maps straight into Java records,
 * so the rest of your code works with types, not strings.
 */
@RestController
public class StudyPlanController {

    public record StudyTask(String day, String topic, int hours, String resource) {}

    public record StudyPlan(String subject, int totalHours, List<StudyTask> tasks, List<String> tips) {}

    private final ChatClient chatClient;

    public StudyPlanController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // GET /study-plan?subject=Data Structures&days=5
    @GetMapping("/study-plan")
    public StudyPlan studyPlan(@RequestParam String subject,
                               @RequestParam(defaultValue = "5") int days) {
        return chatClient.prompt()
                .user(u -> u.text("Create a {days}-day exam revision plan for {subject}. "
                                + "Keep each day under 4 hours.")
                        .param("days", days)
                        .param("subject", subject))
                .call()
                .entity(StudyPlan.class);
    }
}
