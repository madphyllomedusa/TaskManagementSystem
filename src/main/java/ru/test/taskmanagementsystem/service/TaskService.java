package ru.test.taskmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.test.taskmanagementsystem.model.dto.CommentDto;
import ru.test.taskmanagementsystem.model.dto.TaskDto;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;

public interface TaskService {
    TaskDto addTask(TaskDto taskDto);
    TaskDto updateTask(TaskDto taskDto);
    TaskDto getTaskById(Long id);
    void deleteTaskById(Long id);
    TaskDto changeStatus(Long id, Status status);
    TaskDto changePriority(Long id, Priority priority);
    TaskDto assignTask(Long taskId, Long assigneeId);
    CommentDto addComment(Long taskId, String commentText);
    Page<TaskDto> filterTasks(String authorUsername, String assigneeUsername,
                              Priority priority, Status status,
                              int page, int size);

}
