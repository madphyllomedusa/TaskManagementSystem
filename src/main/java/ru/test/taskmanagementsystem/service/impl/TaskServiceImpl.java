package ru.test.taskmanagementsystem.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.expectionhandler.ForbiddenException;
import ru.test.taskmanagementsystem.expectionhandler.NotFoundException;
import ru.test.taskmanagementsystem.model.dto.CommentDto;
import ru.test.taskmanagementsystem.model.dto.TaskDto;
import ru.test.taskmanagementsystem.model.dto.UserDto;
import ru.test.taskmanagementsystem.model.entity.Comment;
import ru.test.taskmanagementsystem.model.entity.Task;
import ru.test.taskmanagementsystem.model.entity.User;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;
import ru.test.taskmanagementsystem.model.mapper.TaskMapper;
import ru.test.taskmanagementsystem.repository.CommentRepository;
import ru.test.taskmanagementsystem.repository.TaskRepository;
import ru.test.taskmanagementsystem.repository.UserRepository;
import ru.test.taskmanagementsystem.service.TaskService;
import ru.test.taskmanagementsystem.service.UserService;
import ru.test.taskmanagementsystem.specification.TaskSpecification;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public TaskDto addTask(TaskDto taskDto) {
        UserDto currentUser = userService.getCurrentUser();
        logger.info("Trying to add new task: {} by {}", taskDto.getTitle(), currentUser.getUsername());
        Task task = taskMapper.toEntity(taskDto);

        User author = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с id не найден " + currentUser.getId()));
        task.setAuthor(author);

        Task savedTask = taskRepository.save(task);
        logger.info("Task saved {}", taskDto.getTitle());

        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskDto updateTask(TaskDto taskDto) {
        logger.info("Trying to update task with id: {}", taskDto.getId());


        Task task = taskRepository.findById(taskDto.getId())
                .orElseThrow(() -> new NotFoundException("Задача с id не найдена: " + taskDto.getId()));


        if (taskDto.getTitle() != null) {
            task.setTitle(taskDto.getTitle());
        }
        if (taskDto.getDescription() != null) {
            task.setDescription(taskDto.getDescription());
        }
        if (taskDto.getPriority() != null) {
            task.setPriority(taskDto.getPriority());
        }
        if (taskDto.getStatus() != null) {
            task.setStatus(taskDto.getStatus());
        }
        if (taskDto.getAssigneeUsername() != null) {
            User assignee = userRepository.findByUsername(taskDto.getAssigneeUsername())
                    .orElseThrow(() -> new NotFoundException("Исполнитель с username " + taskDto.getAssigneeUsername() + " не найден"));
            task.setAssignee(assignee);
        }

        Task updatedTask = taskRepository.save(task);
        logger.info("Task updated successfully: {}", updatedTask);

        return taskMapper.toDto(updatedTask);
    }


    @Override
    public TaskDto getTaskById(Long id) {
        logger.info("Trying to get task by id: {}", id);
        if (id == null || id < 1) {
            logger.error("Wrong id {}", id);
            throw new BadRequestException("Wrong id " + id);
        }
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Wrong id " + id));

        return taskMapper.toDto(task);
    }

    @Override
    public void deleteTaskById(Long id) {
        logger.info("Trying to delete task by id: {}", id);

        if (id == null || id < 1) {
            logger.error("Wrong id {}", id);
            throw new BadRequestException("Wrong id " + id);
        }
        logger.info("Task with id {} successfully deleted", id);
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TaskDto changeStatus(Long id, Status status) {
        logger.info("Trying to change status by id: {}", id);
        if (id == null || id < 1) {
            logger.error("Wrong id {}", id);
            throw new BadRequestException("Wrong id " + id);
        }

        Task task = taskMapper.toEntity(getTaskById(id));

        if (!mayManageTask(task)) {
            throw new ForbiddenException("У вас нет доступа для изменения статуса этой задачи");
        }

        logger.info("Task status successfully changed from {} to {}", task.getStatus(), status);
        task.setStatus(status);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskDto changePriority(Long id, Priority priority) {
        logger.info("Trying to change priority by id: {}", id);

        if (id == null || id < 1) {
            logger.error("Wrong id {}", id);
            throw new BadRequestException("Wrong id " + id);
        }

        Task task = taskMapper.toEntity(getTaskById(id));

        logger.info("Task priority successfully changed from {} to {}", task.getPriority(), priority);

        task.setPriority(priority);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskDto assignTask(Long taskId, Long assigneeId) {
        logger.info("Trying to assign task by id {} to assignee {}", taskId, assigneeId);

        User user = userRepository.findById(assigneeId)
                .orElseThrow(() -> new NotFoundException("Wrong id " + assigneeId));

        if (taskId == null || taskId < 1) {
            logger.error("Wrong id {}", taskId);
            throw new BadRequestException("Wrong id " + taskId);
        }

        Task task = taskMapper.toEntity(getTaskById(taskId));

        logger.info("Task assign successfully changed from {} to {}", task.getAssignee().getUsername(),
                user.getUsername());

        task.setAssignee(user);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long taskId, String commentText) {
        logger.info("Adding comment to task with id: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Задача с id не найдена: " + taskId));

        UserDto currentUser = userService.getCurrentUser();
        if (!task.getAuthor().getId().equals(currentUser.getId()) &&
                (task.getAssignee() == null || !task.getAssignee().getId().equals(currentUser.getId()))) {
            throw new ForbiddenException("У вас нет прав на добавление комментария к этой задаче");
        }

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setText(commentText);
        comment.setUser(userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден")));

        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment added successfully: {}", savedComment);

        return taskMapper.toCommentDto(savedComment);
    }

    @Override
    public Page<TaskDto> filterTasks(String author, String assignee,
                                     Priority priority, Status status,
                                     int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Task> specification = Specification.where(
                TaskSpecification.hasAuthor(author))
                .and(TaskSpecification.hasAssignee(assignee))
                .and(TaskSpecification.hasPriority(priority))
                .and(TaskSpecification.hasStatus(status));

        Page<Task> tasks = taskRepository.findAll(specification, pageable);
        return tasks.map(taskMapper::toDto);
    }


    private boolean mayManageTask(Task task) {
        UserDto currentUser = userService.getCurrentUser();
        return task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId());
    }
}
