package dev.surly.ai.collab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan("dev.surly.ai.collab.config")
@SpringBootApplication
public class SpringAICollabApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAICollabApplication.class, args);
	}

}
