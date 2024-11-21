package ru.test.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
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
import ru.test.taskmanagementsystem.model.dto.CommentDto;
import ru.test.taskmanagementsystem.model.dto.TaskDto;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;

@RequestMapping("/tasks")
public interface TaskControllerApi {

    @Operation(summary = "Создать задачу", description = "Добавляет новую задачу в систему.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача успешно создана",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Автор не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping
    ResponseEntity<TaskDto> addTask(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные новой задачи", required = true,
                    content = @Content(schema = @Schema(implementation = TaskDto.class)))
            TaskDto taskDto
    );

    @Operation(summary = "Обновить задачу", description = "Обновляет данные задачи по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные задачи успешно обновлены",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный ID задачи или данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача или пользователь не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/{id}")
    ResponseEntity<TaskDto> updateTask(
            @Parameter(description = "Идентификатор задачи", required = true)
            @PathVariable Long id,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Обновлённые данные задачи", required = true,
                    content = @Content(schema = @Schema(implementation = TaskDto.class)))
            TaskDto taskDto
    );

    @Operation(summary = "Получить задачу", description = "Возвращает данные задачи по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные задачи успешно получены",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный ID задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{id}")
    ResponseEntity<TaskDto> getTaskById(
            @Parameter(description = "Идентификатор задачи", required = true)
            @PathVariable Long id
    );

    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "400", description = "Некорректный ID задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTask(
            @Parameter(description = "Идентификатор задачи", required = true)
            @PathVariable Long id
    );

    @Operation(summary = "Изменить статус задачи", description = "Обновляет статус задачи по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус успешно изменён",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный ID задачи или статус",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён (не является администратором или исполнителем задачи)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/{id}/status")
    ResponseEntity<TaskDto> updateTaskStatus(
            @Parameter(description = "Идентификатор задачи", required = true)
            @PathVariable Long id,
            @Parameter(description = "Новый статус задачи", required = true)
            @RequestParam Status status
    );

    @Operation(summary = "Изменить приоритет задачи", description = "Обновляет приоритет задачи по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Приоритет успешно изменён",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный ID задачи или приоритет",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён (требуется роль администратора)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/{id}/priority")
    ResponseEntity<TaskDto> updateTaskPriority(
            @Parameter(description = "Идентификатор задачи", required = true)
            @PathVariable Long id,
            @Parameter(description = "Новый приоритет задачи", required = true)
            @RequestParam Priority priority
    );

    @Operation(summary = "Изменить исполнителя задачи", description = "Обновляет исполнителя задачи по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Исполнитель успешно изменён",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный ID задачи или исполнителя",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён (требуется роль администратора)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача или исполнитель не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/{id}/assignee")
    ResponseEntity<TaskDto> updateAssignTask(
            @Parameter(description = "Идентификатор задачи", required = true)
            @PathVariable Long id,
            @Parameter(description = "Новый исполнитель задачи", required = true)
            @RequestParam Long assigneeId
    );

    @Operation(summary = "Добавить комментарий к задаче", description = "Добавляет комментарий к задаче по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Комментарий успешно добавлен",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный ID задачи или текст комментария",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён (не является администратором или исполнителем задачи)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/{id}/comments")
    ResponseEntity<CommentDto> addComment(
            @Parameter(description = "Идентификатор задачи", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Текст комментария", required = true,
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
            @RequestBody String commentText
    );

    @Operation(summary = "Фильтрация задач", description = "Возвращает список задач с возможностью фильтрации и пагинации.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач успешно получен",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры фильтрации или пагинации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping
    ResponseEntity<Page<TaskDto>> filterTasks(
            @Parameter(description = "Имя пользователя автора задачи")
            @RequestParam(required = false) String authorUsername,

            @Parameter(description = "Имя пользователя исполнителя задачи")
            @RequestParam(required = false) String assigneeUsername,

            @Parameter(description = "Приоритет задачи")
            @RequestParam(required = false) Priority priority,

            @Parameter(description = "Статус задачи")
            @RequestParam(required = false) Status status,

            @Parameter(description = "Номер страницы для пагинации", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы для пагинации", example = "10")
            @RequestParam(defaultValue = "10") int size
    );
}
