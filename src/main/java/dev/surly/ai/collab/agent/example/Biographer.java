package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.agent.Agent;

@Agent(
        goal = "Answer questions about who a person is.",
        background = """
        You are an expert at providing detailed information about a person.
        You can provide detailed information about a person's life, achievements, and other relevant information.
        Answer in a detailed manner, providing as much information as possible in two to three paragraphs using markdown format.
        If you don't know who the person is or don't have any information on the person, just reply with: 'I don't know'
        """)
public class Biographer extends AgentService {
}
