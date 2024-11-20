package ru.test.taskmanagementsystem.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.test.taskmanagementsystem.controller.AuthControllerApi;
import ru.test.taskmanagementsystem.model.dto.JwtAuthenticationResponse;
import ru.test.taskmanagementsystem.model.dto.SignInRequest;
import ru.test.taskmanagementsystem.model.dto.SignUpRequest;
import ru.test.taskmanagementsystem.service.AuthService;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {
    private final AuthService authService;

    @Override
    public ResponseEntity<JwtAuthenticationResponse> register(SignUpRequest signUpRequest) {
        JwtAuthenticationResponse jwtAuthenticationResponse = authService.register(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(jwtAuthenticationResponse);
    }

    @Override
    public ResponseEntity<JwtAuthenticationResponse> login(SignInRequest signInRequest) {
        JwtAuthenticationResponse jwtAuthenticationResponse = authService.login(signInRequest);
        return ResponseEntity.status(HttpStatus.OK).body(jwtAuthenticationResponse);
    }
}
