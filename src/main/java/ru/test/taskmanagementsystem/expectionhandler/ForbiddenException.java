package ru.test.taskmanagementsystem.expectionhandler;

/**
 * Исключение, выбрасываемое при отсутствии прав доступа к ресурсу.
 */
public class ForbiddenException extends RuntimeException {
    /**
     * Конструктор исключения.
     *
     * @param message сообщение ошибки
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
