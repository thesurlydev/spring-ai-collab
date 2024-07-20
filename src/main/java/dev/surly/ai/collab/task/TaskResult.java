package dev.surly.ai.collab.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.surly.ai.collab.util.ConversionUtils;
import lombok.Data;
import lombok.Getter;
import org.springframework.ai.image.Image;

import java.util.List;

@Getter
@Data
public class TaskResult {
    private final Task task;
    private final String agentName;
    private final String toolName;
    private final Object data;
    private final String dataType;
    private TaskError taskError;
    private List<TaskTiming> timings;

    public TaskResult(Task task, String agentName, String toolName, Object data, List<TaskTiming> timings) {
        this.task = task;
        this.agentName = agentName;
        this.toolName = toolName;
        this.data = data;
        if (data != null) {
            this.dataType = data.getClass().getName();
        } else {
            this.dataType = null;
        }
        this.timings = timings;
    }

    public TaskResult(Task task, String agentName, String toolName, TaskError taskError) {
        this.task = task;
        this.agentName = agentName;
        this.toolName = toolName;
        this.data = null;
        this.dataType = null;
        this.taskError = taskError;
    }

    public Long getDuration() {
        if (timings == null || timings.isEmpty()) {
            return 0L;
        }
        return timings.stream()
                .map(TaskTiming::timeMs)
                .reduce(0L, Long::sum);
    }

    public Object display() {
        if (data instanceof Image) {
            return data;
        } else if (data instanceof String raw) {
            var markdown = ConversionUtils.convertToMarkdown(raw);
            return ConversionUtils.convertToHtml(markdown);
        } else {
            String prettyJson;
            try {
                ObjectMapper mapper = new ObjectMapper()
                        .enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty print
                prettyJson = mapper.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return prettyJson;
        }
    }
}
