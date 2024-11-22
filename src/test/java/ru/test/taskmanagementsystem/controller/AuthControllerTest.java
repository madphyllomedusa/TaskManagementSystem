package ru.test.taskmanagementsystem.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.test.taskmanagementsystem.config.JwtService;
import ru.test.taskmanagementsystem.config.SecurityConfig;
import ru.test.taskmanagementsystem.expectionhandler.BadRequestException;
import ru.test.taskmanagementsystem.model.dto.JwtAuthenticationResponse;
import ru.test.taskmanagementsystem.model.dto.SignInRequest;
import ru.test.taskmanagementsystem.model.dto.SignUpRequest;
import ru.test.taskmanagementsystem.service.AuthService;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @ParameterizedTest
    @MethodSource("provideRegisterTestData")
    void testRegister(SignUpRequest signUpRequest, JwtAuthenticationResponse expectedResponse, int expectedStatus) throws Exception {
        if (expectedStatus == 201) {
            Mockito.when(authService.register(Mockito.any(SignUpRequest.class))).thenReturn(expectedResponse);
        } else {
            Mockito.doThrow(new BadRequestException("Invalid data")).when(authService).register(Mockito.any(SignUpRequest.class));
        }

        var resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(signUpRequest)))
                .andExpect(status().is(expectedStatus));

        if (expectedStatus == 201) {
            resultActions.andExpect(jsonPath("$.token").value(expectedResponse.getToken()));
        }
    }

    static Stream<Arguments> provideRegisterTestData() {
        return Stream.of(
                Arguments.of(
                        new SignUpRequest("user1@mail.com", "username1", "password123", "password123"),
                        new JwtAuthenticationResponse("valid-jwt-token-1"),
                        201
                ),
                Arguments.of(
                        new SignUpRequest("user2@mail.com", "username2", "password456", "password456"),
                        new JwtAuthenticationResponse("valid-jwt-token-2"),
                        201
                ),
                Arguments.of(
                        new SignUpRequest("", "user@mail.com", "password123", "password123"),
                        null,
                        400
                ),
                Arguments.of(
                        new SignUpRequest("username", "invalid-email", "password123", "password123"),
                        null,
                        400
                ),
                Arguments.of(
                        new SignUpRequest("username", "user@mail.com", "password123", "wrongpassword"),
                        null,
                        400
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideLoginTestData")
    void testLogin(SignInRequest signInRequest, JwtAuthenticationResponse expectedResponse, int expectedStatus) throws Exception {
        if (expectedStatus == 200) {
            Mockito.when(authService.login(Mockito.any(SignInRequest.class))).thenReturn(expectedResponse);
        } else {
            Mockito.doThrow(new BadRequestException("Invalid credentials")).when(authService).login(Mockito.any(SignInRequest.class));
        }

        var resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(signInRequest)))
                .andExpect(status().is(expectedStatus));

        if (expectedStatus == 200) {
            resultActions.andExpect(jsonPath("$.token").value(expectedResponse.getToken()));
        }
    }

    static Stream<Arguments> provideLoginTestData() {
        return Stream.of(
                Arguments.of(
                        new SignInRequest("user1@mail.com", "password123"),
                        new JwtAuthenticationResponse("valid-jwt-token-1"),
                        200
                ),
                Arguments.of(
                        new SignInRequest("user2@mail.com", "password456"),
                        new JwtAuthenticationResponse("valid-jwt-token-2"),
                        200
                ),
                Arguments.of(
                        new SignInRequest("user@mail.com", "wrongpassword"),
                        null,
                        400
                ),
                Arguments.of(
                        new SignInRequest("nonexistentuser@mail.com", "password123"),
                        null,
                        400
                )
        );
    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
