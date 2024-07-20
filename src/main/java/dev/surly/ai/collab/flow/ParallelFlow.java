package dev.surly.ai.collab.flow;

import dev.surly.ai.collab.task.AgentTaskExecutor;
import dev.surly.ai.collab.task.Task;
import dev.surly.ai.collab.task.TaskResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class ParallelFlow implements Flow {
    private final List<Task> tasks = new ArrayList<>();
    private final AgentTaskExecutor agentTaskExecutor;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ParallelFlow(AgentTaskExecutor agentTaskExecutor) {
        this.agentTaskExecutor = agentTaskExecutor;
    }

    @Override
    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public List<TaskResult> execute() {
        CompletionService<TaskResult> completionService = new ExecutorCompletionService<>(executorService);
        for (Task task : tasks) {
            completionService.submit(() -> agentTaskExecutor.executeTask(task));
        }
        List<TaskResult> results = new ArrayList<>();
        for (Task task : tasks) {
            TaskResult taskResult;
            try {
                taskResult = completionService.take().get();
            } catch (InterruptedException | ExecutionException e) {
                taskResult = logAndReturnTaskError(log, task, e);
            }
            results.add(taskResult);
        }
        return results;
    }
}
