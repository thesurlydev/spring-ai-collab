package dev.surly.ai.collab.flow;

import dev.surly.ai.collab.task.Task;
import dev.surly.ai.collab.task.TaskResult;

import java.util.List;

public interface Flow {
    void addTask(Task task);
    List<TaskResult> execute();
}
