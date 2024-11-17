package ru.test.taskmanagementsystem.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.test.taskmanagementsystem.model.dto.CommentDto;
import ru.test.taskmanagementsystem.model.dto.TaskDto;
import ru.test.taskmanagementsystem.model.dto.UserDto;
import ru.test.taskmanagementsystem.model.entity.Task;
import ru.test.taskmanagementsystem.model.entity.User;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;
import ru.test.taskmanagementsystem.model.mapper.TaskMapper;
import ru.test.taskmanagementsystem.model.mapper.UserMapper;
import ru.test.taskmanagementsystem.repository.CommentRepository;
import ru.test.taskmanagementsystem.repository.TaskRepository;
import ru.test.taskmanagementsystem.repository.UserRepository;
import ru.test.taskmanagementsystem.service.TaskService;
import ru.test.taskmanagementsystem.service.UserService;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TaskDto addTask(TaskDto taskDto) {

        return null;
    }

    @Override
    public TaskDto updateTask(TaskDto taskDto) {
        return null;
    }

    @Override
    public TaskDto getTaskById(Long id) {
        return null;
    }

    @Override
    public void deleteTaskById(Long id) {

    }

    @Override
    public TaskDto changeStatus(Long id, Status status) {
        return null;
    }

    @Override
    public TaskDto changePriority(Long id, Priority priority) {
        return null;
    }

    @Override
    public TaskDto assignTask(Long taskId, Long assigneeId) {
        return null;
    }

    @Override
    public CommentDto addComment(Long taskId, CommentDto commentDto) {
        return null;
    }
}
