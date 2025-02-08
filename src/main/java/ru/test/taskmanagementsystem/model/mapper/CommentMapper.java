package ru.test.taskmanagementsystem.model.mapper;

import org.springframework.stereotype.Component;
import ru.test.taskmanagementsystem.model.dto.request.comment.CommentRequest;
import ru.test.taskmanagementsystem.model.dto.request.comment.CommentUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.comment.CommentResponse;
import ru.test.taskmanagementsystem.model.entity.Comment;
import ru.test.taskmanagementsystem.model.entity.Task;
import ru.test.taskmanagementsystem.model.entity.User;

import java.time.OffsetDateTime;

/**
 * Маппер для преобразования объектов Comment.
 */
@Component
public class CommentMapper {

    /**
     * Конвертирует DTO-запрос в сущность комментария.
     *
     * @param request DTO-запрос с текстом комментария.
     * @param user    Пользователь, оставивший комментарий.
     * @param task    Задача, к которой относится комментарий.
     * @return Объект Comment.
     */
    public Comment toCommentEntity(CommentRequest request, User user, Task task) {
        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setUser(user);
        comment.setTask(task);
        return comment;
    }

    /**
     * Обновляет текст комментария из DTO-запроса.
     *
     * @param request DTO-запрос с обновленным текстом.
     * @param comment Объект комментария, который нужно обновить.
     */
    public void updateCommentFromRequest(CommentUpdateRequest request, Comment comment) {
        if (request.getText() != null) {
            comment.setText(request.getText());
        }
        comment.setUpdatedAt(OffsetDateTime.now());
    }

    /**
     * Конвертирует сущность комментария в DTO-ответ.
     *
     * @param comment Сущность комментария.
     * @return DTO-ответ CommentResponse.
     */
    public CommentResponse toCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setText(comment.getText());
        response.setAuthorName(comment.getUser().getUsername());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        response.setTaskId(comment.getTask() != null ? comment.getTask().getId() : null);
        return response;
    }
}
