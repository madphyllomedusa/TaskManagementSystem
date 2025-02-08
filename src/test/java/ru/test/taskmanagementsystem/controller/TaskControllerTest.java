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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.test.taskmanagementsystem.config.JwtService;
import ru.test.taskmanagementsystem.config.SecurityConfig;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.expectionhandler.CustomAccessDeniedHandler;
import ru.test.taskmanagementsystem.expectionhandler.NotFoundException;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskRequest;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.task.TaskResponse;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;
import ru.test.taskmanagementsystem.service.TaskService;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import({SecurityConfig.class, CustomAccessDeniedHandler.class})
@AutoConfigureMockMvc
@WithMockUser
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtService jwtService;

    @ParameterizedTest
    @MethodSource("provideAddTaskTestData")
    @WithMockUser(roles = "ADMIN")
    void testAddTask(TaskRequest taskRequest, TaskResponse expectedResponse, int expectedStatus) throws Exception {
        if (expectedStatus == 201) {
            Mockito.when(taskService.addTask(Mockito.any(TaskRequest.class))).thenReturn(expectedResponse);
        } else {
            Mockito.doThrow(new BadRequestException("Invalid data"))
                   .when(taskService).addTask(Mockito.any(TaskRequest.class));
        }
        var resultActions = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskRequest)))
                .andExpect(status().is(expectedStatus));
        if (expectedStatus == 201) {
            resultActions.andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                         .andExpect(jsonPath("$.title").value(expectedResponse.getTitle()));
        }
    }

    static Stream<Arguments> provideAddTaskTestData() {
        return Stream.of(
                Arguments.of(
                        new TaskRequest("Task1", "Description1", "PENDING", "HIGH", "3d", null),
                        new TaskResponse(
                                1L,
                                "Task1",
                                "Description1",
                                "PENDING",
                                "HIGH",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                null,
                                OffsetDateTime.parse("2025-02-11T12:00:00+03:00"),
                                "admin",
                                null,
                                null
                        ),
                        201
                ),
                Arguments.of(
                        new TaskRequest("", "Description", "PENDING", "HIGH", "3d",null),
                        null,
                        400
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideUpdateTaskTestData")
    @WithMockUser(roles = "ADMIN")
    void testUpdateTask(Long id, TaskUpdateRequest updateRequest, TaskResponse expectedResponse, int expectedStatus) throws Exception {
        if (expectedStatus == 200) {
            Mockito.when(taskService.updateTask(Mockito.eq(id), Mockito.any(TaskUpdateRequest.class))).thenReturn(expectedResponse);
        } else {
            Mockito.doThrow(new BadRequestException("Invalid data"))
                   .when(taskService).updateTask(Mockito.eq(id), Mockito.any(TaskUpdateRequest.class));
        }
        var resultActions = mockMvc.perform(put("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest)))
                .andExpect(status().is(expectedStatus));
        if (expectedStatus == 200) {
            resultActions.andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                         .andExpect(jsonPath("$.title").value(expectedResponse.getTitle()));
        }
    }

    static Stream<Arguments> provideUpdateTaskTestData() {
        return Stream.of(
                Arguments.of(
                        1L,
                        new TaskUpdateRequest("Updated Task", "Updated description", "IN_PROGRESS", "MEDIUM", "2d", "assigneeUsername", "admin"),
                        new TaskResponse(
                                1L,
                                "Updated Task",
                                "Updated description",
                                "IN_PROGRESS",
                                "MEDIUM",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T13:00:00+03:00"),
                                null,
                                OffsetDateTime.parse("2025-02-10T12:00:00+03:00"),
                                "admin",
                                "assigneeUsername",
                                null
                        ),
                        200
                ),
                Arguments.of(
                        2L,
                        new TaskUpdateRequest("", "Desc", "IN_PROGRESS", "MEDIUM", "2d", "assigneeUsername", null),
                        null,
                        400
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetTaskByIdTestData")
    void testGetTaskById(Long id, TaskResponse expectedResponse, int expectedStatus) throws Exception {
        if (expectedStatus == 200) {
            Mockito.when(taskService.getTaskResponseById(id)).thenReturn(expectedResponse);
        } else {
            Mockito.doThrow(new NotFoundException("Not found"))
                   .when(taskService).getTaskResponseById(id);
        }
        var resultActions = mockMvc.perform(get("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus));
        if (expectedStatus == 200) {
            resultActions.andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                         .andExpect(jsonPath("$.title").value(expectedResponse.getTitle()));
        }
    }

    static Stream<Arguments> provideGetTaskByIdTestData() {
        return Stream.of(
                Arguments.of(
                        1L,
                        new TaskResponse(
                                1L,
                                "Task1",
                                "Description1",
                                "PENDING",
                                "HIGH",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                null,
                                OffsetDateTime.parse("2025-02-11T12:00:00+03:00"),
                                "admin",
                                null,
                                null
                        ),
                        200
                ),
                Arguments.of(99L, null, 404)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDeleteTaskTestData")
    @WithMockUser(roles = "ADMIN")
    void testDeleteTask(Long id, int expectedStatus) throws Exception {
        if (expectedStatus == 204) {
            Mockito.doNothing().when(taskService).deleteTaskById(id);
        } else {
            Mockito.doThrow(new NotFoundException("Not found"))
                   .when(taskService).deleteTaskById(id);
        }
        mockMvc.perform(delete("/tasks/{id}", id))
                .andExpect(status().is(expectedStatus));
    }

    static Stream<Arguments> provideDeleteTaskTestData() {
        return Stream.of(
                Arguments.of(1L, 204),
                Arguments.of(99L, 404)
        );
    }

    @ParameterizedTest
    @MethodSource("provideFilterTasksTestData")
    void testFilterTasks(String author, String assignee, Priority priority, Status status, Long id, int page, int size, long totalElements, int expectedStatus) throws Exception {
        var pageable = PageRequest.of(page, size);
        var taskResponse = new TaskResponse(
                1L,
                "Task1",
                "Description1",
                "PENDING",
                "HIGH",
                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                null,
                OffsetDateTime.parse("2025-02-11T12:00:00+03:00"),
                "admin",
                null,
                null
        );
        var pageResponse = new PageImpl<>(List.of(taskResponse), pageable, totalElements);
        Mockito.when(taskService.filterTasks(author, assignee, priority, status, id, page, size)).thenReturn(pageResponse);

        var resultActions = mockMvc.perform(get("/tasks")
                        .param("author", author)
                        .param("assignee", assignee)
                        .param("priority", priority.name())
                        .param("status", status.name())
                        .param("id", id == null ? "" : id.toString())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus));

        resultActions.andExpect(jsonPath("$.content[0].id").value(1L))
                     .andExpect(jsonPath("$.content[0].title").value("Task1"));
    }

    static Stream<Arguments> provideFilterTasksTestData() {
        return Stream.of(
                Arguments.of("admin", "", Priority.HIGH, Status.PENDING, null, 0, 10, 1L, 200)
        );
    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }
}
