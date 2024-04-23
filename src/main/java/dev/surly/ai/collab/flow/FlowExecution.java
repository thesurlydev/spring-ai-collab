package dev.surly.ai.collab.flow;

import dev.surly.ai.collab.task.TaskResult;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class FlowExecution {

    private final UUID id;
    private final Flow flow;

    public FlowExecution(Flow flow) {
        this.id = UUID.randomUUID();
        this.flow = flow;
    }

    public FlowExecutionResult execute() {
        List<TaskResult> results = flow.execute();
        return new FlowExecutionResult(results);
    }

    @Override public String toString() {
        return "FlowExecution(id=" + this.getId() + ", flow=" + this.getFlow() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowExecution that = (FlowExecution) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
