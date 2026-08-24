package com.example.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public class ProjectDtos {

    public record CreateProjectRequest(
            @NotBlank String name,
            String description
    ) {}

    public record ProjectResponse(
            Long id,
            String name,
            String description,
            String ownerEmail,
            Instant createdAt
    ) {}
}
