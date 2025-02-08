package ru.test.taskmanagementsystem.expectionhandler;

/**
 * Исключение, выбрасываемое при отсутствии прав доступа.
 */
public class AccessDeniedException extends RuntimeException {
    /**
     * Создает исключение с указанным сообщением.
     *
     * @param message сообщение ошибки
     */
    public AccessDeniedException(String message) {
        super(message);
    }
}
