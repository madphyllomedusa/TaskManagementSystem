package ru.test.taskmanagementsystem.service;

import org.springframework.data.domain.Page;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskRequest;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskUpdateDeadlineRequest;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.task.TaskResponse;
import ru.test.taskmanagementsystem.model.entity.Task;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;

/**
 * Сервис для управления задачами.
 */
public interface TaskService {

    /**
     * Создает новую задачу.
     *
     * @param taskRequest объект с данными новой задачи
     * @return объект {@link TaskResponse}, содержащий данные созданной задачи
     */
    TaskResponse addTask(TaskRequest taskRequest);

    /**
     * Обновляет существующую задачу.
     *
     * @param id идентификатор задачи
     * @param taskUpdateRequest объект с обновленными данными задачи
     * @return объект {@link TaskResponse}, содержащий обновленную задачу
     */
    TaskResponse updateTask(Long id, TaskUpdateRequest taskUpdateRequest);

    /**
     * Получает задачу по ее идентификатору в виде DTO-объекта.
     *
     * @param id идентификатор задачи
     * @return объект {@link TaskResponse}, содержащий данные задачи
     */
    TaskResponse getTaskResponseById(Long id);

    /**
     * Получает задачу по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @return объект {@link Task}, представляющий сущность задачи
     */
    Task getTaskById(Long id);

    /**
     * Удаляет задачу по ее идентификатору.
     *
     * @param id идентификатор задачи для удаления
     */
    void deleteTaskById(Long id);

    /**
     * Изменяет статус задачи.
     *
     * @param id идентификатор задачи
     * @param status новый статус задачи
     * @return объект {@link TaskResponse}, содержащий обновленные данные задачи
     */
    TaskResponse changeStatus(Long id, Status status);

    /**
     * Изменяет приоритет задачи.
     *
     * @param id идентификатор задачи
     * @param priority новый приоритет задачи
     * @return объект {@link TaskResponse}, содержащий обновленные данные задачи
     */
    TaskResponse changePriority(Long id, Priority priority);

    /**
     * Назначает исполнителя для задачи.
     *
     * @param taskId идентификатор задачи
     * @param assigneeId идентификатор пользователя, которому назначается задача
     * @return объект {@link TaskResponse}, содержащий обновленные данные задачи
     */
    TaskResponse assignTask(Long taskId, Long assigneeId);

    /**
     * Добавление времени к дедлайну задачи
     *
     * @param taskId идентификатор задачи
     * @param extension количество добавленного времени
     *
     * @return объект {@link TaskResponse}, содержащий обновленные данные задачи
     */
    TaskResponse extendDeadline(Long taskId, TaskUpdateDeadlineRequest extension);

    /**
     * Фильтрует задачи по различным параметрам с пагинацией.
     *
     * @param authorUsername имя пользователя-автора задачи (опционально)
     * @param assigneeUsername имя пользователя-исполнителя задачи (опционально)
     * @param priority приоритет задачи (опционально)
     * @param status статус задачи (опционально)
     * @param id идентификатор задачи (опционально)
     * @param page номер страницы
     * @param size количество элементов на странице
     * @return объект {@link Page<TaskResponse>}, содержащий отфильтрованный список задач
     */
    Page<TaskResponse> filterTasks(String authorUsername, String assigneeUsername,
                                   Priority priority, Status status, Long id,
                                   int page, int size);
}
