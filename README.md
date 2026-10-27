# Campus Assistant: a Spring AI demo

The code from my guest lecture **"Building AI-Powered Applications with Java and Spring AI"** at VIT Vellore (SCORE), 28 October 2026.

It's one small Spring Boot app that we built up step by step: chat, structured output, memory and tools, and RAG. A small agent is there as a bonus.

- **Code-along exercises:** [EXERCISES.md](EXERCISES.md), one small change after each demo
- **Session notes:** [NOTES.md](NOTES.md), everything from the talk in plain words
- **Playground:** [open the interactive page](https://usv240.github.io/spring-ai-campus-assistant/), small toys for each idea, nothing to install
- **Slides:** [slides.pdf](slides.pdf)

## Run it in your browser (nothing to install)

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/usv240/spring-ai-campus-assistant)

1. Sign in to GitHub (a free account is enough) and click the button above.
2. Wait about 5 minutes the first time. It installs Java, Maven and Ollama and downloads the AI model for you. You'll see the progress in the terminal.
3. When it says **All set**, run this in the terminal:
   ```bash
   mvn spring-boot:run
   ```
4. Open `requests.http` and click **Send Request** above any line, or open the forwarded port 8080 in your browser.

Good to know:
- The model runs inside your Codespace, so you don't need an API key and nothing leaves GitHub's machine.
- GitHub's free plan includes 120 core hours a month. This Codespace uses a 4-core machine, so that's about 30 hours of use. With the [GitHub Student Developer Pack](https://education.github.com/pack) you get more.
- **Stop the Codespace when you're done** (Codespaces page → ⋯ → Stop) so you don't use up your free hours. Your files stay saved.

## Run it on your own laptop instead

1. Install **Java 21+**, **Maven** and **[Ollama](https://ollama.com/download)**.
2. Download the models (about 2.3 GB):
   ```bash
   ollama pull llama3.2
   ollama pull nomic-embed-text
   ```
3. Run `mvn spring-boot:run`.

Less than 8 GB of RAM? Use the smaller model: `ollama pull llama3.2:1b` and set `spring.ai.ollama.chat.model: llama3.2:1b` in `application.yml`.

## The demos

| Demo | What it shows | Try it | Code |
|---|---|---|---|
| 1 | Talking to a model, system prompts, streaming | `/chat`, `/chat/stream`, `/explain` | `ChatController.java` |
| 2 | Getting Java objects back | `/study-plan` | `StudyPlanController.java` |
| 3 | Memory, and letting the AI call Java methods | `/assistant` | `AssistantController.java`, `LabBookingTools.java` |
| 4 | Answering from your own documents (RAG) | `/ask/no-rag`, `/ask` | `RagController.java`, `AiConfig.java` |
| Bonus | A small agent: give it a goal and it plans the tool calls (not covered live, small models are unreliable at this) | `/assistant` with a goal | same as Demo 3 |

Every request is ready to click in `requests.http`.

Tip: set `logging.level.org.springframework.ai: DEBUG` in `application.yml` to see the full prompts, tool calls and the handbook pieces that get sent to the model.

## Use a cloud model instead (optional)

The code only talks to `ChatClient`, so switching provider means changing one dependency and the config. For example, for OpenAI replace `spring-ai-starter-model-ollama` with `spring-ai-starter-model-openai` and set your key as an environment variable (never in the code): `SPRING_AI_OPENAI_API_KEY=...`. Google Gemini and Anthropic work the same way, see the [Spring AI docs](https://docs.spring.io/spring-ai/reference/api/chatmodel.html).

## Practice: make it yours

Pick one, push it to your own GitHub with a README:

1. **Library tool:** add `@Tool` methods to search books and renew a loan, using the library rules in `course-handbook.md`.
2. **Your own RAG:** swap the sample handbook for your real syllabus and ask it questions.
3. **Quiz maker:** an endpoint that returns `List<QuizQuestion>` for any topic, and another that grades answers.
4. **Memory that survives a restart:** store conversations in PostgreSQL or Redis.

## If something goes wrong

| Problem | Fix |
|---|---|
| `Connection refused localhost:11434` | Ollama isn't running. In a Codespace run `bash .devcontainer/start-ollama.sh`; on a laptop start the Ollama app. |
| The first request is slow | The model is loading. The next ones are faster. |
| It ignores the tools or gives odd answers | Small models are less reliable with tools. Rephrase, or try a bigger model. |
| The study plan fails to parse | Run it again. Small models sometimes return broken JSON. |

The handbook in `src/main/resources/docs` is made up, so the RAG demo has data the model has never seen.
