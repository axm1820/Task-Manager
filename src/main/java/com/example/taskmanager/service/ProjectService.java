package com.example.taskmanager.service;

import com.example.taskmanager.dto.ProjectDtos.CreateProjectRequest;
import com.example.taskmanager.dto.ProjectDtos.ProjectResponse;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.ForbiddenOperationException;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectResponse createProject(CreateProjectRequest request, User owner) {
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .owner(owner)
                .build();

        return toResponse(projectRepository.save(project));
    }

    public Page<ProjectResponse> listMyProjects(User owner, Pageable pageable) {
        return projectRepository.findByOwnerId(owner.getId(), pageable).map(this::toResponse);
    }

    public ProjectResponse getProject(Long projectId, User requester) {
        Project project = findOwnedProject(projectId, requester);
        return toResponse(project);
    }

    public void deleteProject(Long projectId, User requester) {
        Project project = findOwnedProject(projectId, requester);
        projectRepository.delete(project);
    }

    /**
     * Fetches a project and enforces that the requester owns it, unless they're an admin.
     * This is the kind of authorization check that's easy to forget and important to test.
     */
    public Project findOwnedProject(Long projectId, User requester) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        boolean isOwner = project.getOwner().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You do not have access to this project");
        }

        return project;
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getEmail(),
                project.getCreatedAt()
        );
    }
}
