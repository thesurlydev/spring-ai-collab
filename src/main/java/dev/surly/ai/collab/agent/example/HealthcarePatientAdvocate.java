package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.Agent;
import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Agent(goal = "Provide assistance to patients with healthcare needs")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/agents/healthcare-advocate")
@Slf4j
@Component
@Profile("healthcare")
public class HealthcarePatientAdvocate extends AgentService {

    private final VectorStore vectorStore;
    private final OpenAiChatModel openAiChatModel;

    @Tool(name = "Healthcare benefits query interface", description = "Provide information about healthcare insurance benefits")
    public String getHealthcareBenefitsInfo(String healthcareBenefitsQuestion) {

        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.query(healthcareBenefitsQuestion).withTopK(1)
        );
        String content = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining(System.lineSeparator()));

        var systemPromptTemplate = """
                You are a helpful assistant, conversing with a user about health benefits available to them through Providence HealthPlan insurance.
                Use the information from the DOCUMENTS section to provide accurate answers. If unsure or if the answer
                isn't found in the DOCUMENTS section, simply state that you don't know the answer and do not mention
                the DOCUMENTS section.
                                    
                ## DOCUMENTS:
                                    
                {documents}
                """;

        return ChatClient.create(openAiChatModel)
                .prompt()
                .system(sysSpec -> sysSpec.text(systemPromptTemplate).param("documents", content))
                .user(healthcareBenefitsQuestion)
                .call()
                .content();
    }
}
