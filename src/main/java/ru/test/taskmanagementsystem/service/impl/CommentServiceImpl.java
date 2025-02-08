package ru.test.taskmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.test.taskmanagementsystem.expectionhandler.AccessDeniedException;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.expectionhandler.NotFoundException;
import ru.test.taskmanagementsystem.model.dto.request.comment.CommentRequest;
import ru.test.taskmanagementsystem.model.dto.request.comment.CommentUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.comment.CommentResponse;
import ru.test.taskmanagementsystem.model.entity.Comment;
import ru.test.taskmanagementsystem.model.entity.Task;
import ru.test.taskmanagementsystem.model.entity.User;
import ru.test.taskmanagementsystem.model.mapper.CommentMapper;
import ru.test.taskmanagementsystem.repository.CommentRepository;
import ru.test.taskmanagementsystem.service.CommentService;
import ru.test.taskmanagementsystem.service.TaskService;
import ru.test.taskmanagementsystem.service.UserService;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final TaskService taskService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @Override
    @Transactional
    public CommentResponse addComment(Long taskId, CommentRequest commentRequest) {
        logger.info("Starting addComment for task id: {}", taskId);
        validateId(taskId);

        Task task = taskService.getTaskById(taskId);
        User currentUser = userService.getCurrentUser();
        validateAddCommentPermission(task, currentUser);

        Comment comment = commentMapper.toCommentEntity(commentRequest, currentUser, task);
        task.getComments().add(comment);
        commentRepository.save(comment);

        logger.info("Finished addComment for task id: {} by user: {}", taskId, currentUser.getUsername());
        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest commentUpdateRequest) {
        validateId(commentId);
        logger.info("Starting updateComment for comment id: {}", commentId);

        Comment comment = getCommentById(commentId);

        User currentUser = userService.getCurrentUser();
        validateEditOrDeletePermission(comment, currentUser);

        commentMapper.updateCommentFromRequest(commentUpdateRequest, comment);
        commentRepository.save(comment);

        logger.info("Finished updateComment for comment id: {}", commentId);
        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTask(Long taskId) {
        validateId(taskId);
        logger.info("Getting comments for task id: {}", taskId);
        List<Comment> comments = commentRepository.findByTaskId(taskId);
        logger.debug("Found {} comments for task id: {}", comments.size(), taskId);
        return comments.stream()
                .map(commentMapper::toCommentResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentResponseById(Long commentId) {
        Comment comment = getCommentById(commentId);
        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional
    public void deleteCommentById(Long commentId) {
        logger.info("Deleting comment with id: {}", commentId);
        Comment comment = getCommentById(commentId);

        User currentUser = userService.getCurrentUser();
        validateEditOrDeletePermission(comment, currentUser);

        comment.setDeletedAt(OffsetDateTime.now());
        commentRepository.save(comment);
        logger.info("Comment with id: {} marked as deleted", commentId);
    }

    private Comment getCommentById(Long commentId) {
        validateId(commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.error("Comment with id {} not found for deletion", commentId);
                    return new NotFoundException("Комментарий не найден");
                });

    }

    private void validateId(Long id) {
        logger.debug("Validating id: {}", id);
        if (id == null || id < 1) {
            logger.error("Invalid id: {}", id);
            throw new BadRequestException("Неверный id " + id);
        }
        logger.debug("Finished validating id: {}", id);
    }

    private void validateAddCommentPermission(Task task, User user) {
        logger.debug("Validating permission to add comment for user id: {} on task id: {}", user.getId(), task.getId());
        boolean isAdmin = user.getRole().name().equals("ROLE_ADMIN");
        boolean isAuthor = task.getAuthor().getId().equals(user.getId());
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(user.getId());
        if (!isAdmin && !isAuthor && !isAssignee) {
            logger.error("User id: {} does not have permission to add comment to task id: {}", user.getId(), task.getId());
            throw new AccessDeniedException("Недостаточно прав для добавления комментария к задаче");
        }
    }

    private void validateEditOrDeletePermission(Comment comment, User user) {
        logger.debug("Validating permission for user id: {} to edit/delete comment id: {}", user.getId(), comment.getId());
        boolean isAdmin = user.getRole().name().equals("ROLE_ADMIN");
        boolean isCommentAuthor = comment.getUser().getId().equals(user.getId());
        if (!isAdmin && !isCommentAuthor) {
            logger.error("User id: {} does not have permission to edit/delete comment id: {}", user.getId(), comment.getId());
            throw new AccessDeniedException("У вас нет прав для редактирования/удаления этого комментария");
        }
    }
}
