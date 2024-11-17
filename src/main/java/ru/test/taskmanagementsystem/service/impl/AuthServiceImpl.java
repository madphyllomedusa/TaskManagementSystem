package ru.test.taskmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.test.taskmanagementsystem.config.JwtService;
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
    private final static Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public JwtAuthenticationResponse login(SignInRequest signInRequest) {
        String email = signInRequest.getEmail();
        String password = signInRequest.getPassword();
        logger.info("Attempting to login user with email {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().toString());
        logger.info("User {} successfully logged in", user.getEmail());
        logger.info("token: {}", token);
        return new JwtAuthenticationResponse(token);
    }

    @Override
    public JwtAuthenticationResponse register(SignUpRequest signUpRequest) {
        logger.info("Registering user with email {}", signUpRequest.getEmail());
        if(userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            logger.info("User with email {} already exists", signUpRequest.getEmail());
            throw new BadCredentialsException("Пользователь с таким адресом электронной почты "
                    + signUpRequest.getEmail() + " уже зарегистрирован");
        }

        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            throw new BadCredentialsException("Пароли не совпадают");
        }

        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        if(signUpRequest.getRole() == null) {
            signUpRequest.setRole(Role.ROLE_USER);
        }

        User user = userMapper.toEntity(signUpRequest);
        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole().toString());
        logger.info("User {} successfully registered", user.getEmail());
        logger.info("token: {}", token);
        return new JwtAuthenticationResponse(token);
    }
}
