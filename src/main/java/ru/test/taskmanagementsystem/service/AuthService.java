package ru.test.taskmanagementsystem.service;

import ru.test.taskmanagementsystem.model.dto.JwtAuthenticationResponse;
import ru.test.taskmanagementsystem.model.dto.SignInRequest;
import ru.test.taskmanagementsystem.model.dto.SignUpRequest;

public interface AuthService {
    JwtAuthenticationResponse login(SignInRequest signInRequest);
    JwtAuthenticationResponse register(SignUpRequest signUpRequest);
}
