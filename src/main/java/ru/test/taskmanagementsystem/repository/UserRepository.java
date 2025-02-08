package ru.test.taskmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.taskmanagementsystem.model.entity.User;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Ищет пользователя по email.
     *
     * @param email Email пользователя.
     * @return Optional с пользователем, если найден.
     */
    Optional<User> findByEmail(String email);

    /**
     * Ищет пользователя по имени пользователя.
     *
     * @param username Имя пользователя.
     * @return Optional с пользователем, если найден.
     */
    Optional<User> findByUsername(String username);
}
