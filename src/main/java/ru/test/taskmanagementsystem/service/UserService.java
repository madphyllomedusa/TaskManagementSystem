package ru.test.taskmanagementsystem.service;


import ru.test.taskmanagementsystem.model.dto.UserDto;

public interface UserService {
    UserDto getCurrentUser();
    UserDto getUserById(Long id);
    UserDto getUserByUsername(String username);
}
