package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskDtos.CreateTaskRequest;
import com.example.taskmanager.dto.TaskDtos.AssignTaskRequest;
import com.example.taskmanager.dto.TaskDtos.TaskResponse;
import com.example.taskmanager.dto.TaskDtos.UpdateTaskRequest;
import com.example.taskmanager.dto.TaskDtos.UpdateTaskStatusRequest;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    public TaskResponse createTask(Long projectId, CreateTaskRequest request, User requester) {
        Project project = projectService.findOwnedProject(projectId, requester);

        User assignee = null;
        if (request.assigneeId() != null) {
            assignee = findAssignee(request.assigneeId());
        }

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .priority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM)
                .project(project)
                .assignee(assignee)
                .build();

        return toResponse(taskRepository.save(task));
    }

    public Page<TaskResponse> listTasks(
            Long projectId,
            User requester,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueBefore,
            Pageable pageable) {
        projectService.findOwnedProject(projectId, requester); // enforces access
        return taskRepository.findByFilters(projectId, status, priority, dueBefore, pageable).map(this::toResponse);
    }

    public TaskResponse updateStatus(Long projectId, Long taskId, UpdateTaskStatusRequest request, User requester) {
        projectService.findOwnedProject(projectId, requester); // enforces access

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        task.setStatus(request.status());
        return toResponse(taskRepository.save(task));
    }

    public TaskResponse updateTask(
            Long projectId,
            Long taskId,
            UpdateTaskRequest request,
            User requester) {
        projectService.findOwnedProject(projectId, requester); // enforces access

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.assigneeId() != null) {
            task.setAssignee(findAssignee(request.assigneeId()));
        }

        return toResponse(taskRepository.save(task));
    }

    public TaskResponse assignTask(
            Long projectId,
            Long taskId,
            AssignTaskRequest request,
            User requester) {
        projectService.findOwnedProject(projectId, requester); // enforces access

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        task.setAssignee(request.assigneeId() == null
                ? null
                : findAssignee(request.assigneeId()));

        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long projectId, Long taskId, User requester) {
        projectService.findOwnedProject(projectId, requester); // enforces access

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        taskRepository.delete(task);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getAssignee() != null ? task.getAssignee().getEmail() : null
        );
    }

    private User findAssignee(Long assigneeId) {
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assignee not found with id: " + assigneeId));
    }
}
