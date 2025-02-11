package ru.test.taskmanagementsystem.model.dto.request.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO-запрос для обновления задачи.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateRequest {
    /**
     * Заголовок задачи (3-100 символов).
     */
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    /**
     * Описание задачи (10-500 символов).
     */
    @NotBlank(message = "Description cannot be empty")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    /**
     * Статус задачи (PENDING, IN_PROGRESS, COMPLETED).
     */
    @NotBlank(message = "Status cannot be empty")
    @Pattern(regexp = "PENDING|IN_PROGRESS|COMPLETED",
            message = "Status must be PENDING, IN_PROGRESS, or COMPLETED")
    private String status;

    /**
     * Приоритет задачи (LOW, MEDIUM, HIGH).
     */
    @NotBlank(message = "Priority cannot be empty")
    @Pattern(regexp = "LOW|MEDIUM|HIGH",
            message = "Priority must be LOW, MEDIUM, or HIGH")
    private String priority;

    /**
     * Дедлайн задачи (необязательно).
     */
    private String deadline;

    /**
     * Имя пользователя, которому назначена задача.
     */
    @NotBlank(message = "Assignee username cannot be empty")
    private String assigneeUsername;

    /**
     * Имя автора задачи.
     */
    @NotBlank(message = "Author username cannot be empty")
    private String authorUsername;
}
