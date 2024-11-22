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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtService jwtService;

    @BeforeEach
    void setupMocks() {
        // Настройка данных для тестов
        TaskDto task1 = new TaskDto();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setPriority(Priority.HIGH);
        task1.setStatus(Status.IN_PROGRESS);

        TaskDto task2 = new TaskDto();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setPriority(Priority.MEDIUM);
        task2.setStatus(Status.PENDING);

        // Настройка поведения для существующей задачи
        Mockito.when(taskService.getTaskById(1L)).thenReturn(task1);

        // Настройка поведения для отсутствующей задачи
        Mockito.when(taskService.getTaskById(Mockito.longThat(id -> id != 1L)))
                .thenThrow(new NotFoundException("Task not found"));

        // Настройка изменения статуса
        Mockito.when(taskService.changeStatus(1L, Status.IN_PROGRESS)).thenReturn(task1);

        // Настройка изменения приоритета
        Mockito.when(taskService.changePriority(1L, Priority.HIGH)).thenReturn(task1);

        // Настройка назначения задачи
        Mockito.when(taskService.assignTask(1L, 2L)).thenReturn(task1);

        // Настройка создания задачи
        Mockito.when(taskService.addTask(Mockito.any(TaskDto.class))).thenReturn(task1);

        // Настройка удаления задачи
        Mockito.doNothing().when(taskService).deleteTaskById(1L);
        Mockito.doThrow(new NotFoundException("Task not found with id: 302302032"))
                .when(taskService).deleteTaskById(302302032L);

        // Настройка фильтрации задач
        List<TaskDto> tasks = List.of(task1, task2);
        Page<TaskDto> page = new PageImpl<>(tasks);


        Mockito.when(taskService.filterTasks(
                Mockito.anyString(), Mockito.anyString(),
                Mockito.any(Priority.class), Mockito.any(Status.class),
                Mockito.anyInt(), Mockito.anyInt()
        )).thenReturn(page);
    }

    /**
     * Универсальный метод для тестирования эндпоинтов
     */
    @ParameterizedTest
    @MethodSource("provideEndpointTestData")
    void testEndpointCorrectness(
            String httpMethod,
            String uri,
            String role,
            Object request,
            Object response,
            int expectedStatus,
            String jsonPath,
            Object expectedValue) throws Exception {

        // Установка аутентификации
        setupAuthentication(role);

        // Настройка моков
        if (response != null) {
            Mockito.when(invokeServiceMethod(uri, request)).thenReturn(response);
        }

        // Построение HTTP-запроса
        var requestBuilder = switch (httpMethod) {
            case "POST" -> post(uri).contentType(MediaType.APPLICATION_JSON).content(asJsonString(request));
            case "PUT" -> put(uri).contentType(MediaType.APPLICATION_JSON).content(asJsonString(request));
            case "GET" -> get(uri);
            case "DELETE" -> delete(uri);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        };

        // Проверка результата
        var resultActions = mockMvc.perform(requestBuilder).andExpect(status().is(expectedStatus));

        // Проверка JSON-ответа
        if (jsonPath != null) {
            resultActions.andExpect(jsonPath(jsonPath, is(expectedValue)));
        }

        // Очистка аутентификации
        clearAuthentication();
    }

    private Object invokeServiceMethod(String uri, Object request) {
        // Маппинг URI на методы сервиса
        if (uri.startsWith("/tasks/")) {
            String[] pathParts = uri.split("/");
            Long taskId = Long.parseLong(pathParts[2]);
            if (uri.contains("/status")) {
                return taskService.changeStatus(taskId, (Status) request);
            } else if (uri.contains("/priority")) {
                return taskService.changePriority(taskId, (Priority) request);
            } else if (uri.contains("/assignee")) {
                return taskService.assignTask(taskId, (Long) request);
            } else {
                return taskService.getTaskById(taskId);
            }
        } else if (uri.equals("/tasks")) {
            return taskService.addTask((TaskDto) request);
        }
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    private void setupAuthentication(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testUser", null, List.of(new SimpleGrantedAuthority(role)))
        );
    }

    private void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * Источник данных для параметризованных тестов
     */
    static Stream<Arguments> provideEndpointTestData() {
        TaskDto validRequest = new TaskDto();
        validRequest.setTitle("Valid Task");
        validRequest.setPriority(Priority.MEDIUM);
        validRequest.setStatus(Status.PENDING);

        TaskDto validResponse = new TaskDto();
        validResponse.setId(1L);
        validResponse.setTitle("Valid Task");
        validResponse.setPriority(Priority.MEDIUM);
        validResponse.setStatus(Status.PENDING);

        TaskDto invalidRequest = new TaskDto(); // Нет обязательных полей

        return Stream.of(
                // Создание задачи
                Arguments.of("POST", "/tasks", "ROLE_ADMIN", validRequest, validResponse, 201, "$.title", "Valid Task"),
                Arguments.of("POST", "/tasks", "ROLE_USER", validRequest, null, 403, null, null),
                Arguments.of("POST", "/tasks", "ROLE_ADMIN", invalidRequest, null, 400, null, null),

                // Получение задачи
                Arguments.of("GET", "/tasks/1", "ROLE_USER", null, validResponse, 200, "$.id", 1),
                Arguments.of("GET", "/tasks/302302032", "ROLE_USER", null, null, 404, null, null),
                Arguments.of("GET", "/tasks/1", "ROLE_ADMIN", null, validResponse, 200, "$.status", "PENDING"),

                // Обновление статуса
                Arguments.of("PUT", "/tasks/1/status?status=IN_PROGRESS", "ROLE_ADMIN", Status.IN_PROGRESS, validResponse, 200, "$.status", "PENDING"),
                Arguments.of("PUT", "/tasks/1/status?status=IN_PROGRESS", "ROLE_USER", Status.IN_PROGRESS, null, 200, null, null),

                // Удаление задачи
                Arguments.of("DELETE", "/tasks/1", "ROLE_ADMIN", null, null, 204, null, null),
                Arguments.of("DELETE", "/tasks/1", "ROLE_USER", null, null, 403, null, null),

                // Обновление приоритета
                Arguments.of("PUT", "/tasks/1/priority?priority=HIGH", "ROLE_ADMIN", Priority.HIGH, validResponse, 200, "$.priority", "MEDIUM"),
                Arguments.of("PUT", "/tasks/1/priority?priority=HIGH", "ROLE_USER", Priority.HIGH, null, 403, null, null),

                // Назначение задачи
                Arguments.of("PUT", "/tasks/1/assignee?assigneeId=2", "ROLE_ADMIN", 2L, validResponse, 200, "$.id", 1),
                Arguments.of("PUT", "/tasks/1/assignee?assigneeId=2", "ROLE_USER", 2L, null, 403, null, null),

                // Фильтрация данных
                Arguments.of("GET", "/tasks?priority=HIGH", "ROLE_ADMIN", null, null, 200, "$.content[0].priority", "HIGH"),
                Arguments.of("GET", "/tasks?status=PENDING", "ROLE_ADMIN", null, null, 200, "$.content[1].status", "PENDING"),
                Arguments.of("GET", "/tasks?authorUsername=user1", "ROLE_USER", null, null, 403, null, null)
        );
    }
}
