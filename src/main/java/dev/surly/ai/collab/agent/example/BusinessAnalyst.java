package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.Agent;
import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.agent.example.model.CompanyDetail;
import dev.surly.ai.collab.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.Map;

@Agent(
        goal = "Provide business analysis of companies"
)
@RequiredArgsConstructor
@Slf4j
public class BusinessAnalyst extends AgentService {

    @Value("classpath:/prompts/agent-company-focus.st")
    private Resource companyFocusUserPrompt;

    @Tool(name = "GetCompanyFocus", description = "Given the name of a company, return the focus of the company")
    public String getCompanyFocus(String companyName) {
        Prompt prompt = createPrompt(companyFocusUserPrompt, Map.of(
                "companyName", companyName
        ));
        return callPromptForString(prompt);
    }

    @Tool(name = "GetCompanyDetail", description = "Given a company name, get the details about the company including website URL")
    public CompanyDetail getCompanyDetails(String name) {
        var outputConverter = new BeanOutputConverter<>(CompanyDetail.class);

        String userMessage =
                """
                Get the details including website url and address for the company: {name}.
                Only provide the stock ticker if the company is public.
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("name", name, "format",
                outputConverter.getFormat()));
        Prompt prompt = promptTemplate.create();

        log.info("Prompt: {}", prompt.toString());

        Generation generation = chatModel.call(prompt).getResult();

        CompanyDetail detail = outputConverter.convert(generation.getOutput().getContent());
        log.info("CompanyDetail: {}", detail);
        return detail;
    }
}
