package ru.test.taskmanagementsystem.model.dto.response.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.test.taskmanagementsystem.model.enums.Role;

/**
 * DTO-ответ для пользователя.
 */
@Data
@NoArgsConstructor
public class UserResponse {
    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Имя пользователя.
     */
    private String username;

    /**
     * Роль пользователя (ROLE_USER или ROLE_ADMIN).
     */
    private Role role;
}

