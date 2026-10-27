# Session notes: Building AI-Powered Applications with Java and Spring AI

*Guest lecture by Ujwal Vanjare · VIT Vellore, SCORE · 28 October 2026*

These notes follow the session: what AI is, why it matters, where it's used, how it works, what we built, and what to do next. No background needed. The words in **bold** are explained in the glossary at the end.

---

# What

## What is AI?

AI (artificial intelligence) means a computer program that does things we normally expect a human to do, like reading, writing, recognising pictures or answering questions. Instead of following rules that someone typed in one by one, it learns patterns from huge amounts of examples. You use it every day: when your phone suggests the next word, or when YouTube suggests a video.

*Everyday example:* Your phone keyboard suggests "you" after you type "thank". Nobody wrote a rule for that. It learned it from lots of text.

## What is an LLM?

An LLM (large language model) is an AI that works with text. It has read a very large amount of text, and it is good at one job: guessing the next word. It reads your message, guesses the most likely next word, adds it, and repeats until the answer is done. ChatGPT, Gemini and Claude are all LLMs.

*Everyday example:* If I say "Roses are red, violets are...", you say "blue" without thinking. An LLM does the same, just with much more reading behind it.

## What is Spring AI?

Spring is a very popular Java toolkit for building apps. Spring AI is a part of it that lets your Java app talk to an AI model with a few lines of code. You write your code once, and you can switch between models (Ollama, OpenAI, Gemini, Claude) by changing a setting, not your code. The main thing you use is called `ChatClient`.

*Everyday example:* Think of a universal phone charger. Your app plugs into Spring AI, and Spring AI plugs into whichever model you pick.

---

# Why

## Why use AI in your apps?

Most apps are good at storing and showing data, and weak at understanding what a person means. With an LLM, a student can type "when can I book the lab tomorrow afternoon?" in their own words and the app understands. You don't have to build a form with ten dropdowns. It saves time for users and lets small teams build things that used to need a large team.

*Everyday example:* Compare a railway app with ten filters to just asking "cheapest train to Chennai tonight?".

## Why Java?

Python is great for training models. But most companies don't train models, they use them through an API (a way for programs to talk to each other). Many company systems, like banks, railways and online shops, are already written in Java and Spring. So if you know Spring Boot, adding AI to those systems is a short step, not a new career.

*Everyday example:* You don't need to know how to build an engine to drive a car. Most companies are drivers, not engine makers.

---

# Where

## Where is AI used in the real world?

- **Customer support:** chatbots that answer from a company's own help pages.
- **Education:** tutors that explain a topic at your level.
- **Banking and insurance:** reading forms and summarising long documents.
- **Healthcare:** helping doctors write notes faster.
- **Coding:** tools that suggest and explain code.
- **Your college:** a bot that answers questions from the handbook, like the one we built today.

*Everyday example:* When a food app lets you type "something spicy under 200 rupees" and shows results, an LLM is often reading that sentence.

---

# How

## How it picks words

The LLM does not store answers. It builds the answer one small piece at a time. A piece is called a **token**, and it is roughly three quarters of a word. At each step it scores every possible next token and picks one. The setting called **temperature** controls how adventurous it is: low (0.1 to 0.3) for facts and code, higher (0.7 to 1.0) for stories and ideas.

*Everyday example:* Finishing a friend's sentence. A close friend picks the safe ending. A joker picks a surprising one. Temperature is how much of a joker you let it be.

**Writing good prompts.** A **prompt** is the message you send. A vague prompt ("Write about Java streams") gets a vague answer. A clear prompt says who it's talking to, what you want, what format, and what to do when unsure: "You are helping second-year students prepare for a viva. Explain Java streams in 5 bullet points with one 3-line code example. If you're not sure, say so."

## How it remembers (memory)

An LLM forgets everything after each answer. Each message is a fresh start. To make it feel like a conversation, our app re-sends the last few messages along with the new one. We keep a separate conversation for each student, using their registration number. That is why "book the 2 PM one" works in message 2.

*Everyday example:* Calling customer care and having to explain your problem again from the start every time. Memory is the agent reading your earlier notes first.

In our code: `AssistantController.java`.

## How it does tasks (tool calling)

An LLM can only produce text. It cannot book a lab by itself. So we give it **tools**: ordinary Java methods with a short description. When you say "book the AI Lab at 2 PM", the AI replies "please run `bookSlot` with AI Lab and 14:00". Our Java code runs it and sends the result back. The AI never touches the data. Our code stays in charge and can refuse bad requests.

*Everyday example:* A waiter and a kitchen. You tell the waiter your order, the waiter writes it down, the kitchen cooks. The waiter (AI) never cooks, and the kitchen (your Java code) decides what actually gets made.

In our code: `LabBookingTools.java`. Give the AI a goal plus some tools and let it choose the steps, and people call that an "agent".

## How it answers from your documents (RAG)

The AI has never read your college handbook, so it guesses or invents an answer. **RAG** (retrieval augmented generation) fixes that. When the app starts, we cut the handbook into small pieces and store them in a way that can be searched by meaning. When someone asks a question, we find the closest pieces, paste them into the prompt, and ask the AI to answer from them.

*Everyday example:* An open-book exam. The student (AI) doesn't need to memorise the book. We open it to the right page just before they answer.

In our code: `RagController.java` and `AiConfig.java`.

## How to use AI safely

- **API keys (passwords for AI services):** never put them in your code or on GitHub. Use environment variables.
- **Tricky inputs:** users and documents can try to give the AI new instructions. Keep important checks in Java code, not in the prompt.
- **Cost:** you pay for every token. Use the smallest model that does the job.
- **Mistakes:** the AI can sound sure and still be wrong. Test with real examples.
- **Privacy:** know what data you are sending outside your company.
- **Watching it:** turn on logging so you can see every prompt and tool call when someone says "the bot did something weird".

*Everyday example:* A new intern is smart, but you still don't hand them the bank vault keys on day one.

---

# What we built

One small Spring Boot app called **Campus Assistant**. We built it in four steps, and you changed it yourself in the exercises.

| Step | What it does | Try it | Code |
|---|---|---|---|
| Demo 1: Chat | Talks to the AI, one reply or word by word | `/chat`, `/chat/stream`, `/explain` | `ChatController.java` |
| Demo 2: Neat answers | Gets a study plan back as data, not text | `/study-plan` | `StudyPlanController.java` |
| Demo 3: Memory and tools | Remembers you and books lab slots | `/assistant` | `AssistantController.java`, `LabBookingTools.java` |
| Demo 4: Your documents | Answers from the college handbook | `/ask`, `/ask/no-rag` | `RagController.java`, `AiConfig.java` |

| Exercise | What you change |
|---|---|
| 1 | Give the AI a personality |
| 2 | Add a `difficulty` field to the study plan |
| 3 | Write a `cancelBooking` tool |
| 4 | Add a Canteen section to the handbook |

Steps are in [EXERCISES.md](EXERCISES.md) and the finished code is in [SOLUTIONS.md](SOLUTIONS.md). There is also a small bonus: giving the AI a goal and letting it plan the steps.

---

# What to do next

1. **Finish the exercises**, especially 3 and 4, in your own time.
2. **Build one small project end to end** and put it online with a short demo video. Ideas: a bot that answers from your own syllabus, a resume reviewer that gives a score, a quiz maker for placement prep.
3. **Learn Java and Spring Boot well.** AI is one more tool on top of that.
4. **Go to hackathons**, even if you lose. They make you finish things.

## Where to go next

- [Spring AI documentation](https://docs.spring.io/spring-ai/reference/)
- [Spring AI for Beginners](https://github.com/microsoft/Spring-AI-for-Beginners), a free course by Microsoft
- [Official Spring AI examples](https://github.com/spring-projects/spring-ai-examples)
- [Ollama](https://ollama.com), to run models for free on your laptop
- [Model Context Protocol](https://modelcontextprotocol.io), a common "plug" so one tool works with many AI apps

Questions after the session? Find me on [LinkedIn](https://www.linkedin.com/in/ujwalvanjare/).

---

# Words you'll hear

- **AI model:** a program that learned patterns from lots of examples and can now make predictions or write things.
- **LLM (large language model):** an AI model for text that works by guessing the next word, again and again.
- **Prompt:** the message or instructions you send to the AI.
- **Token:** a small piece of a word (about three quarters of a word) that the AI reads and writes.
- **Temperature:** a setting for how safe or adventurous the AI is when it picks words.
- **Spring AI:** a part of Spring that lets Java apps talk to AI models with little code.
- **ChatClient:** the Spring AI object we use to send messages to the AI and get replies.
- **Record:** a small Java class that just holds data, like one day of a study plan.
- **Tool calling:** letting the AI ask our Java code to run a method, such as booking a lab.
- **RAG:** adding the right pieces of your own documents to the prompt so the AI answers from them.
- **Codespaces:** GitHub's coding workspace that runs in your browser, so you install nothing.
