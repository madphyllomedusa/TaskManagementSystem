package ru.test.taskmanagementsystem.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskRequest;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskUpdateDeadlineRequest;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.task.TaskResponse;
import ru.test.taskmanagementsystem.model.entity.Task;
import ru.test.taskmanagementsystem.model.entity.User;
import ru.test.taskmanagementsystem.model.enums.Status;
import ru.test.taskmanagementsystem.model.mapper.TaskMapper;
import ru.test.taskmanagementsystem.repository.TaskRepository;
import ru.test.taskmanagementsystem.service.impl.TaskServiceImpl;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private TaskServiceImpl taskService;

    @ParameterizedTest
    @MethodSource("provideAddTaskData")
    void testAddTask(TaskRequest taskRequest, TaskResponse expectedResponse) {
        User currentUser = new User();
        currentUser.setId(100L);
        currentUser.setRole(ru.test.taskmanagementsystem.model.enums.Role.ROLE_USER);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        Task task = new Task();
        task.setId(expectedResponse.getId());
        when(taskMapper.toTaskEntity(taskRequest, currentUser)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toTaskResponse(task)).thenReturn(expectedResponse);

        TaskResponse actual = taskService.addTask(taskRequest);

        assertEquals(expectedResponse, actual);
    }

    static Stream<Arguments> provideAddTaskData() {
        return Stream.of(
                Arguments.of(
                        new TaskRequest("Task1", "Description1", "PENDING", "HIGH", "3d", null),
                        new TaskResponse(
                                1L, "Task1", "Description1", "PENDING", "HIGH",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                null,
                                OffsetDateTime.parse("2025-02-11T12:00:00+03:00"),
                                "admin", null, null
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideUpdateTaskData")
    void testUpdateTask(Long id, TaskUpdateRequest updateRequest, TaskResponse expectedResponse) {
        Task task = new Task();
        task.setId(id);

        User currentUser = new User();
        currentUser.setId(100L);
        currentUser.setUsername("assigneeUsername");
        currentUser.setRole(ru.test.taskmanagementsystem.model.enums.Role.ROLE_USER);
        task.setAuthor(currentUser);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        doNothing().when(taskMapper).updateTaskFromRequest(eq(updateRequest), eq(task), any());
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toTaskResponse(task)).thenReturn(expectedResponse);

        TaskResponse actual = taskService.updateTask(id, updateRequest);

        assertEquals(expectedResponse, actual);
    }


    static Stream<Arguments> provideUpdateTaskData() {
        return Stream.of(
                Arguments.of(
                        1L,
                        new TaskUpdateRequest("Updated Task", "Updated description", "IN_PROGRESS", "MEDIUM", "2d", "assigneeUsername", "admin"),
                        new TaskResponse(
                                1L, "Updated Task", "Updated description", "IN_PROGRESS", "MEDIUM",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T13:00:00+03:00"),
                                null,
                                OffsetDateTime.parse("2025-02-10T12:00:00+03:00"),
                                "admin", "assigneeUsername", null
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetTaskByIdData")
    void testGetTaskById(Long id, TaskResponse expectedResponse) {
        Task task = new Task();
        task.setId(id);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskMapper.toTaskResponse(task)).thenReturn(expectedResponse);

        TaskResponse actual = taskService.getTaskResponseById(id);

        assertEquals(expectedResponse, actual);
    }

    static Stream<Arguments> provideGetTaskByIdData() {
        return Stream.of(
                Arguments.of(
                        1L,
                        new TaskResponse(
                                1L, "Task1", "Description1", "PENDING", "HIGH",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                null,
                                OffsetDateTime.parse("2025-02-11T12:00:00+03:00"),
                                "admin", null, null
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideDeleteTaskData")
    void testDeleteTaskById(Long id) {
        Task task = new Task();
        task.setId(id);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        User currentUser = new User();
        currentUser.setId(100L);
        currentUser.setRole(ru.test.taskmanagementsystem.model.enums.Role.ROLE_USER);
        task.setAuthor(currentUser);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        taskService.deleteTaskById(id);

        assertNotNull(task.getDeletedAt());
        verify(taskRepository).save(task);
    }

    static Stream<Arguments> provideDeleteTaskData() {
        return Stream.of(
                Arguments.of(1L)
        );
    }

    @ParameterizedTest
    @MethodSource("provideChangeStatusData")
    void testChangeStatus(Long id, Status newStatus, TaskResponse expectedResponse) {
        Task task = new Task();
        task.setId(id);
        task.setStatus(Status.PENDING);

        User currentUser = new User();
        currentUser.setId(100L);
        currentUser.setRole(ru.test.taskmanagementsystem.model.enums.Role.ROLE_USER);
        task.setAssignee(currentUser);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toTaskResponse(task)).thenReturn(expectedResponse);

        TaskResponse actual = taskService.changeStatus(id, newStatus);

        assertEquals(expectedResponse, actual);
    }

    static Stream<Arguments> provideChangeStatusData() {
        return Stream.of(
                Arguments.of(
                        1L,
                        Status.IN_PROGRESS,
                        new TaskResponse(
                                1L, "Task1", "Description1", "IN_PROGRESS", "HIGH",
                                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                                OffsetDateTime.parse("2025-02-08T12:30:00+03:00"),
                                null,
                                OffsetDateTime.parse("2025-02-11T12:00:00+03:00"),
                                "admin", null, null
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideExtendDeadlineData")
    void testExtendDeadline(Long id, String extensionStr, OffsetDateTime currentDeadline, OffsetDateTime expectedNewDeadline) {
        Task task = new Task();
        task.setId(id);
        task.setDeadlineAt(currentDeadline);

        User currentUser = new User();
        currentUser.setId(100L);
        currentUser.setRole(ru.test.taskmanagementsystem.model.enums.Role.ROLE_USER);
        task.setAuthor(currentUser);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        Duration duration = computeDuration(extensionStr);
        when(taskMapper.parseDuration(extensionStr)).thenReturn(duration);
        TaskResponse expectedResponse = new TaskResponse(
                id, "Task1", "Description1", "PENDING", "HIGH",
                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                null,
                expectedNewDeadline,
                "admin", null, null
        );
        when(taskMapper.toTaskResponse(task)).thenReturn(expectedResponse);

        TaskResponse actual = taskService.extendDeadline(id, new TaskUpdateDeadlineRequest(extensionStr));

        assertEquals(expectedNewDeadline, task.getDeadlineAt());
        assertEquals(expectedResponse, actual);
    }

    static Stream<Arguments> provideExtendDeadlineData() {
        return Stream.of(
                Arguments.of(
                        1L,
                        "1d 2h 30m",
                        OffsetDateTime.parse("2025-02-08T12:00:00+03:00"),
                        OffsetDateTime.parse("2025-02-09T14:30:00+03:00")
                ),
                Arguments.of(
                        1L,
                        "3h",
                        OffsetDateTime.parse("2025-02-08T10:00:00+03:00"),
                        OffsetDateTime.parse("2025-02-08T13:00:00+03:00")
                )
        );
    }


    private Duration computeDuration(String extensionStr) {
        String[] parts = extensionStr.split("\\s+");
        long days = 0, hours = 0, minutes = 0;
        for (String part : parts) {
            if (part.endsWith("d")) {
                days += Long.parseLong(part.substring(0, part.length() - 1));
            } else if (part.endsWith("h")) {
                hours += Long.parseLong(part.substring(0, part.length() - 1));
            } else if (part.endsWith("m")) {
                minutes += Long.parseLong(part.substring(0, part.length() - 1));
            } else {
                throw new BadRequestException("Неверный формат расширения: " + extensionStr);
            }
        }
        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes);
    }
}
