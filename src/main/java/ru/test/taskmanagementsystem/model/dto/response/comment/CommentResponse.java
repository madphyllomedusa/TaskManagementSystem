package ru.test.taskmanagementsystem.model.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * DTO-ответ для комментария.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    /**
     * Уникальный идентификатор комментария.
     */
    private Long id;

    /**
     * Имя автора комментария.
     */
    private String authorName;

    /**
     * Текст комментария.
     */
    private String text;

    /**
     * Дата и время создания комментария.
     */
    private OffsetDateTime createdAt;

    /**
     * Дата и время последнего обновления комментария.
     */
    private OffsetDateTime updatedAt;

    /**
     * ID задачи, к которой относится комментарий.
     */
    private Long taskId;
}

