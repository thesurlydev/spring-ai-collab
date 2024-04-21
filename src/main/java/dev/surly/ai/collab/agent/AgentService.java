package dev.surly.ai.collab.agent;

import dev.surly.ai.collab.exception.ToolInvocationException;
import dev.surly.ai.collab.exception.ToolNotFoundException;
import dev.surly.ai.collab.task.Task;
import dev.surly.ai.collab.task.TaskResult;
import dev.surly.ai.collab.tool.ToolMetadata;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@ToString
@Slf4j
public class AgentService {

    @Autowired
    protected ChatClient chatClient;

    @Value("classpath:/prompts/choose-tool.st")
    private Resource chooseAgentUserPrompt;

    @Value("classpath:/prompts/choose-tool-args.st")
    private Resource chooseToolArgsUserPrompt;

    @Value("classpath:/prompts/choose-tool-args-no-format.st")
    private Resource chooseToolArgsUserPromptNoFormat;

    @Getter
    private final String name;
    @Getter
    private final String goal;
    @Getter
    private final String background;
    @Getter
    private final Boolean disabled;
    @Getter
    private final Map<String, ToolMetadata> tools = new HashMap<>();
    @Getter
    List<Message> messages = new ArrayList<>();

    public Prompt createPrompt(Resource promptTemplateResource,
                               Map<String, Object> promptModel) {
        PromptTemplate promptTemplate = new PromptTemplate(promptTemplateResource, promptModel);
        return promptTemplate.create();
    }

    public String callPromptForString(Prompt prompt) {
        Generation generation = chatClient.call(prompt).getResult();
        return generation.getOutput().getContent();
    }

    public Object callPromptForBean(Prompt prompt, BeanOutputParser beanOutputParser) {
        Generation generation = chatClient.call(prompt).getResult();
        String out = generation.getOutput().getContent();
        return beanOutputParser.parse(out);
    }

    public void addSystemMessage(String message) {
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(message);
        messages.add(systemTemplate.createMessage());
    }

    public void addUserMessage(String message) {
        UserMessage userMessage = new UserMessage(message);
        messages.add(userMessage);
    }

    public AgentService() {
        this.name = this.getClass().getSimpleName();
        if (this.getClass().isAnnotationPresent(Agent.class)) {
            Agent annotation = this.getClass().getAnnotation(Agent.class);
            this.goal = annotation.goal();
            this.background = annotation.background();
            this.disabled = annotation.disabled();
        } else {
            throw new IllegalStateException("Agent annotation is required on Agent classes");
        }
    }

    public void addTool(ToolMetadata toolMetadata) {
        tools.put(toolMetadata.name(), toolMetadata);
    }

    public TaskResult executeTask(Task task) throws ToolInvocationException {
        log.info("Executing task: {}", task);
        if (tools.isEmpty()) {
            log.info("{} agent has no tools configured, executing task via LLM", this.name);
            return executeTaskViaLLM(task);
        }
        ToolMetadata toolMetadata = chooseTool(task);
        Object args = getToolArgs(toolMetadata, task);
        try {
            Object toolResult = invokeTool(toolMetadata.method(), args);
            TaskResult tr = new TaskResult(this.name, toolResult);
            log.info("TaskResult: {}", tr);
            return tr;
        } catch (Exception e) {
            throw new ToolInvocationException("Error invoking tool: " + toolMetadata + " for task: " + task, e);
        }
    }

    private <T> T invokeTool(Method method, Object args) throws Exception {
        if (args == null) {
            log.info("Invoking method: {}", method.toString());
            T result = (T) method.invoke(this);
            return result;
        } else {
            log.info("Invoking method: {} with args: {}", method.toString(), args);
            log.info("args type: {}", args.getClass().getName());
            T result = (T) method.invoke(this, args);
            return result;
        }
    }

    private TaskResult executeTaskViaLLM(Task task) {

        if (this.background != null && !this.background.isEmpty()) {
            addSystemMessage(this.background);
        }

        StringBuilder dateContext = new StringBuilder();
        dateContext
                .append("The date and time right now is: ")
                .append(ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)))
                .append(". Use this date to answer any questions related to the current date and time.");
        addSystemMessage(dateContext.toString());

        var taskDescription = task.getDescription();
        addUserMessage(taskDescription);

        Prompt prompt = new Prompt(messages);
        String data = callPromptForString(prompt);

        return new TaskResult(this.name, data);
    }

    private ToolMetadata chooseTool(Task task) {
        StringBuilder toolList = new StringBuilder();
        tools.values().stream()
                .map(toolMetadata -> toolMetadata.name() + ": " + toolMetadata.description() + "\r\n")
                .forEach(toolList::append);
        Prompt prompt = createPrompt(chooseAgentUserPrompt, Map.of(
                "task", task.getDescription(),
                "tools", toolList.toString()
        ));
        String toolName = callPromptForString(prompt);
        ToolMetadata tool = tools.get(toolName);
        log.info("Chosen tool: {}", tool);
        if (tool == null) {
            throw new ToolNotFoundException("No tool found with name: " + toolName);
        }
        return tool;
    }

    private Object getToolArgs(@NonNull ToolMetadata toolMetadata, @NonNull Task task) {
        Class<?> returnType = toolMetadata.getReturnType();
        if (returnType == null) {
            return null;
        }

        if (returnType.isPrimitive() || "java.lang.String".equals(returnType.getName())) {
            Prompt prompt = createPrompt(chooseToolArgsUserPromptNoFormat, Map.of(
                    "task", task.getDescription(),
                    "signature", toolMetadata.getMethodArgsAsString()
            ));
            return callPromptForString(prompt);
        } else {
            var outputParser = new BeanOutputParser<>(returnType);
            Prompt prompt = createPrompt(chooseToolArgsUserPrompt, Map.of(
                    "task", task.getDescription(),
                    "signature", toolMetadata.getMethodArgsAsString(),
                    "format", outputParser.getFormat()
            ));
            return callPromptForBean(prompt, outputParser);
        }
    }
}
