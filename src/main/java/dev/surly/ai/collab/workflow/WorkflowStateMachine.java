package dev.surly.ai.collab.workflow;

import org.springframework.stereotype.Component;

@Component
public class WorkflowStateMachine {
    private WorkflowState currentState;

    public WorkflowStateMachine() {
        this.currentState = WorkflowState.DATA_COLLECTION;
    }

    public void transition() {
        switch (currentState) {
            case DATA_COLLECTION:
                currentState = WorkflowState.DATA_ANALYSIS;
                break;
            case DATA_ANALYSIS:
                currentState = WorkflowState.COMPLETED;
                break;
            case COMPLETED:
                break;
        }
    }

    public WorkflowState getCurrentState() {
        return currentState;
    }
}
