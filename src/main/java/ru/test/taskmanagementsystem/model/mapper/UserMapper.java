package ru.test.taskmanagementsystem.model.mapper;

import org.springframework.stereotype.Component;
import ru.test.taskmanagementsystem.model.dto.SignUpRequest;
import ru.test.taskmanagementsystem.model.dto.UserDto;
import ru.test.taskmanagementsystem.model.entity.User;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername().toLowerCase());
        userDto.setEmail(user.getEmail().toLowerCase());
        userDto.setRole(user.getRole());
        return userDto;
    }

    public User toSignUpRequest(SignUpRequest signUpRequest) {
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail().toLowerCase());
        user.setUsername(signUpRequest.getUsername().toLowerCase());
        user.setPassword(signUpRequest.getPassword());
        user.setRole(signUpRequest.getRole());
        return user;
    }

    public User toEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail().toLowerCase());
        user.setUsername(userDto.getUsername().toLowerCase());
        user.setRole(userDto.getRole());
        return user;

    }

}
