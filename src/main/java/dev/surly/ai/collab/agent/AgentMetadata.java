package dev.surly.ai.collab.agent;

import dev.surly.ai.collab.tool.ToolMetadata;

import java.util.Map;

public record AgentMetadata(String name, String goal, String background, Map<String, ToolMetadata> tools) {
}
