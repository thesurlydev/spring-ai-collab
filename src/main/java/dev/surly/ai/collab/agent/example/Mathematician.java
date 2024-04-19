package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.agent.example.model.SumRequest;
import dev.surly.ai.collab.agent.Agent;
import dev.surly.ai.collab.tool.Tool;

@Agent(goal = "Answer mathematical questions and solve problems")
public class Mathematician extends AgentService {
    @Tool(name = "AddingMachine", description = "Add a list of numbers together")
    public int sum(SumRequest sumRequest) {
        return sumRequest.numbers().stream().mapToInt(Integer::intValue).sum();
    }
}
