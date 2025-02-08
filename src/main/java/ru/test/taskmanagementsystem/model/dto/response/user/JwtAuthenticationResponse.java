package ru.test.taskmanagementsystem.model.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO-ответ для аутентификации.
 */
@Data
@AllArgsConstructor
public class JwtAuthenticationResponse {
    /**
     * JWT-токен аутентификации.
     */
    private String token;
}
