package ru.test.taskmanagementsystem.model.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO-запрос для аутентификации пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
    /**
     * Email пользователя.
     * Должен быть в формате email.
     */
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    /**
     * Пароль пользователя.
     */
    @NotBlank(message = "Пароль не должен быть пустым")
    private String password;

    @Override
    public String toString() {
        return email;
    }
}

