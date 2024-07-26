package dev.surly.ai.collab.workflow;

import dev.surly.ai.collab.agent.AgentRegistry;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorkflowCoordinator {
    private final AgentRegistry agentRegistry;
    private final WorkflowStateMachine workflowStateMachine;
    private final Map<String, Object> context;

    public WorkflowCoordinator(AgentRegistry agentRegistry, WorkflowStateMachine workflowStateMachine) {
        this.agentRegistry = agentRegistry;
        this.workflowStateMachine = workflowStateMachine;
        this.context = new HashMap<>();
    }

    public void executeWorkflow(String complexTask) {
        while (workflowStateMachine.getCurrentState() != WorkflowState.COMPLETED) {
            agentRegistry.enabledAgents().values().forEach(agent -> {
                if (agent.canPerform(workflowStateMachine.getCurrentState())) {
                    agent.performTask(complexTask, context);
                }
            });
            workflowStateMachine.transition();
        }
    }
}
