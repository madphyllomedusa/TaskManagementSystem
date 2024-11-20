package ru.test.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.test.taskmanagementsystem.model.dto.JwtAuthenticationResponse;
import ru.test.taskmanagementsystem.model.dto.SignInRequest;
import ru.test.taskmanagementsystem.model.dto.SignUpRequest;
import ru.test.taskmanagementsystem.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Регистрация пользователя",
            description = """
                    Регистрация нового пользователя в сервисе.
                    Пользователь должен предоставить уникальный email и пароль, совпадающий с подтверждением пароля.
                    Если роль не указана, будет назначена роль 'ROLE_USER' по умолчанию.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = """
                    Возможные причины:
                    - Email уже зарегистрирован.
                    - Пароли не совпадают.
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> register(
            @RequestBody
            @Schema(description = "Данные пользователя для регистрации", implementation = SignUpRequest.class) SignUpRequest signUpRequest) {
        JwtAuthenticationResponse jwtAuthenticationResponse = authService.register(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(jwtAuthenticationResponse);
    }

    @Operation(
            summary = "Авторизация пользователя",
            description = """
                    Авторизация уже зарегистрированного пользователя в сервисе.
                    Пользователь должен предоставить свой email и пароль, который он указывал при регистрации.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Пароль не совпадает с сохраненным в системе.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "email введенный пользователем отсутствует в базе данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(
            @RequestBody
            @Schema(description = "Данные пользователя для авторизации", implementation = SignInRequest.class) SignInRequest signInRequest) {
        JwtAuthenticationResponse jwtAuthenticationResponse = authService.login(signInRequest);
        return ResponseEntity.status(HttpStatus.OK).body(jwtAuthenticationResponse);
    }
}
