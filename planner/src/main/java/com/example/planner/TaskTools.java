package com.example.planner;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TaskTools {
    record Task(int id, String title, LocalDate due, boolean done) {}
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    @McpTool(description = "Add a study task with a due date")
    public String addTask(
            @McpToolParam(description = "Task title") String title,
            @McpToolParam(description = "Due date as YYYY-MM-DD") String dueDate) {
        tasks.add(new Task(nextId, title, LocalDate.parse(dueDate), false));
        return "Added #" + nextId++ + ": " + title + " (due " + dueDate + ")";
    }

    @McpTool(description = "List all tasks")
    public String listTasks() {
        return tasks.isEmpty() ? "No tasks." : tasks.toString();
    }

    @McpTool(description = "Show pending tasks due within the next N days")
    public String tasksDueSoon(@McpToolParam(description = "Number of days") int days) {
        LocalDate limit = LocalDate.now().plusDays(days);
        return tasks.stream()
                .filter(t -> !t.done() && !t.due().isAfter(limit))
                .toList().toString();
    }

    @McpTool(description = "Mark a task as completed")
    public String completeTask(@McpToolParam(description = "Task id") int id) {
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            if (t.id() == id) {
                tasks.set(i, new Task(t.id(), t.title(), t.due(), true));
                return "Completed: " + t.title();
            }
        }
        return "Task not found.";
    }
}
