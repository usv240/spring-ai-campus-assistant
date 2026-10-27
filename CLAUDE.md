# Campus Assistant: guest lecture demo

Demo app for Ujwal's guest lecture "Building AI-Powered Applications with Java and Spring AI" at VIT Vellore on 28 October 2026, 6:00 to 8:00 PM IST (8:30 to 10:30 AM US Eastern). Students watch it live and run it later in GitHub Codespaces.

Stack: Java 21, Spring Boot 4.1.2, Spring AI 2.0.2, Ollama (`llama3.2` for chat, `nomic-embed-text` for embeddings). No API keys.

## Important context

- This code was written against the Spring AI 2.0.2 source but has **never been compiled or run**. Expect possible compile errors or small API mismatches. Fix them with the smallest change that keeps each demo's behaviour, and check the fix against the Spring AI 2.0 reference docs: https://docs.spring.io/spring-ai/reference/
- Keep the code simple and readable. It is shown on screen to students, so do not add abstractions, extra layers or clever tricks. Keep the existing comments.
- The slides quote some of this code. If you change a method name, a class name or what an endpoint does, list the change at the end so the slides can be updated.

## Demos and what "working" means

Run each request in `requests.http` with the app running.

| Demo | Request | Working means |
|---|---|---|
| 1 | `/chat`, `/chat/stream`, `/explain` | Text comes back; `/chat/stream` arrives in pieces; `/explain` changes with `level` |
| 2 | `/study-plan` | JSON matching the `StudyPlan` record (retry once if a small model returns bad JSON) |
| Prompting (short talk, no exercise) | the two `/chat` prompt requests | Both answer; the clear one follows the format asked |
| 3 | `/assistant`, two turns, same `user` | Turn 2 books 14:00 in AI Lab and the console prints `>>> TOOL EXECUTED` |
| 4 | `/ask/no-rag`, `/ask` | `/ask` mentions 82% from the handbook; startup log shows `Ingested N handbook chunks` |
| Bonus (not in the live session) | `/assistant` with the "free slot in the Cloud Lab after lunch" goal (also on port 8081 with the `agent` profile) | `listFreeSlots`, then `bookSlot` in the DEBUG log, and `>>> TOOL EXECUTED` in the console. `llama3.2` is unreliable here; `qwen2.5:7b` (`mvn spring-boot:run -Dspring-boot.run.profiles=agent`, needs `ollama pull qwen2.5:7b`) is better but not perfect |

For demos 4 and 5, temporarily set `logging.level.org.springframework.ai: DEBUG` to see prompts and tool calls, then set it back to INFO.

Small local models are sometimes unreliable with tools and JSON. If a demo fails only because of the model's answer (not an exception), note it rather than rewriting the code.

## Code-along exercises

`EXERCISES.md` has four small changes: Part A (Exercises 1 and 2) is done live in Codespaces, Part B (3 and 4) is for later, plus a bonus goal. `SOLUTIONS.md` has the finished code. Code carries markers `// EXERCISE n START HERE` (and an HTML comment at the end of `course-handbook.md`); do not remove or rename them. After the demos work, apply each exercise on a scratch copy or branch exactly as written in `EXERCISES.md`, check it works, then undo it, so `main` stays at the starting point students begin from. Report any exercise that fails.

The audience may have little CS background: keep code comments plain English and explain any technical word the first time it appears in the docs.

## Local setup

1. Java 21+ and Maven 3.9+ (check `java -version`, `mvn -v`).
2. Ollama from https://ollama.com/download, then `ollama pull llama3.2` and `ollama pull nomic-embed-text`.
3. `mvn spring-boot:run`, app on port 8080.

Ask before installing anything system-wide.

## Publishing to GitHub (only when asked)

Always ask before creating a repo, pushing, or changing GitHub settings.

1. Create a **public** repo named `spring-ai-campus-assistant` (with `gh repo create` if the GitHub CLI is installed and signed in).
2. Replace `YOUR-USERNAME` with the real GitHub username in `README.md` and `docs/index.html`.
3. Push `main`.
4. Turn on GitHub Pages from the `main` branch, `/docs` folder. The playground is `docs/index.html`.
5. If `slides.pdf` is given, add it at the repo root; the README links to it.

## Files

- `src/main/java/com/example/campusai/`: one controller per demo, `LabBookingTools` (tools), `AiConfig` (ChatClient, vector store, handbook loading)
- `src/main/resources/docs/course-handbook.md`: made-up college handbook for RAG
- `requests.http`: every demo request, in session order
- `NOTES.md`: student session notes, plain language
- `docs/index.html`: interactive playground (GitHub Pages)
- `.devcontainer/`: Codespaces setup (installs Ollama, pulls models)
