package dev.surly.ai.collab.agent.example.model;

public record CompanyDetail(String name,
                            String websiteLink,
                            String stockTicker,
                            String numberOfEmployees,
                            String summary,
                            String location
) {}
