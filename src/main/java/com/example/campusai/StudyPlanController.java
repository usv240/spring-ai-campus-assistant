package com.example.campusai;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * DEMO 2 - Structured output.
 * Instead of plain text, the AI gives back neat data that Spring AI fills into
 * Java records (small classes that just hold data). Your code then works with
 * real fields like plan.subject(), not with a block of text.
 */
@RestController
public class StudyPlanController {

    // EXERCISE 2 START HERE: add a difficulty field to the StudyTask record below (see EXERCISES.md)
    // One day of the plan
    public record StudyTask(String day, String topic, int hours, String resource) {}

    // The whole plan: a subject, the total hours, the daily tasks and some tips
    public record StudyPlan(String subject, int totalHours, List<StudyTask> tasks, List<String> tips) {}

    private final ChatClient chatClient;

    public StudyPlanController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // Ask the AI for a revision plan and get it back as a StudyPlan object.
    // Try: GET /study-plan?subject=Data Structures&days=5
    @GetMapping("/study-plan")
    public StudyPlan studyPlan(@RequestParam String subject,
                               @RequestParam(defaultValue = "5") int days) {
        return chatClient.prompt()
                // Own instructions for this endpoint, so a fun personality from Exercise 1 cannot break the data
                .system("You are Campus Assistant, a helpful assistant for university students.")
                // {days} and {subject} are blanks filled in from the request
                .user(u -> u.text("Create a {days}-day exam revision plan for {subject}. "
                                + "Keep each day under 4 hours. Fill in every field, including totalHours.")
                        .param("days", days)
                        .param("subject", subject))
                .call()
                // entity = turn the AI's reply into a StudyPlan object
                .entity(StudyPlan.class);
    }
}
