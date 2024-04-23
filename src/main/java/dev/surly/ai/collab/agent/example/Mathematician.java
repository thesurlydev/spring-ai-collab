package dev.surly.ai.collab.agent.example;

import dev.surly.ai.collab.agent.Agent;
import dev.surly.ai.collab.agent.AgentService;
import dev.surly.ai.collab.agent.example.model.MathRequest;
import dev.surly.ai.collab.tool.Tool;

import java.util.List;
import java.util.stream.Collectors;

@Agent(goal = "Answer mathematical questions and solve problems")
public class Mathematician extends AgentService {

    @Tool(name = "Adder", description = "Add a list of numbers together")
    public double add(MathRequest request) {
        double sum = 0;
        for (double num : request.numbers()) {
            sum += num;
        }
        return sum;
    }

    @Tool(name = "Subtractor", description = "Subtract a list of numbers")
    public double subtract(MathRequest request) {
        if (request.numbers().isEmpty()) return 0;
        double result = request.numbers().getFirst();
        for (int i = 1; i < request.numbers().size(); i++) {
            result -= request.numbers().get(i);
        }
        return result;
    }

    @Tool(name = "Multiplier", description = "Multiply a list of numbers together")
    public double multiply(MathRequest request) {
        if (request.numbers().isEmpty()) return 0;
        double result = 1;
        for (double num : request.numbers()) {
            result *= num;
        }
        return result;
    }

    @Tool(name = "Divider", description = "Divide a list of numbers")
    public double divide(MathRequest request) {
        if (request.numbers().isEmpty()) return Double.NaN;
        double result = request.numbers().getFirst();
        for (int i = 1; i < request.numbers().size(); i++) {
            if (request.numbers().get(i) == 0) {
                return Double.NaN; // Return NaN if division by zero is attempted
            }
            result /= request.numbers().get(i);
        }
        return result;
    }

    @Tool(name = "Square", description = "Square each of the list of numbers")
    public List<Double> square(MathRequest request) {
        return request.numbers().stream()
                .map(num -> num * num)
                .collect(Collectors.toList());
    }

    @Tool(name = "SquareRoot", description = "Calculate the square root of each number")
    public List<Double> squareRoot(MathRequest request) {
        return request.numbers().stream()
                .map(Math::sqrt)
                .collect(Collectors.toList());
    }
}
