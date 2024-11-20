package ru.test.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.test.taskmanagementsystem.model.enums.Role;

@Data
public class SignUpRequest {
    @Schema(description = "Адрес электронной почты пользователя", example = "user@example.com")
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
    @Schema(description = "Уникальное имя пользователя", example = "username123")
    private String username;
    @Schema(description = "Пароль пользователя", example = "password123")
    private String password;
    @Schema(description = "Подтверждение пароля, должно совпадать с password", example = "password123")
    private String confirmPassword;
    private Role role;

    @Override
    public String toString() {
        return email;
    }
}
