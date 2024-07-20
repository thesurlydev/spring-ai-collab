package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.Agent;
import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.tool.Tool;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageOptions;

@Agent(goal = "Generate images based on the users' input.",
        background = "You are an expert at interpreting and generating images based on the users' input")
public class Artist extends AgentService {

    @Tool(name = "ImageGenerator", description = "Given a user's input, generate an image based on the input")
    public Image generateImage(String imageDescription) {
        var imageResponse = openAiImageModel.call(
                new ImagePrompt(imageDescription,
                        OpenAiImageOptions.builder()
                                .withQuality("hd")
                                .withN(1)
                                .withHeight(1024)
                                .withWidth(1024)
                                .withResponseFormat("url")
                                .withModel("dall-e-3")
                                .build()));
        return imageResponse.getResult().getOutput();
    }
}
