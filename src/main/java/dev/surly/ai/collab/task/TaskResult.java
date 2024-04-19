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
    private final AgentService agent;
    private final Object data;
    private final String dataType;
    private final ObjectMapper mapper;

    public TaskResult(AgentService agent, Object data) {
        this.agent = agent;
        this.data = data;
        this.dataType = data.getClass().getName();
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty print
    }

    public String display() {
        if (data instanceof String raw) {
            var markdown = ConversionUtils.convertToMarkdown(raw);
            return ConversionUtils.convertToHtml(markdown);
        } else {
            String prettyJson;
            try {
                prettyJson = mapper.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return prettyJson;
        }
    }
}
