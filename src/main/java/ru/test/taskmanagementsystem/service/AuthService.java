package ru.test.taskmanagementsystem.service;

import ru.test.taskmanagementsystem.model.dto.response.user.JwtAuthenticationResponse;
import ru.test.taskmanagementsystem.model.dto.request.user.SignInRequest;
import ru.test.taskmanagementsystem.model.dto.request.user.SignUpRequest;

/**
 * Интерфейс сервиса аутентификации и регистрации пользователей.
 * Предоставляет методы для входа и регистрации в системе.
 */
public interface AuthService {

    /**
     * Авторизует пользователя в системе.
     * Проверяет учетные данные и выдает JWT-токен при успешной аутентификации.
     *
     * @param signInRequest объект, содержащий email и пароль пользователя
     * @return объект {@link JwtAuthenticationResponse}, содержащий сгенерированный JWT-токен
     * @throws ru.test.taskmanagementsystem.expectionhandler.NotFoundException если пользователь с указанным email не найден
     * @throws ru.test.taskmanagementsystem.expectionhandler.BadRequestException если пароль неверный
     */
    JwtAuthenticationResponse login(SignInRequest signInRequest);

    /**
     * Регистрирует нового пользователя в системе.
     * Проверяет уникальность email и соответствие паролей.
     * После успешной регистрации выдает JWT-токен.
     *
     * @param signUpRequest объект с регистрационными данными пользователя
     * @return объект {@link JwtAuthenticationResponse}, содержащий сгенерированный JWT-токен
     * @throws ru.test.taskmanagementsystem.expectionhandler.BadRequestException если email уже используется или пароли не совпадают
     */
    JwtAuthenticationResponse register(SignUpRequest signUpRequest);
}

