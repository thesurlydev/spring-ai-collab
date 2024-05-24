package dev.surly.ai.collab.agent.example.model;

import java.util.List;
import java.util.StringJoiner;

public record JobRateRequest(String jobDescription, List<String> qualifications, List<String> interests) {

    private static final StringJoiner JOINER = new StringJoiner("\n- ", "\n- ", "\n");

    public String qualificationsForPrompt() {
        return join(qualifications);
    }

    public String interestsForPrompt() {
        return join(interests);
    }

    private String join(List<String> list) {
        for (String item : list) {
            JOINER.add(item);
        }
        return JOINER.toString();
    }
}
