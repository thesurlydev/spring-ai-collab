package dev.surly.ai.collab.task;

import dev.surly.ai.collab.nlp.NlpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class TaskDeconstructor {

    private final NlpService nlpService;

    public List<Task> deconstruct(List<Task> tasks) {
        List<Task> subtasks = tasks.stream()
                .map(Task::getDescription)
                .flatMap(s -> nlpService.getSubtasks(s).stream())
                .map(Task::new)
                .toList();

        List<Task> result;
        if (subtasks.isEmpty()) {
            result = tasks;
        } else if (subtasks.size() == 1) {
            result = subtasks;
        } else {
            result = subtasks.subList(1, subtasks.size());
        }

        StringBuilder subtasksOutput = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            Task task = result.get(i);
            subtasksOutput.append(i+1).append(". ").append(task.getDescription()).append("\n");
        }
        log.info("Subtasks:\n{}\n", subtasksOutput);

        return result;
    }
}
