package ru.test.taskmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.test.taskmanagementsystem.model.dto.response.user.JwtAuthenticationResponse;
import ru.test.taskmanagementsystem.model.dto.request.user.SignInRequest;
import ru.test.taskmanagementsystem.model.dto.request.user.SignUpRequest;
import ru.test.taskmanagementsystem.service.AuthService;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 *
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Регистрирует нового пользователя.
     *
     * @param signUpRequest данные пользователя для регистрации
     * @return токен аутентификации
     */
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> register(@Valid @RequestBody SignUpRequest signUpRequest) {
        JwtAuthenticationResponse jwtAuthenticationResponse = authService.register(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(jwtAuthenticationResponse);
    }

    /**
     * Аутентифицирует пользователя.
     *
     * @param signInRequest данные пользователя для входа
     * @return токен аутентификации
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody SignInRequest signInRequest) {
        JwtAuthenticationResponse jwtAuthenticationResponse = authService.login(signInRequest);
        return ResponseEntity.ok(jwtAuthenticationResponse);
    }
}
