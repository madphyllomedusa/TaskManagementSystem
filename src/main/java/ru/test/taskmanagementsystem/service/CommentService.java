package ru.test.taskmanagementsystem.service;

import ru.test.taskmanagementsystem.model.dto.request.comment.CommentRequest;
import ru.test.taskmanagementsystem.model.dto.request.comment.CommentUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.comment.CommentResponse;

import java.util.List;

/**
 * Сервис для управления комментариями к задачам.
 */
public interface CommentService {

    /**
     * Добавляет новый комментарий к задаче.
     *
     * @param taskId идентификатор задачи, к которой добавляется комментарий
     * @param commentRequest объект с текстом комментария
     * @return объект {@link CommentResponse}, содержащий данные созданного комментария
     */
    CommentResponse addComment(Long taskId, CommentRequest commentRequest);

    /**
     * Обновляет существующий комментарий.
     *
     * @param commentId идентификатор комментария
     * @param commentUpdateRequest объект с обновленными данными комментария
     * @return объект {@link CommentResponse}, содержащий обновленный комментарий
     */
    CommentResponse updateComment(Long commentId, CommentUpdateRequest commentUpdateRequest);

    /**
     * Получает список комментариев, относящихся к конкретной задаче.
     *
     * @param taskId идентификатор задачи
     * @return список объектов {@link CommentResponse}, содержащих комментарии к задаче
     */
    List<CommentResponse> getCommentsByTask(Long taskId);

    /**
     * Получает комментарий по его идентификатору.
     *
     * @param commentId идентификатор комментария
     * @return объект {@link CommentResponse}, содержащий данные комментария
     */
    CommentResponse getCommentResponseById(Long commentId);

    /**
     * Удаляет комментарий по его идентификатору.
     *
     * @param commentId идентификатор комментария для удаления
     */
    void deleteCommentById(Long commentId);
}
