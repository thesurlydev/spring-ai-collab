package dev.surly.ai.collab.validation;

import dev.surly.ai.collab.task.TaskResult;

public interface TaskResultValidator extends Validator<TaskResult> {
    boolean validate(TaskResult taskResult);
}
