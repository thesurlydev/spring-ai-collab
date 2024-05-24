package dev.surly.ai.collab.agent.example.model;

import java.util.List;

public record JobRateResponse(Integer rating,
                              List<String> detractingFactors,
                              List<String> enhancingFactors,
                              String roleDescription,
                              String companyDescription,
                              List<String> responsibilities,
                              List<String> requirements) {
}
