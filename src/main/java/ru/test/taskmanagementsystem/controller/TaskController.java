package ru.test.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.test.taskmanagementsystem.model.dto.CommentDto;
import ru.test.taskmanagementsystem.model.dto.TaskDto;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;
import ru.test.taskmanagementsystem.service.TaskService;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

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
    public ResponseEntity<TaskDto> addTask(@RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.addTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

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
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable @Parameter(description = "ID задачи") Long id,
            @RequestBody TaskDto taskDto) {
        taskDto.setId(id);
        TaskDto updatedTask = taskService.updateTask(taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Получить задачу", description = "Возвращает данные задачи по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные задачи успешно получены",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Не верно указан id задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Не найдена задача",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является админом",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable @Parameter(description = "ID задачи") Long id) {
        TaskDto taskDto = taskService.getTaskById(id);
        return ResponseEntity.ok(taskDto);
    }

    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "400", description = "Не верно указан id задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Не найдена задача",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является админом",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable @Parameter(description = "ID задачи") Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Изменить статус задачи", description = "Обновляет статус задачи по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус задачи успешно обновлён",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Не верно указан id задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Это задача не принадлежит пользователю",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @PathVariable @Parameter(description = "ID задачи") Long id,
            @RequestParam @Parameter(description = "Новый статус задачи") Status status) {
        TaskDto updatedTask = taskService.changeStatus(id, status);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Изменить приоритет задачи", description = "Обновляет приоритет задачи по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Приоритет задачи успешно обновлён",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Не верно указан id задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является админом",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}/priority")
    public ResponseEntity<TaskDto> updateTaskPriority(
            @PathVariable @Parameter(description = "ID задачи") Long id,
            @RequestParam @Parameter(description = "Новый приоритет задачи") Priority priority) {
        TaskDto updatedTask = taskService.changePriority(id, priority);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Изменить исполнителя задачи", description = "Обновляет исполнителя задачи по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус задачи успешно обновлён",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Не верно указан Id задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Исполнитель не найден по assigneeId",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является админом",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}/assignee")
    public ResponseEntity<TaskDto> updateAssignTask(
            @PathVariable @Parameter(description = "ID задачи") Long id,
            @RequestParam @Parameter(description = "ID исполнителя задачи") Long assigneeId) {
        TaskDto updatedTask = taskService.assignTask(id, assigneeId);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Добавить комментарий к задаче", description = "Добавляет комментарий к задаче по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Коментарий создан",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "400", description = "Не верно указан Id задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = """
                    Может быть в случаях:
                    - Не найдена задача
                    - Не найден пользователь
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = """
                    - Пользователь не является админостратором
                    - Пользователь не является автором задачи
                    - Пользователь не является испольнителем задачи
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable @Parameter(description = "ID задачи") Long id,
            @RequestBody @Parameter(description = "Текст комментария") String commentText) {
        CommentDto newComment = taskService.addComment(id, commentText);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }

    @Operation(summary = "Фильтр задач", description = "Возвращает список задач с фильтрацией и пагинацией.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач успешно получен",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры фильтрации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является админом",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Page<TaskDto>> filterTasks(
            @RequestParam(required = false) @Parameter(description = "Автор задачи") String authorUsername,
            @RequestParam(required = false) @Parameter(description = "Исполнитель задачи") String assigneeUsername,
            @RequestParam(required = false) @Parameter(description = "Приоритет задачи") Priority priority,
            @RequestParam(required = false) @Parameter(description = "Статус задачи") Status status,
            @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Размер страницы") int size) {
        Page<TaskDto> tasks = taskService.filterTasks(authorUsername, assigneeUsername, priority, status, page, size);
        return ResponseEntity.ok(tasks);
    }

}

