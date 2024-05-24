package dev.surly.ai.collab.statemachine;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(TaskEvent taskEvent) {
        applicationEventPublisher.publishEvent(taskEvent);
    }
}
