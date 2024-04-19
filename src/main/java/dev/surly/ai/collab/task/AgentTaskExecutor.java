package dev.surly.ai.collab.task;

import dev.surly.ai.collab.agent.AgentRegistry;
import dev.surly.ai.collab.exception.ToolInvocationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AgentTaskExecutor {

    private final ChatClient chatClient;
    private final TaskPlanner taskPlanner;
    private final AgentRegistry agentRegistry;

    public TaskResult processTask(Task task) throws ToolInvocationException {
        log.info("Processing task: {}", task);
        return taskPlanner.chooseAgent(chatClient, task)
                .map(agentRegistry::getAgent)
                .map(agent -> agent.processTask(task))
                .orElseThrow(() -> handleNoToolAvailable(task));
    }

    public List<TaskResult> processTasks(ChatClient chatClient, List<Task> tasks) throws ToolInvocationException {
        return tasks.stream()
                .map(task -> taskPlanner.chooseAgent(chatClient, task)
                        .map(agentRegistry::getAgent)
                        .map(agent -> agent.processTask(task))
                        .orElseThrow(() -> handleNoToolAvailable(task))
                )
                .toList();
    }

    private @NotNull ToolInvocationException handleNoToolAvailable(Task task) {
        log.error("No tool available to process task: {}", task);
        return new ToolInvocationException("No tool available to process task");
    }
}
