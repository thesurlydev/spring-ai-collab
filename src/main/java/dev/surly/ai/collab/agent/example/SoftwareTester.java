package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.Agent;
import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.tool.Tool;
import lombok.extern.slf4j.Slf4j;

@Agent(goal = "Test software",
        background = "You are an expert software engineer in all major programming languages. Test software for bugs and issues.")
@Slf4j
public class SoftwareTester extends AgentService {
    @Tool(name ="TestWriter", description = "Write comprehensive test cases for a given software class, method, or function")
    public String writeTests(String code) {
        return "TODO";
    }
}
