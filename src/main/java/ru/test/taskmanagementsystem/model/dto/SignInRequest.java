package ru.test.taskmanagementsystem.model.dto;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String password;

    @Override
    public String toString() {
        return email;
    }
}
