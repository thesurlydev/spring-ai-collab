package dev.surly.ai.collab.controller.model;

import dev.surly.ai.collab.task.Task;
import lombok.Data;

@Data
public class TeamForm {
    private String task;
    private String agent;

    public Task toTask() {
        if (agent != null && !agent.isEmpty()) {
            return new Task(task, agent);
        } else {
            return new Task(task);
        }
    }
}
