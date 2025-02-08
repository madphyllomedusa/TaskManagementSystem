package ru.test.taskmanagementsystem.model.dto.response.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.test.taskmanagementsystem.model.dto.response.comment.CommentResponse;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO-ответ для задачи.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    /**
     * Уникальный идентификатор задачи.
     */
    private Long id;

    /**
     * Заголовок задачи.
     */
    private String title;

    /**
     * Описание задачи.
     */
    private String description;

    /**
     * Текущий статус задачи.
     */
    private String status;

    /**
     * Приоритет задачи.
     */
    private String priority;

    /**
     * Дата и время создания задачи.
     */
    private OffsetDateTime createdAt;

    /**
     * Дата и время последнего обновления задачи.
     */
    private OffsetDateTime updatedAt;

    /**
     * Дата и время закрытия задачи.
     */
    private OffsetDateTime closedAt;

    /**
     * Дата и время дедлайна задачи.
     */
    private OffsetDateTime deadlineAt;

    /**
     * Имя пользователя, создавшего задачу.
     */
    private String authorUsername;

    /**
     * Имя пользователя, которому назначена задача.
     */
    private String assigneeUsername;

    /**
     * Список комментариев, относящихся к задаче.
     */
    private List<CommentResponse> comments;
}

