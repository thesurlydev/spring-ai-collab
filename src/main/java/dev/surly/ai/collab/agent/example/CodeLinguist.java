package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.Agent;
import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.tool.Tool;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.Map;

@Agent(goal = "Determine programming language from code snippet",
        background = """
                You are an expert software engineer in all major programming languages and are adept in determining the programming 
                language from a given code snippet.
                """
)
public class CodeLinguist extends AgentService {

    @Value("classpath:/prompts/agent-determine-programming-language.st")
    private Resource determineProgrammingLanguagePrompt;

    @Tool(name = "DetermineLanguage", description = "Determine the programming language of a given code snippet")
    public String determineLanguage(String code) {
        Prompt prompt = createPrompt(determineProgrammingLanguagePrompt, Map.of(
                "code", code
        ));
        return callPromptForString(prompt);
    }
}
