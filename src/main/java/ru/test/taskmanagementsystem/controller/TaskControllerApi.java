package ru.test.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.test.taskmanagementsystem.model.dto.CommentDto;
import ru.test.taskmanagementsystem.model.dto.TaskDto;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;

@RequestMapping("/tasks")
public interface TaskControllerApi {

    @Operation(summary = "Создать задачу", description = "Администратор добавляет новую задачу в систему.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача успешно создана",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "404", description = "Не удалось найти автора",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является админом",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping
    ResponseEntity<TaskDto> addTask(@RequestBody TaskDto taskDto);

    @Operation(summary = "Обновить задачу", description = "Обновляет данные задачи по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные задачи успешно обновлены",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "404", description = """
                    Может быть в случаях:
                    - Не найдена задача
                    - Не найден исполнитель или автор
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является админом",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/{id}")
    ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto);

    @Operation(summary = "Получить задачу", description = "Возвращает данные задачи по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные задачи успешно получены",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Не верно указан id задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Не найдена задача",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{id}")
    ResponseEntity<TaskDto> getTaskById(@PathVariable Long id);

    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "400", description = "Не верно указан id задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Не найдена задача",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTask(@PathVariable Long id);

    @Operation(summary = "Изменить статус задачи", description = "Обновляет статус задачи по её ID.")
    @PutMapping("/{id}/status")
    ResponseEntity<TaskDto> updateTaskStatus(@PathVariable Long id, @RequestParam Status status);

    @Operation(summary = "Изменить приоритет задачи", description = "Обновляет приоритет задачи по её ID.")
    @PutMapping("/{id}/priority")
    ResponseEntity<TaskDto> updateTaskPriority(@PathVariable Long id, @RequestParam Priority priority);

    @Operation(summary = "Изменить исполнителя задачи", description = "Обновляет исполнителя задачи по её ID.")
    @PutMapping("/{id}/assignee")
    ResponseEntity<TaskDto> updateAssignTask(@PathVariable Long id, @RequestParam Long assigneeId);

    @Operation(summary = "Добавить комментарий к задаче", description = "Добавляет комментарий к задаче по её ID.")
    @PostMapping("/{id}/comments")
    ResponseEntity<CommentDto> addComment(@PathVariable Long id, @RequestBody String commentText);

    @Operation(summary = "Фильтр задач", description = "Возвращает список задач с фильтрацией и пагинацией.")
    @GetMapping
    ResponseEntity<Page<TaskDto>> filterTasks(
            @RequestParam(required = false) String authorUsername,
            @RequestParam(required = false) String assigneeUsername,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);
}
