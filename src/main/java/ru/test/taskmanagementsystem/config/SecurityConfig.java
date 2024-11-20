package ru.test.taskmanagementsystem.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.OffsetDateTime;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/tasks").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/tasks/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/tasks/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/tasks/{id}/status").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/tasks/{id}/priority").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/tasks/{id}/assignee").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/tasks/{id}/comments").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/tasks").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType("application/json; charset=UTF-8");

                                    String jsonResponse = String
                                            .format("{\"timestamp\": \"%s\"," +
                                                            " \"error\": \"Forbidden\"," +
                                                            " \"status\": %d," +
                                                            " \"message\": \"%s\"," +
                                                            " \"path\": \"%s\"}",
                                            OffsetDateTime.now(),
                                            HttpServletResponse.SC_FORBIDDEN,
                                            "У вас нет доступа",
                                            request.getRequestURI());

                                    response.getWriter().write(jsonResponse);
                                    response.getWriter().flush();
                                }
                        )
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
