package ru.test.taskmanagementsystem.service;

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
    CommentDto addComment(Long taskId, CommentDto commentDto);

}
