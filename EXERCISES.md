# Exercises

Four small changes to the app. You don't need to know much Java. Every step says exactly what to click and what to paste.

**Fell behind or stuck?** That's fine. Watch the screen and come back to this later. Every exercise has a "Stuck?" tip, and the finished code is in [SOLUTIONS.md](SOLUTIONS.md).

- **Before you start:** get the app running (do this in the break)
- **Part A: Do these in the session:** Exercise 1 and Exercise 2
- **Part B: Try these on your own:** Exercise 3 and Exercise 4
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
4. Open the file `requests.http` (in the file list on the left). Click **Send Request** just above the first `GET` line. If an answer appears on the right, you're ready.

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
