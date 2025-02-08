package ru.test.taskmanagementsystem.model.dto.request.task;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO-запрос для обновления дедлайна задачи.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateDeadlineRequest {
    /**
     * Время продления дедлайна (например, "3h" или "2d").
     */
    @NotBlank(message = "Extension cannot be empty")
    private String extension;
}

