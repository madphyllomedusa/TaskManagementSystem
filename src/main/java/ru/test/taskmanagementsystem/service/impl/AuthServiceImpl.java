package ru.test.taskmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.test.taskmanagementsystem.config.JwtService;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.expectionhandler.NotFoundException;
import ru.test.taskmanagementsystem.model.dto.response.user.JwtAuthenticationResponse;
import ru.test.taskmanagementsystem.model.dto.request.user.SignInRequest;
import ru.test.taskmanagementsystem.model.dto.request.user.SignUpRequest;
import ru.test.taskmanagementsystem.model.entity.User;
import ru.test.taskmanagementsystem.model.enums.Role;
import ru.test.taskmanagementsystem.model.mapper.UserMapper;
import ru.test.taskmanagementsystem.repository.UserRepository;
import ru.test.taskmanagementsystem.service.AuthService;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public JwtAuthenticationResponse login(SignInRequest signInRequest) {
        String email = signInRequest.getEmail();
        String password = signInRequest.getPassword();
        logger.info("Attempting to login user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с email " + email + " не найден"));

        if (!passwordEncoder.matches(password, new String(user.getPassword(), StandardCharsets.UTF_8))) {
            logger.error("Incorrect password for user with email: {}", email);
            throw new BadRequestException("Неверный пароль для пользователя " + email);
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        logger.info("User with email {} successfully logged in", email);
        return new JwtAuthenticationResponse(token);
    }

    @Override
    public JwtAuthenticationResponse register(SignUpRequest signUpRequest) {
        String email = signUpRequest.getEmail();
        logger.info("Attempting to register user with email: {}", email);

        isEmailNotUnique(email);

        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            logger.error("Passwords do not match for user with email: {}", email);
            throw new BadRequestException("Пароли не совпадают");
        }

        String hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        User user = userMapper.fromSignUpRequest(signUpRequest);
        user.setPassword(hashedPassword.getBytes(StandardCharsets.UTF_8));
        user.setRole(Role.ROLE_USER);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole());
        logger.info("User with email {} successfully registered", email);
        return new JwtAuthenticationResponse(token);
    }

    /**
     * Проверяет, является ли email уникальным.
     * Если email уже зарегистрирован в системе, выбрасывает исключение.
     *
     * @param email email пользователя для проверки
     * @throws BadRequestException если email уже зарегистрирован в системе
     */
    private void isEmailNotUnique(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            logger.error("User with email {} already exists", email);
            throw new BadRequestException("Пользователь с таким email " + email + " уже зарегистрирован");
        }
    }
}