package ru.test.taskmanagementsystem.service;


import ru.test.taskmanagementsystem.model.entity.User;

/**
 * Сервис для управления пользователями.
 */
public interface UserService {

    /**
     * Получает текущего аутентифицированного пользователя.
     *
     * @return объект {@link User}, представляющий текущего пользователя
     */
    User getCurrentUser();

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект {@link User}, содержащий данные найденного пользователя
     */
    User getUserById(Long id);

    /**
     * Получает пользователя по его имени (username).
     *
     * @param username имя пользователя
     * @return объект {@link User}, содержащий данные найденного пользователя
     */
    User getUserByUsername(String username);
}
