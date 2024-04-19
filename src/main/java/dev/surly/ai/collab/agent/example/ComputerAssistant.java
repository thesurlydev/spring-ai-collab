package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.agent.Agent;
import dev.surly.ai.collab.tool.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Agent(goal = "Provide useful answers about the host computer including reading files and directories")
public class ComputerAssistant extends AgentService {

    @Tool(name = "DirectoryReader", description = "Given a directory, list all the files in the directory.")
    public List<String> readDirectory(String path) {
        File f = new File(path);
        List<String> out = new ArrayList<>();
        if (f.exists()) {
            String[] list = f.list();
            if (list != null) {
                out = Arrays.asList(list);
            }
        }
        return out;
    }
}
