package ru.test.taskmanagementsystem.service;


import ru.test.taskmanagementsystem.model.dto.UserDto;

public interface UserService {
    boolean isAdmin(UserDto userDto);
    UserDto getCurrentUser();
    String getUsernameById(Long id);
}
