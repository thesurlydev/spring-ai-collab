package dev.surly.ai.collab.task;

import dev.surly.ai.collab.agent.AgentRegistry;
import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.tool.Tool;
import dev.surly.ai.collab.tool.ToolMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.parser.MapOutputParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class TaskPlanner {

    private final ChatClient chatClient;
    private final AgentRegistry agentRegistry;

    @Value("classpath:/prompts/task-planner-choose-agents.st")
    private Resource chooseAgentsUserPrompt;

    @Value("classpath:/prompts/task-planner-choose-agent.st")
    private Resource chooseAgentUserPrompt;

    @Tool(name = "TaskAssigner")
    public List<TaskAssignment> assign(List<Task> tasks) {
        return tasks.stream()
                .map(task -> new TaskAssignment(task, chooseAgent(chatClient, task).orElse(null)))
                .toList();
    }

    /**
     * Given a task, determine which agent is most capable of accomplishing the task
     * TODO add tools to prompt to aid in decisioning
     *
     * @param task
     * @return
     */
    public Optional<String> chooseAgent(ChatClient chatClient, Task task) {
        long start = System.currentTimeMillis();
        Map<String, AgentService> agents = agentRegistry.enabledAgents();

        log.info("Found {} enabled agents", agents.size());
//        agents.forEach((k, v) -> log.info("Agent: {}, Goal: {}", k, v.getGoal()));

        if (task.getAgent() != null) {
            log.info("Task specified agent: {}", task.getAgent());
            return agents.keySet().stream().filter(s -> s.equals(task.getAgent())).findFirst();
        }

        if (agents.isEmpty()) {
            log.warn("No agents available");
            return Optional.empty();
        }

        if (agents.size() == 1) {
            var agent = agents.entrySet().stream().findFirst().get();
            log.warn("Only one agent available: {}", agent);
            return Optional.ofNullable(agent.getKey());
        }

        var outputParser = new BeanOutputParser<>(String.class);

        StringBuilder agentList = new StringBuilder();
        for (Map.Entry<String, AgentService> entry : agents.entrySet()) {
            agentList.append(entry.getKey()).append(": ").append(entry.getValue().getGoal()).append("\r\n");
        }

        PromptTemplate promptTemplate = new PromptTemplate(chooseAgentUserPrompt, Map.of(
                "task", task.getDescription(),
                "agents", agentList.toString(),
                "format", outputParser.getFormat()
        ));
        Prompt prompt = promptTemplate.create();

        Generation generation = chatClient.call(prompt).getResult();
        String out = generation.getOutput().getContent();
        Long elapsed = System.currentTimeMillis() - start;
        log.info("Selected Agent: {} in {} ms", out, elapsed);

        return Optional.ofNullable(outputParser.parse(out));
    }

    private Map<String, Object> chooseAgents(ChatClient chatClient, List<Task> tasks) {
        Map<String, AgentService> agents = agentRegistry.enabledAgents();

        if (agents.isEmpty()) {
            log.warn("No agents available");
            return Map.of();
        }

        log.info("Found {} enabled agents", agents.size());
        agents.forEach((k, v) -> log.info("Agent: {}, Goal: {}", k, v.getGoal()));

        var outputParser = new MapOutputParser();

        StringBuilder agentList = new StringBuilder();
        for (Map.Entry<String, AgentService> entry : agents.entrySet()) {
            var agentName = entry.getKey();
            var agent = entry.getValue();
            var agentGoal = agent.getGoal();
            var tools = agent.getTools();
            StringBuilder toolList = generateToolListForPrompt(tools);
            agentList.append(agentName).append(": ").append(agentGoal)
                    .append("\r\n")
                    .append(toolList);
        }

        StringBuilder taskList = new StringBuilder();
        for (Task task : tasks) {
            taskList.append(task.getDescription()).append("\r\n");
        }

        PromptTemplate promptTemplate = new PromptTemplate(chooseAgentsUserPrompt, Map.of(
                "tasks", taskList.toString(),
                "agents", agentList.toString(),
                "format", outputParser.getFormat()
        ));
        Prompt prompt = promptTemplate.create();

        Generation generation = chatClient.call(prompt).getResult();
        String out = generation.getOutput().getContent();

        return outputParser.parse(out);
    }

    private @NotNull StringBuilder generateToolListForPrompt(Map<String, ToolMetadata> tools) {
        StringBuilder toolList = new StringBuilder();
        toolList.append("The tools available from this agent are: ");
        for (Map.Entry<String, ToolMetadata> toolEntry : tools.entrySet()) {
            var toolName = toolEntry.getKey();
            var toolDesc = toolEntry.getValue().description();
            boolean toolDisabled = toolEntry.getValue().disabled();
            if (!toolDisabled) {
                toolList.append("- ").append(toolName).append(": ").append(toolDesc).append("\r\n");
            }
        }
        return toolList;
    }
}
