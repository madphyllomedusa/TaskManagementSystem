package ru.test.taskmanagementsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.expectionhandler.NotFoundException;
import ru.test.taskmanagementsystem.model.entity.User;
import ru.test.taskmanagementsystem.model.enums.Role;
import ru.test.taskmanagementsystem.repository.UserRepository;
import ru.test.taskmanagementsystem.service.impl.UserServiceImpl;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @ParameterizedTest
    @MethodSource("provideGetCurrentUserData")
    void testGetCurrentUser_Success(String email, User expectedUser) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        User actual = userService.getCurrentUser();

        assertEquals(expectedUser, actual);
    }

    static Stream<Arguments> provideGetCurrentUserData() {
        User user = new User();
        user.setId(100L);
        user.setEmail("user@mail.com");
        user.setRole(Role.ROLE_USER);
        return Stream.of(
            Arguments.of("user@mail.com", user)
        );
    }

    @ParameterizedTest
    @MethodSource("provideNotAuthenticatedData")
    void testGetCurrentUser_NotAuthenticated(Authentication auth) {

        when(securityContext.getAuthentication()).thenReturn(auth);

        assertThrows(BadRequestException.class, () -> userService.getCurrentUser());
    }

    static Stream<Arguments> provideNotAuthenticatedData() {
        return Stream.of(
            Arguments.of((Authentication) null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetUserByIdData")
    void testGetUserById_Success(Long id, User expectedUser) {
        when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

        User actual = userService.getUserById(id);

        assertEquals(expectedUser, actual);
    }

    static Stream<Arguments> provideGetUserByIdData() {
        User user = new User();
        user.setId(100L);
        user.setRole(Role.ROLE_USER);
        return Stream.of(
            Arguments.of(100L, user)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUserIdNotFoundData")
    void testGetUserById_NotFound(Long id) {
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(id));
    }

    static Stream<Arguments> provideUserIdNotFoundData() {
        return Stream.of(
            Arguments.of(200L)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetUserByUsernameData")
    void testGetUserByUsername_Success(String username, User expectedUser) {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        User actual = userService.getUserByUsername(username);

        assertEquals(expectedUser, actual);
    }

    static Stream<Arguments> provideGetUserByUsernameData() {
        User user = new User();
        user.setId(100L);
        user.setUsername("testuser");
        user.setRole(Role.ROLE_USER);
        return Stream.of(
            Arguments.of("testuser", user)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUserByUsernameNotFoundData")
    void testGetUserByUsername_NotFound(String username) {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserByUsername(username));
    }

    static Stream<Arguments> provideUserByUsernameNotFoundData() {
        return Stream.of(
            Arguments.of("nonexistent")
        );
    }
}
