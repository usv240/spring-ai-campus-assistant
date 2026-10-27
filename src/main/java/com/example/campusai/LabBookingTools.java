package com.example.campusai;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return freeSlots.getOrDefault(findLab(lab), List.of());
    }

    @Tool(description = "Book a free slot in a lab for a student. Always call listFreeSlots first to check the slot is free. "
            + "Returns a confirmation or an error message. Only tell the user it is booked if this returns a confirmation.")
    public String bookSlot(@ToolParam(description = "Lab name, e.g. AI Lab") String lab,
                           @ToolParam(description = "Time slot in HH:mm, must be one of the free slots") String time,
                           @ToolParam(description = "Student's registration number") String regNo) {
        lab = findLab(lab);
        time = toHHmm(time);
        List<String> slots = freeSlots.get(lab);
        if (slots == null) {
            return "Booking failed: there is no lab called " + lab + ". Labs: " + freeSlots.keySet();
        }
        if (!slots.remove(time)) {
            // Telling the model what IS free lets it fix its own mistake and try again
            return "Booking failed: slot " + time + " in " + lab + " is not available. Free slots in " + lab + ": " + slots;
        }
        System.out.println(">>> TOOL EXECUTED: booked " + lab + " at " + time + " for " + regNo);
        return "Booked " + lab + " at " + time + " for " + regNo + ". Confirmation id: LAB-" + Math.abs((lab + time + regNo).hashCode() % 10000);
    }

    // Small models are sloppy with arguments, so tidy them up in Java:
    // "cloud", "Cloud-Lab" and "CLOUD LAB" all become "Cloud Lab".
    private String findLab(String lab) {
        String wanted = lab.toLowerCase().replaceAll("[^a-z]", "").replace("lab", "");
        for (String name : freeSlots.keySet()) {
            if (name.toLowerCase().replaceAll("[^a-z]", "").replace("lab", "").equals(wanted)) {
                return name;
            }
        }
        return lab;
    }

    // "2 PM", "2:00 pm", "14:00" and "1400" all become "14:00".
    private String toHHmm(String time) {
        Matcher m = Pattern.compile("(\\d{1,2}):?(\\d{2})?\\s*(am|pm)?").matcher(time.toLowerCase().trim());
        if (!m.matches()) {
            return time;
        }
        int hour = Integer.parseInt(m.group(1));
        int minute = m.group(2) == null ? 0 : Integer.parseInt(m.group(2));
        if ("pm".equals(m.group(3)) && hour < 12) {
            hour += 12;
        }
        if ("am".equals(m.group(3)) && hour == 12) {
            hour = 0;
        }
        return String.format("%02d:%02d", hour, minute);
    }

    // EXERCISE 3: add your cancelBooking tool here (see EXERCISES.md)
}
