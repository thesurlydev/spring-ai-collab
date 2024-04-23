package dev.surly.ai.collab.flow;

import dev.surly.ai.collab.task.AgentTaskExecutor;
import dev.surly.ai.collab.task.Task;
import dev.surly.ai.collab.task.TaskResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SequentialFlow implements Flow {

    private final List<Task> tasks = new ArrayList<>();
    private final AgentTaskExecutor agentTaskExecutor;

    public SequentialFlow(AgentTaskExecutor agentTaskExecutor) {
        this.agentTaskExecutor = agentTaskExecutor;
    }

    @Override
    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public List<TaskResult> execute() {
        return tasks.stream()
                .map(task -> {
                    try {
                        return agentTaskExecutor.executeTask(task);
                    } catch (Exception e) {
                        log.error("Error executing task", e);
                        return new TaskResult(task, "unknown", "unknown", e);
                    }
                }).toList();
    }
}
