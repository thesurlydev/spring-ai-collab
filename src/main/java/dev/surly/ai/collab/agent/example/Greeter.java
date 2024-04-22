package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.agent.Agent;
import dev.surly.ai.collab.tool.Tool;

@Agent(goal = "Say hello", background = "You are a friendly person and greet everyone you encounter")
public class Greeter extends AgentService {
    @Tool(name = "SayHello", description = "Be friendly and say hello")
    public String sayHello() {
        return "Hello, World!";
    }
}
