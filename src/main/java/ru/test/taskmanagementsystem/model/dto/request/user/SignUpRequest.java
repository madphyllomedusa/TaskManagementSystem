package ru.test.taskmanagementsystem.model.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO-запрос для регистрации пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    /**
     * Email пользователя.
     * Должен быть в формате email.
     */
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    /**
     * Имя пользователя.
     */
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String username;

    /**
     * Пароль пользователя.
     */
    @NotBlank(message = "Пароль не должен быть пустым")
    private String password;

    /**
     * Подтверждение пароля.
     * Должно совпадать с основным паролем.
     */
    @NotBlank(message = "Подтверждение пароля не должно быть пустым")
    private String confirmPassword;

    @Override
    public String toString() {
        return email;
    }
}

