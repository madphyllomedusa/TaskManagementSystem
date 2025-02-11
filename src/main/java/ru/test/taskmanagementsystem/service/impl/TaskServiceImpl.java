package ru.test.taskmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.test.taskmanagementsystem.expectionhandler.AccessDeniedException;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.expectionhandler.NotFoundException;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskRequest;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskUpdateDeadlineRequest;
import ru.test.taskmanagementsystem.model.dto.request.task.TaskUpdateRequest;
import ru.test.taskmanagementsystem.model.dto.response.task.TaskResponse;
import ru.test.taskmanagementsystem.model.entity.Task;
import ru.test.taskmanagementsystem.model.entity.User;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;
import ru.test.taskmanagementsystem.model.mapper.TaskMapper;
import ru.test.taskmanagementsystem.repository.TaskRepository;
import ru.test.taskmanagementsystem.service.TaskService;
import ru.test.taskmanagementsystem.service.UserService;
import ru.test.taskmanagementsystem.specification.TaskSpecification;

import java.time.Duration;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;

    @Override
    public TaskResponse addTask(TaskRequest taskRequest) {
        logger.info("Starting addTask with title {}", taskRequest.getTitle());
        User currentUser = userService.getCurrentUser();
        Task task = taskMapper.toTaskEntity(taskRequest, currentUser);
        task = taskRepository.save(task);
        logger.info("Finished addTask with title {}", taskRequest.getTitle());
        return taskMapper.toTaskResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest taskUpdateRequest) {
        logger.info("Starting updateTask with id {}", id);
        Task task = getTaskById(id);
        User currentUser = userService.getCurrentUser();

        if (!isAdmin(currentUser) && !isAuthor(currentUser, task)) {
            logger.error("User id {} doesn't have permission to update task id {}", currentUser.getId(), id);
            throw new AccessDeniedException("Недостаточно прав для обновления задачи");
        }

        User assignee = null;
        if (taskUpdateRequest.getAssigneeUsername() != null) {
            if (!isAdmin(currentUser) && !taskUpdateRequest.getAssigneeUsername().equals(currentUser.getUsername())) {
                logger.error("User id {} doesn't have permission to assign task id {} to another user", currentUser.getId(), id);
                throw new AccessDeniedException("Недостаточно прав для назначения исполнителя");
            }
            assignee = userService.getUserByUsername(taskUpdateRequest.getAssigneeUsername());
        }

        taskMapper.updateTaskFromRequest(taskUpdateRequest, task, assignee);
        task = taskRepository.save(task);
        logger.info("Finished updateTask with id {}", id);
        return taskMapper.toTaskResponse(task);
    }

    @Override
    public TaskResponse getTaskResponseById(Long id) {
        logger.info("Starting getTaskResponseById with id {}", id);
        Task task = getTaskById(id);
        logger.info("Finished getTaskResponseById with id {}", id);
        return taskMapper.toTaskResponse(task);
    }

    @Override
    public Task getTaskById(Long id) {
        logger.info("Starting getTaskById with id {}", id);
        validateId(id);
        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Task with id {} not found", id);
                    return new NotFoundException("Задача не найдена");
                });
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        logger.info("Starting deleteTaskById with id {}", id);
        Task task = getTaskById(id);
        User currentUser = userService.getCurrentUser();
        if (!isAdmin(currentUser) && !isAuthor(currentUser, task)) {
            logger.error("User id {} doesn't have permission to delete task id {}", currentUser.getId(), id);
            throw new AccessDeniedException("Недостаточно прав для удаления задачи");
        }
        task.setDeletedAt(OffsetDateTime.now());
        taskRepository.save(task);
        logger.info("Finished deleteTaskById with id {}", id);
    }

    @Override
    @Transactional
    public TaskResponse changeStatus(Long id, Status status) {
        logger.info("Starting changeStatus for task id {} with status {}", id, status.name());
        Task task = getTaskById(id);
        User currentUser = userService.getCurrentUser();

        if (!isAdmin(currentUser) && !isAssignee(currentUser, task)) {
            logger.error("User id {} doesn't have permission to change status for task id {}", currentUser.getId(), id);
            throw new AccessDeniedException("Недостаточно прав для изменения статуса задачи");
        }


        task.setStatus(status);

        if(task.getStatus().equals(Status.COMPLETED)){
            task.setClosedAt(OffsetDateTime.now());
        }

        taskRepository.save(task);
        logger.info("Finished changeStatus for task id {} with status {}", id, status.name());
        return taskMapper.toTaskResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse changePriority(Long id, Priority priority) {
        logger.info("Starting changePriority for task id {} with priority {}", id, priority.name());
        Task task = getTaskById(id);
        User currentUser = userService.getCurrentUser();
        if (!isAdmin(currentUser) && !isAuthor(currentUser, task)) {
            logger.error("User id {} doesn't have permission to change priority for task id {}", currentUser.getId(), id);
            throw new AccessDeniedException("Недостаточно прав для изменения приоритета задачи");
        }
        task.setPriority(priority);
        taskRepository.save(task);
        logger.info("Finished changePriority for task id {} with priority {}", id, priority.name());
        return taskMapper.toTaskResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse assignTask(Long taskId, Long assigneeId) {
        logger.info("Starting assignTask for task id {} with assignee id {}", taskId, assigneeId);
        validateId(assigneeId);
        Task task = getTaskById(taskId);
        User currentUser = userService.getCurrentUser();
        if (!isAdmin(currentUser) && !isAuthor(currentUser, task)) {
            logger.error("User id {} doesn't have permission to assign task id {}", currentUser.getId(), taskId);
            throw new AccessDeniedException("Недостаточно прав для назначения исполнителя");
        }
        User assignee = userService.getUserById(assigneeId);
        task.setAssignee(assignee);
        taskRepository.save(task);
        logger.info("Finished assignTask for task id {} with assignee id {}", taskId, assigneeId);
        return taskMapper.toTaskResponse(task);
    }

    @Override
    public Page<TaskResponse> filterTasks(String author, String assignee,
                                          Priority priority, Status status,
                                          Long id,
                                          int page, int size) {
        logger.info("Starting filterTasks with parameters: author {}, assignee {}, priority {}, status {}, id {}, page {}, size {}",
                author, assignee, priority, status, id, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Specification<Task> specification = Specification.where(TaskSpecification.hasAuthor(author))
                .and(TaskSpecification.hasAssignee(assignee))
                .and(TaskSpecification.hasPriority(priority))
                .and(TaskSpecification.hasStatus(status))
                .and(TaskSpecification.hasId(id));
        Page<Task> tasks = taskRepository.findAll(specification, pageable);
        logger.info("Finished filterTasks with {} tasks found", tasks.getTotalElements());
        return tasks.map(taskMapper::toTaskResponse);
    }

    @Override
    @Transactional
    public TaskResponse extendDeadline(Long taskId, TaskUpdateDeadlineRequest extension) {
        logger.info("Starting extendDeadline for task id {} with extension '{}'", taskId, extension);

        Task task = getTaskById(taskId);
        User currentUser = userService.getCurrentUser();
        if (!isAdmin(currentUser) && !isAuthor(currentUser, task)) {
            logger.error("User id {} doesn't have permission to add time task id {}", currentUser.getId(), taskId);
            throw new AccessDeniedException("Недостаточно прав для назначения исполнителя");
        }        OffsetDateTime currentDeadline = task.getDeadlineAt();
        if (currentDeadline == null) {
            throw new BadRequestException("Для задачи не задан дедлайн");
        }
        Duration extensionDuration = taskMapper.parseDuration(extension.getExtension());
        OffsetDateTime newDeadline = currentDeadline.plus(extensionDuration);
        task.setDeadlineAt(newDeadline);
        taskRepository.save(task);
        logger.info("Finished extendDeadline for task id {}. New deadline: {}", taskId, newDeadline);
        return taskMapper.toTaskResponse(task);
    }


    private void validateId(Long id) {
        logger.debug("Starting validateId with id {}", id);
        if (id == null || id < 1) {
            logger.error("Wrong id {}", id);
            throw new BadRequestException("Неверный id " + id);
        }
        logger.debug("Finished validateId with id {}", id);
    }


    private boolean isAdmin(User user) {
        return "ROLE_ADMIN".equals(user.getRole().name());
    }

    private boolean isAuthor(User user, Task task) {
        return task.getAuthor().getId().equals(user.getId());
    }

    private boolean isAssignee(User user, Task task) {
        return task.getAssignee() != null && task.getAssignee().getId().equals(user.getId());
    }
}
