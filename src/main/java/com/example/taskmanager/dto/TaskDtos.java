package com.example.taskmanager.dto;

import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.TaskPriority;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class TaskDtos {

    public record CreateTaskRequest(
            @NotBlank String title,
            String description,
            LocalDate dueDate,
            TaskPriority priority,
            Long assigneeId
    ) {}

    public record UpdateTaskRequest(
            String title,
            String description,
            LocalDate dueDate,
            TaskPriority priority,
            TaskStatus status,
            Long assigneeId
    ) {}

    public record UpdateTaskStatusRequest(
            TaskStatus status
    ) {}

    public record AssignTaskRequest(
            Long assigneeId
    ) {}

    public record TaskResponse(
            Long id,
            String title,
            String description,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueDate,
            Long projectId,
            Long assigneeId,
            String assigneeEmail
    ) {}
}
