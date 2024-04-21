package dev.surly.ai.collab.validation;

import dev.surly.ai.collab.task.TaskResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CompositeTaskResultValidator implements TaskResultValidator {

    private final List<Predicate<TaskResult>> predicates = new ArrayList<>();

    public CompositeTaskResultValidator addPredicate(Predicate<TaskResult> predicate) {
        predicates.add(predicate);
        return this;
    }

    @Override
    public boolean validate(TaskResult taskResult) {
        return predicates.stream().allMatch(predicate -> predicate.test(taskResult)); // All predicates returned true
    }
}
