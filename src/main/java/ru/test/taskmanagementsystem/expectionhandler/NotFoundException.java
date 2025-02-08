package ru.test.taskmanagementsystem.expectionhandler;

/**
 * Исключение, выбрасываемое при отсутствии запрашиваемого ресурса.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Конструктор исключения.
     *
     * @param message сообщение ошибки
     */
    public NotFoundException(String message) {
        super(message);
    }
}
