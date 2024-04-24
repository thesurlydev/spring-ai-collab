package dev.surly.ai.collab.agent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
