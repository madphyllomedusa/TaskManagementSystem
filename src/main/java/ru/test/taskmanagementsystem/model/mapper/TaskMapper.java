package ru.test.taskmanagementsystem.model.mapper;

import org.springframework.stereotype.Component;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskRequest;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.comment.CommentResponse;
import ru.test.taskmanagementsystem.model.dto.response.task.TaskResponse;
import ru.test.taskmanagementsystem.model.entity.Task;
import ru.test.taskmanagementsystem.model.entity.User;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * Маппер для преобразования объектов Task.
 */
@Component
public class TaskMapper {

    /**
     * Преобразует DTO-запрос в сущность Task.
     *
     * @param request DTO-запрос с данными задачи.
     * @param author  Автор задачи.
     * @return Объект Task.
     */
    public Task toTaskEntity(TaskRequest request, User author) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? Status.valueOf(request.getStatus()) : null);
        task.setPriority(request.getPriority() != null ? Priority.valueOf(request.getPriority()) : null);
        task.setDeadlineAt(parseDeadline(request.getDeadline()));
        task.setAuthor(author);
        return task;
    }

    /**
     * Обновляет сущность Task из DTO-запроса.
     *
     * @param request  DTO-запрос с новыми параметрами.
     * @param task     Задача, которую нужно обновить.
     * @param assignee Новый исполнитель задачи.
     */
    public void updateTaskFromRequest(TaskUpdateRequest request, Task task, User assignee) {
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(Status.valueOf(request.getStatus()));
        }
        if (request.getPriority() != null) {
            task.setPriority(Priority.valueOf(request.getPriority()));
        }
        if (request.getDeadline() != null) {
            task.setDeadlineAt(parseDeadline(request.getDeadline()));
        }
        if (assignee != null) {
            task.setAssignee(assignee);
        }
    }

    /**
     * Конвертирует сущность Task в DTO-ответ.
     *
     * @param task Объект задачи.
     * @return DTO-ответ TaskResponse.
     */
    public TaskResponse toTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
        response.setPriority(task.getPriority() != null ? task.getPriority().name() : null);
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setClosedAt(task.getClosedAt());
        response.setDeadlineAt(task.getDeadlineAt());
        response.setAuthorUsername(task.getAuthor().getUsername());
        response.setAssigneeUsername(task.getAssignee() != null ? task.getAssignee().getUsername() : null);
        response.setComments(task.getComments() == null ? null :
                task.getComments().stream().map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getUser().getUsername(),
                        comment.getText(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt(),
                        comment.getTask().getId()
                )).toList());
        return response;
    }

    /**
     * Разбирает строку длительности (например, "2d 5h 30m") и конвертирует в Duration.
     *
     * @param durationInput Строка с длительностью.
     * @return Duration с указанной продолжительностью.
     */
    public Duration parseDuration(String durationInput) {
        if (durationInput == null || durationInput.isEmpty()) {
            return Duration.ZERO;
        }
        String[] parts = durationInput.trim().split("\\s+");
        long days = 0, hours = 0, minutes = 0;
        for (String part : parts) {
            if (part.matches("\\d+d")) {
                days += Long.parseLong(part.substring(0, part.length() - 1));
            } else if (part.matches("\\d+h")) {
                hours += Long.parseLong(part.substring(0, part.length() - 1));
            } else if (part.matches("\\d+m")) {
                minutes += Long.parseLong(part.substring(0, part.length() - 1));
            } else {
                throw new BadRequestException("Неверный формат расширения: " + durationInput);
            }
        }
        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes);
    }

    /**
     * Разбирает строку дедлайна и конвертирует в OffsetDateTime.
     *
     * @param deadlineInput Строка с дедлайном.
     * @return OffsetDateTime с указанным дедлайном.
     */
    private OffsetDateTime parseDeadline(String deadlineInput) {
        if (deadlineInput == null || deadlineInput.isEmpty()) {
            return null;
        }
        deadlineInput = deadlineInput.trim();
        try {
            String[] parts = deadlineInput.split("\\s+");
            long days = 0;
            long hours = 0;
            long minutes = 0;
            boolean valid = true;
            for (String part : parts) {
                if (part.matches("\\d+d")) {
                    days += Long.parseLong(part.substring(0, part.length() - 1));
                } else if (part.matches("\\d+h")) {
                    hours += Long.parseLong(part.substring(0, part.length() - 1));
                } else if (part.matches("\\d+m")) {
                    minutes += Long.parseLong(part.substring(0, part.length() - 1));
                } else {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                return OffsetDateTime.now().plusDays(days).plusHours(hours).plusMinutes(minutes);
            } else {
                return OffsetDateTime.parse(deadlineInput);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Неверный формат дедлайна: " + deadlineInput);
        }
    }
}
