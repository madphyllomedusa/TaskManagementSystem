package ru.test.taskmanagementsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.test.taskmanagementsystem.config.JwtService;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.expectionhandler.NotFoundException;
import ru.test.taskmanagementsystem.model.dto.request.user.SignInRequest;
import ru.test.taskmanagementsystem.model.dto.request.user.SignUpRequest;
import ru.test.taskmanagementsystem.model.dto.response.user.JwtAuthenticationResponse;
import ru.test.taskmanagementsystem.model.entity.User;
import ru.test.taskmanagementsystem.model.enums.Role;
import ru.test.taskmanagementsystem.model.mapper.UserMapper;
import ru.test.taskmanagementsystem.repository.UserRepository;
import ru.test.taskmanagementsystem.service.impl.AuthServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForLogin")
    void testLogin(String email, String password, boolean validPassword, boolean shouldThrowException) {
        SignInRequest signInRequest = new SignInRequest(email, password);

        String encodedPassword = "encodedPassword123";

        if (shouldThrowException) {
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> authService.login(signInRequest));

            verify(userRepository, times(1)).findByEmail(email);
            verifyNoInteractions(passwordEncoder);
        } else {
            User mockUser = new User();
            mockUser.setEmail(email);

            when(passwordEncoder.encode(any(CharSequence.class))).thenReturn(encodedPassword);

            mockUser.setPassword(encodedPassword.getBytes(StandardCharsets.UTF_8));
            mockUser.setRole(Role.ROLE_USER);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

            if (!validPassword) {
                when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

                assertThrows(BadRequestException.class, () -> authService.login(signInRequest));

                verify(passwordEncoder, times(1)).matches(password, encodedPassword);
            } else {
                when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
                when(jwtService.generateToken(email, mockUser.getRole())).thenReturn("token");

                JwtAuthenticationResponse response = authService.login(signInRequest);

                assertNotNull(response);
                assertEquals("token", response.getToken());

                verify(passwordEncoder, times(1)).matches(password, encodedPassword);
                verify(jwtService, times(1)).generateToken(email, mockUser.getRole());
            }
            verify(userRepository, times(1)).findByEmail(email);
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForRegister")
    void testRegister(SignUpRequest signUpRequest, boolean emailExists, boolean passwordsMatch) {
        when(userRepository.findByEmail(signUpRequest.getEmail()))
                .thenReturn(emailExists ? Optional.of(new User()) : Optional.empty());
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("hashedPassword");

        if (!passwordsMatch) {
            assertThrows(BadRequestException.class, () -> authService.register(signUpRequest));
        } else if (emailExists) {
            assertThrows(BadRequestException.class, () -> authService.register(signUpRequest));
        } else {
            User user = new User();
            when(userMapper.fromSignUpRequest(signUpRequest)).thenReturn(user);
            when(userRepository.save(user)).thenReturn(user);
            when(jwtService.generateToken(user.getEmail(), Role.ROLE_USER)).thenReturn("token");

            JwtAuthenticationResponse response = authService.register(signUpRequest);
            assertNotNull(response);
            assertEquals("token", response.getToken());

            verify(userRepository, times(1)).save(user);
        }
    }

    static Stream<Arguments> provideTestDataForLogin() {
        return Stream.of(
                Arguments.of("user1@example.com", "wrongPassword", false, false),
                Arguments.of("user1@example.com", "encodedPassword123", true, false),
                Arguments.of("unknown@example.com", "password", false, true)
        );
    }

    static Stream<Object[]> provideTestDataForRegister() {
        return Stream.of(
                new Object[]{
                        new SignUpRequest("user1@example.com", "user", "password", "password"),
                        false, true},
                new Object[]{
                        new SignUpRequest("user2@example.com", "user", "password", "mismatch"),
                        false, false},
                new Object[]{
                        new SignUpRequest("user3@example.com", "user", "password", "password"),
                        true, true}
        );
    }
}