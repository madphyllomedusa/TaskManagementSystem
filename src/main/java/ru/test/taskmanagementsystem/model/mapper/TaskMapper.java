package ru.test.taskmanagementsystem.model.mapper;

import org.springframework.stereotype.Component;
import ru.test.taskmanagementsystem.model.dto.CommentDto;
import ru.test.taskmanagementsystem.model.dto.TaskDto;
import ru.test.taskmanagementsystem.model.entity.Comment;
import ru.test.taskmanagementsystem.model.entity.Task;

@Component
public class TaskMapper {
    public TaskDto toDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setPriority(task.getPriority());
        taskDto.setStatus(task.getStatus());
        taskDto.setAssigneeUsername(task.getAssignee().getUsername());
        taskDto.setAuthorUsername(task.getAuthor().getUsername());
        taskDto.setComments(task.getComments().stream()
                .map(this::toCommentDto).toList());
        return taskDto;
    }

    public Task toEntity(TaskDto taskDto) {
        Task task = new Task();
        task.setId(taskDto.getId());
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setPriority(taskDto.getPriority());
        task.setStatus(taskDto.getStatus());
        return task;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setUsername(comment.getUser().getUsername());
        return commentDto;
    }
}
