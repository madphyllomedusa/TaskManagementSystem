package ru.test.taskmanagementsystem.expectionhandler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для приложения.
 * Перехватывает стандартные исключения и возвращает корректные HTTP-ответы.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение {@link BadRequestException}.
     *
     * @param e       исключение
     * @param request HTTP-запрос
     * @return ответ с кодом 400 (Bad Request)
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException e, HttpServletRequest request) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает исключение {@link NotFoundException}.
     *
     * @param e       исключение
     * @param request HTTP-запрос
     * @return ответ с кодом 404 (Not Found)
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает исключение {@link ForbiddenException}.
     *
     * @param e       исключение
     * @param request HTTP-запрос
     * @return ответ с кодом 403 (Forbidden)
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException e, HttpServletRequest request) {
        return buildResponseEntity(HttpStatus.FORBIDDEN, e.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает исключение {@link AccessDeniedException}.
     *
     * @param e       исключение
     * @param request HTTP-запрос
     * @return ответ с кодом 403 (Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        return buildResponseEntity(HttpStatus.FORBIDDEN, e.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает исключения валидации {@link MethodArgumentNotValidException}.
     * Возвращает список ошибок валидации в теле ответа.
     *
     * @param ex исключение валидации
     * @return ответ с кодом 400 (Bad Request) и списком ошибок
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Формирует стандартный ответ с деталями ошибки.
     *
     * @param status  HTTP-статус
     * @param message сообщение ошибки
     * @param path    путь запроса
     * @return объект ответа с деталями ошибки
     */
    private ResponseEntity<Object> buildResponseEntity(
            HttpStatus status,
            String message,
            String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("error", status.getReasonPhrase());
        body.put("status", status.value());
        body.put("message", message);
        body.put("path", path);
        return new ResponseEntity<>(body, status);
    }
}
