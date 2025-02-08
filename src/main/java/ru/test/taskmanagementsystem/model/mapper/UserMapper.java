package ru.test.taskmanagementsystem.model.mapper;

import org.springframework.stereotype.Component;
import ru.test.taskmanagementsystem.model.dto.request.user.SignUpRequest;
import ru.test.taskmanagementsystem.model.dto.response.user.JwtAuthenticationResponse;
import ru.test.taskmanagementsystem.model.dto.response.user.UserResponse;
import ru.test.taskmanagementsystem.model.entity.User;

import java.time.OffsetDateTime;

/**
 * Маппер для преобразования объектов User.
 */
@Component
public class UserMapper {

    /**
     * Преобразует DTO-запрос регистрации в сущность User.
     *
     * @param signUpRequest DTO-запрос с данными пользователя.
     * @return Объект User.
     */
    public User fromSignUpRequest(SignUpRequest signUpRequest) {
        if (signUpRequest == null) return null;

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail().toLowerCase());
        user.setPassword(signUpRequest.getPassword().getBytes());
        user.setCreatedAt(OffsetDateTime.now());
        return user;
    }
}

