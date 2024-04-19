package dev.surly.ai.collab.agent;

import dev.surly.ai.collab.tool.Tool;
import dev.surly.ai.collab.tool.ToolMetadata;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

@Component
@Slf4j
public class AgentRegistry {

    private final Map<String, AgentService> allAgents = new HashMap<>();
    private final ApplicationContext applicationContext;

    public AgentRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void initializeAgents() {
        Map<String, AgentService> agentBeans = applicationContext.getBeansOfType(AgentService.class);
        agentBeans.values().forEach(agent -> {
            addTools(agent);
            registerAgent(agent);
        });
    }

    private void addTools(AgentService agent) {
        Arrays.stream(agent.getClass().getDeclaredMethods())
                .filter(this::hasToolAnnotation)
                .map(this::createTool)
                .forEach(agent::addTool);
    }

    private boolean hasToolAnnotation(Method method) {
        return AnnotationUtils.findAnnotation(method, Tool.class) != null;
    }

    private ToolMetadata createTool(Method method) {
        Tool tool = AnnotationUtils.findAnnotation(method, Tool.class);
        String name = Objects.requireNonNull(tool).name() != null ? tool.name() : "";
        String description = tool.description() != null ? tool.description() : "";
        boolean disabled = tool.disabled();
        return new ToolMetadata(name, description, method, disabled);
    }

    private void registerAgent(AgentService agent) {
        allAgents.put(agent.getName(), agent);
    }

    public AgentService getAgent(String agentName) {
        return allAgents.get(agentName);
    }

    public Map<String, AgentService> allAgents() {
        return allAgents;
    }

    public Map<String, AgentService> enabledAgents() {
        return allAgents.entrySet().stream()
                .filter(entry -> !entry.getValue().getDisabled())
                .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), Map::putAll);
    }
}
