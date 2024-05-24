package dev.surly.ai.collab.statemachine;

import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SimpleStateMachineService {
    private final StateMachine<States, Events> simpleStateMachine;

    @EventListener
    public void onApplicationEvent(TaskEvent taskEvent) {
        CloudEvent ce = taskEvent.getCloudEvent();
        switch(ce.getType()) {
            case "START":
                simpleStateMachine.sendEvent(Events.START);
                break;
            case "FINISH":
                simpleStateMachine.sendEvent(Events.FINISH);
                break;
        }
    }
}
