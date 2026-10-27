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
 * DEMO 3 - Tool calling (letting the AI do tasks).
 * These are plain Java methods the AI is allowed to use. The AI never touches
 * the data itself: it asks for a method to be run, Spring AI runs it, and the
 * result goes back to the AI. Our Java code stays in control.
 */
@Component
public class LabBookingTools {

    // A fake "database" so the demo needs nothing else installed.
    // Each lab maps to the list of times that are still free.
    private final Map<String, List<String>> freeSlots = new ConcurrentHashMap<>(Map.of(
            "AI Lab", new CopyOnWriteArrayList<>(List.of("09:00", "11:00", "14:00", "16:00")),
            "Cloud Lab", new CopyOnWriteArrayList<>(List.of("10:00", "15:00")),
            "IoT Lab", new CopyOnWriteArrayList<>(List.of("13:00"))));

    // The description is what the AI reads to decide when to use this tool
    @Tool(description = "Get today's date. Use it when the user mentions today, tomorrow or a weekday.")
    public String today() {
        return LocalDate.now().toString();
    }

    // Tool 1: show which times are free in a lab
    @Tool(description = "List the free time slots for a lab. Valid labs: AI Lab, Cloud Lab, IoT Lab.")
    public List<String> listFreeSlots(@ToolParam(description = "Lab name, e.g. AI Lab") String lab) {
        return freeSlots.getOrDefault(findLab(lab), List.of());
    }

    // Tool 2: book a free slot and return a confirmation
    @Tool(description = "Book a free slot in a lab for a student. Always call listFreeSlots first to check the slot is free. "
            + "Returns a confirmation or an error message. Only tell the user it is booked if this returns a confirmation.")
    public String bookSlot(@ToolParam(description = "Lab name, e.g. AI Lab") String lab,
                           @ToolParam(description = "Time slot in HH:mm, must be one of the free slots") String time,
                           @ToolParam(description = "Student's registration number") String regNo) {
        // Tidy up what the AI sent (see findLab and toHHmm below)
        lab = findLab(lab);
        time = toHHmm(time);
        List<String> slots = freeSlots.get(lab);
        // Stop if the lab name is not one we know
        if (slots == null) {
            return "Booking failed: there is no lab called " + lab + ". Labs: " + freeSlots.keySet();
        }
        // Remove the slot from the free list. If it was not there, it is taken or does not exist.
        if (!slots.remove(time)) {
            // Telling the AI what IS free lets it fix its own mistake and try again
            return "Booking failed: slot " + time + " in " + lab + " is not available. Free slots in " + lab + ": " + slots;
        }
        // This line shows in the terminal, so we can see the tool really ran
        System.out.println(">>> TOOL EXECUTED: booked " + lab + " at " + time + " for " + regNo);
        return "Booked " + lab + " at " + time + " for " + regNo + ". Confirmation id: LAB-" + Math.abs((lab + time + regNo).hashCode() % 10000);
    }

    // Small AI models are sloppy with what they send, so we tidy it up in Java:
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

    // Same idea for times: "2 PM", "2:00 pm", "14:00" and "1400" all become "14:00".
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

    // EXERCISE 3 START HERE: paste your cancelBooking tool below this line (see EXERCISES.md)
}
