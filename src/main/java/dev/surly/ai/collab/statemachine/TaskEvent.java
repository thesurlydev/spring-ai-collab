package dev.surly.ai.collab.statemachine;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventBuilder;
import org.springframework.context.ApplicationEvent;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TaskEvent extends ApplicationEvent {

    private final CloudEvent cloudEvent;

    public TaskEvent(Object source, Events eventType, byte[] data) {
        super(source);
        this.cloudEvent = new CloudEventBuilder()
                .withId(UUID.randomUUID().toString())
                .withType(eventType.name())
                .withSource(URI.create("https://surly.dev/collab/task"))
                .withTime(OffsetDateTime.now())
                .withData(data)
                .build();
    }

    public CloudEvent getCloudEvent() {
        return cloudEvent;
    }

}
