package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskDtos.CreateTaskRequest;
import com.example.taskmanager.dto.TaskDtos.AssignTaskRequest;
import com.example.taskmanager.dto.TaskDtos.TaskResponse;
import com.example.taskmanager.dto.TaskDtos.UpdateTaskRequest;
import com.example.taskmanager.dto.TaskDtos.UpdateTaskStatusRequest;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(projectId, request, currentUser));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> listTasks(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(
                    iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate dueBefore,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.listTasks(
                projectId, currentUser, status, priority, dueBefore, pageable));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.updateTask(projectId, taskId, request, currentUser));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody UpdateTaskStatusRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.updateStatus(projectId, taskId, request, currentUser));
    }

    @PatchMapping("/{taskId}/assignee")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody AssignTaskRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.assignTask(
                projectId, taskId, request, currentUser));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser) {
        taskService.deleteTask(projectId, taskId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
