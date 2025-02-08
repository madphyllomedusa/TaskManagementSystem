package ru.test.taskmanagementsystem.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.test.taskmanagementsystem.config.JwtService;
import ru.test.taskmanagementsystem.config.SecurityConfig;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.expectionhandler.CustomAccessDeniedHandler;
import ru.test.taskmanagementsystem.expectionhandler.NotFoundException;
import ru.test.taskmanagementsystem.model.dto.request.comment.CommentRequest;
import ru.test.taskmanagementsystem.model.dto.request.comment.CommentUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.comment.CommentResponse;
import ru.test.taskmanagementsystem.service.CommentService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import({SecurityConfig.class, CustomAccessDeniedHandler.class})
@AutoConfigureMockMvc
@WithMockUser
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CommentService commentService;

    @ParameterizedTest
    @MethodSource("provideAddCommentTestData")
    void testAddComment(Long taskId, CommentRequest commentRequest, CommentResponse expectedResponse, int expectedStatus) throws Exception {
        if (expectedStatus == 201) {
            Mockito.when(commentService.addComment(Mockito.eq(taskId), Mockito.any(CommentRequest.class)))
                    .thenReturn(expectedResponse);
        } else {
            Mockito.doThrow(new BadRequestException("Invalid data")).when(commentService)
                    .addComment(Mockito.eq(taskId), Mockito.any(CommentRequest.class));
        }

        var resultActions = mockMvc.perform(post("/tasks/{taskId}/comments", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(commentRequest)))
                .andExpect(status().is(expectedStatus));

        if (expectedStatus == 201) {
            resultActions.andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                    .andExpect(jsonPath("$.text").value(expectedResponse.getText()))
                    .andExpect(jsonPath("$.authorName").value(expectedResponse.getAuthorName()));
        }
    }

    static Stream<Arguments> provideAddCommentTestData() {
        return Stream.of(
                // Положительный тест: валидный комментарий
                Arguments.of(1L,
                        new CommentRequest("This is a comment"),
                        new CommentResponse(10L, "user1",
                                "This is a comment",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                1L
                        ),
                        201),
                // Негативный тест: пустой текст комментария
                Arguments.of(2L,
                        new CommentRequest(""),
                        null,
                        400)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUpdateCommentTestData")
    void testUpdateComment(Long commentId, CommentUpdateRequest updateRequest, CommentResponse expectedResponse, int expectedStatus) throws Exception {
        if (expectedStatus == 200) {
            Mockito.when(commentService.updateComment(Mockito.eq(commentId), Mockito.any(CommentUpdateRequest.class)))
                    .thenReturn(expectedResponse);
        } else {
            Mockito.doThrow(new BadRequestException("Invalid data")).when(commentService)
                    .updateComment(Mockito.eq(commentId), Mockito.any(CommentUpdateRequest.class));
        }
        var resultActions = mockMvc.perform(put("/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest)))
                .andExpect(status().is(expectedStatus));
        if (expectedStatus == 200) {
            resultActions.andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                    .andExpect(jsonPath("$.text").value(expectedResponse.getText()));
        }
    }

    static Stream<Arguments> provideUpdateCommentTestData() {
        return Stream.of(
                Arguments.of(10L,
                        new CommentUpdateRequest("Updated comment text"),
                        new CommentResponse(10L, "user1",
                                "Updated comment text",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:30:00+03:00"),
                                1L),
                        200),
                Arguments.of(20L,
                        new CommentUpdateRequest(""),
                        null,
                        400)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetCommentByIdTestData")
    void testGetCommentById(Long commentId, CommentResponse expectedResponse, int expectedStatus) throws Exception {
        if (expectedStatus == 200) {
            Mockito.when(commentService.getCommentResponseById(commentId)).thenReturn(expectedResponse);
        } else {
            Mockito.doThrow(new NotFoundException("Not found")).when(commentService).getCommentResponseById(commentId);
        }
        var resultActions = mockMvc.perform(get("/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus));
        if (expectedStatus == 200) {
            resultActions.andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                    .andExpect(jsonPath("$.text").value(expectedResponse.getText()));
        }
    }

    static Stream<Arguments> provideGetCommentByIdTestData() {
        return Stream.of(
                Arguments.of(10L,
                        new CommentResponse(10L, "user1",
                                "This is a comment",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                1L),
                        200),
                Arguments.of(99L, null, 404)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetAllCommentsTestData")
    void testGetAllComments(Long taskId, List<CommentResponse> expectedResponses, int expectedStatus) throws Exception {
        if (expectedStatus == 200) {
            Mockito.when(commentService.getCommentsByTask(taskId)).thenReturn(expectedResponses);
        } else {
            Mockito.doThrow(new NotFoundException("Not found")).when(commentService).getCommentsByTask(taskId);
        }
        var resultActions = mockMvc.perform(get("/tasks/{taskId}/comments", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus));
        if (expectedStatus == 200 && !expectedResponses.isEmpty()) {
            resultActions.andExpect(jsonPath("$[0].id").value(expectedResponses.get(0).getId()))
                    .andExpect(jsonPath("$[0].text").value(expectedResponses.get(0).getText()));
        }
    }

    static Stream<Arguments> provideGetAllCommentsTestData() {
        return Stream.of(
                Arguments.of(1L,
                        List.of(new CommentResponse(10L, "user1",
                                "Comment text",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                1L)),
                        200)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDeleteCommentTestData")
    void testDeleteCommentById(Long commentId, int expectedStatus) throws Exception {
        if (expectedStatus == 204) {
            Mockito.doNothing().when(commentService).deleteCommentById(commentId);
        } else {
            Mockito.doThrow(new NotFoundException("Not found")).when(commentService).deleteCommentById(commentId);
        }
        mockMvc.perform(delete("/comments/{commentId}", commentId))
                .andExpect(status().is(expectedStatus));
    }

    static Stream<Arguments> provideDeleteCommentTestData() {
        return Stream.of(
                Arguments.of(10L, 204),
                Arguments.of(99L, 404)
        );
    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }
}
