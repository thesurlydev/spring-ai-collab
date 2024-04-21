package dev.surly.ai.collab.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.util.ConversionUtils;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class TaskResult {
    private final String agentName;
    private final Object data;
    private final String dataType;

    public TaskResult(String agentName, Object data) {
        this.agentName = agentName;
        this.data = data;
        this.dataType = data.getClass().getName();
    }

    public String display() {
        if (data instanceof String raw) {
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
