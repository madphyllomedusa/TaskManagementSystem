package ru.test.taskmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.test.taskmanagementsystem.model.dto.CommentDto;
import ru.test.taskmanagementsystem.model.dto.TaskDto;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;
import ru.test.taskmanagementsystem.service.TaskService;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> addTask(@RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.addTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        TaskDto taskDto = taskService.getTaskById(id);
        return ResponseEntity.ok(taskDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(@PathVariable Long id, @RequestParam Status status) {
        TaskDto updatedTask = taskService.changeStatus(id, status);
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/{id}/priority")
    public ResponseEntity<TaskDto> updateTaskPriority(@PathVariable Long id, @RequestParam Priority priority) {
        TaskDto updatedTask = taskService.changePriority(id, priority);
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/{id}/assignee")
    public ResponseEntity<TaskDto> updateAssignTask(@PathVariable Long id, @RequestParam Long assigneeId) {
        TaskDto updatedTask = taskService.assignTask(id, assigneeId);
        return ResponseEntity.ok(updatedTask);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long id, @RequestBody String commentText) {
        CommentDto newComment = taskService.addComment(id, commentText);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }

    @GetMapping
    public ResponseEntity<Page<TaskDto>> filterTasks(
            @RequestParam(required = false) String authorUsername,
            @RequestParam(required = false) String assigneeUsername,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TaskDto> tasks = taskService.filterTasks(authorUsername, assigneeUsername, priority, status, page, size);
        return ResponseEntity.ok(tasks);
    }

}

