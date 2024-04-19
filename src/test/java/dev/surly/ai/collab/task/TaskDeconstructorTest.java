package dev.surly.ai.collab.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TaskDeconstructorTest {

    @Autowired
    TaskDeconstructor taskDeconstructor;

    @Test
    public void testDeconstruct_No_Subtasks() {
        var task = new Task("Give me information about Alphabet");
        var subtasks = taskDeconstructor.deconstruct(List.of(task));
        assertFalse(subtasks.isEmpty());
        assertEquals(1, subtasks.size());
    }

    @Test
    public void testDeconstruct_Scrape_No_Subtasks() {
        var task = new Task("scrape yahoo.com");
        var subtasks = taskDeconstructor.deconstruct(List.of(task));
        assertFalse(subtasks.isEmpty());
        assertEquals(1, subtasks.size());
    }

    @Test
    public void testDeconstruct_Subtasks() {
        var task = new Task("Search for the top 5 results for Java, then scrape each page");
        var subtasks = taskDeconstructor.deconstruct(List.of(task));
        assertEquals(2, subtasks.size());
        assertEquals("Search for the top 5 results for Java", subtasks.getFirst().getDescription());
        assertEquals("scrape each page", subtasks.get(1).getDescription());
    }
}
