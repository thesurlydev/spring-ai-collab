package dev.surly.ai.collab.agent;

import dev.surly.ai.collab.exception.ToolInvocationException;
import dev.surly.ai.collab.exception.ToolNotFoundException;
import dev.surly.ai.collab.task.Task;
import dev.surly.ai.collab.task.TaskResult;
import dev.surly.ai.collab.task.TaskTiming;
import dev.surly.ai.collab.tool.ToolMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiImageModel;
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
    protected OpenAiImageModel openAiImageModel;

    @Autowired
    protected ChatModel chatModel;

    @Value("classpath:/prompts/choose-tool.st")
    private Resource chooseToolUserPrompt;

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
        Generation generation = chatModel.call(prompt).getResult();
        return generation.getOutput().getContent();
    }

    public Object callPromptForBean(Prompt prompt, BeanOutputConverter beanOutputConverter) {
        Generation generation = chatModel.call(prompt).getResult();
        String out = generation.getOutput().getContent();
        return beanOutputConverter.convert(out);
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
        List<TaskTiming> timings = new ArrayList<>();

        if (tools.isEmpty()) {
            log.info("{} agent has no tools configured, executing task via LLM", this.name);
            return executeTaskViaLLM(task, timings);
        }

        var startChooseTool = System.currentTimeMillis();
        ToolMetadata toolMetadata = chooseTool(task);
        timings.add(new TaskTiming("chooseTool", System.currentTimeMillis() - startChooseTool));
        if (toolMetadata.name().equals("__NO_TOOL__")) {
            return executeTaskViaLLM(task, timings);
        }

        var startGetArgs = System.currentTimeMillis();
        Object args = null;
        Class<?> returnType = toolMetadata.getReturnType();
        if (returnType != null) {
            if (returnType.isPrimitive() || "java.lang.String".equals(returnType.getName())) {
                args = getArgsAsString(task, toolMetadata);
            } else {
                args = getArgsAsObject(task, returnType, toolMetadata);
            }
        }
        timings.add(new TaskTiming("getArgs", System.currentTimeMillis() - startGetArgs));

        try {
            var startInvokeTool = System.currentTimeMillis();
            Object toolResult = invokeTool(toolMetadata.method(), args);
            timings.add(new TaskTiming("invokeTool", System.currentTimeMillis() - startInvokeTool));
            TaskResult tr = new TaskResult(task, this.name, toolMetadata.name(), toolResult, timings);
            log.info("TaskResult: {}", tr);
            return tr;
        } catch (Exception e) {
            throw new ToolInvocationException("Error invoking tool: " + toolMetadata + " for task: " + task, e);
        }
    }

    private Object getArgsAsString(Task task, ToolMetadata toolMetadata) {
        Prompt prompt = createPrompt(chooseToolArgsUserPromptNoFormat, Map.of(
                "task", task.getDescription(),
                "signature", toolMetadata.getMethodArgsAsString()
        ));
        return callPromptForString(prompt);
    }

    private Object getArgsAsObject(Task task, Class<?> returnType, ToolMetadata toolMetadata) {
        BeanOutputConverter outputConverter = new BeanOutputConverter(returnType);
        Prompt prompt = createPrompt(chooseToolArgsUserPrompt, Map.of(
                "task", task.getDescription(),
                "signature", toolMetadata.getMethodArgsAsString(),
                "format", outputConverter.getFormat()
        ));
        return callPromptForBean(prompt, outputConverter);
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

    public void addDateContext() {
        StringBuilder dateContext = new StringBuilder();
        dateContext
                .append("The date and time right now is: ")
                .append(ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)))
                .append(". Use this date to answer any questions related to the current date and time.");
        addSystemMessage(dateContext.toString());
    }

    public void addMathInstructions() {
        addSystemMessage("If you perform any math calculations, please return the resulting number and nothing else. Do not return a sentence or any other text.");
    }

    protected TaskResult executeTaskViaLLM(Task task, List<TaskTiming> timings) {

        var startLLM = System.currentTimeMillis();
        if (this.background != null && !this.background.isEmpty()) {
            addSystemMessage(this.background);
        }

        addDateContext();
        addMathInstructions();

        var taskDescription = task.getDescription();
        addUserMessage(taskDescription);

        Prompt prompt = new Prompt(messages);
        String data = callPromptForString(prompt);

        timings.add(new TaskTiming("executeViaLLM", System.currentTimeMillis() - startLLM));

        return new TaskResult(task, this.name, null, data, timings);
    }

    private ToolMetadata chooseTool(Task task) {
        StringBuilder toolList = new StringBuilder();
        tools.values().stream()
                .map(toolMetadata -> toolMetadata.name() + ": " + toolMetadata.description() + "\r\n")
                .forEach(toolList::append);
        Prompt prompt = createPrompt(chooseToolUserPrompt, Map.of(
                "task", task.getDescription(),
                "tools", toolList.toString()
        ));
        String toolName = callPromptForString(prompt);
        if (toolName.equals("__NO_TOOL__")) {
            log.warn("No suitable tool found for task: {}", task.getDescription());
            return new ToolMetadata("__NO_TOOL__", null, null, false);
        }
        ToolMetadata tool = tools.get(toolName);
        log.info("Chosen tool: {}", tool);
        if (tool == null) {
            throw new ToolNotFoundException("No tool found with name: " + toolName);
        }
        return tool;
    }
}
