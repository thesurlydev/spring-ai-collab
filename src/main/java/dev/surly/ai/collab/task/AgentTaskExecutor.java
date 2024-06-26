package dev.surly.ai.collab.task;

import dev.surly.ai.collab.agent.AgentRegistry;
import dev.surly.ai.collab.exception.ToolInvocationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AgentTaskExecutor {

    private final ChatModel chatModel;
    private final TaskPlanner taskPlanner;
    private final AgentRegistry agentRegistry;

    public TaskResult executeTask(Task task) throws ToolInvocationException {
        log.info("Executing task: {}", task);
        return taskPlanner.chooseAgent(chatModel, task)
                .map(agentRegistry::getAgent)
                .map(agent -> agent.executeTask(task))
                .orElseThrow(() -> handleNoToolAvailable(task));
    }

    public List<TaskResult> executeTasks(ChatModel chatModel, List<Task> tasks) throws ToolInvocationException {
        return tasks.stream()
                .map(task -> taskPlanner.chooseAgent(chatModel, task)
                        .map(agentRegistry::getAgent)
                        .map(agent -> agent.executeTask(task))
                        .orElseThrow(() -> handleNoToolAvailable(task))
                )
                .toList();
    }

    private @NotNull ToolInvocationException handleNoToolAvailable(Task task) {
        log.error("No tool available to execute task: {}", task);
        return new ToolInvocationException("No tool available to execute task");
    }
}
