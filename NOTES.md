# Session notes: Building AI-Powered Applications with Java and Spring AI

*Guest lecture by Ujwal Vanjare · VIT Vellore, SCORE · 28 October 2026*

These are the notes from the session, in the order we covered things. The code for every demo is in this repo.

---

## 1. Why Java for AI?

Python is great for training models, but most companies don't train models. They use them through an API, from apps that are very often written in Java: banks, railways, online shops. So if you know Spring Boot, you already know most of what you need.

## 2. What an LLM actually does

Think of it as a really good autocomplete. It reads your text, guesses the most likely next word, adds it, and repeats. That's all it does. It just does it so well that it can write code and explain things.

A few words you'll keep hearing:

- **Prompt:** what you send to the model.
- **Token:** a small piece of a word, roughly three quarters of a word in English. You pay for every token in and out.
- **Context window:** how much text the model can read in one go.
- **Temperature:** how adventurous it is when it picks the next word. Low (0.1 to 0.3) for facts, code and JSON. Higher (0.7 to 1.0) for stories and ideas.

Try the next-word game and the token counter on the playground page.

## 3. What it can't do on its own

| The problem | What it looks like | Fixed in |
|---|---|---|
| It forgets | You tell it your name, next message it's gone | Demo 3 |
| It can't do things | It can explain how to book a lab, but can't book one | Demo 3 |
| It doesn't know your data | It has never read your college handbook | Demo 4 |
| It makes things up | Sounds very sure, still wrong | Demo 4 |
| It only gives text | Hard to use the answer in your code | Demo 2 |

## 4. Where Spring AI fits

If you've used JDBC, you already get the idea. Your code talks to one interface, `ChatClient`, and Spring AI talks to whichever model you choose: Ollama, OpenAI, Gemini, Claude and others. To switch models you change a dependency and the config, not your code.

**Advisors** wrap each call, like servlet filters. Memory and RAG are both advisors.

## 5. Demo 1: talking to the model

```java
String reply = chatClient.prompt()
        .user(message)
        .call()
        .content();
```

- `.system("...")` sets who the model is talking to and how it should behave. Same model, very different answers.
- `.stream()` instead of `.call()` sends the reply word by word, like ChatGPT does.

## 6. Demo 2: getting Java objects back

Text is nice to read but hard to use in code. So we ask for a record:

```java
StudyPlan plan = chatClient.prompt()
        .user("Make a 5-day revision plan for Data Structures")
        .call()
        .entity(StudyPlan.class);
```

Spring AI tells the model the shape of the record and reads the reply back into Java for you.

## 7. Writing good prompts

Talk to it like a new teammate, not a search box. A vague prompt like "Write about Java streams" gets a vague answer. A clear one says four things:

- **Who** it's talking to: "You're helping second-year students prepare for a viva."
- **What** you want, exactly: "Explain Java streams."
- **Format** of the answer: "In 5 short bullet points, with one 3-line code example."
- **What to do when unsure:** "If you're not sure about something, say so."

Showing one example of the output you want usually works better than describing it.

## 8. Demo 3: memory

The model forgets everything between messages. It's like calling customer care and explaining your problem from the start every single time. The memory advisor keeps the last few messages for each user (by a conversation id) and sends them along with every new question.

## 9. Demo 3: tool calling, the waiter and the kitchen

1. You place an order: "Book the AI Lab at 2 PM."
2. The waiter (the model) writes it down: `bookSlot("AI Lab", "14:00", ...)`.
3. The kitchen (your Java code) actually does the work.
4. The waiter brings back the result: "Booked!"

The model never goes into the kitchen. It only asks. Your code decides what really happens, so it can check permissions and refuse bad requests. A tool is just a Java method with `@Tool` and a description.

This is the idea that turns a chatbot into an agent.

## 10. Demo 4: RAG, an open-book exam for the AI

Instead of hoping the model already knows the answer, we hand it the right page just before it answers.

**Once, when the app starts:** read the handbook, cut it into small pieces, turn each piece into numbers that capture its meaning (embeddings), and store them in a vector store.

**Every time someone asks:** find the pieces closest in meaning to the question, paste them into the prompt, and let the model answer from them.

In Spring AI that's one advisor: `QuestionAnswerAdvisor`.

Why not paste the whole handbook into every prompt? Real documents are huge, and you pay per token.

## 11. Demo 5: a small agent

Same code as Demo 3, just a bigger ask. Instead of telling it each step, give it a goal: "Book me the earliest free slot from 1 PM onwards, in any lab." The model decides by itself to check each lab's free slots, compare them, and then book one. A goal, some tools and a loop: that's an agent. Spring AI also limits how many tool calls one request can make, so it can't loop forever.

## 12. MCP: one plug for AI tools

Remember when every phone had a different charger, and then USB-C came along? MCP (Model Context Protocol) is that for AI tools. You write a tool once, and Claude Desktop, coding assistants and your own apps can all use it. In Spring AI you mark a method with `@McpTool`.

## 13. Before anything goes live

- **API keys:** never in the code or on GitHub. Use environment variables.
- **Tricky inputs:** users and documents can try to give the AI new instructions. Keep important checks in Java code.
- **Cost:** every token costs money. Use the smallest model that works.
- **Testing:** the same question can give different answers. Test with real examples.
- **Privacy:** know what data you're sending outside your company.
- **Watching it:** Spring AI can log every prompt and tool call, and send metrics you can chart. That's what you check when someone says "the bot did something weird".

## 14. If I were in your place

- Get really good at Java and Spring Boot first.
- Build one AI project end to end and put it online with a short demo video.
- Go to hackathons, even if you lose. They make you finish things.

Project ideas: a bot that answers from your own syllabus, a resume reviewer that gives a score, a quiz maker for placement prep, an expense tracker that reads receipts.

---

## Where to go next

- [Spring AI documentation](https://docs.spring.io/spring-ai/reference/)
- [Spring AI for Beginners](https://github.com/microsoft/Spring-AI-for-Beginners), a free course by Microsoft
- [Official Spring AI examples](https://github.com/spring-projects/spring-ai-examples)
- [Ollama](https://ollama.com), to run models for free on your laptop
- [Model Context Protocol](https://modelcontextprotocol.io)

Questions after the session? Find me on [LinkedIn](https://www.linkedin.com/in/ujwalvanjare/).
