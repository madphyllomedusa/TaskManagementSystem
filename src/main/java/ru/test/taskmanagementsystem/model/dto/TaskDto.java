package ru.test.taskmanagementsystem.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;

import java.util.List;

@Data
public class TaskDto {
    private Long id;
    @NotBlank(message = "Название задачи не должно быть пустым")
    private String title;

    private String description;
    @NotNull(message = "Статус задачи обязателен")
    private Status status;
    @NotNull(message = "Приоритет задачи обязателен")
    private Priority priority;
    private String authorUsername;
    private String assigneeUsername;
    private List<CommentDto> comments;

    public TaskDto() {
        this.status = Status.PENDING;
        this.priority = Priority.MEDIUM;
    }
}
