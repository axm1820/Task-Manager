package com.example.taskmanager.dto;

import com.example.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class TaskDtos {

    public record CreateTaskRequest(
            @NotBlank String title,
            String description,
            LocalDate dueDate,
            Long assigneeId
    ) {}

    public record UpdateTaskStatusRequest(
            TaskStatus status
    ) {}

    public record TaskResponse(
            Long id,
            String title,
            String description,
            TaskStatus status,
            LocalDate dueDate,
            Long projectId,
            String assigneeEmail
    ) {}
}
