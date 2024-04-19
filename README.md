# Spring AI Collab

An agent framework using [Spring AI](https://spring.io/projects/spring-ai).

## Features

- Support for multiple agents and tools via simple annotations.
- Leverages [Spring AI](https://spring.io/projects/spring-ai) for abstractions. 
- Automatically selects agent and tools based on the given task.
- Web chat interface to perform tasks and optionally assign an agent.
    - If no agent is specified, the underlying LLM is used to choose an agent based on the task. 

## Roadmap

Note: Some of the roadmap features depend on Chat message history which is not available in Spring AI yet.

- Process multiple tasks at once.
- Compose "teams" of agents that collaboratively work together to accomplish tasks.
- Add JVM code creation and execution. (Java, Kotlin)

## Requirements

This project uses [OpenAI](https://openai.com/) as the default LLM.

- Set `OPENAI_API_KEY` environment variable. 

## Build and Test

To build and run tests:
```shell
./gradlew clean build
```

## Inspired by

- [Microsoft's Autogen](https://www.microsoft.com/en-us/research/project/autogen/)
- [Crew AI](https://www.crewai.com/)
- 