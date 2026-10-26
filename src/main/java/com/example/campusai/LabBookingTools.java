package com.example.campusai;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * DEMO 3 - Tool calling.
 * Plain Java methods the model is allowed to call. The model never touches
 * this data directly: it asks for a tool call, Spring AI runs the method,
 * and the result goes back to the model.
 */
@Component
public class LabBookingTools {

    // Fake "database" so the demo has no external dependencies
    private final Map<String, List<String>> freeSlots = new ConcurrentHashMap<>(Map.of(
            "AI Lab", new CopyOnWriteArrayList<>(List.of("09:00", "11:00", "14:00", "16:00")),
            "Cloud Lab", new CopyOnWriteArrayList<>(List.of("10:00", "15:00")),
            "IoT Lab", new CopyOnWriteArrayList<>(List.of("13:00"))));

    @Tool(description = "Get today's date. Use it when the user mentions today, tomorrow or a weekday.")
    public String today() {
        return LocalDate.now().toString();
    }

    @Tool(description = "List the free time slots for a lab. Valid labs: AI Lab, Cloud Lab, IoT Lab.")
    public List<String> listFreeSlots(@ToolParam(description = "Lab name, e.g. AI Lab") String lab) {
        return freeSlots.getOrDefault(lab, List.of());
    }

    @Tool(description = "Book a free slot in a lab for a student. Returns a confirmation or an error message.")
    public String bookSlot(@ToolParam(description = "Lab name, e.g. AI Lab") String lab,
                           @ToolParam(description = "Time slot in HH:mm, must be one of the free slots") String time,
                           @ToolParam(description = "Student's registration number") String regNo) {
        List<String> slots = freeSlots.get(lab);
        if (slots == null || !slots.remove(time)) {
            return "Slot " + time + " in " + lab + " is not available.";
        }
        System.out.println(">>> TOOL EXECUTED: booked " + lab + " at " + time + " for " + regNo);
        return "Booked " + lab + " at " + time + " for " + regNo + ". Confirmation id: LAB-" + Math.abs((lab + time + regNo).hashCode() % 10000);
    }

    // EXERCISE 4: add your cancelBooking tool here (see EXERCISES.md)
}
