package ru.test.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    @Schema(description = "Адрес электронной почты пользователя", example = "user@example.com")
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @Schema(description = "Пароль пользователя", example = "password123")
    private String password;

    @Override
    public String toString() {
        return email;
    }
}
