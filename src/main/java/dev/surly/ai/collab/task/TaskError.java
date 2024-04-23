package dev.surly.ai.collab.task;

public record TaskError(String message, Throwable throwable) {
}
