package dev.surly.ai.collab.agent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.stringtemplate.v4.ST;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AgentRegistryTest {

    @Autowired AgentRegistry agentRegistry;

    @Test
    public void registryContainsAgents() {
        Map<String, AgentService> allAgents = agentRegistry.allAgents();
        assertFalse(allAgents.isEmpty());
    }

    @Test
    public void testPromptRendering() {
        String templateString = "The items are:\n{items :{item | - {item}\n }}";
        List<String> itemList = Arrays.asList("apple", "banana", "cherry");
        PromptTemplate promptTemplate = new PromptTemplate(templateString);
        Message message = promptTemplate.createMessage(Map.of("items", itemList));

        String expected = "The items are:\n" +
                "- apple\n" +
                "- banana\n" +
                "- cherry\n";

        assertEquals(expected, message.getContent());
    }

    @Test
    public void testPromptRendering2() {
        String templateString = "The items are:\n{items:{item| - {item}\n}}";
        List<String> itemList = Arrays.asList("apple", "banana", "cherry");
        PromptTemplate promptTemplate = new PromptTemplate(templateString);
        Message message = promptTemplate.createMessage(Map.of("items", itemList));

        String expected = "The items are:\n" +
                "- apple\n" +
                "- banana\n" +
                "- cherry\n";

        assertEquals(expected, message.getContent());
    }

}
