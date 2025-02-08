package ru.test.taskmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.test.taskmanagementsystem.model.dto.request.comment.CommentRequest;
import ru.test.taskmanagementsystem.model.dto.request.comment.CommentUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.comment.CommentResponse;
import ru.test.taskmanagementsystem.service.CommentService;

import java.util.List;

/**
 * Контроллер для управления комментариями к задачам.
 */
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Добавляет комментарий к задаче.
     *
     * @param taskId         идентификатор задачи
     * @param commentRequest данные комментария
     * @return созданный комментарий
     */
    @PostMapping("/tasks/{taskId}/comments")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long taskId,
                                                      @Valid @RequestBody CommentRequest commentRequest) {
        CommentResponse newComment = commentService.addComment(taskId, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }

    /**
     * Обновляет комментарий.
     *
     * @param commentId            идентификатор комментария
     * @param commentUpdateRequest обновленные данные комментария
     * @return обновленный комментарий
     */
    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
        CommentResponse updated = commentService.updateComment(commentId, commentUpdateRequest);
        return ResponseEntity.ok(updated);
    }

    /**
     * Получает комментарий по идентификатору.
     *
     * @param commentId идентификатор комментария
     * @return найденный комментарий
     */
    @GetMapping("/comments/{commentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long commentId) {
        CommentResponse comment = commentService.getCommentResponseById(commentId);
        return ResponseEntity.ok(comment);
    }

    /**
     * Получает список комментариев для конкретной задачи.
     *
     * @param taskId идентификатор задачи
     * @return список комментариев
     */
    @GetMapping("/tasks/{taskId}/comments")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<List<CommentResponse>> getAllComments(@PathVariable Long taskId) {
        List<CommentResponse> comments = commentService.getCommentsByTask(taskId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Удаляет комментарий по идентификатору.
     *
     * @param commentId идентификатор комментария
     * @return пустой ответ с кодом 204
     */
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> deleteCommentById(@PathVariable Long commentId) {
        commentService.deleteCommentById(commentId);
        return ResponseEntity.noContent().build();
    }
}
