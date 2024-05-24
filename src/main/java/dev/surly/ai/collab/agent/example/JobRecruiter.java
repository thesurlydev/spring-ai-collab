package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.Agent;
import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.agent.example.model.JobRateRequest;
import dev.surly.ai.collab.agent.example.model.JobRateResponse;
import dev.surly.ai.collab.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Agent(goal = "Provide guidance and support to an individual in their search for a job")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/agents/job-recruiter")
@Slf4j
public class JobRecruiter extends AgentService {

    @Value("classpath:/prompts/agent-job-rating.st")
    private Resource jobRaterPrompt;

    @PostMapping("/rate-job")
    @Tool(name = "JobRater", description = "Given a job description, rate the job based on how well it matches the user's skills and interests")
    public JobRateResponse rateJob(@RequestBody JobRateRequest jobRateRequest) {
        var outputConverter = new BeanOutputConverter<>(JobRateResponse.class);

        PromptTemplate promptTemplate = new PromptTemplate(jobRaterPrompt,
                Map.of(
                        "jobDescription", jobRateRequest.jobDescription(),
                        "qualifications", jobRateRequest.qualificationsForPrompt(),
                        "interests", jobRateRequest.interestsForPrompt(),
                        "format", outputConverter.getFormat()
                )
        );
        Prompt prompt = promptTemplate.create();

        log.info("Prompt: {}", prompt.toString());

        Generation generation = chatModel.call(prompt).getResult();

        JobRateResponse response = outputConverter.convert(generation.getOutput().getContent());
        log.info("JobRateResponse: {}", response);
        return response;
    }
}
