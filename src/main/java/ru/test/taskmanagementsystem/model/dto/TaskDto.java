package ru.test.taskmanagementsystem.model.dto;

import lombok.Data;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;

import java.util.List;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private String authorUsername;
    private String assigneeUsername;
    private List<CommentDto> comments;
}