package ru.test.taskmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskRequest;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskUpdateDeadlineRequest;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.task.TaskResponse;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;
import ru.test.taskmanagementsystem.service.TaskService;

/**
 * Контроллер для управления задачами.
 */
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Создает новую задачу.
     *
     * @param taskRequest данные новой задачи
     * @return созданная задача
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TaskResponse> addTask(@Valid @RequestBody TaskRequest taskRequest) {
        TaskResponse createdTask = taskService.addTask(taskRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id                идентификатор задачи
     * @param taskUpdateRequest обновленные данные задачи
     * @return обновленная задача
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest taskUpdateRequest) {
        TaskResponse updatedTask = taskService.updateTask(id, taskUpdateRequest);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Получает задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return найденная задача
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse taskResponse = taskService.getTaskResponseById(id);
        return ResponseEntity.ok(taskResponse);
    }

    /**
     * Удаляет задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return пустой ответ с кодом 204
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет статус задачи.
     *
     * @param id     идентификатор задачи
     * @param status новый статус задачи
     * @return обновленная задача
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id,
                                                         @RequestParam Status status) {
        TaskResponse updatedTask = taskService.changeStatus(id, status);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Обновляет приоритет задачи.
     *
     * @param id       идентификатор задачи
     * @param priority новый приоритет задачи
     * @return обновленная задача
     */
    @PatchMapping("/{id}/priority")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TaskResponse> updateTaskPriority(@PathVariable Long id,
                                                           @RequestParam Priority priority) {
        TaskResponse updatedTask = taskService.changePriority(id, priority);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Назначает задачу другому пользователю.
     *
     * @param id         идентификатор задачи
     * @param assigneeId идентификатор нового исполнителя
     * @return обновленная задача
     */
    @PatchMapping("/{id}/assignee")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TaskResponse> updateAssignTask(@PathVariable Long id,
                                                         @RequestParam Long assigneeId) {
        TaskResponse updatedTask = taskService.assignTask(id, assigneeId);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Увеличивает срок выполнения задачи.
     *
     * @param id        идентификатор задачи
     * @param extension количество добавляемого времени
     * @return обновленная задача
     */
    @PatchMapping("/{id}/add-time")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TaskResponse> updateDeadline(@PathVariable Long id, @RequestBody TaskUpdateDeadlineRequest extension){
        TaskResponse updatedTask = taskService.extendDeadline(id, extension);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Фильтрует задачи по различным критериям.
     *
     * @param author   автор задачи
     * @param assignee исполнитель задачи
     * @param priority приоритет задачи
     * @param status   статус задачи
     * @param id       идентификатор задачи
     * @param page     номер страницы
     * @param size     количество элементов на странице
     * @return страница с отфильтрованными задачами
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<Page<TaskResponse>> filterTasks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TaskResponse> tasks = taskService.filterTasks(author, assignee, priority, status, id, page, size);
        return ResponseEntity.ok(tasks);
    }
}


