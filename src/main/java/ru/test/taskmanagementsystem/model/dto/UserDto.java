package ru.test.taskmanagementsystem.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.test.taskmanagementsystem.model.enums.Role;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Role role;

    @Override
    public String toString() {
        return email + " " + role;
    }
}
