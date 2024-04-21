package dev.surly.ai.collab.controller;

import dev.surly.ai.collab.Team;
import dev.surly.ai.collab.agent.AgentRegistry;
import dev.surly.ai.collab.controller.model.TeamForm;
import dev.surly.ai.collab.task.Task;
import dev.surly.ai.collab.task.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TeamController {
    private final Team team;
    private final AgentRegistry agentRegistry;

    @GetMapping("/")
    public String team(Model model) {
        model.addAttribute("teamForm", new TeamForm());
        model.addAttribute("agents", agentRegistry.enabledAgents());
        return "index";
    }

    @PostMapping("/")
    public String executeTask(@ModelAttribute TeamForm teamForm, Model model) {
        log.info("Given task: {}", teamForm);
        Task task = teamForm.toTask();
        List<TaskResult> taskResults = team
                .addTasks(List.of(task))
                .kickoff();

        TaskResult taskResult = taskResults.getFirst();
        model.addAttribute("taskResult", taskResult);
        model.addAttribute("agents", agentRegistry.enabledAgents());
        return "index";
    }
}
