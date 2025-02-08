package ru.test.taskmanagementsystem.model.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO-запрос для обновления комментария.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateRequest {
    /**
     * Новый текст комментария.
     * Должен содержать от 3 до 300 символов.
     */
    @NotBlank
    @Size(min = 3, max = 300, message = "Comment must be between 3 and 300 characters")
    private String text;
}
