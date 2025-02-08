package ru.test.taskmanagementsystem.expectionhandler;

/**
 * Исключение выбрасываемое при BadRequest
 */
public class BadRequestException extends RuntimeException {
    /**
     * Создает исключение с указанным сообщением.
     *
     * @param message сообщение ошибки
     */
    public BadRequestException(String message) {
        super(message);
    }
}
