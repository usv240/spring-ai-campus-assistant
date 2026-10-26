# Code-along exercises

Short changes you make during the session, one after each demo. Each takes 2 to 5 minutes.

Stuck or fell behind? That's completely fine. Watch the screen, and come back to these later. Everything is in this repo.

---

## 0. Get ready (at the start of the session)

1. Open this repo and click **Open in Codespaces** in the README. Sign in to GitHub if asked.
2. Leave it running while the intro happens. The first time takes about 5 minutes.
3. When the terminal says **All set**, run:
   ```bash
   mvn spring-boot:run
   ```
4. Open `requests.http` and click **Send Request** above the first line. If you get an answer, you're ready.

**How to restart after a code change:** click in the terminal, press `Ctrl+C`, then run `mvn spring-boot:run` again. It takes about 20 seconds.

---

## 1. After Demo 1: give it a personality

Open `src/main/java/com/example/campusai/AiConfig.java` and find `defaultSystem`. Replace the text inside the quotes with your own, for example:

```java
.defaultSystem("""
        You are Campus Assistant. Answer like an excited cricket commentator,
        in under 60 words.
        """)
```

Restart, then send the first `/chat` request again. Try a few personalities. What happens if you ask it to always answer in Hindi or Tamil?

---

## 2. After Demo 2: add a field

Open `StudyPlanController.java` and add a `difficulty` field to the `StudyTask` record:

```java
public record StudyTask(String day, String topic, int hours, String resource, String difficulty) {}
```

Restart and send the `/study-plan` request. The model now fills in `difficulty` for every task, and you didn’t write any parsing code.

---

## 3. After the prompting part: fix a vague prompt

No restart needed. In `requests.http`, find the **Prompting - vague** request and rewrite the message to say who it’s for, what you want, the format, and what to do when unsure. Send it and compare with the vague one.

---

## 4. After Demo 3: write your own tool

Open `LabBookingTools.java`. Find the line `// EXERCISE 4` and paste this under it:

```java
@Tool(description = "Cancel a lab booking and free the slot again")
public String cancelBooking(@ToolParam(description = "Lab name, e.g. AI Lab") String lab,
                            @ToolParam(description = "Time slot in HH:mm") String time) {
    List<String> slots = freeSlots.get(lab);
    if (slots == null) {
        return "There is no lab called " + lab + ".";
    }
    if (!slots.contains(time)) {
        slots.add(time);
    }
    System.out.println(">>> TOOL EXECUTED: cancelled " + lab + " at " + time);
    return "Cancelled " + lab + " at " + time + ". The slot is free again.";
}
```

Restart. Book a slot with the two Demo 3 requests, then send:

```
GET http://localhost:8080/assistant?user=21BCE1234&message=Actually, cancel that booking.
```

Watch the terminal for `TOOL EXECUTED: cancelled`. You just gave the AI a new ability with one Java method.

---

## 5. After Demo 4: teach it something new

Open `src/main/resources/docs/course-handbook.md` and add this at the end (or write your own section):

```markdown
## Canteen
The main canteen is open from 7 AM to 10 PM every day. The night canteen near
the hostels stays open until 2 AM during exam weeks.
```

Restart, then ask:

```
GET http://localhost:8080/ask?question=Until when is the canteen open during exams?
```

It answers from your new section. Now try `/ask/no-rag` with the same question and compare.

---

## 6. After Demo 5: give it your own goal

Send a goal of your own to the assistant, for example:

```
GET http://localhost:8080/assistant?user=22BIT0042&message=Find me any free slot in the Cloud Lab after lunch and book it. My reg no is 22BIT0042.
```

Did it check the slots before booking? If you did exercise 4, try: “Book the AI Lab at 4 PM, then cancel it and book 11 AM instead.”

---

## When you’re done

- **Stop your Codespace** (github.com/codespaces → ⋯ → Stop) so you don’t use up your free hours. Your changes stay saved.
- Want to keep your changes? In the Source Control panel, commit them and choose to publish to your own GitHub repo.
- Try one of the bigger practice ideas in the README.
