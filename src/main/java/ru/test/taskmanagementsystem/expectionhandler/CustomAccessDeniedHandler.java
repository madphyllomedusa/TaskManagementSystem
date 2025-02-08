package ru.test.taskmanagementsystem.expectionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Обработчик ошибок доступа для Spring Security.
 * Отправляет JSON-ответ с кодом 403 (Forbidden) и детализированным сообщением.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Обрабатывает ошибки доступа.
     *
     * @param request               HTTP-запрос
     * @param response              HTTP-ответ
     * @param accessDeniedException исключение, связанное с отсутствием доступа
     * @throws IOException      если произошла ошибка ввода/вывода
     * @throws ServletException если произошла ошибка сервлета
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       org.springframework.security.access.AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        logger.error("Access denied: {}", accessDeniedException.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("message", "У вас нет доступа");
        body.put("path", request.getRequestURI());

        String jsonResponse = objectMapper.writeValueAsString(body);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
