package ru.test.taskmanagementsystem.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.test.taskmanagementsystem.model.enums.Role;

@Data
public class SignUpRequest {
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private Role role;

    @Override
    public String toString() {
        return email;
    }
}
