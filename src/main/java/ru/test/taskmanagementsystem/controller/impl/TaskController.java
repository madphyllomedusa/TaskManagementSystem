package ru.test.taskmanagementsystem.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import ru.test.taskmanagementsystem.controller.TaskControllerApi;
import ru.test.taskmanagementsystem.model.dto.CommentDto;
import ru.test.taskmanagementsystem.model.dto.TaskDto;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;
import ru.test.taskmanagementsystem.service.TaskService;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskControllerApi {

    private final TaskService taskService;

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> addTask(TaskDto taskDto) {
        TaskDto createdTask = taskService.addTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> updateTask(Long id, TaskDto taskDto) {
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<TaskDto> getTaskById(Long id) {
        TaskDto taskDto = taskService.getTaskById(id);
        return ResponseEntity.ok(taskDto);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTask(Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> updateTaskStatus(Long id, Status status) {
        TaskDto updatedTask = taskService.changeStatus(id, status);
        return ResponseEntity.ok(updatedTask);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> updateTaskPriority(Long id, Priority priority) {
        TaskDto updatedTask = taskService.changePriority(id, priority);
        return ResponseEntity.ok(updatedTask);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> updateAssignTask(Long id, Long assigneeId) {
        TaskDto updatedTask = taskService.assignTask(id, assigneeId);
        return ResponseEntity.ok(updatedTask);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CommentDto> addComment(Long id, String commentText) {
        CommentDto newComment = taskService.addComment(id, commentText);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<TaskDto>> filterTasks(String authorUsername, String assigneeUsername, Priority priority, Status status, int page, int size) {
        Page<TaskDto> tasks = taskService.filterTasks(authorUsername, assigneeUsername, priority, status, page, size);
        return ResponseEntity.ok(tasks);
    }
}
