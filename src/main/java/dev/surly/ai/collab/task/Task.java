package dev.surly.ai.collab.task;

import lombok.Data;

@Data
public class Task {
    private final String description;
    private final String agent;

    public Task(String description, String agent) {
        this.description = description;
        this.agent = agent;
    }

    public Task(String description) {
        this(description, null);
    }
}
