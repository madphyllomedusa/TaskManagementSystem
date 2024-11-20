package ru.test.taskmanagementsystem.model.dto;

import lombok.Data;
import ru.test.taskmanagementsystem.model.enums.Role;

@Data
public class SignUpRequest {
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
