package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.TaskPriority;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
    Page<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);

    @Query("""
            select t from Task t
            where t.project.id = :projectId
              and (:status is null or t.status = :status)
              and (:priority is null or t.priority = :priority)
              and (:dueBefore is null or t.dueDate <= :dueBefore)
            """)
    Page<Task> findByFilters(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("dueBefore") LocalDate dueBefore,
            Pageable pageable);

    java.util.Optional<Task> findByIdAndProjectId(Long id, Long projectId);
}
