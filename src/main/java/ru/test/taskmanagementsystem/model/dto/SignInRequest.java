package ru.test.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SignInRequest {
    @Schema(description = "Адрес электронной почты пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "Пароль пользователя", example = "password123")
    private String password;

    @Override
    public String toString() {
        return email;
    }
}
