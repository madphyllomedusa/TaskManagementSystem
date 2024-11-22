package ru.test.taskmanagementsystem.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import ru.test.taskmanagementsystem.config.JwtService;
import ru.test.taskmanagementsystem.config.SecurityConfig;
import ru.test.taskmanagementsystem.expectionhandler.NotFoundException;
import ru.test.taskmanagementsystem.model.dto.TaskDto;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;
import ru.test.taskmanagementsystem.service.TaskService;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtService jwtService;

    private final String defaultRole = "ROLE_USER";
    private final String defaultEmail = "user@example.com";

    @BeforeEach
    void setupMocks() {
        setupAuthentication(defaultRole, defaultEmail);

        // Задача для администратора
        TaskDto taskWithAdmin = new TaskDto();
        taskWithAdmin.setId(1L);
        taskWithAdmin.setTitle("Task for Admin");
        taskWithAdmin.setPriority(Priority.HIGH);
        taskWithAdmin.setStatus(Status.IN_PROGRESS);
        taskWithAdmin.setAssigneeUsername("admin@example.com");

        // Задача для пользователя
        TaskDto taskForUser = new TaskDto();
        taskForUser.setId(2L);
        taskForUser.setTitle("Task for User");
        taskForUser.setPriority(Priority.MEDIUM);
        taskForUser.setStatus(Status.PENDING);
        taskForUser.setAssigneeUsername("user@example.com");

        // Моки для методов сервиса
        Mockito.when(taskService.getTaskById(1L)).thenReturn(taskWithAdmin);
        Mockito.when(taskService.getTaskById(2L)).thenReturn(taskForUser);
        Mockito.when(taskService.getTaskById(Mockito.longThat(id -> id != 1L && id != 2L)))
                .thenThrow(new NotFoundException("Task not found"));

        Mockito.when(taskService.addTask(Mockito.any(TaskDto.class))).thenAnswer(invocation -> {
            TaskDto request = invocation.getArgument(0);
            request.setId(3L);
            return request;
        });

        Mockito.when(taskService.changeStatus(Mockito.eq(1L), Mockito.eq(Status.COMPLETED)))
                .thenAnswer(invocation -> {
                    TaskDto task = new TaskDto();
                    task.setId(1L);
                    task.setTitle("Task for Admin");
                    task.setStatus(Status.COMPLETED);
                    task.setPriority(Priority.HIGH);
                    return task;
                });

        Mockito.when(taskService.changeStatus(Mockito.eq(2L), Mockito.eq(Status.COMPLETED)))
                .thenAnswer(invocation -> {
                    TaskDto task = new TaskDto();
                    task.setId(2L);
                    task.setTitle("Task for User");
                    task.setStatus(Status.COMPLETED);
                    task.setPriority(Priority.MEDIUM);
                    task.setAssigneeUsername("user@example.com");
                    return task;
                });

        Mockito.when(taskService.changePriority(1L, Priority.LOW)).thenAnswer(invocation -> {
            TaskDto task = new TaskDto();
            task.setId(1L);
            task.setTitle("Task for Admin");
            task.setPriority(Priority.LOW);
            task.setStatus(Status.IN_PROGRESS);
            return task;
        });

        Mockito.doNothing().when(taskService).deleteTaskById(1L);
        Mockito.doThrow(new NotFoundException("Task not found")).when(taskService).deleteTaskById(100L);

        Mockito.when(taskService.assignTask(1L, 2L)).thenReturn(taskWithAdmin);
    }

    @ParameterizedTest
    @MethodSource("provideCreateTaskTestData")
    void testCreateTask(String role, String email, TaskDto request, int expectedStatus, String jsonPath, Object expectedValue) throws Exception {
        setupAuthentication(role, email);

        var resultActions = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().is(expectedStatus));

        if (jsonPath != null) {
            resultActions.andExpect(jsonPath(jsonPath, is(expectedValue)));
        }
    }

    @ParameterizedTest
    @MethodSource("provideGetTaskTestData")
    void testGetTask(String role, String email, Long taskId, int expectedStatus, String jsonPath, Object expectedValue) throws Exception {
        setupAuthentication(role, email);

        var resultActions = mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().is(expectedStatus));

        if (jsonPath != null) {
            resultActions.andExpect(jsonPath(jsonPath, is(expectedValue)));
        }
    }

    @ParameterizedTest
    @MethodSource("provideDeleteTaskTestData")
    void testDeleteTask(String role, String email, Long taskId, int expectedStatus) throws Exception {
        setupAuthentication(role, email);

        mockMvc.perform(delete("/tasks/" + taskId))
                .andExpect(status().is(expectedStatus));
    }

    @ParameterizedTest
    @MethodSource("provideUpdateTaskStatusTestData")
    void testUpdateTaskStatus(String role, String email, Long taskId, Status newStatus, int expectedStatus, String jsonPath, Object expectedValue) throws Exception {
        setupAuthentication(role, email);


        var resultActions = mockMvc.perform(put("/tasks/" + taskId + "/status?status=" + newStatus))
                .andExpect(status().is(expectedStatus));

        if (jsonPath != null && expectedStatus == 200) {
            resultActions.andExpect(jsonPath(jsonPath, is(expectedValue)));
        }
    }

    @ParameterizedTest
    @MethodSource("provideAssignTaskTestData")
    void testAssignTask(String role, String email, Long taskId, Long assigneeId, int expectedStatus, String jsonPath, Object expectedValue) throws Exception {
        setupAuthentication(role, email);

        var resultActions = mockMvc.perform(put("/tasks/" + taskId + "/assignee?assigneeId=" + assigneeId))
                .andExpect(status().is(expectedStatus));

        if (jsonPath != null) {
            resultActions.andExpect(jsonPath(jsonPath, is(expectedValue)));
        }
    }

    private void setupAuthentication(String role, String email) {
        System.out.println("Setting up authentication for: " + email + " with role: " + role);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority(role)))
        );
    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    static Stream<Arguments> provideCreateTaskTestData() {
        TaskDto validRequest = new TaskDto();
        validRequest.setTitle("Valid Task");
        validRequest.setPriority(Priority.MEDIUM);
        validRequest.setStatus(Status.PENDING);

        TaskDto invalidRequest = new TaskDto();

        return Stream.of(
                Arguments.of("ROLE_ADMIN", "admin@example.com", validRequest, 201, "$.title", "Valid Task"),
                Arguments.of("ROLE_USER", "user@example.com", validRequest, 403, null, null),
                Arguments.of("ROLE_ADMIN", "admin@example.com", invalidRequest, 400, null, null)
        );
    }

    static Stream<Arguments> provideGetTaskTestData() {
        return Stream.of(
                Arguments.of("ROLE_USER", "user@example.com", 1L, 200, "$.id", 1),
                Arguments.of("ROLE_USER", "user@example.com", 100L, 404, null, null),
                Arguments.of("ROLE_ADMIN", "admin@example.com", 1L, 200, "$.status", "IN_PROGRESS")
        );
    }

    static Stream<Arguments> provideUpdateTaskStatusTestData() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", "admin@example.com", 1L, Status.COMPLETED, 200, "$.status", "COMPLETED"),
                Arguments.of("ROLE_USER", "user@example.com", 2L, Status.COMPLETED, 200, "$.status", "COMPLETED")
        );
    }

    static Stream<Arguments> provideDeleteTaskTestData() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", "admin@example.com", 1L, 204),
                Arguments.of("ROLE_USER", "user@example.com", 2L, 403),
                Arguments.of("ROLE_ADMIN", "admin@example.com", 100L, 404)
        );
    }

    static Stream<Arguments> provideAssignTaskTestData() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", "admin@example.com", 1L, 2L, 200, "$.id", 1),
                Arguments.of("ROLE_USER", "user@example.com", 1L, 2L, 403, null, null)
        );
    }
}
