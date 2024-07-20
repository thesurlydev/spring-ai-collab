package dev.surly.ai.collab.flow;

import dev.surly.ai.collab.task.Task;
import dev.surly.ai.collab.task.TaskError;
import dev.surly.ai.collab.task.TaskResult;
import org.slf4j.Logger;

import java.util.List;

public interface Flow {
    void addTask(Task task);
    List<TaskResult> execute();

    default TaskResult logAndReturnTaskError(Logger log, Task task, Throwable t) {
        var taskError = new TaskError("Error executing task " + task, t);
        log.error(taskError.message(), taskError.throwable());
        return new TaskResult(task, "unknown", "unknown", taskError);
    }
}
