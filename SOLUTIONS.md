# Solutions

The finished code for each exercise, so you can compare with yours. Back to [EXERCISES.md](EXERCISES.md).

Your text can differ in small ways (for example your own personality in Exercise 1). What matters is that the app starts and the "What you should see" part of the exercise works.

---

## Exercise 1: Give the AI a personality

File: `src/main/java/com/example/campusai/AiConfig.java`. The `chatClient` method after the change:

```java
// EXERCISE 1 START HERE: change the text inside defaultSystem below, then restart (see EXERCISES.md)
@Bean
ChatClient chatClient(ChatClient.Builder builder) {
    // defaultSystem = the standing instructions the AI follows for every question
    return builder
            .defaultSystem("""
                    You are Campus Assistant. Answer like an excited cricket commentator,
                    in under 60 words.
                    """)
            .build();
}
```

---

## Exercise 2: Add a "difficulty" field

File: `src/main/java/com/example/campusai/StudyPlanController.java`. The two records after the change:

```java
// EXERCISE 2 START HERE: add a difficulty field to the StudyTask record below (see EXERCISES.md)
// One day of the plan
public record StudyTask(String day, String topic, int hours, String resource, String difficulty) {}

// The whole plan: a subject, the total hours, the daily tasks and some tips
public record StudyPlan(String subject, int totalHours, List<StudyTask> tasks, List<String> tips) {}
```

Nothing else in the file changes.

---

## Exercise 3: Write a cancelBooking tool

File: `src/main/java/com/example/campusai/LabBookingTools.java`. The end of the file after the change (the class closes with the last `}`):

```java
    // EXERCISE 3 START HERE: paste your cancelBooking tool below this line (see EXERCISES.md)
    // Tool 3: cancel a booking and make the slot free again
    @Tool(description = "Cancel a lab booking and make the slot free again")
    public String cancelBooking(@ToolParam(description = "Lab name, e.g. AI Lab") String lab,
                                @ToolParam(description = "Time slot in HH:mm") String time) {
        // Tidy up what the AI sent (these two helper methods are already in this file)
        lab = findLab(lab);
        time = toHHmm(time);
        List<String> slots = freeSlots.get(lab);
        if (slots == null) {
            return "Cancel failed: there is no lab called " + lab + ".";
        }
        // Put the time back in the free list
        if (!slots.contains(time)) {
            slots.add(time);
        }
        System.out.println(">>> TOOL EXECUTED: cancelled " + lab + " at " + time);
        return "Cancelled " + lab + " at " + time + ". The slot is free again.";
    }
}
```

---

## Exercise 4: Add a Canteen section to the handbook

File: `src/main/resources/docs/course-handbook.md`. The end of the file after the change:

```markdown
## Internships
Students may take a full-semester internship in their final year if their CGPA is
7.5 or above and they have no pending backlogs. The faculty mentor must approve
the internship offer letter before the student joins.

<!-- EXERCISE 4: add your new section below this line -->
## Canteen
The main canteen is open from 7 AM to 10 PM every day. The night canteen near
the hostels stays open until 2 AM during exam weeks.
```

Remember to restart the app after changing the handbook.
