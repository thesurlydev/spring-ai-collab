package dev.surly.ai.collab.agent.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import dev.surly.ai.collab.agent.Agent;
import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.tool.Tool;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Agent(goal = "Interpret and answer questions about software code")
@Slf4j
public class SoftwareEngineer extends AgentService {
    @Tool(name = "SchemaGenerator", description = "Given the fully qualified name of a class, generate a JSON schema for it")
    public String generateSchema(String className) {
        log.info("Generating schema for: {}", className);
        Class<?> aClass;
        try {
            aClass = Class.forName(className, false, this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            log.error("Class not found", e);
            return "Class not found";
        }
        return generateSchema(aClass);
    }

    /*
    Stolen from BeanOutputParser
     */
    private String generateSchema(Class<?> clazz) {

        JacksonModule jacksonModule = new JacksonModule();
        SchemaGeneratorConfigBuilder configBuilder =
                (new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON))
                        .with(jacksonModule);
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        JsonNode jsonNode = generator.generateSchema(clazz, new Type[0]);
        ObjectWriter objectWriter = (new ObjectMapper())
                .writer((new DefaultPrettyPrinter())
                        .withObjectIndenter((new DefaultIndenter())
                                .withLinefeed(System.lineSeparator())));

        String jsonSchema;
        try {
            jsonSchema = objectWriter.writeValueAsString(jsonNode);
        } catch (JsonProcessingException var8) {
            JsonProcessingException e = var8;
            throw new RuntimeException("Could not pretty print json schema for " + clazz, e);
        }
        return String.format("\n```%s```\n", jsonSchema);
    }
}
