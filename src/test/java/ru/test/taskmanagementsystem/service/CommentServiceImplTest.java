package ru.test.taskmanagementsystem.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.test.taskmanagementsystem.model.dto.request.comment.CommentRequest;
import ru.test.taskmanagementsystem.model.dto.request.comment.CommentUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.comment.CommentResponse;
import ru.test.taskmanagementsystem.model.entity.Comment;
import ru.test.taskmanagementsystem.model.entity.Task;
import ru.test.taskmanagementsystem.model.entity.User;
import ru.test.taskmanagementsystem.model.mapper.CommentMapper;
import ru.test.taskmanagementsystem.repository.CommentRepository;
import ru.test.taskmanagementsystem.service.impl.CommentServiceImpl;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private TaskService taskService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private CommentServiceImpl commentService;

    @ParameterizedTest
    @MethodSource("provideAddCommentData")
    void testAddComment(Long taskId, CommentRequest commentRequest, CommentResponse expectedResponse) {
        Task task = new Task();
        task.setId(taskId);
        task.setComments(new ArrayList<>());

        User taskAuthor = new User();
        taskAuthor.setId(100L);
        taskAuthor.setRole(ru.test.taskmanagementsystem.model.enums.Role.ROLE_USER);
        task.setAuthor(taskAuthor);

        User currentUser = new User();
        currentUser.setId(100L);
        currentUser.setRole(ru.test.taskmanagementsystem.model.enums.Role.ROLE_USER);

        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        Comment commentEntity = new Comment();
        commentEntity.setId(10L);

        when(commentMapper.toCommentEntity(commentRequest, currentUser, task)).thenReturn(commentEntity);
        when(commentRepository.save(commentEntity)).thenReturn(commentEntity);
        when(commentMapper.toCommentResponse(commentEntity)).thenReturn(expectedResponse);

        CommentResponse actual = commentService.addComment(taskId, commentRequest);

        assertEquals(expectedResponse, actual);
    }

    static Stream<Arguments> provideAddCommentData() {
        return Stream.of(
                Arguments.of(
                        1L,
                        new CommentRequest("This is a comment"),
                        new CommentResponse(
                                10L,
                                "user1",
                                "This is a comment",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                1L
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideUpdateCommentData")
    void testUpdateComment(Long commentId, CommentUpdateRequest updateRequest, CommentResponse expectedResponse) {
        Comment commentEntity = new Comment();
        commentEntity.setId(commentId);

        User currentUser = new User();
        currentUser.setId(100L);
        currentUser.setRole(ru.test.taskmanagementsystem.model.enums.Role.ROLE_USER);
        currentUser.setUsername("user1");
        commentEntity.setUser(currentUser);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentEntity));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        doNothing().when(commentMapper).updateCommentFromRequest(eq(updateRequest), eq(commentEntity));
        when(commentRepository.save(commentEntity)).thenReturn(commentEntity);
        when(commentMapper.toCommentResponse(commentEntity)).thenReturn(expectedResponse);

        CommentResponse actual = commentService.updateComment(commentId, updateRequest);

        assertEquals(expectedResponse, actual);
    }


    static Stream<Arguments> provideUpdateCommentData() {
        return Stream.of(
                Arguments.of(
                        10L,
                        new CommentUpdateRequest("Updated comment text"),
                        new CommentResponse(
                                10L,
                                "user1",
                                "Updated comment text",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:30:00+03:00"),
                                1L
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetCommentByIdData")
    void testGetCommentById(Long commentId, CommentResponse expectedResponse) {
        Comment commentEntity = new Comment();
        commentEntity.setId(commentId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentEntity));
        when(commentMapper.toCommentResponse(commentEntity)).thenReturn(expectedResponse);

        CommentResponse actual = commentService.getCommentResponseById(commentId);

        assertEquals(expectedResponse, actual);
    }

    static Stream<Arguments> provideGetCommentByIdData() {
        return Stream.of(
                Arguments.of(
                        10L,
                        new CommentResponse(
                                10L,
                                "user1",
                                "This is a comment",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                1L
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideDeleteCommentData")
    void testDeleteCommentById(Long commentId) {
        Comment commentEntity = new Comment();
        commentEntity.setId(commentId);

        User currentUser = new User();
        currentUser.setId(100L);
        currentUser.setRole(ru.test.taskmanagementsystem.model.enums.Role.ROLE_USER);
        commentEntity.setUser(currentUser);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentEntity));
        when(userService.getCurrentUser()).thenReturn(currentUser);

        commentService.deleteCommentById(commentId);

        assertNotNull(commentEntity.getDeletedAt());
        verify(commentRepository).save(commentEntity);
    }

    static Stream<Arguments> provideDeleteCommentData() {
        return Stream.of(
                Arguments.of(10L)
        );
    }
}
