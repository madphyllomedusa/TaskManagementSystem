package ru.test.taskmanagementsystem.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.test.taskmanagementsystem.model.enums.Role;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String username;

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotNull(message = "Роль пользователя обязательна")
    private Role role;

    @Override
    public String toString() {
        return email + " " + role;
    }
}
