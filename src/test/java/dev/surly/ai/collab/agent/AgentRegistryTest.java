package dev.surly.ai.collab.agent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class AgentRegistryTest {

    @Autowired AgentRegistry agentRegistry;

    @Test
    public void testAgentRegistry() {
        assertNotNull(agentRegistry);
    }

    @Test
    public void numberOfAgents() {
        Map<String, AgentService> allAgents = agentRegistry.allAgents();
        assertEquals(10, allAgents.size());
    }
}
