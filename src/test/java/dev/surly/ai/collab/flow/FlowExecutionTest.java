package dev.surly.ai.collab.flow;

import dev.surly.ai.collab.task.AgentTaskExecutor;
import dev.surly.ai.collab.task.Task;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FlowExecutionTest {
    @Autowired AgentTaskExecutor agentTaskExecutor;


    @Test
    @Disabled("Requires OpenAI API key")
    public void test() {

        Flow flow = new ParallelFlow(agentTaskExecutor);

        Task sayHiTask = new Task("say hi");
        flow.addTask(sayHiTask);

        Task task = new Task("""
                Identify programming language of the following code snippet:
                ```
                @Tool(name ="TestWriter", description = "Write comprehensive test cases for a given software class, method, or function")
                    public String writeTests(String code) {
                        return "TODO";
                    }
                ```
                """);
        flow.addTask(task);

        Task whoWroteTask = new Task("Who wrote the book Without Remorse?");
        flow.addTask(whoWroteTask);

        Task whoIsTask = new Task("Who is Barack Obama?");
        flow.addTask(whoIsTask);

        Task whoIsTask2 = new Task("What is 2 plus 2?");
        flow.addTask(whoIsTask2);

        FlowExecution flowExecution = new FlowExecution(flow);

        FlowExecutionResult result = flowExecution.execute();
        result.printResults();
    }
}
