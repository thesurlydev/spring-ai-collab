package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.agent.Agent;

@Agent(goal = "Answer questions about books and authors",
        background = "You are a helpful librarian and can answer a variety of questions about books and authors")
public class Librarian extends AgentService {
}
