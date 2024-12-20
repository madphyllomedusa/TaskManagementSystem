package ru.test.taskmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.test.taskmanagementsystem.config.JwtService;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.expectionhandler.NotFoundException;
import ru.test.taskmanagementsystem.model.dto.JwtAuthenticationResponse;
import ru.test.taskmanagementsystem.model.dto.SignInRequest;
import ru.test.taskmanagementsystem.model.dto.SignUpRequest;
import ru.test.taskmanagementsystem.model.entity.User;
import ru.test.taskmanagementsystem.model.enums.Role;
import ru.test.taskmanagementsystem.model.mapper.UserMapper;
import ru.test.taskmanagementsystem.repository.UserRepository;
import ru.test.taskmanagementsystem.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public JwtAuthenticationResponse login(SignInRequest signInRequest) {
        String email = signInRequest.getEmail();
        String password = signInRequest.getPassword();
        logger.info("Attempting to login user");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Неверный пароль");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().toString());
        logger.info("User successfully logged in");
        return new JwtAuthenticationResponse(token);
    }

    @Override
    public JwtAuthenticationResponse register(SignUpRequest signUpRequest) {
        logger.info("Attempting to register user");
        boolean isEmailNotUnique = userRepository.findByEmail(signUpRequest.getEmail()).isPresent();
        boolean isUsernameNotUnique = userRepository.findByUsername(signUpRequest.getUsername()).isPresent();
        boolean isPasswordSame = signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword());

        if (isEmailNotUnique) {
            logger.info("User with email {} already exists", signUpRequest.getEmail());
            throw new BadRequestException("Пользователь с таким адресом электронной почты "
                    + signUpRequest.getEmail() + " уже зарегистрирован");
        }

        if (isUsernameNotUnique) {
            logger.info("Username {} already exists", signUpRequest.getUsername());
            throw new BadRequestException("Этот username уже занят");
        }

        if (!isPasswordSame) {
            throw new BadRequestException("Пароли не совпадают");
        }

        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        User user = userMapper.fromSignUpRequest(signUpRequest);
        user.setRole(Role.ROLE_USER);
        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole().toString());
        logger.info("User successfully registered");
        return new JwtAuthenticationResponse(token);
    }
}
