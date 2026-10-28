# Exercises

Four small changes to the app. You don't need to know much Java. Every step says exactly what to click and what to paste.

**Fell behind or stuck?** That's fine. Watch the screen and come back to this later. Every exercise has a "Stuck?" tip, and the finished code is in [SOLUTIONS.md](SOLUTIONS.md).

- **Before you start:** get the app running (do this in the break)
- **Part A: Do these in the session:** Exercise 1 and Exercise 2
- **Part B: Try these on your own:** Exercise 3 and Exercise 4
- **Part C: Extra practice (easy):** Exercises 5, 6 and 7
- **Part D: Challenges (a little harder):** Exercises 8, 9 and 10
- **Bonus:** give the AI a goal

---

## Before you start

Do this in the break. It takes about 5 minutes the first time.

1. Open the repo's README and click the **Open in Codespaces** button. Sign in to GitHub if it asks. (A Codespace is a coding workspace that runs in your browser, so you install nothing.)
2. Wait until the terminal at the bottom says **All set**. This is when it downloads the AI model.
3. In the terminal, type this and press Enter:
   ```bash
   mvn spring-boot:run
   ```
   Wait until you see a line saying `Started CampusAiApplication`.
4. Open the file `requests.http` (in the file list on the left). Click **Send Request** just above the first `GET` line. **Click once only.** The bottom bar says "Waiting..." while the AI works.
5. **The very first answer takes about 3 minutes**, because the AI is loading. That is normal. Don't click again. The answer opens in a new tab on the right. After that first one, answers take 5 to 30 seconds.

**How to restart after a change:** click in the terminal, press `Ctrl+C`, press the Up arrow, press Enter. It takes about 20 seconds.

---

# Part A: Do these in the session

## Exercise 1: Give the AI a personality

**Goal:** Change how the AI talks by editing its standing instructions (the "system prompt").

**Time:** 5 minutes

**Where:** `src/main/java/com/example/campusai/AiConfig.java`. Look for the comment `// EXERCISE 1 START HERE`. The text to change is just below it, inside `defaultSystem`.

**Steps:**
1. Open `AiConfig.java`.
2. Find the line `// EXERCISE 1 START HERE`.
3. Look a few lines below for `.defaultSystem("""`. It runs until the line that ends with `""")`. The text inside is the instructions.
4. Select everything from `.defaultSystem("""` down to `""")` (both lines included) and replace it with this:
   ```java
   .defaultSystem("""
           You are Campus Assistant. Answer like an excited cricket commentator,
           in under 60 words.
           """)
   ```
5. Save the file (`Ctrl+S`).
6. Restart the app (click the terminal, `Ctrl+C`, Up arrow, Enter).

**What you should see:** In `requests.http`, click **Send Request** on the request under "Exercise 1: check your personality". It is this one:
```
GET http://localhost:8080/chat?message=Explain dependency injection in 3 lines
```
The answer should sound like a cricket commentator ("And here comes a brilliant delivery..."), and it should be short.

**Why this matters:** The same AI can act like a tutor, a commentator or a support agent. You choose that with a few lines of text.

**Stuck?** The most common mistake is deleting a `"""` or a `)`. If the app won't start, read the red error in the terminal. It names the line. Compare with [SOLUTIONS.md](SOLUTIONS.md). Once it works, try another personality: a pirate, or "answer only in Hindi".

Need the answer? See [SOLUTIONS.md](SOLUTIONS.md).

---

## Exercise 2: Add a "difficulty" field

**Goal:** Add one new field to a Java record and watch the AI fill it in without any other code.

**Time:** 5 minutes

**Where:** `src/main/java/com/example/campusai/StudyPlanController.java`. Look for the comment `// EXERCISE 2 START HERE`. The `StudyTask` record is just below it. (A record is a small Java class that just holds data.)

**Steps:**
1. Open `StudyPlanController.java`.
2. Find the line `// EXERCISE 2 START HERE`.
3. Just below it, find this line:
   ```java
   public record StudyTask(String day, String topic, int hours, String resource) {}
   ```
4. Replace that whole line with this (you are adding `, String difficulty`):
   ```java
   public record StudyTask(String day, String topic, int hours, String resource, String difficulty) {}
   ```
5. Save the file (`Ctrl+S`).
6. Restart the app (click the terminal, `Ctrl+C`, Up arrow, Enter).

**What you should see:** In `requests.http`, send the request under "Exercise 2: check the difficulty field":
```
GET http://localhost:8080/study-plan?subject=Data Structures&days=5
```
The answer is JSON (neat data). Every item in `tasks` now has a `"difficulty"`, like `"Easy"` or `"Medium"`. (Small AI models sometimes slip and you get an error page instead. If that happens, click **Send Request** again.)

**Why this matters:** You described the shape of the answer you want, and the AI followed it. You wrote no code to read the reply.

**Stuck?** The most common mistake is forgetting the comma before `String difficulty`, or the `{}` at the end. If `difficulty` is missing or `null` for some tasks, send the request again. Small AI models sometimes slip. If you get an error, send it again.

Need the answer? See [SOLUTIONS.md](SOLUTIONS.md).

---

# Part B: Try these on your own

## Exercise 3: Write a cancelBooking tool

**Goal:** Give the AI a new ability by writing one Java method (a "tool").

**Time:** 10 minutes

**Where:** `src/main/java/com/example/campusai/LabBookingTools.java`. Look for the comment `// EXERCISE 3 START HERE` near the bottom of the file.

**Steps:**
1. Open `LabBookingTools.java` and scroll to the bottom.
2. Find the line `// EXERCISE 3 START HERE`.
3. On a new line just below it, paste this:
   ```java
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
   ```
4. Make sure the pasted code sits **above** the final `}` that closes the class.
5. Save the file (`Ctrl+S`) and restart the app.
6. In `requests.http`, send the two **Demo 3** requests ("message 1", then "message 2") so that 14:00 in the AI Lab gets booked.

**What you should see:** Send the request under "Exercise 3: cancel the booking":
```
GET http://localhost:8080/assistant?user=21BCE1234&message=Actually, cancel that booking.
```
Look at the **terminal**. You should see a line `>>> TOOL EXECUTED: cancelled AI Lab at 14:00`. The chat reply should say it was cancelled.

**Why this matters:** A "tool" is just a Java method with a description. The AI reads the description and decides when to use it.

**Stuck?** The most common mistake is pasting the method outside the class (after the last `}`). Paste it above that last `}`. If the AI says it cancelled but the terminal shows no `TOOL EXECUTED` line, it did not really do it. Small models sometimes claim things. Trust the terminal and try again.

Need the answer? See [SOLUTIONS.md](SOLUTIONS.md).

---

## Exercise 4: Add a Canteen section to the handbook

**Goal:** Teach the AI something new by adding a paragraph to the handbook it reads.

**Time:** 5 minutes

**Where:** `src/main/resources/docs/course-handbook.md`. Look for the last line: `<!-- EXERCISE 4: add your new section below this line -->`.

**Steps:**
1. Open `course-handbook.md` and scroll to the bottom.
2. Find the line `<!-- EXERCISE 4: add your new section below this line -->`.
3. Press Enter to make a new line below it, then paste this:
   ```markdown
   ## Canteen
   The main canteen is open from 7 AM to 10 PM every day. The night canteen near
   the hostels stays open until 2 AM during exam weeks.
   ```
4. Save the file (`Ctrl+S`).
5. Restart the app. (The handbook is read only when the app starts, so you must restart.)

**What you should see:** Send the first request under "Exercise 4" in `requests.http`:
```
GET http://localhost:8080/ask?question=Until when is the canteen open during exams?
```
The answer should say **2 AM** (the night canteen). Now send the second request, `/ask/no-rag`, with the same question. It cannot know, so it guesses or says it doesn't know.

**Why this matters:** You taught the AI a new fact without training it. You just gave it a page to read.

**Stuck?** The most common mistake is not restarting, so the app still has the old handbook. Restart and ask again. Also check the section is below the `<!-- EXERCISE 4 ... -->` line and saved.

Need the answer? See [SOLUTIONS.md](SOLUTIONS.md).

---

# Part C: Extra practice (easy)

Finished Exercises 1 to 4? These three are quick and need almost no Java. Do them in any order.

## Exercise 5: Change the temperature

**Goal:** See how the "temperature" setting makes the AI safer or more adventurous.

**Time:** 5 minutes

**Where:** `src/main/resources/application.yml`, the line `temperature: 0.3`.

**Steps:**
1. Open `application.yml` and find `temperature: 0.3` (under `chat:`). Temperature is a number that controls how many chances the AI takes when it picks the next word. Low is safe, high is creative.
2. Change it to `temperature: 0.1`. Save the file (`Ctrl+S`) and restart the app.
3. In `requests.http`, send the request under "Extra 5: temperature test" **three times** and note the three answers.
4. Change it to `temperature: 1.2`. Save, restart, and send the same request three more times.

**What you should see:** The request is:
```
GET http://localhost:8080/chat?message=Write a one-line slogan for our college canteen.
```
At 0.1 the three slogans are the same, or almost the same. At 1.2 they are clearly different from each other.

**Why this matters:** Use a low temperature for facts and code, and a high one for ideas and stories.

**Stuck?** The most common mistake is forgetting to restart. The setting is read when the app starts. Change it back to `0.3` when you are done.

Need the answer? See [SOLUTIONS.md](SOLUTIONS.md).

---

## Exercise 6: Add a new lab

**Goal:** Give the assistant a fourth lab, using only a few small edits.

**Time:** 8 minutes

**Where:** `LabBookingTools.java` (two places) and `AssistantController.java` (one place).

**Steps:**
1. Open `LabBookingTools.java`. Find the line that ends with `new CopyOnWriteArrayList<>(List.of("13:00"))));` (the IoT Lab).
2. Replace that line with these two lines (note the comma after `"13:00"))` and the new lab):
   ```java
           "IoT Lab", new CopyOnWriteArrayList<>(List.of("13:00")),
           "Robotics Lab", new CopyOnWriteArrayList<>(List.of("12:00", "17:00"))));
   ```
3. In the same file, find `Valid labs: AI Lab, Cloud Lab, IoT Lab.` and change it to `Valid labs: AI Lab, Cloud Lab, IoT Lab, Robotics Lab.`
4. Open `AssistantController.java`. Find `The labs are AI Lab, Cloud Lab and IoT Lab.` and change it to `The labs are AI Lab, Cloud Lab, IoT Lab and Robotics Lab.`
5. Save both files and restart the app.

**What you should see:** In `requests.http`, send the two requests under "Extra 6: new lab". The first asks which Robotics Lab slots are free. The answer lists `12:00` and `17:00`. The second books one, and the terminal shows `>>> TOOL EXECUTED: booked Robotics Lab at 12:00`.

**Why this matters:** The AI reads the tool descriptions and instructions you write. If you forget to update them, it keeps saying the lab doesn't exist.

**Stuck?** The most common mistake is a missing comma at the end of the IoT Lab line, or forgetting step 3 or 4. The AI may then say "there is no Robotics Lab".

Need the answer? See [SOLUTIONS.md](SOLUTIONS.md).

---

## Exercise 7: Watch the AI choose a tool

**Goal:** Turn on detailed logging, so you can see the AI ask for a tool and your Java code run it.

**Time:** 5 minutes

**Where:** `src/main/resources/application.yml`, at the very bottom.

**Steps:**
1. Open `application.yml` and find the line `org.springframework.ai: INFO`.
2. Change `INFO` to `DEBUG`. Save and restart the app.
3. In `requests.http`, send the two **Demo 3** requests ("message 1", then "message 2").
4. Look at the terminal. Many more lines appear now. Click in the terminal and press `Ctrl+F` to search for `Executing tool call`.

**What you should see:** Lines like `Executing tool call: bookSlot`, then `Successful execution of tool: bookSlot`, and then our own line `>>> TOOL EXECUTED: booked AI Lab at 14:00`. The first two show the AI asking for a tool. The last one shows your Java code doing the work.

**Why this matters:** When an AI app does something odd, the log shows which tool the AI chose, and when.

**Stuck?** The terminal moves fast, so use `Ctrl+F` to search. If you see no `Executing tool call` line, the AI did not use a tool. Send the request again. Set it back to `INFO` when you are done, or the terminal stays noisy.

Need the answer? See [SOLUTIONS.md](SOLUTIONS.md).

---

# Part D: Challenges (a little harder)

Each one adds something new to the app. Take your time. If you get stuck, [SOLUTIONS.md](SOLUTIONS.md) has the finished code.

## Exercise 8: Make a quiz maker

**Goal:** Create a new endpoint that returns a list of quiz questions as neat data.

**Time:** 10 minutes

**Where:** A new file in `src/main/java/com/example/campusai/`.

**Steps:**
1. In the file list on the left, right-click the `campusai` folder and choose **New File**. Name it `QuizController.java`.
2. Paste this whole thing into the new file:
   ```java
   package com.example.campusai;

   import java.util.List;

   import org.springframework.ai.chat.client.ChatClient;
   import org.springframework.core.ParameterizedTypeReference;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RequestParam;
   import org.springframework.web.bind.annotation.RestController;

   // Makes multiple-choice quizzes. The AI's reply is turned into a list of QuizQuestion objects.
   @RestController
   public class QuizController {

       // One question: the text, four options and the right answer
       public record QuizQuestion(String question, List<String> options, String answer) {}

       private final ChatClient chatClient;

       public QuizController(ChatClient chatClient) {
           this.chatClient = chatClient;
       }

       // Try: GET /quiz?topic=Java streams&count=3
       @GetMapping("/quiz")
       public List<QuizQuestion> quiz(@RequestParam String topic,
                                      @RequestParam(defaultValue = "3") int count) {
           return chatClient.prompt()
                   .system("You are Campus Assistant, a helpful assistant for university students.")
                   .user(u -> u.text("Write {count} multiple-choice questions about {topic}. "
                                   + "Each question has exactly 4 options. Fill in every field.")
                           .param("count", count)
                           .param("topic", topic))
                   .call()
                   // A list of QuizQuestion, not just one object
                   .entity(new ParameterizedTypeReference<List<QuizQuestion>>() {});
       }
   }
   ```
3. Save the file and restart the app.

**What you should see:** Send the request under "Extra 8: quiz maker":
```
GET http://localhost:8080/quiz?topic=Java streams&count=3
```
The answer is a list of 3 items. Each has a `question`, four `options`, and an `answer`.

**Why this matters:** The same trick as Exercise 2, but for a whole list. This is how you build apps that need many items, like quizzes, flashcards or to-do lists.

**Stuck?** If you get an error page, send the request again. Small AI models sometimes slip. If the app won't start, check that all the `import` lines are there, and that the file name is exactly `QuizController.java`.

Need the answer? See [SOLUTIONS.md](SOLUTIONS.md).

---

## Exercise 9: Give the AI a second document

**Goal:** Let the assistant answer from two documents, not one.

**Time:** 10 minutes

**Where:** A new file in `src/main/resources/docs/`, and the `ingestHandbook` method in `AiConfig.java`.

**Steps:**
1. In the file list, right-click the `docs` folder (inside `src/main/resources`) and choose **New File**. Name it `hostel-rules.md`.
2. Paste this into it and save:
   ```markdown
   # Hostel rules

   ## Gates and visitors
   Hostel gates close at 10 PM for first-year students and at 11 PM for everyone
   else. Late entry needs written permission from the warden. Visitors are allowed
   only in the visitor lounge, between 4 PM and 6 PM on weekends.
   ```
3. Open `AiConfig.java`. At the top, add this line with the other `import java.util...` lines:
   ```java
   import java.util.ArrayList;
   ```
4. In `AiConfig.java`, replace the whole `ingestHandbook` method (from `@Bean` just above it, down to its closing `}`) with this:
   ```java
   @Bean
   ApplicationRunner ingestHandbook(VectorStore vectorStore,
                                    @Value("classpath*:docs/*.md") Resource[] files) {
       return args -> {
           // Read every .md file in the docs folder, not just one
           List<Document> documents = new ArrayList<>();
           for (Resource file : files) {
               documents.addAll(new TextReader(file).get());
           }
           // Cut it into small pieces (about 100 tokens each) so we can fetch just the relevant ones
           List<Document> chunks = TokenTextSplitter.builder().withChunkSize(100).build().apply(documents);
           // Store the pieces in the vector store
           vectorStore.add(chunks);
           System.out.println("Ingested " + chunks.size() + " handbook chunks into the vector store");
       };
   }
   ```
5. Save and restart. In the terminal, the line `Ingested ... handbook chunks` should show a bigger number than before.

**What you should see:** Send the request under "Extra 9: second document":
```
GET http://localhost:8080/ask?question=What time do hostel gates close for first-year students?
```
The answer says **10 PM**, and it came from the new file. A handbook question, like the attendance one, still works.

**Why this matters:** Real apps read many documents. Now you can drop any `.md` file into the folder and the AI knows it after a restart.

**Stuck?** The most common mistake is not replacing the whole method, so there are two methods with the same name. Delete the old one. Also check the new file is in `docs`, and ends with `.md`.

Need the answer? See [SOLUTIONS.md](SOLUTIONS.md).

---

## Exercise 10: Let Java say no

**Goal:** Add a check in Java that refuses a booking when the registration number looks wrong, even if the AI wants to book it.

**Time:** 10 minutes

**Where:** `LabBookingTools.java`, inside the `bookSlot` method.

**Steps:**
1. Open `LabBookingTools.java` and find the method `bookSlot`.
2. Find the line `time = toHHmm(time);` inside it.
3. Paste this just **below** that line:
   ```java
           // Java stays in charge: refuse anything that does not look like 21BCE1234
           if (!regNo.toUpperCase().matches("\\d{2}[A-Z]{3}\\d{4}")) {
               return "Booking failed: " + regNo + " is not a valid registration number. It looks like 21BCE1234.";
           }
   ```
4. Save and restart the app.

**What you should see:** Send the two requests under "Extra 10: let Java say no". The first uses the registration number `hello`:
```
GET http://localhost:8080/assistant?user=22BIT0042&message=Book the AI Lab at 4 PM. My reg no is hello.
```
The reply says the booking did not go through (the exact words vary), and the terminal does **not** show `TOOL EXECUTED`. Java refused it. The second request uses `22BIT0042`, which is valid, so it books, and the terminal shows `TOOL EXECUTED: booked AI Lab at 16:00`.

**Why this matters:** Important rules belong in your Java code, not in the instructions to the AI. The AI can be tricked or can make mistakes. Your code can't.

**Stuck?** The most common mistake is pasting the check above `time = toHHmm(time);` or outside the method. Put it just below. Small AI models sometimes skip the tool on the first try, so if nothing happens, send the request again. If the second request says the slot is not available, the slot was already booked earlier. Restart the app to reset all slots.

**Stretch:** Allow each student at most 2 bookings. Keep a count per registration number in a `Map<String, Integer>`, and refuse the third one.

Need the answer? See [SOLUTIONS.md](SOLUTIONS.md).

---

# Bonus: Give the AI a goal

Not part of the live session. Try it on your own time.

Instead of telling the AI each step, give it one goal and let it work out the steps (this is what people call an "agent"). In `requests.http`, send the request under "Bonus: give the AI a goal":

```
GET http://localhost:8080/assistant?user=21BCE1234&message=Find me a free slot in the Cloud Lab after lunch and book it. My reg no is 21BCE1234.
```

Now look at the terminal, not the chat reply. `TOOL EXECUTED` shows what really happened. Small models sometimes say "booked" when nothing was booked, so trust the terminal.

If you did Exercise 3, try a goal with several steps:
```
GET http://localhost:8080/assistant?user=22BIT0042&message=My reg no is 22BIT0042. Book the AI Lab at 4 PM, then cancel it and book 11 AM instead.
```

---

## When you're done

- **Stop your Codespace** (github.com/codespaces, click the three dots, then Stop) so you don't use up your free hours. Your changes stay saved.
- Want to keep your work? Open the Source Control panel, commit, and publish to your own GitHub.
- Read [NOTES.md](NOTES.md) for the ideas behind everything you just did.
