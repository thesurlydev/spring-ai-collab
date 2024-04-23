package dev.surly.ai.collab.flow;

import dev.surly.ai.collab.task.TaskResult;

import java.util.List;

public record FlowExecutionResult(List<TaskResult> taskResults) {
    public void printResults() {
        taskResults.forEach(System.out::println);
    }
}
